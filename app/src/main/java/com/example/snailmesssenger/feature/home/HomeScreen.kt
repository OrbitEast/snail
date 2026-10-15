package com.example.snailmesssenger.feature.home

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.snailmesssenger.core.designsystem.Spacing
import com.example.snailmesssenger.core.designsystem.SnailAvatar
import com.example.snailmesssenger.core.designsystem.SnailEmptyState
import com.example.snailmesssenger.data.model.Conversation
import com.example.snailmesssenger.data.repository.DemoAuthRepository
import com.example.snailmesssenger.data.repository.DemoMessagingRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Home / conversation list — Telegram-style layout:
 * large left-aligned app title, inline search field, full-width chat rows
 * with avatar, name, preview ("You:" prefix + read ticks), time and badge.
 */
@Composable
fun HomeScreen(
    onOpenChat: (String) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenSettings: () -> Unit,
    onFindPeople: () -> Unit,
    onLogout: () -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    val conversations = remember { DemoMessagingRepository.getConversations() }
    val filtered = remember(searchQuery, conversations) {
        if (searchQuery.isBlank()) conversations
        else conversations.filter {
            it.peer.displayName.contains(searchQuery, ignoreCase = true) ||
                it.peer.username.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = Spacing.xl, end = Spacing.sm, top = Spacing.md, bottom = Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Snail",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = onFindPeople) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "New message",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Spacer(Modifier.width(Spacing.xs))
                    IconButton(onClick = onOpenProfile) {
                        SnailAvatar(
                            displayId = DemoAuthRepository.currentUser?.id ?: "me",
                            displayName = DemoAuthRepository.currentUser?.displayName ?: "You",
                            size = 32,
                        )
                    }
                }
                // Search field — pill style like Telegram
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg, vertical = Spacing.sm)
                        .clip(RoundedCornerShape(22.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { /* field is always active */ },
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Spacing.lg),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.width(Spacing.md))
                        Box(modifier = Modifier.weight(1f)) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                ),
                                singleLine = true,
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onFindPeople,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
            ) {
                Icon(Icons.Default.Edit, contentDescription = "New message")
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (filtered.isEmpty()) {
                if (searchQuery.isNotBlank()) {
                    SnailEmptyState(
                        icon = Icons.Default.Search,
                        heading = "No results found",
                        supportingText = "Try a different name or username.",
                        actionText = "Clear search",
                        onAction = { searchQuery = "" },
                    )
                } else {
                    SnailEmptyState(
                        icon = Icons.Default.ChatBubbleOutline,
                        heading = "Your conversations start here.",
                        supportingText = "Find someone by username to start a private chat.",
                        actionText = "Find people",
                        onAction = onFindPeople,
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filtered, key = { it.id }) { conversation ->
                        ConversationRow(conversation = conversation, onClick = { onOpenChat(conversation.id) })
                    }
                }
            }
        }
    }
}

/**
 * Single conversation row — Telegram-style:
 * name left / time right on line 1, preview left / badge right on line 2.
 * Outgoing last messages are prefixed with "You: " and show read ticks.
 */
@Composable
private fun ConversationRow(
    conversation: Conversation,
    onClick: () -> Unit,
) {
    val lastMessage = conversation.messages.lastOrNull()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SnailAvatar(
            displayId = conversation.peer.id,
            displayName = conversation.peer.displayName,
            size = 54,
            showOnlineIndicator = true,
            isOnline = conversation.peer.isOnline,
        )
        Spacer(Modifier.width(Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = conversation.peer.displayName,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(Modifier.width(Spacing.sm))
                Text(
                    text = lastMessage?.let { formatRowTimestamp(it.timestampMs) } ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (conversation.unreadCount > 0) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
            Spacer(Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (lastMessage?.isOutgoing == true) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.width(2.dp))
                }
                Text(
                    text = when {
                        lastMessage == null -> ""
                        lastMessage.isOutgoing -> "You: ${lastMessage.text}"
                        else -> lastMessage.text
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (conversation.unreadCount > 0) {
                    Spacer(Modifier.width(Spacing.sm))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .defaultMinSize(minWidth = 22.dp)
                            .height(22.dp)
                            .padding(horizontal = 6.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = conversation.unreadCount.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            }
        }
    }
}

/** Telegram-style row timestamp: time today, weekday this week, else date. */
private fun formatRowTimestamp(ms: Long): String {
    val now = Calendar.getInstance()
    val then = Calendar.getInstance().apply { timeInMillis = ms }
    val sameDay = now.get(Calendar.YEAR) == then.get(Calendar.YEAR) &&
        now.get(Calendar.DAY_OF_YEAR) == then.get(Calendar.DAY_OF_YEAR)
    return when {
        sameDay -> SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(ms))
        now.timeInMillis - ms < 7L * 86_400_000L -> SimpleDateFormat("EEE", Locale.getDefault()).format(Date(ms))
        now.get(Calendar.YEAR) == then.get(Calendar.YEAR) -> SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(ms))
        else -> SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date(ms))
    }
}
