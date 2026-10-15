package com.example.snailmessenger.feature.chat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.snailmesssenger.feature.media.AttachmentViewModel
import com.example.snailmesssenger.feature.media.MediaPreviewScreen
import com.example.snailmesssenger.feature.media.SnailAttachmentBottomSheet

@Composable
fun ChatDetailScreen(
    conversationId: String,
    onBack: () -> Unit,
    viewModel: ChatDetailViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val attachmentViewModel: AttachmentViewModel = viewModel()
    val attachmentState by attachmentViewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(conversationId) {
        viewModel.loadConversation(conversationId)
    }
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.lastIndex)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        containerColor = Color(0xFFE7EBF0),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ChatHeaderAvatar(state.title)
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = state.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = if (state.isOnline) "online" else "last seen recently",
                                fontSize = 12.sp,
                                color = if (state.isOnline) Color(0xFF2AA84A)
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Call, contentDescription = "Call")
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                },
            )
        },
        bottomBar = {
            MessageInputBar(
                draft = state.draft,
                onDraftChanged = viewModel::updateDraft,
                onSend = { viewModel.sendMessage() },
                onAttach = attachmentViewModel::showSheet,
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            ChatDoodleBackground()
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                items(state.messages, key = { it.id }) { message ->
                    MessageBubble(message = message)
                }
            }
        }
    }
    SnailAttachmentBottomSheet(
        state = attachmentState,
        onDismiss = attachmentViewModel::hideSheet,
        onShortcut = attachmentViewModel::setShortcut,
        onToggleMedia = attachmentViewModel::toggleSelection,
        onPreview = attachmentViewModel::openPreview,
    )
    MediaPreviewScreen(
        state = attachmentState,
        onBack = attachmentViewModel::closePreview,
        onCaptionChange = attachmentViewModel::updateCaption,
        onQualityChange = attachmentViewModel::setHighQuality,
        onTimerToggle = {
            attachmentViewModel.setSelfDestruct(if (attachmentState.selection.selfDestructSeconds == null) 5 else null)
        },
        onSend = {
            attachmentViewModel.sendSelected { media, caption, _, _ ->
                viewModel.sendMessage(caption.ifBlank { "📎 ${media.size} media item(s)" })
            }
        },
    )
    }
}

@Composable
private fun ChatHeaderAvatar(name: String) {
    val initials = remember(name) {
        name.split(" ").filter(String::isNotBlank).take(2).joinToString("") { it.first().uppercase() }
    }
    Box(
        modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(initials, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
private fun MessageInputBar(
    draft: String,
    onDraftChanged: (String) -> Unit,
    onSend: () -> Unit,
    onAttach: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 3.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            IconButton(onClick = onAttach, modifier = Modifier.size(42.dp)) {
                Icon(Icons.Default.AttachFile, contentDescription = "Attach")
            }
            IconButton(onClick = {}, modifier = Modifier.size(42.dp)) {
                Icon(Icons.Default.EmojiEmotions, contentDescription = "Emoji")
            }
            Box(
                modifier = Modifier.weight(1f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                if (draft.isEmpty()) {
                    Text(
                        text = "Write a message...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp,
                    )
                }
                BasicTextField(
                    value = draft,
                    onValueChange = onDraftChanged,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp),
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                    keyboardActions = KeyboardActions(onDone = { onSend() }),
                )
            }
            Spacer(Modifier.width(5.dp))
            Surface(
                modifier = Modifier.size(46.dp).clip(CircleShape).clickable {
                    if (draft.isNotBlank()) onSend()
                },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (draft.isBlank()) Icons.Default.Mic else Icons.AutoMirrored.Filled.Send,
                        contentDescription = if (draft.isBlank()) "Voice message" else "Send",
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatDoodleBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val effect = PathEffect.dashPathEffect(floatArrayOf(8f, 14f), 0f)
        drawCircle(
            color = Color(0xFFCFD6DE).copy(alpha = 0.25f),
            radius = 34f,
            center = center.copy(x = center.x * 0.45f, y = center.y * 0.25f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f, pathEffect = effect),
        )
        drawCircle(
            color = Color(0xFFCFD6DE).copy(alpha = 0.22f),
            radius = 22f,
            center = center.copy(x = center.x * 1.6f, y = center.y * 1.45f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f),
        )
        drawLine(
            color = Color(0xFFCFD6DE).copy(alpha = 0.22f),
            start = androidx.compose.ui.geometry.Offset(40f, size.height * 0.6f),
            end = androidx.compose.ui.geometry.Offset(140f, size.height * 0.53f),
            strokeWidth = 2f,
        )
    }
}
