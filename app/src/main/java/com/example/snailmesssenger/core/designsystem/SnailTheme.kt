package com.example.snailmesssenger.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** App-wide theme preference. */
enum class ThemePreference { SYSTEM, LIGHT, DARK }

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = LightBlueAccent,
    onPrimaryContainer = DeepNavy,
    background = LightBackground,
    onBackground = PrimaryText,
    surface = MainSurface,
    onSurface = PrimaryText,
    surfaceVariant = LightBackground,
    onSurfaceVariant = SecondaryText,
    outline = DividerBorder,
    outlineVariant = DividerBorder,
    error = ErrorRed,
    onError = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = DarkElevatedSurface,
    onPrimaryContainer = DarkPrimaryText,
    background = DarkBackground,
    onBackground = DarkPrimaryText,
    surface = DarkSurface,
    onSurface = DarkPrimaryText,
    surfaceVariant = DarkElevatedSurface,
    onSurfaceVariant = DarkSecondaryText,
    outline = DarkBorder,
    outlineVariant = DarkBorder,
    error = ErrorRed,
    onError = Color.White,
)

/** Exposes the current theme preference so screens can gate nonessential animation. */
val LocalThemePreference = staticCompositionLocalOf { ThemePreference.SYSTEM }

@Composable
fun SnailTheme(
    themePreference: ThemePreference = ThemePreference.SYSTEM,
    content: @Composable () -> Unit,
) {
    val dark = when (themePreference) {
        ThemePreference.SYSTEM -> isSystemInDarkTheme()
        ThemePreference.LIGHT -> false
        ThemePreference.DARK -> true
    }

    val colorScheme = if (dark) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalThemePreference provides themePreference) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = SnailTypography,
            shapes = SnailShapes,
            content = content,
        )
    }
}
