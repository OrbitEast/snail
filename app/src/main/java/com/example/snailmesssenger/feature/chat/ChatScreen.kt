package com.example.snailmesssenger.feature.chat

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snailmesssenger.core.designsystem.SnailAvatar
import com.example.snailmesssenger.core.designsystem.Spacing
import com.example.snailmesssenger.data.model.Conversation
import com.example.snailmesssenger.data.model.Message
import com.example.snailmesssenger.data.repository.DemoMessagingRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String,
    onBack: () -> Unit,
) {
    val conversation = remember(conversationId) {
        DemoMessagingRepository.getConversation(conversationId)
    }
    if (conversation == null) {
        Scaffold(
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            topBar = {
                TopAppBar(
                    title = { Text("Chat") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                )
            },
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Conversation not found.", style = MaterialTheme.typography.bodyLarge)
            }
        }
        return
    }

    var inputText by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(conversation.messages) }
    var showProfileSheet by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showCallDialog by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    fun send() {
        val text = inputText.trim()
        if (text.isEmpty()) return
        DemoMessagingRepository.sendMessage(conversationId, text)
        messages = DemoMessagingRepository.getConversation(conversationId)?.messages ?: messages
        inputText = ""
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding(),
        topBar = {
            TopAppBar(
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                title = {
                    Row(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .clickable { showProfileSheet = true }
                            .padding(vertical = 4.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SnailAvatar(
                            displayId = conversation.peer.id,
                            displayName = conversation.peer.displayName,
                            size = 36,
                            showOnlineIndicator = true,
                            isOnline = conversation.peer.isOnline,
                        )
                        Spacer(Modifier.width(Spacing.md))
                        Column {
                            Text(
                                text = conversation.peer.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = if (conversation.peer.isOnline) "online" else "@${conversation.peer.username}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (conversation.peer.isOnline) {
                                    com.example.snailmesssenger.core.designsystem.SuccessGreen
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Normal voice call button (no video call)
                    IconButton(onClick = { showCallDialog = true }) {
                        Icon(Icons.Default.Call, contentDescription = "Voice Call")
                    }
                    // Three dots overflow menu
                    Box {
                        IconButton(onClick = { showOverflowMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("View profile") },
                                onClick = {
                                    showOverflowMenu = false
                                    showProfileSheet = true
                                },
                            )
                            DropdownMenuItem(
                                text = { Text("Mute notifications") },
                                onClick = { showOverflowMenu = false },
                            )
                            DropdownMenuItem(
                                text = { Text("Clear history") },
                                onClick = { showOverflowMenu = false },
                            )
                            DropdownMenuItem(
                                text = { Text("Block user") },
                                onClick = { showOverflowMenu = false },
                            )
                        }
                    }
                },
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .navigationBarsPadding()
                    .padding(horizontal = Spacing.sm, vertical = 6.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = Spacing.lg),
                ) {
                    BasicTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Spacing.md),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                        decorationBox = { innerTextField ->
                            Box {
                                if (inputText.isEmpty()) {
                                    Text(
                                        text = "Message…",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                innerTextField()
                            }
                        },
                    )
                }
                Spacer(Modifier.width(Spacing.sm))
                IconButton(
                    onClick = { send() },
                    enabled = inputText.trim().isNotEmpty(),
                    modifier = Modifier
                        .size(Spacing.buttonHeight)
                        .clip(CircleShape)
                        .background(
                            if (inputText.trim().isNotEmpty()) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant,
                        ),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (inputText.trim().isNotEmpty()) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = Spacing.md,
                    vertical = Spacing.sm,
                ),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                items(messages, key = { it.id }) { message ->
                    MessageBubble(message = message)
                }
            }
        }
    }

    if (showProfileSheet) {
        PeerProfileSheet(
            peer = conversation.peer,
            onDismiss = { showProfileSheet = false },
            onCallClick = {
                showProfileSheet = false
                showCallDialog = true
            },
        )
    }

    if (showCallDialog) {
        AlertDialog(
            onDismissRequest = { showCallDialog = false },
            title = { Text("Voice Call") },
            text = { Text("Calling ${conversation.peer.displayName} via secure local voice channel...") },
            confirmButton = {
                TextButton(onClick = { showCallDialog = false }) { Text("End Call") }
            },
        )
    }
}

/**
 * Message bubble — Telegram-style: rounded pill-ish bubble with
 * timestamp (and read ticks for outgoing) tucked inside the bubble
 * at the bottom-right of the text block.
 */
@Composable
private fun MessageBubble(message: Message) {
    val outgoing = message.isOutgoing

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (outgoing) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Column(
            horizontalAlignment = if (outgoing) Alignment.End else Alignment.Start,
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (outgoing) 18.dp else 6.dp,
                            bottomEnd = if (outgoing) 6.dp else 18.dp,
                        ),
                    )
                    .background(
                        if (outgoing) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
                    )
                    .padding(start = 12.dp, end = 8.dp, top = 8.dp, bottom = 6.dp),
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 20.sp),
                        color = if (outgoing) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                    )
                    Spacer(Modifier.height(2.dp))
                    // Meta: time (+ ticks for outgoing) right-aligned under the text.
                    Row(verticalAlignment = Alignment.CenterVertically) {
                    if (outgoing) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Read",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                        )
                        Spacer(Modifier.width(3.dp))
                    }
                    Text(
                        text = formatTime(message.timestampMs),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (outgoing) {
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
            }
        }
    }
    }
}

private fun formatTime(ms: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(ms))
