package com.example.snailmesssenger.data.repository

import com.example.snailmesssenger.data.model.User
import kotlinx.coroutines.delay

/**
 * Authentication surface for SNAIL Messenger.
 *
 * Phase 1 uses [DemoAuthRepository] which keeps everything in memory.
 * When a real backend exists, provide a new implementation that performs
 * actual network authentication and swap it at the composition root —
 * the UI layer only depends on this interface.
 */
interface AuthRepository {
    /** Register a local demo account. Throws on invalid input. */
    suspend fun register(username: String, password: String, confirmPassword: String): User

    /** Log in to a local demo account. Throws on invalid credentials. */
    suspend fun login(username: String, password: String): User

    /** Log out and discard the in-memory session. */
    fun logout()

    /** The current in-memory session user, or null when logged out. */
    var currentUser: User?

    /** Update profile fields for the current session user. */
    fun updateUser(updated: User)
}

/**
 * In-memory demo implementation.
 * Accounts live only for the lifetime of this process; nothing is persisted
 * to disk and no network calls are made. This is intentional for Phase 1.
 *
 * Implemented as a singleton so the session survives screen navigation
 * within the single-activity Compose setup.
 */
object DemoAuthRepository : AuthRepository {

    private val accounts = mutableMapOf<String, User>()

    override var currentUser: User? = null

    override fun updateUser(updated: User) {
        currentUser = updated
        accounts[updated.username] = updated
    }

    override suspend fun register(username: String, password: String, confirmPassword: String): User {
        delay(600) // simulate a brief round-trip for the loading state
        val trimmed = username.trim()
        require(trimmed.isNotEmpty()) { "Username cannot be empty." }
        require(trimmed.matches(Regex("^[a-z0-9_]{3,20}$"))) {
            "Usernames use 3–20 lowercase letters, numbers and underscores."
        }
        require(password.length >= 6) { "Password must be at least 6 characters." }
        require(password == confirmPassword) { "Passwords do not match." }
        require(!accounts.containsKey(trimmed)) { "That username is already taken." }
        val user = User(
            id = "u_${trimmed.hashCode().toUInt()}",
            username = trimmed,
            displayName = trimmed.replaceFirstChar { it.uppercase() },
            bio = "",
            isOnline = true,
        )
        accounts[trimmed] = user
        currentUser = user
        return user
    }

    override suspend fun login(username: String, password: String): User {
        delay(600)
        val trimmed = username.trim()
        require(trimmed.isNotEmpty()) { "Username cannot be empty." }
        require(password.isNotEmpty()) { "Password cannot be empty." }
        val user = accounts[trimmed] ?: throw IllegalArgumentException(
            "No local demo account found for \"$trimmed\". Create one first."
        )
        currentUser = user
        return user
    }

    override fun logout() {
        currentUser = null
    }
}
