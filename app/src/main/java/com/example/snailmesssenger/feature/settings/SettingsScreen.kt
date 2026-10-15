package com.example.snailmesssenger.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.snailmesssenger.core.designsystem.SnailSectionHeading
import com.example.snailmesssenger.core.designsystem.SnailSectionRow
import com.example.snailmesssenger.core.designsystem.Spacing
import com.example.snailmesssenger.data.preferences.UserPreferences
import com.example.snailmesssenger.data.repository.DemoAuthRepository

@OptIn(ExperimentalMaterial3Api::class)
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
) {
    val theme by preferences.theme.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            // Appearance
            SnailSectionHeading(text = "Appearance & Display")
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "Appearance",
                subtitle = "Theme: ${theme.name.lowercase().replaceFirstChar { it.uppercase() }}",
                icon = Icons.Default.DarkMode,
                onClick = onOpenAppearance,
            )

            Spacer(Modifier.height(Spacing.xl))

            // Account & Profile
            SnailSectionHeading(text = "Account")
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "Edit Profile",
                subtitle = "@${DemoAuthRepository.currentUser?.username ?: "you"}",
                icon = Icons.Default.Person,
                onClick = onOpenEditProfile,
            )
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "Log out",
                icon = Icons.AutoMirrored.Filled.Logout,
                onClick = { showLogoutDialog = true },
                destructive = true,
            )

            Spacer(Modifier.height(Spacing.xl))

            // Notifications
            SnailSectionHeading(text = "Notifications")
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "Notifications and Sounds",
                subtitle = "Message alerts, sounds & preview",
                icon = Icons.Default.Notifications,
                onClick = onOpenNotifications,
            )

            Spacer(Modifier.height(Spacing.xl))

            // Privacy and security
            SnailSectionHeading(text = "Privacy and security")
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "Privacy and Security",
                subtitle = "Last seen, passcode, sessions & blocked users",
                icon = Icons.Default.Security,
                onClick = onOpenPrivacySecurity,
            )

            Spacer(Modifier.height(Spacing.xl))

            // Data & Storage
            SnailSectionHeading(text = "Data and Storage")
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "Data and Storage",
                subtitle = "Network usage & cache management",
                icon = Icons.Default.Storage,
                onClick = onOpenDataStorage,
            )

            Spacer(Modifier.height(Spacing.xl))

            // About
            SnailSectionHeading(text = "About")
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "Snail",
                icon = Icons.Default.Info,
            )
            Text(
                text = "An independent messaging app prototype.\nVersion 1.2.0 (Phase 2 — Full UI & Settings)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = Spacing.xl, vertical = Spacing.md),
            )

            Spacer(Modifier.height(Spacing.xxxl))
        }
    }

    // Logout confirmation
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log out") },
            text = { Text("This will end your local demo session.") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    DemoAuthRepository.logout()
                    onLogout()
                }) { Text("Log out") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            },
        )
    }
}
