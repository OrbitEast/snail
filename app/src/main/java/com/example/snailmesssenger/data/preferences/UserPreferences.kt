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

    private fun loadTheme(): ThemePreference =
        runCatching { ThemePreference.valueOf(prefs.getString(KEY_THEME, ThemePreference.SYSTEM.name)!!) }
            .getOrDefault(ThemePreference.SYSTEM)

    private fun loadAnimations(): Boolean =
        prefs.getBoolean(KEY_ANIMATIONS, true)

    companion object {
        private const val KEY_THEME = "theme"
        private const val KEY_ANIMATIONS = "animations"
    }
}
