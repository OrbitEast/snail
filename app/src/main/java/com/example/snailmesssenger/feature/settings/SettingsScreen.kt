package com.example.snailmesssenger.feature.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.snailmesssenger.data.preferences.UserPreferences

@Composable
fun SettingsScreen(
    preferences: UserPreferences,
    onBack: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenPrivacySecurity: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenDataStorage: () -> Unit,
    onOpenAppearance: () -> Unit,
    onOpenEditProfile: () -> Unit,
    onLogout: () -> Unit,
    showBackButton: Boolean = true,
    viewModel: SettingsViewModel = viewModel(),
) {
    AnimatedContent(
        targetState = Unit,
        transitionSpec = {
            (fadeIn(com.example.snailmesssenger.core.designsystem.SnailMotion.QuickSpringSpec) +
                scaleIn(initialScale = 0.985f, animationSpec = com.example.snailmesssenger.core.designsystem.SnailMotion.QuickSpringSpec)) togetherWith
                (fadeOut(com.example.snailmesssenger.core.designsystem.SnailMotion.QuickSpringSpec) +
                    scaleOut(targetScale = 0.985f, animationSpec = com.example.snailmesssenger.core.designsystem.SnailMotion.QuickSpringSpec))
        },
        label = "settings-entry-motion",
    ) {
        DeepSettingsScreen(
            preferences = preferences,
            onBack = onBack,
            onOpenProfile = onOpenProfile,
            onOpenPrivacySecurity = onOpenPrivacySecurity,
            onOpenNotifications = onOpenNotifications,
            onOpenDataStorage = onOpenDataStorage,
            onOpenAppearance = onOpenAppearance,
            onOpenEditProfile = onOpenEditProfile,
            onLogout = onLogout,
            showBackButton = showBackButton,
            viewModel = viewModel,
        )
    }
}
