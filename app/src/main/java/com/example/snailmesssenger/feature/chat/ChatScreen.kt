package com.example.snailmesssenger.feature.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.filled.Done
import com.example.snailmesssenger.core.designsystem.DarkBubbleIn
import com.example.snailmesssenger.core.designsystem.DarkBubbleOut
import com.example.snailmesssenger.core.designsystem.LightBubbleIn
import com.example.snailmesssenger.core.designsystem.LightBubbleOut
import com.example.snailmesssenger.core.designsystem.ReadCheckmarkGreen
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import android.Manifest
import android.content.Intent
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import android.provider.OpenableColumns
import com.example.snailmesssenger.core.designsystem.snailGlass
import coil.compose.AsyncImage
import com.example.snailmesssenger.core.designsystem.SnailAvatar
import com.example.snailmesssenger.core.designsystem.Spacing
import com.example.snailmesssenger.data.model.Conversation
import com.example.snailmesssenger.data.model.ConversationKind
import com.example.snailmesssenger.data.model.Message
import com.example.snailmesssenger.data.repository.DemoMessagingRepository
import com.example.snailmesssenger.feature.theme.LocalChatBubbleRadius
import com.example.snailmesssenger.feature.theme.LocalChatTextSize
import com.example.snailmesssenger.feature.theme.LocalSnailThemeState
import com.example.snailmesssenger.feature.theme.NightMode
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
    val themeState = LocalSnailThemeState.current
    val isDarkTheme = when (themeState.nightMode) {
        NightMode.SYSTEM -> isSystemInDarkTheme()
        NightMode.LIGHT -> false
        NightMode.DARK -> true
    }
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
    var showAttachmentMenu by remember { mutableStateOf(false) }
    var showPollCreator by remember { mutableStateOf(false) }
    var pollQuestion by remember { mutableStateOf("") }
    var pollOptions by remember { mutableStateOf(listOf("", "")) }
    var selectedMessage by remember { mutableStateOf<Message?>(null) }
    var forwardingMessage by remember { mutableStateOf<Message?>(null) }
    var editingMessageId by remember { mutableStateOf<String?>(null) }
    var replyTarget by remember { mutableStateOf<Message?>(null) }
    var isRecordingVoice by remember { mutableStateOf(false) }
    var recordingSeconds by remember { mutableStateOf(0) }
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var voiceRecordingFile by remember { mutableStateOf<File?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current
    val listState = rememberLazyListState()

    fun startVoiceRecording() {
        if (isRecordingVoice) return
        val directory = File(context.filesDir, "voice_messages")
        val outputFile = File(directory, "voice_${System.currentTimeMillis()}.m4a")
        var recorder: MediaRecorder? = null
        try {
            check(directory.exists() || directory.mkdirs()) { "Unable to create voice-message storage." }
            recorder = MediaRecorder()
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            recorder.setAudioEncodingBitRate(96_000)
            recorder.setAudioSamplingRate(44_100)
            recorder.setOutputFile(outputFile.absolutePath)
            recorder.prepare()
            recorder.start()
            voiceRecordingFile = outputFile
            mediaRecorder = recorder
            recordingSeconds = 0
            isRecordingVoice = true
        } catch (error: Exception) {
            runCatching { recorder?.release() }
            outputFile.delete()
            coroutineScope.launch {
                snackbarHostState.showSnackbar(error.message ?: "Could not start voice recording.")
            }
        }
    }

    fun stopAndSendVoiceRecording() {
        val activeRecorder = mediaRecorder ?: return
        val outputFile = voiceRecordingFile
        val duration = recordingSeconds
        val recordedSuccessfully = runCatching {
            activeRecorder.stop()
            true
        }.getOrDefault(false)
        runCatching { activeRecorder.reset() }
        runCatching { activeRecorder.release() }
        mediaRecorder = null
        voiceRecordingFile = null
        isRecordingVoice = false
        recordingSeconds = 0

        if (!recordedSuccessfully || outputFile == null || !outputFile.exists() || outputFile.length() < 512L) {
            outputFile?.delete()
            coroutineScope.launch { snackbarHostState.showSnackbar("Voice message was too short. Record for a little longer.") }
            return
        }

        val contentUri = runCatching {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", outputFile)
        }.getOrNull()
        if (contentUri == null) {
            coroutineScope.launch { snackbarHostState.showSnackbar("Could not prepare the voice-message file.") }
            return
        }
        DemoMessagingRepository.sendAttachment(
            conversationId = conversationId,
            uri = contentUri.toString(),
            mimeType = "audio/mp4",
            name = "Voice message ${formatRecordingDuration(duration)}.m4a",
            caption = "Voice message",
        )
        messages = DemoMessagingRepository.getConversation(conversationId)?.messages ?: messages
    }

    val microphonePermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) startVoiceRecording()
        else coroutineScope.launch { snackbarHostState.showSnackbar("Microphone permission is needed to record a voice message.") }
    }

    LaunchedEffect(isRecordingVoice) {
        while (isRecordingVoice) {
            delay(1_000)
            recordingSeconds += 1
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            val activeRecorder = mediaRecorder
            if (activeRecorder != null) {
                runCatching { activeRecorder.stop() }
                runCatching { activeRecorder.release() }
            }
            if (isRecordingVoice) voiceRecordingFile?.delete()
        }
    }

    val mediaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10),
    ) { uris ->
        uris.take(10).forEach { uri ->
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
            DemoMessagingRepository.sendAttachment(
                conversationId = conversationId,
                uri = uri.toString(),
                mimeType = mimeType,
                name = resolveDisplayName(context, uri),
            )
        }
        if (uris.isNotEmpty()) {
            messages = DemoMessagingRepository.getConversation(conversationId)?.messages ?: messages
        }
    }
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
    ) { uris ->
        uris.take(10).forEach { uri ->
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
            DemoMessagingRepository.sendAttachment(
                conversationId = conversationId,
                uri = uri.toString(),
                mimeType = mimeType,
                name = resolveDisplayName(context, uri),
            )
        }
        if (uris.isNotEmpty()) {
            messages = DemoMessagingRepository.getConversation(conversationId)?.messages ?: messages
        }
    }

    LaunchedEffect(conversationId, conversation.unreadCount) {
        if (conversation.unreadCount > 0) {
            DemoMessagingRepository.markConversationRead(conversationId)
        }
    }

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
        val editId = editingMessageId
        if (editId != null) {
            DemoMessagingRepository.editMessage(conversationId, editId, text)
            editingMessageId = null
        } else {
            val quoted = replyTarget
            DemoMessagingRepository.sendMessage(
                conversationId = conversationId,
                text = text,
                replyToMessageId = quoted?.id,
                replyToText = quoted?.text,
            )
        }
        messages = DemoMessagingRepository.getConversation(conversationId)?.messages ?: messages
        inputText = ""
        replyTarget = null
        showEmojiPicker = false
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().statusBarsPadding().imePadding(),
        containerColor = parseChatColor(themeState.wallpaperHex),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                                displayId = if (conversation.kind == ConversationKind.DIRECT) conversation.peer.id else conversation.id,
                                displayName = conversation.title ?: conversation.peer.displayName,
                                size = 42,
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = conversation.title ?: conversation.peer.displayName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.5.sp),
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = when (conversation.kind) {
                                        ConversationKind.DIRECT -> if (conversation.peer.isOnline) "online" else "last seen recently"
                                        ConversationKind.GROUP -> "${conversation.participantIds.size} members"
                                        ConversationKind.CHANNEL -> "${conversation.participantIds.size} subscribers"
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (conversation.kind == ConversationKind.DIRECT && conversation.peer.isOnline) ReadCheckmarkGreen
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold,
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
                        if (conversation.kind == ConversationKind.DIRECT) {
                            IconButton(onClick = onVoiceCall) {
                                Icon(Icons.Default.Call, contentDescription = "Start voice call")
                            }
                            IconButton(onClick = onVideoCall) {
                                Icon(Icons.Default.Videocam, contentDescription = "Start video call")
                            }
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
                AnimatedVisibility(
                    visible = searchVisible,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
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
                    .navigationBarsPadding()
                    .snailGlass(
                        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
                        baseColor = MaterialTheme.colorScheme.surface,
                        elevation = 12.dp,
                    )
                    .animateContentSize()
                    .padding(top = 7.dp, bottom = 5.dp),
            ) {
                AnimatedVisibility(
                    visible = replyTarget != null && editingMessageId == null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Replying to ${replyTarget?.let { if (it.isOutgoing) "you" else conversation.peer.displayName }.orEmpty()}",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge,
                            )
                            Text(
                                text = replyTarget?.text.orEmpty().ifBlank { "Attachment" },
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        IconButton(onClick = { replyTarget = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel reply")
                        }
                    }
                }
                AnimatedVisibility(
                    visible = editingMessageId != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Editing message",
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                        )
                        IconButton(onClick = { editingMessageId = null; inputText = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel edit")
                        }
                    }
                }
                AnimatedVisibility(
                    visible = showEmojiPicker,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
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
                        Box {
                            IconButton(
                                onClick = { showAttachmentMenu = true },
                                modifier = Modifier.size(38.dp),
                            ) {
                                Icon(
                                    Icons.Default.AttachFile,
                                    contentDescription = "Attach photo, video, or file",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                            DropdownMenu(
                                expanded = showAttachmentMenu,
                                onDismissRequest = { showAttachmentMenu = false },
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Photo or video") },
                                    leadingIcon = { Icon(Icons.Default.PlayCircleFilled, contentDescription = null) },
                                    onClick = {
                                        showAttachmentMenu = false
                                        mediaPicker.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo),
                                        )
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text("File or document") },
                                    leadingIcon = { Icon(Icons.Default.InsertDriveFile, contentDescription = null) },
                                    onClick = {
                                        showAttachmentMenu = false
                                        filePicker.launch(arrayOf("*/*"))
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text("Poll") },
                                    leadingIcon = { Icon(Icons.Default.Poll, contentDescription = null) },
                                    onClick = {
                                        showAttachmentMenu = false
                                        showPollCreator = true
                                    },
                                )
                            }
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
                        IconButton(
                            onClick = {
                                if (isRecordingVoice) {
                                    stopAndSendVoiceRecording()
                                } else if (
                                    ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                                    PackageManager.PERMISSION_GRANTED
                                ) {
                                    startVoiceRecording()
                                } else {
                                    microphonePermission.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
                            modifier = Modifier.size(38.dp),
                        ) {
                            Icon(
                                imageVector = if (isRecordingVoice) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = if (isRecordingVoice) "Stop and send voice message" else "Record voice message",
                                tint = if (isRecordingVoice) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(21.dp),
                            )
                        }
                        Box(
                            modifier = Modifier.weight(1f).padding(start = 7.dp, bottom = 11.dp, top = 8.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            if (inputText.isEmpty()) {
                                Text(
                                    text = if (isRecordingVoice) "Recording · ${formatRecordingDuration(recordingSeconds)}" else "Message",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isRecordingVoice) MaterialTheme.colorScheme.error
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            BasicTextField(
                                value = inputText,
                                onValueChange = { inputText = it },
                                enabled = !isRecordingVoice,
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
                        enabled = inputText.trim().isNotEmpty() && !isRecordingVoice,
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
                        MessageBubble(
                            message = message,
                            isDark = isDarkTheme,
                            onLongPress = { selectedMessage = message },
                            onVotePoll = { messageId, optionIndex ->
                                DemoMessagingRepository.voteOnPoll(conversationId, messageId, optionIndex)
                                messages = DemoMessagingRepository.getConversation(conversationId)?.messages ?: messages
                            },
                        )
                    }
                }
            }
            }
        }
    }

    if (showPollCreator) {
        AlertDialog(
            onDismissRequest = { showPollCreator = false },
            title = { Text("Create poll") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 440.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = pollQuestion,
                        onValueChange = { pollQuestion = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Question") },
                        singleLine = true,
                    )
                    pollOptions.forEachIndexed { index, option ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = option,
                                onValueChange = { value ->
                                    pollOptions = pollOptions.mapIndexed { optionIndex, old ->
                                        if (optionIndex == index) value else old
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                label = { Text("Option ${index + 1}") },
                                singleLine = true,
                            )
                            if (pollOptions.size > 2) {
                                IconButton(onClick = {
                                    pollOptions = pollOptions.filterIndexed { optionIndex, _ -> optionIndex != index }
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove option")
                                }
                            }
                        }
                    }
                    if (pollOptions.size < 10) {
                        TextButton(onClick = { pollOptions = pollOptions + "" }) { Text("Add option") }
                    }
                    Text(
                        "Polls are saved on this device in this prototype.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = pollQuestion.isNotBlank() &&
                        pollOptions.map { it.trim() }.filter { it.isNotEmpty() }.distinct().size >= 2,
                    onClick = {
                        DemoMessagingRepository.createPoll(conversationId, pollQuestion, pollOptions)
                        messages = DemoMessagingRepository.getConversation(conversationId)?.messages ?: messages
                        showPollCreator = false
                        pollQuestion = ""
                        pollOptions = listOf("", "")
                    },
                ) { Text("Create poll") }
            },
            dismissButton = {
                TextButton(onClick = { showPollCreator = false }) { Text("Cancel") }
            },
        )
    }

    selectedMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { selectedMessage = null },
            title = { Text("Message actions") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        listOf("👍", "❤️", "😂", "🔥", "😮").forEach { emoji ->
                            Surface(
                                onClick = {
                                    DemoMessagingRepository.reactToMessage(conversationId, message.id, emoji)
                                    messages = DemoMessagingRepository.getConversation(conversationId)?.messages ?: messages
                                    selectedMessage = null
                                },
                                shape = CircleShape,
                                color = if (message.reaction == emoji) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant,
                            ) {
                                Text(emoji, modifier = Modifier.padding(9.dp), fontSize = 20.sp)
                            }
                        }
                    }
                    TextButton(
                        onClick = {
                            replyTarget = message
                            editingMessageId = null
                            selectedMessage = null
                        },
                    ) { Text("Reply") }
                    TextButton(
                        onClick = {
                            forwardingMessage = message
                            selectedMessage = null
                        },
                    ) { Text("Forward") }
                    TextButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(message.text))
                            selectedMessage = null
                        },
                    ) { Text("Copy text") }
                    if (message.isOutgoing && message.attachmentUri == null) {
                        TextButton(
                            onClick = {
                                inputText = message.text
                                editingMessageId = message.id
                                selectedMessage = null
                            },
                        ) { Text("Edit message") }
                    }
                    TextButton(
                        onClick = {
                            DemoMessagingRepository.deleteMessage(conversationId, message.id)
                            messages = DemoMessagingRepository.getConversation(conversationId)?.messages ?: messages
                            selectedMessage = null
                        },
                    ) { Text("Delete from this device") }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedMessage = null }) { Text("Close") }
            },
        )
    }

    forwardingMessage?.let { message ->
        val availableTargets = conversations.filter { it.id != conversationId && !it.isArchived }
        AlertDialog(
            onDismissRequest = { forwardingMessage = null },
            title = { Text("Forward message") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Choose a conversation on this device.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (availableTargets.isEmpty()) {
                        Text(
                            "No other conversations available.",
                            modifier = Modifier.padding(vertical = 18.dp),
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().heightIn(max = 340.dp),
                        ) {
                            items(availableTargets, key = { it.id }) { target ->
                                TextButton(
                                    onClick = {
                                        DemoMessagingRepository.forwardMessage(target.id, message)
                                        forwardingMessage = null
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            target.peer.displayName,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                        Text(
                                            message.text.ifBlank { message.attachmentName ?: "Attachment" },
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { forwardingMessage = null }) { Text("Cancel") }
            },
        )
    }

    if (showProfileSheet) {
        PeerProfileSheet(
            peer = conversation.peer,
            kind = conversation.kind,
            memberCount = if (conversation.participantIds.isEmpty()) 1 else conversation.participantIds.size,
            isMuted = conversation.isMuted,
            onToggleMute = {
                DemoMessagingRepository.setConversationMuted(conversation.id, !conversation.isMuted)
            },
            onDismiss = { showProfileSheet = false },
            onCallClick = { showProfileSheet = false; onVoiceCall() },
        )
    }
}

@Composable
private fun MessageBubble(
    message: Message,
    isDark: Boolean,
    onLongPress: () -> Unit,
    onVotePoll: (String, Int) -> Unit,
) {
    val messageFontSize = LocalChatTextSize.current
    val bubbleRadius = LocalChatBubbleRadius.current
    val outgoing = message.isOutgoing
    val context = LocalContext.current
    var showImagePreview by remember(message.id) { mutableStateOf(false) }
    val bubbleColor = when {
        outgoing && isDark -> DarkBubbleOut
        outgoing && !isDark -> LightBubbleOut
        !outgoing && isDark -> DarkBubbleIn
        else -> LightBubbleIn
    }
    val textColor = when {
        outgoing && isDark -> Color.White
        outgoing && !isDark -> Color.Black
        !outgoing && isDark -> Color.White
        else -> Color.Black
    }
    val isPlaceholderCaption = (message.attachmentUri != null &&
        (message.text == "Photo" || message.text == "Video" || message.text == "Voice message" || message.text.startsWith("File: "))) ||
        message.pollQuestion != null

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
                    bottomStart = if (outgoing) bubbleRadius else 4.dp,
                    bottomEnd = if (outgoing) 4.dp else bubbleRadius,
                ),
                shadowElevation = 1.dp,
                modifier = Modifier.widthIn(max = 310.dp)
                    .animateContentSize()
                    .combinedClickable(onClick = {}, onLongClick = onLongPress),
            ) {
                Column(
                    modifier = Modifier.padding(
                        start = if (message.attachmentUri != null) 6.dp else 12.dp,
                        end = if (message.attachmentUri != null) 6.dp else 10.dp,
                        top = 7.dp,
                        bottom = 5.dp,
                    ),
                    horizontalAlignment = Alignment.End,
                ) {
                    if (message.isForwarded) {
                        Text(
                            "Forwarded",
                            modifier = Modifier.fillMaxWidth().padding(start = 4.dp, bottom = 3.dp),
                            color = textColor.copy(alpha = 0.70f),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        )
                    }
                    if (message.pollQuestion != null && message.pollOptions.size >= 2) {
                        val totalVotes = message.pollVotes.sum()
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(7.dp),
                            horizontalAlignment = Alignment.Start,
                        ) {
                            Text(
                                "POLL",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            )
                            Text(
                                message.pollQuestion,
                                color = textColor,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            )
                            message.pollOptions.forEachIndexed { optionIndex, option ->
                                val votes = message.pollVotes.getOrElse(optionIndex) { 0 }
                                val progress = if (totalVotes > 0) (votes.toFloat() / totalVotes).coerceIn(0f, 1f) else 0f
                                Surface(
                                    onClick = { onVotePoll(message.id, optionIndex) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (message.pollSelectedOption == optionIndex)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f),
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalArrangement = Arrangement.spacedBy(5.dp),
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                option,
                                                modifier = Modifier.weight(1f),
                                                color = MaterialTheme.colorScheme.onSurface,
                                                style = MaterialTheme.typography.bodyMedium,
                                            )
                                            Text(
                                                votes.toString(),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                style = MaterialTheme.typography.labelMedium,
                                            )
                                        }
                                        Box(
                                            modifier = Modifier.fillMaxWidth().height(4.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)),
                                        ) {
                                            if (totalVotes > 0) {
                                                Box(
                                                    modifier = Modifier.fillMaxWidth(progress).height(4.dp)
                                                        .clip(CircleShape)
                                                        .background(MaterialTheme.colorScheme.primary),
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            Text(
                                "$totalVotes vote${if (totalVotes == 1) "" else "s"} · Tap an option to vote",
                                color = textColor.copy(alpha = 0.68f),
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                    message.replyToText?.takeIf { it.isNotBlank() }?.let { quote ->
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = textColor.copy(alpha = 0.08f),
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)) {
                                Text(
                                    "Reply",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                )
                                Text(
                                    quote,
                                    color = textColor.copy(alpha = 0.82f),
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                    val mimeType = message.attachmentMimeType.orEmpty()
                    if (message.attachmentUri != null) {
                        when {
                            mimeType.startsWith("image/") -> AsyncImage(
                                model = message.attachmentUri,
                                contentDescription = message.attachmentName ?: "Attached image",
                                modifier = Modifier.width(240.dp).heightIn(min = 100.dp, max = 230.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { showImagePreview = true },
                                contentScale = ContentScale.Crop,
                            )
                            mimeType.startsWith("audio/") -> Row(
                                modifier = Modifier.fillMaxWidth()
                                    .clickable { openExternalAttachment(context, message) }
                                    .padding(horizontal = 8.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(Icons.Default.GraphicEq, contentDescription = null, modifier = Modifier.size(34.dp))
                                Spacer(Modifier.width(8.dp))
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text("Voice message", fontWeight = FontWeight.SemiBold)
                                    Text(
                                        "Tap to play · ${message.attachmentName.orEmpty()}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = textColor.copy(alpha = 0.7f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                            mimeType.startsWith("video/") -> Row(
                                modifier = Modifier.fillMaxWidth()
                                    .clickable { openExternalAttachment(context, message) }
                                    .padding(horizontal = 8.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(Icons.Default.PlayCircleFilled, contentDescription = null, modifier = Modifier.size(34.dp))
                                Spacer(Modifier.width(8.dp))
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text("Video", fontWeight = FontWeight.SemiBold)
                                    Text(
                                        message.attachmentName ?: "Video attachment",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = textColor.copy(alpha = 0.7f),
                                    )
                                }
                            }
                            else -> Row(
                                modifier = Modifier.fillMaxWidth()
                                    .clickable { openExternalAttachment(context, message) }
                                    .padding(horizontal = 8.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(Icons.Default.InsertDriveFile, contentDescription = null, modifier = Modifier.size(30.dp))
                                Spacer(Modifier.width(8.dp))
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text(
                                        message.attachmentName ?: "File",
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        mimeType.ifBlank { "Document" },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = textColor.copy(alpha = 0.7f),
                                    )
                                }
                            }
                        }
                    }
                    if (message.text.isNotBlank() && !isPlaceholderCaption) {
                        Text(
                            text = message.text,
                            modifier = Modifier.padding(horizontal = if (message.attachmentUri != null) 6.dp else 0.dp)
                                .padding(top = if (message.attachmentUri != null) 4.dp else 0.dp),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = messageFontSize,
                                lineHeight = messageFontSize * 1.32f,
                            ),
                            color = textColor,
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    ) {
                        Text(
                            text = if (message.isEdited) "${formatTime(message.timestampMs)} · edited" else formatTime(message.timestampMs),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = textColor.copy(alpha = if (outgoing) 0.72f else 0.55f),
                        )
                        if (outgoing) {
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Saved on this device",
                                modifier = Modifier.size(16.dp),
                                tint = if (isDark) ReadCheckmarkGreen else Color(0xFF40C351),
                            )
                        }
                    }
                }
            }
            message.reaction?.let { reaction ->
                Surface(
                    modifier = Modifier.padding(top = 2.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.96f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    ),
                ) {
                    Text(reaction, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), fontSize = 15.sp)
                }
            }
        }
        if (showImagePreview && message.attachmentUri != null) {
            Dialog(
                onDismissRequest = { showImagePreview = false },
                properties = DialogProperties(usePlatformDefaultWidth = false),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black),
                    contentAlignment = Alignment.Center,
                ) {
                    AsyncImage(
                        model = message.attachmentUri,
                        contentDescription = message.attachmentName ?: "Image preview",
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        contentScale = ContentScale.Fit,
                    )
                    IconButton(
                        onClick = { showImagePreview = false },
                        modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
                            .background(Color(0x66000000), CircleShape),
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close image preview", tint = Color.White)
                    }
                    Text(
                        text = message.attachmentName.orEmpty(),
                        modifier = Modifier.align(Alignment.BottomCenter).padding(20.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

private fun openExternalAttachment(context: Context, message: Message) {
    val rawUri = message.attachmentUri ?: return
    val uri = runCatching { Uri.parse(rawUri) }.getOrNull() ?: return
    val mimeType = message.attachmentMimeType?.takeIf { it.isNotBlank() } ?: "*/*"
    val intent = Intent(Intent.ACTION_VIEW)
        .setDataAndType(uri, mimeType)
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    runCatching {
        context.startActivity(Intent.createChooser(intent, "Open attachment"))
    }
}

private fun formatRecordingDuration(totalSeconds: Int): String =
    String.format(Locale.getDefault(), "%02d:%02d", totalSeconds / 60, totalSeconds % 60)

private fun formatTime(ms: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(ms))


private fun parseChatColor(hex: String): Color =
    runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrDefault(Color(0xFFE7EBF0))

private fun resolveDisplayName(context: Context, uri: Uri): String {
    val resolver = context.contentResolver
    return runCatching {
        resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0) else null
        }
    }.getOrNull()?.takeIf { it.isNotBlank() } ?: uri.lastPathSegment?.substringAfterLast('/') ?: "Attachment"
}
