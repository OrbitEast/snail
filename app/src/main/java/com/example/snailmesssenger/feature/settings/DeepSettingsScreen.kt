package com.example.snailmesssenger.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeepSettingsScreen(
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
    val state by viewModel.uiState.collectAsState()
    var showTabDialog by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Settings", fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                if (showBackButton) {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                }
            },
        )
    }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding)) {
            item { ProfileHeader(onOpenProfile, onOpenEditProfile) }
            item { Section("Account") }
            item { RowItem(Icons.Default.Person, "My Profile", "Name, username and bio", onOpenProfile) }
            item { RowItem(Icons.Default.Security, "Privacy and Security", "Blocked users, passcode, sessions and privacy", onOpenPrivacySecurity) }

            item { Section("Chat Settings") }
            item {
                Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Text("Text Size", fontWeight = FontWeight.Medium, fontSize = 15.sp)
                    Text("${state.chatTextSize.toInt()}sp", color = SectionGrey, fontSize = 12.sp)
                    Slider(state.chatTextSize, viewModel::setChatTextSize, valueRange = 12f..30f, steps = 17)
                    Text("Live preview", color = SectionGrey, fontSize = 12.sp)
                    PreviewBubble(state.chatTextSize)
                }
            }
            item { RowItem(Icons.Default.Chat, "Background Pattern", "Classic Snail wallpaper", onOpenAppearance) }
            item { ToggleRow(Icons.Default.DarkMode, "Auto-Night Mode", "Automatic night switching", state.autoNightMode, viewModel::setAutoNightMode) }
            item { PaletteRow(state.themePalette, viewModel::setThemePalette) }
            item {
                Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Text("Message Bubble Corner Radius", fontWeight = FontWeight.Medium, fontSize = 15.sp)
                    Text("${state.bubbleCornerRadius.toInt()}dp", color = SectionGrey, fontSize = 12.sp)
                    Slider(state.bubbleCornerRadius, viewModel::setBubbleCornerRadius, valueRange = 0f..16f, steps = 15)
                }
            }

            item { Section("Privacy and Security") }
            item { RowItem(Icons.Default.Security, "Blocked Users", "${state.blockedUsersCount} blocked", onOpenPrivacySecurity) }
            item { ToggleRow(Icons.Default.Fingerprint, "Passcode Lock / Fingerprint", "Protect Snail on this device", state.fingerprintLock, viewModel::setFingerprintLock) }
            item { RowItem(Icons.Default.Lock, "Two-Step Verification", if (state.twoStepEnabled) "Enabled" else "Not enabled") { viewModel.setTwoStepEnabled(!state.twoStepEnabled) } }
            item { RowItem(Icons.Default.Devices, "Active Sessions", "Mobile • Desktop • Web", onOpenPrivacySecurity) }
            item { RowItem(Icons.Default.Security, "Last Seen & Online", "Visibility preferences", onOpenPrivacySecurity) }

            item { Section("Data and Storage") }
            item { StorageCard(state.cacheProgress) }
            item { RowItem(Icons.Default.Storage, "Storage Usage", "Messages and local app data", onOpenDataStorage) }
            item {
                Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = viewModel::startClearCache, enabled = !state.isClearingCache) {
                        if (state.isClearingCache) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp) else Text("Clear Snail Cache")
                    }
                    if (state.isClearingCache) LinearProgressIndicator(progress = { state.cacheProgress }, Modifier.weight(1f).padding(start = 12.dp))
                }
            }

            item { Section("Notifications and Sounds") }
            item { ToggleRow(Icons.Default.Notifications, "Private Chats Notifications", "One-to-one chat alerts", state.privateChatNotifications, viewModel::setPrivateChatNotifications) }
            item { RowItem(Icons.Default.Notifications, "Custom Ringtone", "Default Snail tone", onOpenNotifications) }
            item { ToggleRow(Icons.Default.Notifications, "Include Muted Chats", "Badge counter", state.includeMutedChats, viewModel::setIncludeMutedChats) }
            item { ToggleRow(Icons.Default.Notifications, "Count Unread Messages", "App icon badge", state.countUnreadMessages, viewModel::setCountUnreadMessages) }

            item { Section("Folders / Chat Tabs") }
            item { RowItem(Icons.Default.Folder, "Create Custom Chat Tab", "${state.tabs.size} tabs configured") { showTabDialog = true } }
            item {
                Column(Modifier.fillMaxWidth().padding(start = 72.dp, end = 20.dp, top = 4.dp, bottom = 8.dp)) {
                    state.tabs.forEach { tab ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(tab.name, Modifier.weight(1f), fontSize = 14.sp)
                            if (tab.unreadOnly) Text("Unread", color = TelegramBlue, fontSize = 12.sp)
                        }
                    }
                }
            }

            item { Section("Appearance") }
            item { RowItem(Icons.Default.DarkMode, "Theme", preferences.theme.value.name.lowercase().replaceFirstChar { it.uppercase() }, onOpenAppearance) }
            item { RowItem(Icons.Default.Animation, "Animations", "Smooth navigation transitions", onOpenAppearance) }
            item { TextButton(onClick = onLogout, Modifier.fillMaxWidth().padding(12.dp)) { Text("Log Out", color = MaterialTheme.colorScheme.error) } }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }

    if (showTabDialog) {
        CreateTabDialog(
            onDismiss = { showTabDialog = false },
            onCreate = { viewModel.createTab(it); showTabDialog = false },
        )
    }
}

