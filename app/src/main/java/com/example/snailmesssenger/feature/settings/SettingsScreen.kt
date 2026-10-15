package com.example.snailmesssenger.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.snailmesssenger.core.designsystem.ThemePreference
import com.example.snailmesssenger.data.preferences.UserPreferences

private val TelegramBlue = Color(0xFF2481CC)
private val SectionGrey = Color(0xFF71808D)

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
    val state by viewModel.uiState.collectAsStateCompat()
    var showTabDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            item {
                ProfileHeader(
                    onOpenProfile = onOpenProfile,
                    onEditProfile = onOpenEditProfile,
                )
            }

            item { SettingsSectionTitle("Account") }
            item {
                SettingsRow(
                    icon = Icons.Default.Person,
                    title = "My Profile",
                    subtitle = "Name, username and bio",
                    onClick = onOpenProfile,
                )
            }
            item {
                SettingsRow(
                    icon = Icons.Default.Security,
                    title = "Privacy and Security",
                    subtitle = "Blocked users, passcode, sessions and privacy",
                    onClick = onOpenPrivacySecurity,
                )
            }

            item { SettingsSectionTitle("Chat Settings") }
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Text("Text size", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    Text("${state.chatTextSize.toInt()}sp", color = SectionGrey, fontSize = 12.sp)
                    Slider(
                        value = state.chatTextSize,
                        onValueChange = viewModel::setChatTextSize,
                        valueRange = 12f..30f,
                        steps = 17,
                    )
                    Text("Live preview", fontSize = 12.sp, color = SectionGrey)
                    PreviewBubble(textSize = state.chatTextSize)
                }
            }
            item {
                SettingsRow(Icons.Default.Chat, "Background Pattern", "Classic Snail wallpaper", onClick = onOpenAppearance)
            }
            item {
                SettingsToggleRow(
                    Icons.Default.DarkMode,
                    "Auto-Night Mode",
                    "Switch automatically at night",
                    state.autoNightMode,
                    viewModel::setAutoNightMode,
                )
            }
            item {
                ThemePaletteRow(
                    selected = state.themePalette,
                    onSelect = viewModel::setThemePalette,
                )
            }
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Text("Message bubble corner radius", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    Text("${state.bubbleCornerRadius.toInt()}dp", color = SectionGrey, fontSize = 12.sp)
                    Slider(
                        value = state.bubbleCornerRadius,
                        onValueChange = viewModel::setBubbleCornerRadius,
                        valueRange = 0f..16f,
                        steps = 15,
                    )
                }
            }

            item { SettingsSectionTitle("Privacy and Security") }
            item { SettingsRow(Icons.Default.Security, "Blocked Users", "${state.blockedUsersCount} blocked", onClick = onOpenPrivacySecurity) }
            item { SettingsToggleRow(Icons.Default.Fingerprint, "Passcode Lock / Fingerprint", "Protect Snail on this device", state.fingerprintLock, viewModel::setFingerprintLock) }
            item { SettingsRow(Icons.Default.Lock, "Two-Step Verification", if (state.twoStepEnabled) "Enabled" else "Not enabled", onClick = { viewModel.setTwoStepEnabled(!state.twoStepEnabled) }) }
            item { SettingsRow(Icons.Default.Devices, "Active Sessions", "Mobile • Desktop • Web", onClick = onOpenPrivacySecurity) }
            item { PrivacyPermissionRow("Phone Number", "My Contacts") }
            item { PrivacyPermissionRow("Last Seen & Online", "Everybody") }
            item { PrivacyPermissionRow("Profile Photos", "Everybody") }
            item { PrivacyPermissionRow("Voice Messages", "Everybody") }

            item { SettingsSectionTitle("Data and Storage") }
            item { StorageUsageCard(state.cacheProgress) }
            item {
                SettingsRow(Icons.Default.Storage, "Storage Usage", "Photos 1.2 GB • Videos 640 MB • Files 180 MB • Cache 96 MB", onClick = onOpenDataStorage)
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Button(onClick = viewModel::startClearCache, enabled = !state.isClearingCache) {
                        if (state.isClearingCache) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        else Text("Clear Snail Cache")
                    }
                    if (state.isClearingCache) LinearProgressIndicator(progress = { state.cacheProgress }, modifier = Modifier.weight(1f).padding(start = 12.dp))
                }
            }
            item { SettingsToggleRow(Icons.Default.Storage, "Automatic Media Download — Mobile Data", "Photos and files", state.mobileDataDownloads, viewModel::setMobileDataDownloads) }
            item { SettingsToggleRow(Icons.Default.Storage, "Automatic Media Download — Wi-Fi", "Photos, videos and files", state.wifiDownloads, viewModel::setWifiDownloads) }

            item { SettingsSectionTitle("Notifications and Sounds") }
            item { SettingsToggleRow(Icons.Default.Notifications, "Private Chats Notifications", "Messages from one-to-one chats", state.privateChatNotifications, viewModel::setPrivateChatNotifications) }
            item { SettingsRow(Icons.Default.Notifications, "Custom Ringtone", "Default Snail tone", onClick = onOpenNotifications) }
            item { SettingsToggleRow(Icons.Default.Notifications, "Groups & Channels Notifications", "Group message alerts", state.groupNotifications, viewModel::setGroupNotifications) }
            item { SettingsToggleRow(Icons.Default.Notifications, "Include Muted Chats", "Badge counter", state.includeMutedChats, viewModel::setIncludeMutedChats) }
            item { SettingsToggleRow(Icons.Default.Notifications, "Count Unread Messages", "Show unread count on the app icon", state.countUnreadMessages, viewModel::setCountUnreadMessages) }

            item { SettingsSectionTitle("Folders / Chat Tabs") }
            item {
                SettingsRow(
                    Icons.Default.Folder,
                    "Create Custom Chat Tab",
                    "${state.tabs.size} tabs configured",
                    onClick = { showTabDialog = true },
                )
            }
            items(state.tabs.size) { index ->
                val tab = state.tabs[index]
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 72.dp, end = 20.dp, top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(tab.name, modifier = Modifier.weight(1f), fontSize = 14.sp)
                    if (tab.unreadOnly) Text("Unread", color = TelegramBlue, fontSize = 12.sp)
                }
            }

            item { SettingsSectionTitle("Appearance") }
            item { SettingsRow(Icons.Default.DarkMode, "Theme", preferences.theme.value.name.lowercase().replaceFirstChar { it.uppercase() }, onClick = onOpenAppearance) }
            item { SettingsRow(Icons.Default.ArrowForward, "Animations", "Smooth navigation transitions", onClick = onOpenAppearance) }

            item {
                TextButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                ) { Text("Log Out", color = MaterialTheme.colorScheme.error) }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }

    if (showTabDialog) {
        CreateTabDialog(
            onDismiss = { showTabDialog = false },
            onCreate = { name -> viewModel.createTab(name); showTabDialog = false },
        )
    }
}

