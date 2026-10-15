package com.example.snailmesssenger.feature.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.snailmesssenger.core.designsystem.SnailAvatar
import com.example.snailmesssenger.core.designsystem.Spacing
import com.example.snailmesssenger.data.model.Conversation
import com.example.snailmesssenger.data.model.Message
import com.example.snailmesssenger.data.repository.DemoMessagingRepository
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.snailmesssenger.feature.media.AttachmentViewModel
import com.example.snailmesssenger.feature.media.SnailAttachmentBottomSheet
import com.example.snailmesssenger.feature.theme.LocalChatBubbleRadius
import com.example.snailmesssenger.feature.theme.LocalChatTextSize
import com.example.snailmesssenger.feature.theme.LocalSnailThemeState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String,
    onBack: () -> Unit,
    onVoiceCall: () -> Unit = {},
    onVideoCall: () -> Unit = {},
) {
    val attachmentViewModel: AttachmentViewModel = viewModel(key = "attachments-$conversationId")
    val attachmentState by attachmentViewModel.uiState.collectAsState()
    val themeState = LocalSnailThemeState.current
    val conversations by DemoMessagingRepository.conversationsFlow.collectAsState()
    val conversation = conversations.firstOrNull { it.id == conversationId }
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
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text("Conversation not found.", style = MaterialTheme.typography.bodyLarge)
            }
        }
        return
    }

    var inputText by remember { mutableStateOf("") }
    var messages by remember(conversationId) { mutableStateOf(conversation.messages) }
    var showProfileSheet by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showEmojiPicker by remember { mutableStateOf(false) }
    var emojiCategory by remember { mutableStateOf("Smileys") }
    var recentEmojis by remember {
        mutableStateOf(listOf("😀", "😂", "🥹", "😍", "👍", "❤️", "🔥", "🙏", "🎉", "🤝", "👀", "💯"))
    }
    var searchVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    LaunchedEffect(conversation.messages) {
        messages = conversation.messages
    }
    LaunchedEffect(conversationId) {
        DemoMessagingRepository.markConversationRead(conversationId)
    }

    val filteredMessages = remember(messages, searchQuery) {
        if (searchQuery.isBlank()) messages
        else messages.filter { it.text.contains(searchQuery.trim(), ignoreCase = true) }
    }

    LaunchedEffect(messages.size, searchQuery) {
        if (filteredMessages.isNotEmpty() && searchQuery.isBlank()) {
            listState.animateScrollToItem(filteredMessages.lastIndex)
        }
    }

    fun send() {
        val text = inputText.trim()
        if (text.isEmpty()) return
        DemoMessagingRepository.sendMessage(conversationId, text)
        messages = DemoMessagingRepository.getConversation(conversationId)?.messages ?: messages
        inputText = ""
        showEmojiPicker = false
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().statusBarsPadding().imePadding(),
        containerColor = parseChatColor(themeState.wallpaperHex),
        topBar = {
            Column {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    title = {
                        Row(
                            modifier = Modifier.clip(MaterialTheme.shapes.small)
                                .clickable { showProfileSheet = true }
                                .padding(vertical = 3.dp, horizontal = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            SnailAvatar(
                                displayId = conversation.peer.id,
                                displayName = conversation.peer.displayName,
                                size = 42,
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = conversation.peer.displayName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = "@${conversation.peer.username}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                        IconButton(onClick = onVoiceCall) {
                            Icon(Icons.Default.Call, contentDescription = "Start voice call")
                        }
                        IconButton(onClick = onVideoCall) {
                            Icon(Icons.Default.Videocam, contentDescription = "Start video call")
                        }
                        IconButton(onClick = {
                            if (searchVisible) {
                                searchVisible = false
                                searchQuery = ""
                            } else {
                                searchVisible = true
                            }
                        }) {
                            Icon(
                                imageVector = if (searchVisible) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = if (searchVisible) "Close search" else "Search messages",
                            )
                        }
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
                                    text = { Text(if (searchVisible) "Close search" else "Search messages") },
                                    onClick = {
                                        showOverflowMenu = false
                                        if (searchVisible) {
                                            searchVisible = false
                                            searchQuery = ""
                                        } else {
                                            searchVisible = true
                                        }
                                    },
                                )
                            }
                        }
                    },
                )
                if (searchVisible) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(19.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.width(10.dp))
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    "Search in conversation",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                ),
                            )
                        }
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search", modifier = Modifier.size(17.dp))
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .navigationBarsPadding()
                    .padding(top = 7.dp, bottom = 5.dp),
            ) {
                if (showEmojiPicker) {
                    val emojiCategories = listOf(
                        "Recent" to recentEmojis,
                        "Smileys" to listOf("😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "🥹", "😊", "😇", "🙂", "🙃", "😉", "😍", "🥰", "😘", "😋", "🤔", "🫡", "😎", "🥳", "😭", "😤", "😴", "🤯", "🥺", "😬", "🤗", "🫶", "🤩", "😌"),
                        "People" to listOf("👋", "🤚", "✋", "🖐️", "👌", "🤌", "✌️", "🤞", "🫰", "🤟", "🤘", "🤙", "👈", "👉", "👇", "☝️", "👍", "👎", "👏", "🙌", "🫶", "🙏", "💪", "🫂", "🤝", "💅", "👀", "🧠", "👶", "🧑", "👨", "👩"),
                        "Animals" to listOf("🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮", "🐷", "🐸", "🐵", "🙈", "🐔", "🐧", "🐦", "🦄", "🐝", "🦋", "🐢", "🐬", "🐳", "🦖", "🐙", "🦉", "🦩", "🐾"),
                        "Food" to listOf("🍏", "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐", "🍒", "🍑", "🥭", "🍍", "🥥", "🥑", "🍕", "🍔", "🍟", "🌮", "🍜", "🍣", "🍩", "🍪", "🎂", "🍫", "☕", "🧋", "🥤", "🍿"),
                        "Travel" to listOf("🚗", "🚕", "🚌", "🚎", "🏎️", "🚓", "🚑", "🚒", "🚲", "🛵", "✈️", "🚀", "🚁", "🚆", "🚇", "🚢", "⛵", "🏝️", "🏖️", "🌋", "🗽", "🗼", "🏰", "🏠", "🌍", "🌙", "⭐", "🌈", "☀️", "❄️"),
                        "Symbols" to listOf("❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "💔", "💕", "💞", "💯", "💢", "💥", "💫", "💦", "💤", "🔥", "✨", "🎉", "🎊", "🎁", "🏆", "⚽", "🎮", "🎵", "✅", "❌", "⚠️", "♻️", "💬", "🔒"),
                    )
                    val selectedEmojis = emojiCategories.firstOrNull { it.first == emojiCategory }?.second
                        ?: emojiCategories[1].second
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
                            .padding(top = 8.dp, bottom = 4.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            emojiCategories.forEach { (category, _) ->
                                Surface(
                                    onClick = { emojiCategory = category },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (emojiCategory == category) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surface,
                                ) {
                                    Text(
                                        text = category,
                                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (emojiCategory == category) MaterialTheme.colorScheme.onPrimary
                                            else MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 38.dp),
                            modifier = Modifier.fillMaxWidth().height(164.dp).padding(horizontal = 6.dp, vertical = 4.dp),
                        ) {
                            gridItems(selectedEmojis) { emoji ->
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            inputText += emoji
                                            recentEmojis = (listOf(emoji) + recentEmojis.filterNot { it == emoji }).take(24)
                                            showEmojiPicker = false
                                        },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(text = emoji, fontSize = 24.sp)
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Row(
                        modifier = Modifier.weight(1f)
                            .clip(RoundedCornerShape(25.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(start = 4.dp, end = 14.dp, top = 5.dp, bottom = 5.dp),
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        IconButton(
                            onClick = { attachmentViewModel.showSheet() },
                            modifier = Modifier.size(38.dp),
                        ) {
                            Icon(Icons.Default.AttachFile, contentDescription = "Attach media", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(
                            onClick = { showEmojiPicker = !showEmojiPicker },
                            modifier = Modifier.size(42.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEmotions,
                                contentDescription = "Emoji picker",
                                tint = if (showEmojiPicker) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(23.dp),
                            )
                        }
                        Box(
                            modifier = Modifier.weight(1f).padding(start = 7.dp, bottom = 11.dp, top = 8.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            if (inputText.isEmpty()) {
                                Text(
                                    text = "Message",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            BasicTextField(
                                value = inputText,
                                onValueChange = { inputText = it },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 22.sp,
                                ),
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    imeAction = ImeAction.Default,
                                ),
                                keyboardActions = KeyboardActions(),
                                singleLine = false,
                                maxLines = 5,
                            )
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = { send() },
                        enabled = inputText.trim().isNotEmpty(),
                        modifier = Modifier.padding(bottom = 1.dp).size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (inputText.trim().isNotEmpty()) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant,
                            ),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send message",
                            tint = if (inputText.trim().isNotEmpty()) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(21.dp),
                        )
                    }
                }
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding)
                .background(parseChatColor(themeState.wallpaperHex)),
        ) {
            themeState.wallpaperUri?.let { wallpaperUri ->
                AsyncImage(
                    model = wallpaperUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
            if (themeState.doodlePattern) {
                Text(
                    text = "◌  ✳  ⌁  ◇  ☼  ♧  ◌",
                    modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f),
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
            Column(modifier = Modifier.fillMaxSize()) {
            if (searchVisible && searchQuery.isNotBlank()) {
                Text(
                    text = "${filteredMessages.size} matching message${if (filteredMessages.size == 1) "" else "s"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                )
            }
            if (filteredMessages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (searchQuery.isNotBlank()) "No messages found" else "Say hello 👋",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "Try another word or phrase."
                            else "Your private conversation starts here.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 12.dp,
                        vertical = 12.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    items(filteredMessages, key = { it.id }) { message ->
                        MessageBubble(message = message)
                    }
                }
            }
            }
        }
    }

    if (attachmentState.isSheetVisible) {
        SnailAttachmentBottomSheet(
            state = attachmentState,
            onDismiss = attachmentViewModel::hideSheet,
            onShortcut = attachmentViewModel::setShortcut,
            onToggleMedia = attachmentViewModel::toggleSelection,
            onPreview = attachmentViewModel::openPreview,
        )
    }
    if (attachmentState.isPreviewVisible) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = attachmentViewModel::closePreview,
            title = { Text("Media preview") },
            text = { Text("Selected media is ready for the local demo. Network upload is not configured.") },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    attachmentViewModel.sendSelected { _, _, _, _ -> }
                }) { Text("Done") }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = attachmentViewModel::closePreview) { Text("Back") }
            },
        )
    }
    if (showProfileSheet) {
        PeerProfileSheet(
            peer = conversation.peer,
            onDismiss = { showProfileSheet = false },
            onCallClick = { showProfileSheet = false },
        )
    }
}

@Composable
private fun MessageBubble(message: Message) {
    val messageFontSize = LocalChatTextSize.current
    val bubbleRadius = LocalChatBubbleRadius.current
    val outgoing = message.isOutgoing
    val bubbleColor = if (outgoing) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surface
    val textColor = if (outgoing) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (outgoing) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Column(horizontalAlignment = if (outgoing) Alignment.End else Alignment.Start) {
            Surface(
                color = bubbleColor,
                contentColor = textColor,
                shape = RoundedCornerShape(
                    topStart = bubbleRadius,
                    topEnd = bubbleRadius,
                    bottomStart = if (outgoing) bubbleRadius else 5.dp,
                    bottomEnd = if (outgoing) 5.dp else bubbleRadius,
                ),
                shadowElevation = if (outgoing) 1.dp else 0.dp,
                modifier = Modifier.widthIn(max = 320.dp),
            ) {
                Column(
                    modifier = Modifier.padding(start = 12.dp, end = 9.dp, top = 8.dp, bottom = 6.dp),
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = messageFontSize, lineHeight = messageFontSize * 1.3f),
                        color = textColor,
                    )
                    Spacer(Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formatTime(message.timestampMs),
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor.copy(alpha = if (outgoing) 0.76f else 0.62f),
                        )
                        if (outgoing) {
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Sent",
                                modifier = Modifier.size(15.dp),
                                tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.88f),
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(ms))


private fun parseChatColor(hex: String): Color =
    runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrDefault(Color(0xFFE7EBF0))
