package com.example.snailmesssenger.feature.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.snailmesssenger.data.preferences.UserPreferences
import com.example.snailmesssenger.data.repository.DemoMessagingRepository
import com.example.snailmesssenger.feature.profile.ProfileScreen
import com.example.snailmesssenger.feature.settings.SettingsScreen

private data class MainTab(
    val label: String,
    val icon: ImageVector,
)

private val mainTabs = listOf(
    MainTab("Chats", Icons.Default.ChatBubble),
    MainTab("Contacts", Icons.Default.Contacts),
    MainTab("Settings", Icons.Default.Settings),
    MainTab("Profile", Icons.Default.Person),
)

@Composable
fun MainTabsScreen(
    preferences: UserPreferences,
    onOpenChat: (String) -> Unit,
    onFindPeople: () -> Unit,
    onOpenPrivacySecurity: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenDataStorage: () -> Unit,
    onOpenAppearance: () -> Unit,
    onOpenEditProfile: () -> Unit,
    onLogout: () -> Unit,
    onOpenChatSettings: () -> Unit = onOpenAppearance,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showCalls by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AnimatedContent(
            targetState = selectedTab,
            label = "main-tab-content",
            modifier = Modifier.fillMaxSize(),
        ) { tab ->
            when (tab) {
                0 -> ChatListScreen(
                    onOpenMenu = { showCalls = true },
                    onOpenChat = onOpenChat,
                    onNewChat = onFindPeople,
                )
                1 -> FindPeopleScreen(
                    onBack = { selectedTab = 0 },
                    onStartConversation = { peerId ->
                        val conversation = DemoMessagingRepository.startConversationWith(peerId)
                        onOpenChat(conversation.id)
                    },
                )
                2 -> SettingsScreen(
                    preferences = preferences,
                    onBack = { selectedTab = 0 },
                    onOpenProfile = { selectedTab = 3 },
                    onOpenPrivacySecurity = onOpenPrivacySecurity,
                    onOpenNotifications = onOpenNotifications,
                    onOpenDataStorage = onOpenDataStorage,
                    onOpenAppearance = onOpenAppearance,
                    onOpenEditProfile = onOpenEditProfile,
                    onLogout = onLogout,
                )
                else -> ProfileScreen(
                    onBack = { selectedTab = 0 },
                    onOpenSettings = { selectedTab = 2 },
                    onLogout = onLogout,
                )
            }
        }

        ModernBottomBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp),
        )
    }

    if (showCalls) {
        AlertDialog(
            onDismissRequest = { showCalls = false },
            title = { Text("Calls") },
            text = {
                Text("Call history\n\nNo calls yet. Incoming, outgoing and missed call indicators are ready for the local call layer.")
            },
            confirmButton = {
                TextButton(onClick = { showCalls = false }) { Text("Done") }
            },
        )
    }
}

@Composable
private fun ModernBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 6.dp,
        shadowElevation = 14.dp,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            mainTabs.forEachIndexed { index, tab ->
                val selected = selectedTab == index
                Surface(
                    onClick = { onTabSelected(index) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(22.dp),
                    color = if (selected) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.90f)
                    } else Color.Transparent,
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            modifier = Modifier.size(23.dp),
                            tint = if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = tab.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}
