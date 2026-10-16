package com.example.snailmesssenger.feature.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.snailmesssenger.core.designsystem.SnailMotion
import com.example.snailmesssenger.core.designsystem.snailSelectionProgress
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
    val conversations by DemoMessagingRepository.conversationsFlow.collectAsState()
    val unreadCount = conversations.sumOf { it.unreadCount }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
    ) {
        AnimatedContent(
            targetState = selectedTab,
            label = "main-tab-content",
            modifier = Modifier.fillMaxSize(),
            transitionSpec = {
                val forward = targetState > initialState
                val enter = slideInHorizontally(
                    initialOffsetX = { if (forward) it / 9 else -it / 9 },
                    animationSpec = tween(300, easing = FastOutSlowInEasing),
                ) + fadeIn(tween(190)) + scaleIn(
                    initialScale = 0.985f,
                    animationSpec = tween(300, easing = FastOutSlowInEasing),
                )
                val exit = slideOutHorizontally(
                    targetOffsetX = { if (forward) -it / 12 else it / 12 },
                    animationSpec = tween(240, easing = FastOutSlowInEasing),
                ) + fadeOut(tween(150))
                enter togetherWith exit
            },
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
            unreadCount = unreadCount,
            onTabSelected = { selectedTab = it },
            modifier = Modifier.align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp),
        )
    }

    if (showCalls) {
        AlertDialog(
            onDismissRequest = { showCalls = false },
            title = { Text("Calls") },
            text = { Text("Call history\n\nNo calls yet. Incoming, outgoing and missed call indicators are ready for the local call layer.") },
            confirmButton = { TextButton(onClick = { showCalls = false }) { Text("Done") } },
        )
    }
}

@Composable
private fun ModernBottomBar(
    selectedTab: Int,
    unreadCount: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 6.dp,
        shadowElevation = 16.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.42f)),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            mainTabs.forEachIndexed { index, tab ->
                val selected = selectedTab == index
                val progress = snailSelectionProgress(selected)
                Surface(
                    onClick = { onTabSelected(index) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = progress * 0.90f),
                    tonalElevation = (progress * 2f).dp,
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Box(
                            modifier = Modifier.size((24f + progress * 2f).dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                modifier = Modifier.size((22f + progress * 2f).dp),
                                tint = if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            if (index == 0 && unreadCount > 0) {
                                Surface(
                                    modifier = Modifier.align(Alignment.TopEnd).offset(x = 5.dp, y = (-3).dp),
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError,
                                ) {
                                    Text(
                                        text = unreadCount.coerceAtMost(99).let { if (it == 99) "99+" else it.toString() },
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    )
                                }
                            }
                        }
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
