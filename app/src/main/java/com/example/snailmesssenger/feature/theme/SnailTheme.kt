package com.example.snailmesssenger.feature.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val LocalSnailThemeState = staticCompositionLocalOf { ThemeState() }
val LocalChatTextSize = staticCompositionLocalOf { 16.sp }
val LocalChatBubbleRadius = staticCompositionLocalOf { 16.dp }

@Composable
fun SnailTheme(state: ThemeState, content: @Composable () -> Unit) {
    val dark = when (state.nightMode) {
        NightMode.SYSTEM -> isSystemInDarkTheme()
        NightMode.LIGHT -> false
        NightMode.DARK -> true
    }
    val accent = if (state.palette == SnailPalette.MONOCHROME) {
        runCatching { Color(android.graphics.Color.parseColor(state.customAccentHex)) }.getOrDefault(Color(0xFF2481CC))
    } else Color(state.palette.primaryHex)
    val target = if (dark) {
        darkColorScheme(primary = accent, onPrimary = Color.White, background = if (state.palette == SnailPalette.AMOLED) Color.Black else Color(state.palette.darkBackgroundHex), surface = Color(state.palette.darkSurfaceHex), secondary = accent)
    } else {
        lightColorScheme(primary = accent, onPrimary = Color.White, background = if (state.palette == SnailPalette.DAY_CYAN) Color(0xFFF4FAFD) else Color(state.palette.lightBackgroundHex), surface = Color.White, secondary = accent)
    }
    val primary by animateColorAsState(target.primary, label = "theme-primary")
    val background by animateColorAsState(target.background, label = "theme-background")
    val surface by animateColorAsState(target.surface, label = "theme-surface")
    val base = Typography()
    val typography = base.copy(bodyLarge = base.bodyLarge.copy(fontSize = state.messageTextSp.sp), bodyMedium = base.bodyMedium.copy(fontSize = (state.messageTextSp - 1f).coerceAtLeast(11f).sp))
    CompositionLocalProvider(
        LocalSnailThemeState provides state,
        LocalChatTextSize provides state.messageTextSp.sp,
        LocalChatBubbleRadius provides state.bubbleRadiusDp.dp,
    ) {
        MaterialTheme(colorScheme = target.copy(primary = primary, background = background, surface = surface), typography = typography, content = content)
    }
}
