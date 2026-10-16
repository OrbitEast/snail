package com.example.snailmesssenger.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.example.snailmesssenger.data.model.User
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Locale
import java.util.UUID
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlinx.coroutines.delay
import org.json.JSONObject

/**
 * Authentication surface for SNAIL Messenger.
 *
 * Phase 1 stores demo accounts on this device only. Replace this implementation
 * with a real backend when cross-device accounts are introduced.
 */
interface AuthRepository {
    suspend fun register(username: String, password: String, confirmPassword: String): User
    suspend fun login(username: String, password: String): User
    fun logout()
    var currentUser: User?
    fun updateUser(updated: User)
}

private data class AccountRecord(
    val user: User,
    val salt: String,
    val passwordHash: String,
)

/**
 * Device-local prototype account store. Passwords are never written in plaintext:
 * each account uses a random salt and PBKDF2-HMAC-SHA256 derived hash.
 */
object DemoAuthRepository : AuthRepository {

    private val accounts = mutableMapOf<String, AccountRecord>()
    private var preferences: SharedPreferences? = null

    override var currentUser: User? = null

    fun initialize(context: Context) {
        if (preferences != null) return
        val store = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        preferences = store
        val saved = store.getString(KEY_ACCOUNTS, null)
        val root = saved?.let { runCatching { JSONObject(it) }.getOrNull() }
        if (root != null) {
            val keys = root.keys()
            while (keys.hasNext()) {
                val username = keys.next()
                val entry = root.optJSONObject(username) ?: continue
                val salt = entry.optString("salt")
                val passwordHash = entry.optString("passwordHash")
                if (salt.isBlank() || passwordHash.isBlank()) continue
                val user = User(
                    id = entry.optString("id", "u_${username.hashCode().toUInt()}"),
                    username = username,
                    displayName = entry.optString("displayName", username),
                    bio = entry.optString("bio", ""),
                    isOnline = false,
                )
                accounts[username] = AccountRecord(user, salt, passwordHash)
            }
        }
        currentUser = store.getString(KEY_ACTIVE_USER, null)
            ?.let { activeUsername -> accounts[activeUsername]?.user?.copy(isOnline = false) }
    }

    override fun updateUser(updated: User) {
        val safeUser = updated.copy(isOnline = false)
        currentUser = safeUser
        preferences?.edit()?.putString(KEY_ACTIVE_USER, safeUser.username)?.apply()
        val existing = accounts[safeUser.username] ?: return
        accounts[safeUser.username] = existing.copy(user = safeUser)
        persistAccounts()
    }

    override suspend fun register(username: String, password: String, confirmPassword: String): User {
        delay(350)
        check(preferences != null) { "Local account storage has not been initialized." }
        val trimmed = username.trim().lowercase(Locale.ROOT)
        require(trimmed.matches(Regex("^[a-z0-9_]{3,20}$"))) {
            "Usernames use 3–20 lowercase letters, numbers and underscores."
        }
        require(password.length >= 6) { "Password must be at least 6 characters." }
        require(password == confirmPassword) { "Passwords do not match." }
        require(!accounts.containsKey(trimmed)) { "That username is already taken." }

        val saltBytes = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val salt = Base64.encodeToString(saltBytes, Base64.NO_WRAP)
        val hash = Base64.encodeToString(derivePasswordHash(password, saltBytes), Base64.NO_WRAP)
        val user = User(
            id = "u_${UUID.randomUUID().toString().take(12)}",
            username = trimmed,
            displayName = trimmed.replaceFirstChar { it.uppercase() },
            bio = "",
            isOnline = false,
        )
        accounts[trimmed] = AccountRecord(user, salt, hash)
        currentUser = user
        preferences?.edit()?.putString(KEY_ACTIVE_USER, trimmed)?.apply()
        persistAccounts()
        return user
    }

    override suspend fun login(username: String, password: String): User {
        delay(350)
        check(preferences != null) { "Local account storage has not been initialized." }
        val trimmed = username.trim().lowercase(Locale.ROOT)
        require(trimmed.isNotEmpty()) { "Username is required." }
        require(password.isNotEmpty()) { "Password is required." }
        val account = accounts[trimmed] ?: throw IllegalArgumentException(
            "No local demo account found for \"$trimmed\". Create one first."
        )
        val saltBytes = Base64.decode(account.salt, Base64.NO_WRAP)
        val expectedHash = Base64.decode(account.passwordHash, Base64.NO_WRAP)
        val actualHash = derivePasswordHash(password, saltBytes)
        if (!MessageDigest.isEqual(expectedHash, actualHash)) {
            throw IllegalArgumentException("Incorrect password.")
        }
        val safeUser = account.user.copy(isOnline = false)
        currentUser = safeUser
        preferences?.edit()?.putString(KEY_ACTIVE_USER, trimmed)?.apply()
        return safeUser
    }

    override fun logout() {
        currentUser = null
        preferences?.edit()?.remove(KEY_ACTIVE_USER)?.apply()
    }

    private fun derivePasswordHash(password: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, HASH_BITS)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private fun persistAccounts() {
        val root = JSONObject()
        accounts.forEach { (username, account) ->
            root.put(
                username,
                JSONObject()
                    .put("id", account.user.id)
                    .put("displayName", account.user.displayName)
                    .put("bio", account.user.bio)
                    .put("salt", account.salt)
                    .put("passwordHash", account.passwordHash),
            )
        }
        preferences?.edit()?.putString(KEY_ACCOUNTS, root.toString())?.apply()
    }

    private const val PREFS_NAME = "snail_local_accounts"
    private const val KEY_ACCOUNTS = "accounts_json"
    private const val KEY_ACTIVE_USER = "active_username"
    private const val PBKDF2_ITERATIONS = 120_000
    private const val HASH_BITS = 256
}
