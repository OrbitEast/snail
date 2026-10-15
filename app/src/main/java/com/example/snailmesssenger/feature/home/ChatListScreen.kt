package com.example.snailmesssenger.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ChatListScreen(
    onOpenMenu: () -> Unit,
    onOpenChat: (String) -> Unit,
    onNewChat: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OptimizedChatListScreen(
        onOpenMenu = onOpenMenu,
        onOpenChat = onOpenChat,
        onNewChat = onNewChat,
        modifier = modifier,
    )
}
