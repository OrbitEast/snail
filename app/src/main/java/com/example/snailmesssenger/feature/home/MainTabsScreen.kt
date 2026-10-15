package com.example.snailmesssenger.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.snailmesssenger.data.preferences.UserPreferences
import com.example.snailmesssenger.feature.profile.ProfileScreen
import com.example.snailmesssenger.feature.settings.SettingsScreen
import kotlinx.coroutines.launch

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
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    fun closeDrawer() = scope.launch { drawerState.close() }
    fun selectTab(index: Int) {
        selectedTab = index
        closeDrawer()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = selectedTab == 0,
        drawerContent = {
            SnailDrawerSheet(
                preferences = preferences,
                onClose = { closeDrawer() },
                onOpenChats = { selectTab(0) },
                onOpenProfile = { selectTab(3) },
                onOpenContacts = { selectTab(1) },
                onOpenCalls = { showCalls = true; closeDrawer() },
                onOpenSavedMessages = { onOpenChat("saved"); closeDrawer() },
                onOpenSettings = { onOpenChatSettings(); closeDrawer() },
                onOpenHelp = { },
            )
        },
    ) {
        Column(Modifier.fillMaxSize()) {
            when (selectedTab) {
                0 -> ChatListScreen(
                    onOpenMenu = { scope.launch { drawerState.open() } },
                    onOpenChat = onOpenChat,
                    onNewChat = onFindPeople,
                )
                1 -> FindPeopleScreen(
                    onBack = { selectTab(0) },
                    onStartConversation = { peerId ->
                        val conversation = com.example.snailmessenger.data.repository.DemoMessagingRepository.startConversationWith(peerId)
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

    if (showCalls) {
        AlertDialog(
            onDismissRequest = { showCalls = false },
            title = { Text("Calls") },
            text = {
                Text("Call history\n\nNo calls yet. Incoming, outgoing and missed call indicators are ready for the local call layer.")
            },
            confirmButton = { TextButton(onClick = { showCalls = false }) { Text("Done") } },
        )
    }
}