@Composable
private fun ProfileHeader(onOpenProfile: () -> Unit, onEdit: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onOpenProfile).padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(76.dp).background(TelegramBlue, CircleShape), contentAlignment = Alignment.Center) {
            Text("N", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Box(Modifier.align(Alignment.BottomEnd).size(25.dp).background(Color.White, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.CameraAlt, null, tint = TelegramBlue, modifier = Modifier.size(16.dp))
            }
        }
        Column(Modifier.weight(1f).padding(start = 16.dp)) {
            Text("Nayan", fontSize = 19.sp, fontWeight = FontWeight.Bold)
            Text("@nayan", color = TelegramBlue, fontSize = 13.sp)
            Text("Add a few words about yourself", color = SectionGrey, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
        }
        IconButton(onClick = onEdit) { Icon(Icons.Default.KeyboardArrowRight, null) }
    }
}

@Composable private fun Section(title: String) = Text(title, color = SectionGrey, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 20.dp, vertical = 8.dp))

@Composable
private fun RowItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String? = null, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = TelegramBlue, modifier = Modifier.size(23.dp))
        Column(Modifier.weight(1f).padding(start = 20.dp)) {
            Text(title, fontSize = 15.sp)
            subtitle?.let { Text(it, color = SectionGrey, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp)) }
        }
        Icon(Icons.Default.KeyboardArrowRight, null, tint = SectionGrey)
    }
}

@Composable
private fun ToggleRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = TelegramBlue, modifier = Modifier.size(23.dp))
        Column(Modifier.weight(1f).padding(start = 20.dp)) { Text(title, fontSize = 15.sp); Text(subtitle, color = SectionGrey, fontSize = 12.sp) }
        Switch(checked, onCheckedChange = onChange)
    }
}

@Composable
private fun PaletteRow(selected: String, onSelect: (String) -> Unit) {
    val palettes = listOf("Default Blue", "Cyan", "Green", "Night", "Pitch Black")
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text("Dark Theme Color Palette", fontSize = 15.sp, fontWeight = FontWeight.Medium)
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.padding(top = 8.dp)) {
            palettes.forEach { palette ->
                val selectedNow = selected == palette
                Box(Modifier.size(30.dp).clip(CircleShape).background(paletteColor(palette)).clickable { onSelect(palette) }.padding(if (selectedNow) 3.dp else 0.dp).background(if (selectedNow) Color.White else Color.Transparent, CircleShape))
            }
        }
        Text(selected, color = SectionGrey, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
    }
}

private fun paletteColor(name: String) = when (name) {
    "Cyan" -> Color(0xFF28A9E0)
    "Green" -> Color(0xFF35B66F)
    "Night" -> Color(0xFF1C2733)
    "Pitch Black" -> Color.Black
    else -> TelegramBlue
}

@Composable private fun PreviewBubble(textSize: Float) = Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
    Card(shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5D8))) {
        Text("This is a live chat preview", fontSize = textSize.sp, modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp))
    }
}

@Composable private fun StorageCard(progress: Float) {
    Card(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(16.dp)) {
            Text("Storage Usage", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(progress = { 0.62f }, Modifier.fillMaxWidth())
            Text("Photos 1.2 GB  •  Videos 640 MB  •  Files 180 MB  •  Cache 96 MB", fontSize = 12.sp, color = SectionGrey, modifier = Modifier.padding(top = 8.dp))
            if (progress > 0f) Text("Cache cleared", color = TelegramBlue, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable private fun CreateTabDialog(onDismiss: () -> Unit, onCreate: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Chat Tab") },
        text = { OutlinedTextField(value = name, onValueChange = { name = it }, singleLine = true, label = { Text("Tab name") }) },
        confirmButton = { TextButton(onClick = { onCreate(name) }, enabled = name.isNotBlank()) { Text("Create") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
