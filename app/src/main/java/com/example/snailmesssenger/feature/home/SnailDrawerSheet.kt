package com.example.snailmesssenger.feature.home

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.snailmesssenger.core.designsystem.ThemePreference
import com.example.snailmesssenger.data.preferences.UserPreferences

private val DrawerBlue = Color(0xFF2481CC)
private val DrawerDark = Color(0xFF1C2733)
private val DrawerLightBlue = Color(0xFFB3D7FF)

@Composable
fun SnailDrawerSheet(
    preferences: UserPreferences,
    onClose: () -> Unit,
    onOpenChats: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenContacts: () -> Unit,
    onOpenCalls: () -> Unit,
    onOpenSavedMessages: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHelp: () -> Unit,
    viewModel: DrawerViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val dark = preferences.theme.value == ThemePreference.DARK
    val surface = if (dark) DrawerDark else Color.White
    val primaryText = if (dark) Color.White else Color(0xFF222222)
    val secondaryText = if (dark) Color(0xFFB7C1CA) else Color(0xFF71808D)

    ModalDrawerSheet(
        modifier = Modifier.fillMaxHeight(),
        drawerContainerColor = surface,
    ) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            item {
                DrawerHeader(
                    state = state,
                    dark = dark,
                    onToggleAccountSwitcher = viewModel::toggleAccountSwitcher,
                    onToggleTheme = {
                        preferences.setTheme(if (dark) ThemePreference.LIGHT else ThemePreference.DARK)
                    },
                    onSelectAccount = viewModel::selectAccount,
                    onAddAccount = { onOpenProfile(); onClose() },
                )
            }
            item { HorizontalDivider(color = if (dark) Color(0xFF30404D) else Color(0xFFE4E8EC)) }
            item { DrawerItem(Icons.Default.Person, "My Profile", primaryText) { onOpenProfile(); onClose() } }
            item { DrawerItem(Icons.Default.Bookmark, "Saved Messages", primaryText) { onOpenSavedMessages(); onClose() } }
            item { DrawerItem(Icons.Default.Group, "New Group", primaryText) { viewModel.showNewGroup() } }
            item { DrawerItem(Icons.Default.Person, "Contacts", primaryText) { onOpenContacts(); onClose() } }
            item { DrawerItem(Icons.Default.Call, "Calls", primaryText) { onOpenCalls(); onClose() } }
            item { DrawerItem(Icons.Default.Settings, "Settings", primaryText) { onOpenSettings(); onClose() } }
            item {
                HorizontalDivider(
                    color = if (dark) Color(0xFF30404D) else Color(0xFFE4E8EC),
                    modifier = Modifier.padding(vertical = 6.dp),
                )
            }
            item {
                DrawerItem(Icons.Default.PersonAdd, "Invite Friends", primaryText) {
                    runCatching {
                        context.startActivity(
                            Intent.createChooser(
                                Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "Join me on Snail Messenger — a simple private messenger.")
                                },
                                "Invite friends",
                            ),
                        )
                    }
                    onClose()
                }
            }
            item { DrawerItem(Icons.Default.HelpOutline, "Snail Features / FAQ", primaryText) { viewModel.showHelp() } }
            item {
                Spacer(Modifier.height(20.dp))
                Text(
                    "Snail Messenger • Offline-first",
                    color = secondaryText,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                )
            }
        }
    }

    if (state.showHelp) {
        AlertDialog(
            onDismissRequest = viewModel::dismissHelp,
            title = { Text("Snail Features") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Private-by-design messaging with local-first demo data.")
                    Text("• Username + password authentication")
                    Text("• One-to-one conversations")
                    Text("• Light / dark themes")
                    Text("• Telegram-inspired navigation and settings")
                    Text("• No phone number required for the current offline prototype")
                }
            },
            confirmButton = { TextButton(onClick = viewModel::dismissHelp) { Text("Got it") } },
        )
    }

    if (state.showNewGroup) NewGroupDialog(onDismiss = viewModel::dismissNewGroup)
}

@Composable
private fun DrawerHeader(
    state: DrawerUiState,
    dark: Boolean,
    onToggleAccountSwitcher: () -> Unit,
    onToggleTheme: () -> Unit,
    onSelectAccount: (Int) -> Unit,
    onAddAccount: () -> Unit,
) {
    val active = state.accounts.firstOrNull { it.isActive } ?: state.accounts.first()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (dark) DrawerDark else DrawerBlue)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            IconButton(onClick = onToggleTheme) {
                Icon(
                    if (dark) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle theme",
                    tint = Color.White,
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleAccountSwitcher)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                AvatarCircle(active.initials, 64.dp)
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color(0xFF45C76F), CircleShape)
                        .padding(3.dp)
                        .background(if (dark) DrawerDark else DrawerBlue, CircleShape),
                )
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(active.name, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text(active.username, color = DrawerLightBlue, fontSize = 13.sp)
            }
            Icon(
                if (state.accountSwitcherExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = Color.White,
            )
        }
        if (state.accountSwitcherExpanded) {
            Spacer(Modifier.height(10.dp))
            state.accounts.forEachIndexed { index, account ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectAccount(index) }
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AvatarCircle(account.initials, 42.dp)
                    Column(modifier = Modifier.weight(1f).padding(start = 10.dp)) {
                        Text(account.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                        Text(account.username, color = DrawerLightBlue, fontSize = 12.sp)
                    }
                    if (account.isActive) {
                        Text("✓", color = Color(0xFF55D66B), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onAddAccount)
                    .padding(vertical = 10.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.Add, null, tint = Color.White)
                Text("Add Account", color = Color.White, fontSize = 15.sp, modifier = Modifier.padding(start = 14.dp))
            }
        }
    }
}

@Composable
private fun DrawerItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    textColor: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Text(title, color = textColor, fontSize = 15.sp, modifier = Modifier.padding(start = 22.dp))
    }
}

@Composable
private fun AvatarCircle(initials: String, size: Dp) {
    Box(
        modifier = Modifier.size(size).background(Color(0xFF5B9BD5), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = (size.value / 2.2f).sp)
    }
}

@Composable
private fun NewGroupDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Group") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("Aarav", "Maya", "Riya", "Kabir").forEach { name ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { }.padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AvatarCircle(name.take(1), 40.dp)
                        Text(name, modifier = Modifier.padding(start = 12.dp))
                    }
                }
                Text(
                    "Select members to continue. Group persistence will be added with the local data layer.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Done") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