@Composable
private fun ProfileHeader(onOpenProfile: () -> Unit, onEditProfile: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onOpenProfile).padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(76.dp).background(TelegramBlue, CircleShape), contentAlignment = Alignment.Center) {
            Text("N", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier.align(Alignment.BottomEnd).size(25.dp).background(Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Default.CameraAlt, null, tint = TelegramBlue, modifier = Modifier.size(16.dp)) }
        }
        Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
            Text("Nayan", fontSize = 19.sp, fontWeight = FontWeight.Bold)
            Text("@nayan", color = TelegramBlue, fontSize = 13.sp)
            Text("Add a few words about yourself", color = SectionGrey, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
        }
        IconButton(onClick = onEditProfile) { Icon(Icons.Default.KeyboardArrowRight, null) }
    }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        text,
        color = SectionGrey,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 20.dp, vertical = 8.dp),
    )
}

@Composable
private fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = TelegramBlue, modifier = Modifier.size(23.dp))
        Column(modifier = Modifier.weight(1f).padding(start = 20.dp)) {
            Text(title, fontSize = 15.sp)
            if (subtitle != null) Text(subtitle, color = SectionGrey, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
        }
        Icon(Icons.Default.KeyboardArrowRight, null, tint = SectionGrey)
    }
}

@Composable
private fun SettingsToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = TelegramBlue, modifier = Modifier.size(23.dp))
        Column(modifier = Modifier.weight(1f).padding(start = 20.dp)) {
            Text(title, fontSize = 15.sp)
            Text(subtitle, color = SectionGrey, fontSize = 12.sp)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun PrivacyPermissionRow(title: String, selected: String) {
    SettingsRow(Icons.Default.Security, title, selected, onClick = {})
}

@Composable
private fun ThemePaletteRow(selected: String, onSelect: (String) -> Unit) {
    val palettes = listOf("Default Blue", "Cyan", "Green", "Night", "Pitch Black")
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text("Dark Theme Color Palette", fontSize = 15.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            palettes.forEach { palette ->
                val chosen = palette == selected
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(paletteColor(palette))
                        .clickable { onSelect(palette) }
                        .padding(if (chosen) 3.dp else 0.dp)
                        .background(if (chosen) Color.White else Color.Transparent, CircleShape),
                )
            }
        }
        Text(selected, color = SectionGrey, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
    }
}

private fun paletteColor(name: String): Color = when (name) {
    "Cyan" -> Color(0xFF28A9E0)
    "Green" -> Color(0xFF35B66F)
    "Night" -> Color(0xFF1C2733)
    "Pitch Black" -> Color.Black
    else -> TelegramBlue
}

@Composable
private fun PreviewBubble(textSize: Float) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5D8)),
        ) {
            Text("This is a live chat preview", fontSize = textSize.sp, modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp))
        }
    }
}

@Composable
private fun StorageUsageCard(progress: Float) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Storage Usage", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(progress = { 0.62f }, modifier = Modifier.fillMaxWidth())
            Text("Photos 1.2 GB  •  Videos 640 MB  •  Files 180 MB  •  Cache 96 MB", fontSize = 12.sp, color = SectionGrey, modifier = Modifier.padding(top = 8.dp))
            if (progress > 0f) Text("Cache cleared", color = TelegramBlue, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun CreateTabDialog(onDismiss: () -> Unit, onCreate: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Chat Tab") },
        text = {
            androidx.compose.material3.OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                label = { Text("Tab name") },
            )
        },
        confirmButton = { TextButton(onClick = { onCreate(name) }, enabled = name.isNotBlank()) { Text("Create") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun SettingsViewModel.collectAsStateCompat(): androidx.compose.runtime.State<SettingsUiState> =
    androidx.compose.runtime.collectAsState(this.uiState)
