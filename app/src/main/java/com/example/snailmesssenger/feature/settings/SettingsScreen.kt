package com.example.snailmesssenger.feature.settings

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
    viewModel: SettingsViewModel = viewModel(),
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
        viewModel = viewModel,
    )
}
