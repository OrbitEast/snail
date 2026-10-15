package com.example.snailmesssenger.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.snailmesssenger.core.designsystem.ThemePreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Persists theme and animation preferences locally using SharedPreferences.
 * These are simple, non-sensitive UI settings — no encryption needed.
 */
class UserPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("snail_prefs", Context.MODE_PRIVATE)

    private val _theme = MutableStateFlow(loadTheme())
    val theme: StateFlow<ThemePreference> = _theme.asStateFlow()

    private val _animationsEnabled = MutableStateFlow(loadAnimations())
    val animationsEnabled: StateFlow<Boolean> = _animationsEnabled.asStateFlow()

    fun setTheme(preference: ThemePreference) {
        prefs.edit().putString(KEY_THEME, preference.name).apply()
        _theme.value = preference
    }

    fun setAnimationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ANIMATIONS, enabled).apply()
        _animationsEnabled.value = enabled
    }

    var privateChatNotifications: Boolean
        get() = prefs.getBoolean(KEY_PRIVATE_CHAT_NOTIFICATIONS, true)
        set(value) { prefs.edit().putBoolean(KEY_PRIVATE_CHAT_NOTIFICATIONS, value).apply() }

    var messagePreviews: Boolean
        get() = prefs.getBoolean(KEY_MESSAGE_PREVIEWS, true)
        set(value) { prefs.edit().putBoolean(KEY_MESSAGE_PREVIEWS, value).apply() }

    var notificationSound: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATION_SOUND, true)
        set(value) { prefs.edit().putBoolean(KEY_NOTIFICATION_SOUND, value).apply() }

    var notificationVibration: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATION_VIBRATION, true)
        set(value) { prefs.edit().putBoolean(KEY_NOTIFICATION_VIBRATION, value).apply() }

    var inAppSounds: Boolean
        get() = prefs.getBoolean(KEY_IN_APP_SOUNDS, true)
        set(value) { prefs.edit().putBoolean(KEY_IN_APP_SOUNDS, value).apply() }

    private fun loadTheme(): ThemePreference =
        runCatching { ThemePreference.valueOf(prefs.getString(KEY_THEME, ThemePreference.SYSTEM.name)!!) }
            .getOrDefault(ThemePreference.SYSTEM)

    private fun loadAnimations(): Boolean =
        prefs.getBoolean(KEY_ANIMATIONS, true)

    companion object {
        private const val KEY_THEME = "theme"
        private const val KEY_ANIMATIONS = "animations"
        private const val KEY_PRIVATE_CHAT_NOTIFICATIONS = "private_chat_notifications"
        private const val KEY_MESSAGE_PREVIEWS = "message_previews"
        private const val KEY_NOTIFICATION_SOUND = "notification_sound"
        private const val KEY_NOTIFICATION_VIBRATION = "notification_vibration"
        private const val KEY_IN_APP_SOUNDS = "in_app_sounds"
    }
}
