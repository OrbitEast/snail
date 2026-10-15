package com.example.snailmesssenger.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.material3.Surface
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
import com.example.snailmesssenger.core.designsystem.SnailAvatar
import com.example.snailmesssenger.core.designsystem.SnailEmptyState
import com.example.snailmesssenger.data.model.Conversation
import com.example.snailmesssenger.data.repository.DemoAuthRepository
import com.example.snailmesssenger.data.repository.DemoMessagingRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** SNAIL inbox with a polished, Telegram-inspired private-chat layout. */
@Composable
fun HomeScreen(
    onOpenChat: (String) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenSettings: () -> Unit,
    onFindPeople: () -> Unit,
    onLogout: () -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val conversations = remember { DemoMessagingRepository.getConversations() }
    val onlinePeople = remember(conversations) { conversations.filter { it.peer.isOnline } }
    val filtered = remember(searchQuery, selectedFilter, conversations) {
        conversations.filter { conversation ->
            val matchesSearch = searchQuery.isBlank() ||
                conversation.peer.displayName.contains(searchQuery, ignoreCase = true) ||
                conversation.peer.username.contains(searchQuery, ignoreCase = true)
            val matchesFilter = selectedFilter != "Unread" || conversation.unreadCount > 0
            matchesSearch && matchesFilter
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(bottom = 8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 22.dp, end = 12.dp, top = 10.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SNAIL",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "Your private space",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(onClick = onFindPeople) {
                        Icon(Icons.Default.Edit, contentDescription = "New message",
                            tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(23.dp))
                    }
                    IconButton(onClick = onOpenProfile) {
                        SnailAvatar(
                            displayId = DemoAuthRepository.currentUser?.id ?: "me",
                            displayName = DemoAuthRepository.currentUser?.displayName ?: "You",
                            size = 38,
                        )
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 15.dp, vertical = 13.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, contentDescription = null,
                            modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(10.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (searchQuery.isEmpty()) {
                                Text("Search people or messages",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface),
                                singleLine = true,
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.padding(start = 18.dp, top = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf("All", "Unread").forEach { filter ->
                        val selected = selectedFilter == filter
                        Surface(
                            onClick = { selectedFilter = filter },
                            shape = RoundedCornerShape(50),
                            color = if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant,
                        ) {
                            Text(
                                text = if (filter == "Unread")
                                    "Unread ${conversations.count { it.unreadCount > 0 }}" else "All chats",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
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
                shape = RoundedCornerShape(18.dp),
            ) {
                Icon(Icons.Default.Edit, contentDescription = "New message")
            }
        },
    ) { padding ->
        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                if (searchQuery.isNotBlank() || selectedFilter == "Unread") {
                    SnailEmptyState(
                        icon = Icons.Default.Search,
                        heading = if (selectedFilter == "Unread" && searchQuery.isBlank())
                            "All caught up" else "No results found",
                        supportingText = if (selectedFilter == "Unread" && searchQuery.isBlank())
                            "New unread messages will appear here."
                            else "Try another name or switch to all chats.",
                        actionText = "Show all chats",
                        onAction = { searchQuery = ""; selectedFilter = "All" },
                    )
                } else {
                    SnailEmptyState(
                        icon = Icons.Default.ChatBubbleOutline,
                        heading = "Your conversations start here",
                        supportingText = "Find someone by username to start a private chat.",
                        actionText = "Find people",
                        onAction = onFindPeople,
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 92.dp, top = 4.dp),
            ) {
                if (searchQuery.isBlank() && selectedFilter == "All" && onlinePeople.isNotEmpty()) {
                    item(key = "online_header") {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .padding(start = 22.dp, end = 22.dp, top = 8.dp, bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("ACTIVE NOW", style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.width(8.dp))
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape)
                                .background(com.example.snailmesssenger.core.designsystem.SuccessGreen))
                        }
                    }
                    item(key = "online_people") {
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                                .padding(start = 18.dp, end = 18.dp, bottom = 18.dp),
                            horizontalArrangement = Arrangement.spacedBy(18.dp),
                        ) {
                            onlinePeople.forEach { conversation ->
                                Column(
                                    modifier = Modifier.width(62.dp).clickable { onOpenChat(conversation.id) },
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    SnailAvatar(
                                        displayId = conversation.peer.id,
                                        displayName = conversation.peer.displayName,
                                        size = 52,
                                        showOnlineIndicator = true,
                                        isOnline = true,
                                    )
                                    Spacer(Modifier.height(7.dp))
                                    Text(
                                        text = conversation.peer.displayName.substringBefore(" "),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }
                    }
                }
                item(key = "messages_header") {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = if (selectedFilter == "Unread") "Unread messages" else "Messages",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f),
                        )
                        Text("${filtered.size}", style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                items(filtered, key = { it.id }) { conversation ->
                    ConversationRow(conversation = conversation, onClick = { onOpenChat(conversation.id) })
                }
            }
        }
    }
}

@Composable
private fun ConversationRow(conversation: Conversation, onClick: () -> Unit) {
    val lastMessage = conversation.messages.lastOrNull()
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SnailAvatar(
            displayId = conversation.peer.id,
            displayName = conversation.peer.displayName,
            size = 56,
            showOnlineIndicator = true,
            isOnline = conversation.peer.isOnline,
        )
        Spacer(Modifier.width(14.dp))
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
                Spacer(Modifier.width(8.dp))
                Text(
                    text = lastMessage?.let { formatRowTimestamp(it.timestampMs) } ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (conversation.unreadCount > 0) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(5.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (lastMessage?.isOutgoing == true) {
                    Icon(Icons.Default.DoneAll, contentDescription = "Sent",
                        modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(3.dp))
                }
                Text(
                    text = when {
                        lastMessage == null -> "Start a conversation"
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
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.primary)
                            .defaultMinSize(minWidth = 22.dp).height(22.dp).padding(horizontal = 6.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(conversation.unreadCount.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}

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
