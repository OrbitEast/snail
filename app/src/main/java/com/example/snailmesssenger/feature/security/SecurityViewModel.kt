package com.example.snailmesssenger.feature.security

import android.app.Application
import android.content.Context
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.snailmesssenger.data.security.LocalVault
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/** Local-only security state. No secrets leave the device in this prototype. */
data class SecurityUiState(
    val username: String = "",
    val password: String = "",
    val usernameValid: Boolean = false,
    val passwordValid: Boolean = false,
    val twoStepEnabled: Boolean = false,
    val pinConfigured: Boolean = false,
    val mascotHidden: Boolean = false,
    val pendingTwoStep: Boolean = false,
    val error: String? = null,
    val resetCodeVisible: Boolean = false,
    val generatedResetCode: String? = null,
)

class SecurityViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val _uiState = MutableStateFlow(
        SecurityUiState(
            twoStepEnabled = prefs.getBoolean(KEY_2FA_ENABLED, false),
            pinConfigured = prefs.getString(KEY_2FA_PIN_HASH, null) != null ||
                prefs.getString(KEY_2FA_PIN, null) != null,
        ),
    )
    val uiState: StateFlow<SecurityUiState> = _uiState.asStateFlow()

    init {
        migrateLegacySecrets()
        refreshTwoStepState()
    }

    private fun migrateLegacySecrets() {
        val legacyPin = prefs.getString(KEY_2FA_PIN, null)
        if (legacyPin != null) {
            runCatching { persistPinHash(legacyPin) }
        }
        val storedResetCode = prefs.getString(KEY_RESET_CODE, null)
        if (storedResetCode != null && !LocalVault.isEncrypted(storedResetCode)) {
            runCatching {
                prefs.edit().putString(KEY_RESET_CODE, LocalVault.encrypt(storedResetCode)).apply()
            }
        }
    }

    private fun persistPinHash(pin: String) {
        val saltBytes = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val salt = Base64.encodeToString(saltBytes, Base64.NO_WRAP)
        val hash = Base64.encodeToString(derivePinHash(pin, saltBytes), Base64.NO_WRAP)
        prefs.edit()
            .putString(KEY_2FA_PIN_SALT, salt)
            .putString(KEY_2FA_PIN_HASH, hash)
            .remove(KEY_2FA_PIN)
            .apply()
    }

    private fun derivePinHash(pin: String, saltBytes: ByteArray): ByteArray {
        val spec = PBEKeySpec(pin.toCharArray(), saltBytes, PBKDF2_ITERATIONS, HASH_BITS)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private fun matchesStoredPin(pin: String): Boolean {
        val saltValue = prefs.getString(KEY_2FA_PIN_SALT, null)
        val hashValue = prefs.getString(KEY_2FA_PIN_HASH, null)
        if (saltValue != null && hashValue != null) {
            return runCatching {
                val saltBytes = Base64.decode(saltValue, Base64.NO_WRAP)
                val expected = Base64.decode(hashValue, Base64.NO_WRAP)
                val actual = derivePinHash(pin, saltBytes)
                MessageDigest.isEqual(expected, actual)
            }.getOrDefault(false)
        }
        // Temporary compatibility path: a successful check migrates old plaintext PINs.
        val legacyPin = prefs.getString(KEY_2FA_PIN, null) ?: return false
        val matches = MessageDigest.isEqual(
            legacyPin.toByteArray(Charsets.UTF_8),
            pin.toByteArray(Charsets.UTF_8),
        )
        if (matches) runCatching { persistPinHash(legacyPin) }
        return matches
    }

    fun updateUsername(value: String) {
        _uiState.update { it.copy(username = value, usernameValid = value.trim().length >= 3, error = null) }
    }

    fun updatePassword(value: String, visible: Boolean) {
        _uiState.update {
            it.copy(
                password = value,
                passwordValid = value.length >= 6,
                mascotHidden = value.isNotEmpty() && !visible,
                error = null,
            )
        }
    }

    fun setPasswordVisibility(visible: Boolean) {
        _uiState.update { it.copy(mascotHidden = it.password.isNotEmpty() && !visible) }
    }

    fun validateCredentials(): Boolean {
        val state = _uiState.value
        val usernameValid = state.username.trim().length >= 3
        val passwordValid = state.password.length >= 6
        _uiState.update {
            it.copy(
                usernameValid = usernameValid,
                passwordValid = passwordValid,
                error = when {
                    !usernameValid -> "Username must contain at least 3 characters."
                    !passwordValid -> "Password must contain at least 6 characters."
                    else -> null
                },
            )
        }
        return usernameValid && passwordValid
    }

    fun setTwoStepPin(pin: String): Boolean {
        if (pin.length < 4 || pin.length > 8 || !pin.all(Char::isDigit)) return false
        persistPinHash(pin)
        prefs.edit().putBoolean(KEY_2FA_ENABLED, true).apply()
        _uiState.update { it.copy(twoStepEnabled = true, pinConfigured = true, error = null) }
        return true
    }

    fun disableTwoStep() {
        prefs.edit()
            .remove(KEY_2FA_PIN)
            .remove(KEY_2FA_PIN_SALT)
            .remove(KEY_2FA_PIN_HASH)
            .remove(KEY_RESET_CODE)
            .putBoolean(KEY_2FA_ENABLED, false)
            .apply()
        _uiState.update { it.copy(twoStepEnabled = false, pinConfigured = false, pendingTwoStep = false, resetCodeVisible = false, generatedResetCode = null) }
    }

    fun refreshTwoStepState() {
        _uiState.update {
            it.copy(
                twoStepEnabled = prefs.getBoolean(KEY_2FA_ENABLED, false),
                pinConfigured = prefs.getString(KEY_2FA_PIN_HASH, null) != null ||
                    prefs.getString(KEY_2FA_PIN, null) != null,
            )
        }
    }

    fun beginTwoStepVerification() {
        refreshTwoStepState()
        _uiState.update { it.copy(pendingTwoStep = true, error = null) }
    }

    fun verifyPin(pin: String): Boolean {
        val valid = matchesStoredPin(pin)
        _uiState.update { it.copy(error = if (valid) null else "Incorrect verification PIN.") }
        return valid
    }

    fun requestLocalResetCode() {
        val code = (100000..999999).random().toString()
        prefs.edit().putString(KEY_RESET_CODE, LocalVault.encrypt(code)).apply()
        _uiState.update { it.copy(resetCodeVisible = true, generatedResetCode = code, error = null) }
    }

    fun resetWithCode(code: String, newPin: String): Boolean {
        val stored = prefs.getString(KEY_RESET_CODE, null)
        val decoded = stored?.let { runCatching { LocalVault.decrypt(it) }.getOrElse { it } }
        val validCode = decoded != null && MessageDigest.isEqual(
            code.toByteArray(Charsets.UTF_8),
            decoded.toByteArray(Charsets.UTF_8),
        )
        if (!validCode) {
            _uiState.update { it.copy(error = "Invalid local recovery code.") }
            return false
        }
        if (!setTwoStepPin(newPin)) {
            _uiState.update { it.copy(error = "PIN must contain 4–8 digits.") }
            return false
        }
        prefs.edit().remove(KEY_RESET_CODE).apply()
        _uiState.update { it.copy(resetCodeVisible = false, generatedResetCode = null, pendingTwoStep = false, error = null) }
        return true
    }

    companion object {
        private const val PREFS = "snail_security"
        private const val KEY_2FA_ENABLED = "two_step_enabled"
        private const val KEY_2FA_PIN = "two_step_pin" // legacy migration key; removed after hash migration
        private const val KEY_2FA_PIN_SALT = "two_step_pin_salt"
        private const val KEY_2FA_PIN_HASH = "two_step_pin_hash"
        private const val KEY_RESET_CODE = "two_step_reset_code"
        private const val PBKDF2_ITERATIONS = 120_000
        private const val HASH_BITS = 256
    }
}
