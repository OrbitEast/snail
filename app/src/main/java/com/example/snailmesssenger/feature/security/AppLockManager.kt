package com.example.snailmesssenger.feature.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppLockUiState(
    val enabled: Boolean = false,
    val locked: Boolean = false,
    val failedAttempts: Int = 0,
    val lockoutUntilMs: Long = 0L,
    val message: String? = null,
)

/**
 * Device-local application lock.
 *
 * PINs are salted and PBKDF2-HMAC-SHA256 hashed; the PIN itself is never persisted.
 * This prevents casual access to the app UI, but does not replace Android device lock
 * or end-to-end message encryption.
 */
object AppLockManager {
    private var preferences: SharedPreferences? = null
    private var backgroundedAtMs: Long? = null

    private val _state = MutableStateFlow(AppLockUiState())
    val state: StateFlow<AppLockUiState> = _state.asStateFlow()

    fun initialize(context: Context) {
        if (preferences != null) return
        val store = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        preferences = store
        val enabled = store.getBoolean(KEY_ENABLED, false)
        _state.value = AppLockUiState(
            enabled = enabled,
            locked = enabled,
            failedAttempts = store.getInt(KEY_FAILED_ATTEMPTS, 0),
            lockoutUntilMs = store.getLong(KEY_LOCKOUT_UNTIL_MS, 0L),
        )
    }

    fun enableWithPin(pin: String): Boolean {
        val store = preferences ?: return showError("App lock storage is not ready.")
        if (!isValidPin(pin)) {
            _state.value = _state.value.copy(message = "PIN must contain 4–8 digits.")
            return false
        }
        val saltBytes = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val salt = Base64.encodeToString(saltBytes, Base64.NO_WRAP)
        val hash = Base64.encodeToString(derivePinHash(pin, saltBytes), Base64.NO_WRAP)
        store.edit()
            .putBoolean(KEY_ENABLED, true)
            .putString(KEY_SALT, salt)
            .putString(KEY_HASH, hash)
            .putInt(KEY_FAILED_ATTEMPTS, 0)
            .putLong(KEY_LOCKOUT_UNTIL_MS, 0L)
            .apply()
        backgroundedAtMs = null
        _state.value = AppLockUiState(enabled = true, locked = false)
        return true
    }

    fun disableWithPin(pin: String): Boolean {
        val store = preferences ?: return showError("App lock storage is not ready.")
        if (isLockedOut()) return showLockoutMessage()
        if (!matchesStoredPin(pin)) {
            recordFailedAttempt()
            return false
        }
        store.edit()
            .putBoolean(KEY_ENABLED, false)
            .remove(KEY_SALT)
            .remove(KEY_HASH)
            .putInt(KEY_FAILED_ATTEMPTS, 0)
            .putLong(KEY_LOCKOUT_UNTIL_MS, 0L)
            .apply()
        backgroundedAtMs = null
        _state.value = AppLockUiState()
        return true
    }

    fun unlock(pin: String): Boolean {
        val current = _state.value
        if (!current.enabled || !current.locked) return true
        if (isLockedOut()) return showLockoutMessage()
        if (!matchesStoredPin(pin)) {
            recordFailedAttempt()
            return false
        }
        preferences?.edit()
            ?.putInt(KEY_FAILED_ATTEMPTS, 0)
            ?.putLong(KEY_LOCKOUT_UNTIL_MS, 0L)
            ?.apply()
        _state.value = current.copy(
            locked = false,
            failedAttempts = 0,
            lockoutUntilMs = 0L,
            message = null,
        )
        return true
    }

    fun lockNow() {
        if (_state.value.enabled) {
            _state.value = _state.value.copy(locked = true, message = null)
        }
    }

    /** Lock on cold start, or when returning after 30 seconds away from the app. */
    fun onAppBackgrounded() {
        backgroundedAtMs = System.currentTimeMillis()
    }

    fun onAppForegrounded() {
        val backgroundAt = backgroundedAtMs ?: return
        backgroundedAtMs = null
        if (_state.value.enabled && System.currentTimeMillis() - backgroundAt >= AUTO_LOCK_DELAY_MS) {
            lockNow()
        }
    }

    fun clearMessage() {
        if (_state.value.message != null) _state.value = _state.value.copy(message = null)
    }

    private fun isValidPin(pin: String): Boolean = pin.length in 4..8 && pin.all(Char::isDigit)

    private fun matchesStoredPin(pin: String): Boolean {
        if (!isValidPin(pin)) return false
        val store = preferences ?: return false
        val storedSalt = store.getString(KEY_SALT, null) ?: return false
        val storedHash = store.getString(KEY_HASH, null) ?: return false
        return runCatching {
            val salt = Base64.decode(storedSalt, Base64.NO_WRAP)
            val expected = Base64.decode(storedHash, Base64.NO_WRAP)
            val actual = derivePinHash(pin, salt)
            MessageDigest.isEqual(expected, actual)
        }.getOrDefault(false)
    }

    private fun derivePinHash(pin: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(pin.toCharArray(), salt, PBKDF2_ITERATIONS, HASH_BITS)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private fun isLockedOut(): Boolean =
        (preferences?.getLong(KEY_LOCKOUT_UNTIL_MS, 0L) ?: 0L) > System.currentTimeMillis()

    private fun showLockoutMessage(): Boolean {
        val until = preferences?.getLong(KEY_LOCKOUT_UNTIL_MS, 0L) ?: 0L
        val seconds = ((until - System.currentTimeMillis()).coerceAtLeast(0L) + 999L) / 1_000L
        _state.value = _state.value.copy(message = "Too many incorrect attempts. Try again in $seconds seconds.")
        return false
    }

    private fun recordFailedAttempt() {
        val store = preferences ?: return
        val attempts = store.getInt(KEY_FAILED_ATTEMPTS, 0) + 1
        val lockoutDuration = when {
            attempts >= 15 -> 5 * 60_000L
            attempts >= 10 -> 2 * 60_000L
            attempts >= 5 -> 30_000L
            else -> 0L
        }
        val lockoutUntil = if (lockoutDuration > 0L) System.currentTimeMillis() + lockoutDuration else 0L
        store.edit()
            .putInt(KEY_FAILED_ATTEMPTS, attempts)
            .putLong(KEY_LOCKOUT_UNTIL_MS, lockoutUntil)
            .apply()
        val message = if (lockoutDuration > 0L) {
            "Too many incorrect attempts. Try again in ${lockoutDuration / 1_000L} seconds."
        } else {
            "Incorrect passcode."
        }
        _state.value = _state.value.copy(
            failedAttempts = attempts,
            lockoutUntilMs = lockoutUntil,
            message = message,
        )
    }

    private fun showError(message: String): Boolean {
        _state.value = _state.value.copy(message = message)
        return false
    }

    private const val PREFS_NAME = "snail_app_lock"
    private const val KEY_ENABLED = "enabled"
    private const val KEY_SALT = "pin_salt"
    private const val KEY_HASH = "pin_hash"
    private const val KEY_FAILED_ATTEMPTS = "failed_attempts"
    private const val KEY_LOCKOUT_UNTIL_MS = "lockout_until_ms"
    private const val PBKDF2_ITERATIONS = 120_000
    private const val HASH_BITS = 256
    const val AUTO_LOCK_DELAY_MS = 30_000L
}
