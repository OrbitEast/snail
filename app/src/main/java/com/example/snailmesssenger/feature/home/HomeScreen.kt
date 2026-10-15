package com.example.snailmesssenger.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
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
import com.example.snailmesssenger.core.designsystem.SnailLogo
import com.example.snailmesssenger.core.designsystem.SnailEmptyState
import com.example.snailmesssenger.data.model.Conversation
import com.example.snailmesssenger.data.repository.DemoAuthRepository
import com.example.snailmesssenger.data.repository.DemoMessagingRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * SNAIL inbox: a compact, familiar messenger layout with original SNAIL branding.
 * Uses the app's current Material theme so light/dark appearance stays consistent.
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
    var selectedFilter by remember { mutableStateOf("All") }
    val conversations = remember { DemoMessagingRepository.getConversations() }
    val onlinePeople = remember(conversations) { conversations.filter { it.peer.isOnline } }
    val unreadTotal = conversations.sumOf { it.unreadCount }
    val filtered = remember(searchQuery, selectedFilter, conversations) {
        conversations.filter { conversation ->
            val query = searchQuery.trim()
            val matchesSearch = query.isBlank() ||
                conversation.peer.displayName.contains(query, ignoreCase = true) ||
                conversation.peer.username.contains(query, ignoreCase = true) ||
                conversation.messages.any { it.text.contains(query, ignoreCase = true) }
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
                    .padding(bottom = 10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .height(58.dp)
                        .padding(start = 22.dp, end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SnailLogo(
                        size = 36.dp,
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        shellColor = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SNAIL",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = if (unreadTotal > 0) "$unreadTotal unread messages" else "Private conversations",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                    IconButton(onClick = onOpenProfile) {
                        SnailAvatar(
                            displayId = DemoAuthRepository.currentUser?.id ?: "me",
                            displayName = DemoAuthRepository.currentUser?.displayName ?: "You",
                            size = 36,
                        )
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 17.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Your space. Your people.",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = if (unreadTotal > 0) "You have $unreadTotal unread ${if (unreadTotal == 1) "message" else "messages"}" else "Private chats, at your pace.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f),
                            )
                        }
                        Box(
                            modifier = Modifier.size(44.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    }
                }

                SearchField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf("All", "Unread").forEach { filter ->
                        val selected = selectedFilter == filter
                        Surface(
                            onClick = { selectedFilter = filter },
                            shape = RoundedCornerShape(18.dp),
                            color = if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant,
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 15.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Text(
                                    text = if (filter == "Unread") "Unread" else "All chats",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                                    color = if (selected) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                if (filter == "Unread" && unreadTotal > 0) {
                                    Text(
                                        text = unreadTotal.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) MaterialTheme.colorScheme.onPrimary
                                            else MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
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
        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                if (searchQuery.isNotBlank() || selectedFilter == "Unread") {
                    SnailEmptyState(
                        icon = Icons.Default.Search,
                        heading = if (selectedFilter == "Unread" && searchQuery.isBlank())
                            "You're all caught up" else "No chats found",
                        supportingText = if (selectedFilter == "Unread" && searchQuery.isBlank())
                            "Unread conversations will appear here."
                        else "Try a different name, username or message.",
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
                contentPadding = PaddingValues(bottom = 20.dp, top = 2.dp),
            ) {
                if (searchQuery.isBlank() && selectedFilter == "All" && onlinePeople.isNotEmpty()) {
                    item(key = "active_header") {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .padding(start = 22.dp, end = 22.dp, top = 8.dp, bottom = 13.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp),
                        ) {
                            Text(
                                text = "ACTIVE NOW",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Box(
                                modifier = Modifier.size(6.dp).clip(CircleShape)
                                    .background(com.example.snailmesssenger.core.designsystem.SuccessGreen),
                            )
                        }
                    }
                    item(key = "active_people") {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(start = 18.dp, end = 18.dp, bottom = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(17.dp),
                        ) {
                            onlinePeople.forEach { conversation ->
                                Column(
                                    modifier = Modifier.width(58.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onOpenChat(conversation.id) }
                                        .padding(vertical = 2.dp),
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

                item(key = "list_header") {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(start = 22.dp, end = 22.dp, top = 2.dp, bottom = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = if (selectedFilter == "Unread") "Unread messages" else "Messages",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = filtered.size.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                items(filtered, key = { it.id }) { conversation ->
                    ConversationRow(
                        conversation = conversation,
                        onClick = { onOpenChat(conversation.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(start = 14.dp, end = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(21.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.width(10.dp))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (value.isEmpty()) {
                Text(
                    text = "Search",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                singleLine = true,
            )
        }
        if (value.isNotEmpty()) {
            IconButton(onClick = { onValueChange("") }, modifier = Modifier.size(38.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear search",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ConversationRow(conversation: Conversation, onClick: () -> Unit) {
    val lastMessage = conversation.messages.lastOrNull()
    Column {
        Row(
            modifier = Modifier.fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SnailAvatar(
                displayId = conversation.peer.id,
                displayName = conversation.peer.displayName,
                size = 56,
                showOnlineIndicator = true,
                isOnline = conversation.peer.isOnline,
            )
            Spacer(Modifier.width(13.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = conversation.peer.displayName,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
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
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Sent",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(Modifier.width(3.dp))
                    }
                    Text(
                        text = when {
                            lastMessage == null -> "Start a conversation"
                            lastMessage.isOutgoing -> "You: ${lastMessage.text}"
                            else -> lastMessage.text
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (conversation.unreadCount > 0)
                            MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (conversation.unreadCount > 0) FontWeight.Medium else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    if (conversation.unreadCount > 0) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier.clip(CircleShape)
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
        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(start = 87.dp, end = 18.dp)
                .height(0.6.dp)
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)),
        )
    }
}

private fun formatRowTimestamp(ms: Long): String {
    val now = Calendar.getInstance()
    val then = Calendar.getInstance().apply { timeInMillis = ms }
    val sameDay = now.get(Calendar.YEAR) == then.get(Calendar.YEAR) &&
        now.get(Calendar.DAY_OF_YEAR) == then.get(Calendar.DAY_OF_YEAR)
    return when {
        sameDay -> SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(ms))
        now.timeInMillis - ms < 7L * 86_400_000L ->
            SimpleDateFormat("EEE", Locale.getDefault()).format(Date(ms))
        now.get(Calendar.YEAR) == then.get(Calendar.YEAR) ->
            SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(ms))
        else -> SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date(ms))
    }
}
