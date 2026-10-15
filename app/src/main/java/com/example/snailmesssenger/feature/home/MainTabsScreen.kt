package com.example.snailmesssenger.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import com.example.snailmessenger.data.preferences.UserPreferences
import com.example.snailmessenger.feature.profile.ProfileScreen
import com.example.snailmessenger.feature.settings.SettingsScreen

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
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    fun selectTab(index: Int) {
        selectedTab = index
        scope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = selectedTab == 0,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 28.dp, bottom = 16.dp),
                ) {
                    Text(
                        text = "SNAIL Messenger",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 18.dp),
                    )
                    Text(
                        text = "YOUR SPACE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    )
                    NavigationDrawerItem(
                        label = { Text("Chats") },
                        selected = selectedTab == 0,
                        onClick = { selectTab(0) },
                        icon = { Icon(Icons.Default.ChatBubble, contentDescription = null) },
                        modifier = Modifier.padding(horizontal = 12.dp),
                    )
                    NavigationDrawerItem(
                        label = { Text("Contacts") },
                        selected = selectedTab == 1,
                        onClick = { selectTab(1) },
                        icon = { Icon(Icons.Default.People, contentDescription = null) },
                        modifier = Modifier.padding(horizontal = 12.dp),
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "ACCOUNT",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    )
                    NavigationDrawerItem(
                        label = { Text("Settings") },
                        selected = selectedTab == 2,
                        onClick = { selectTab(2) },
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        modifier = Modifier.padding(horizontal = 12.dp),
                    )
                    NavigationDrawerItem(
                        label = { Text("Profile") },
                        selected = selectedTab == 3,
                        onClick = { selectTab(3) },
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.padding(horizontal = 12.dp),
                    )
                }
            }
        },
    ) {
        when (selectedTab) {
            0 -> ChatListScreen(
                onOpenMenu = { scope.launch { drawerState.open() } },
                onOpenChat = onOpenChat,
                onNewChat = onFindPeople,
            )
            1 -> FindPeopleScreen(
                onBack = { selectTab(0) },
                onStartConversation = { peerId ->
                    val conversation = com.example.snailmessenger.data.repository.DemoMessagingRepository
                        .startConversationWith(peerId)
                    onOpenChat(conversation.id)
                },
            )
            2 -> SettingsScreen(
                preferences = preferences,
                onBack = { selectTab(0) },
                onOpenProfile = { selectTab(3) },
                onOpenPrivacySecurity = onOpenPrivacySecurity,
                onOpenNotifications = onOpenNotifications,
                onOpenDataStorage = onOpenDataStorage,
                onOpenAppearance = onOpenAppearance,
                onOpenEditProfile = onOpenEditProfile,
                onLogout = onLogout,
            )
            3 -> ProfileScreen(
                onBack = { selectTab(0) },
                onOpenSettings = { selectTab(2) },
                onLogout = onLogout,
            )
        }
    }
}
