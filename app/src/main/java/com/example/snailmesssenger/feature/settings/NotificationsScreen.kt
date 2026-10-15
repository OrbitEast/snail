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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.snailmesssenger.core.designsystem.SnailSectionHeading
import com.example.snailmesssenger.core.designsystem.SnailSectionRow
import com.example.snailmesssenger.core.designsystem.Spacing
import com.example.snailmesssenger.data.preferences.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    preferences: UserPreferences,
    onBack: () -> Unit,
) {
    var chatsNotifications by remember { mutableStateOf(preferences.privateChatNotifications) }
    var messagePreviews by remember { mutableStateOf(preferences.messagePreviews) }
    var soundEnabled by remember { mutableStateOf(preferences.notificationSound) }
    var vibrationEnabled by remember { mutableStateOf(preferences.notificationVibration) }
    var inAppSounds by remember { mutableStateOf(preferences.inAppSounds) }

    Scaffold(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = { Text("Notifications and Sounds", style = MaterialTheme.typography.titleLarge) },
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
            SnailSectionHeading(text = "Chats Notifications")
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "Private Chats",
                subtitle = if (chatsNotifications) "Enabled" else "Disabled",
                icon = Icons.Default.Notifications,
                trailing = {
                    Switch(checked = chatsNotifications, onCheckedChange = { chatsNotifications = it; preferences.privateChatNotifications = it })
                },
            )
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "Message Previews",
                subtitle = "Show text in notification",
                icon = Icons.Default.VolumeUp,
                trailing = {
                    Switch(checked = messagePreviews, onCheckedChange = { messagePreviews = it; preferences.messagePreviews = it })
                },
            )

            Spacer(Modifier.height(Spacing.xl))

            SnailSectionHeading(text = "Sounds & Vibration")
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "Sound",
                subtitle = "Default",
                icon = Icons.Default.VolumeUp,
                trailing = {
                    Switch(checked = soundEnabled, onCheckedChange = { soundEnabled = it; preferences.notificationSound = it })
                },
            )
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "Vibration",
                subtitle = "Default",
                icon = Icons.Default.Smartphone,
                trailing = {
                    Switch(checked = vibrationEnabled, onCheckedChange = { vibrationEnabled = it; preferences.notificationVibration = it })
                },
            )

            Spacer(Modifier.height(Spacing.xl))

            SnailSectionHeading(text = "In-App Notifications")
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "In-App Sounds",
                subtitle = "Play sound when app is open",
                icon = Icons.Default.VolumeUp,
                trailing = {
                    Switch(checked = inAppSounds, onCheckedChange = { inAppSounds = it; preferences.inAppSounds = it })
                },
            )

            Spacer(Modifier.height(Spacing.xxxl))
        }
    }
}
