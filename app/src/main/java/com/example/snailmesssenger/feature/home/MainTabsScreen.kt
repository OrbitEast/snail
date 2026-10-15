package com.example.snailmesssenger.feature.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.snailmesssenger.data.preferences.UserPreferences
import com.example.snailmesssenger.feature.profile.ProfileScreen
import com.example.snailmesssenger.feature.settings.SettingsScreen

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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.ChatBubble, contentDescription = "Chats") },
                    label = { Text("Chats") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.People, contentDescription = "Contacts") },
                    label = { Text("Contacts") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
        },
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> HomeScreen(
                    onOpenChat = onOpenChat,
                    onOpenProfile = { selectedTab = 3 },
                    onOpenSettings = { selectedTab = 2 },
                    onFindPeople = { selectedTab = 1 },
                    onLogout = onLogout,
                )
                1 -> FindPeopleScreen(
                    onBack = { selectedTab = 0 },
                    onStartConversation = { peerId ->
                        val conversation = com.example.snailmesssenger.data.repository.DemoMessagingRepository
                            .startConversationWith(peerId)
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
                3 -> ProfileScreen(
                    onBack = { selectedTab = 0 },
                    onOpenSettings = { selectedTab = 2 },
                    onLogout = onLogout,
                )
            }
        }
    }
}
