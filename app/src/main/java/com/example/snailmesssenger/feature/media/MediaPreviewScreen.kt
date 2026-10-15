package com.example.snailmesssenger.feature.media

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private val PreviewBlue = Color(0xFF2481CC)

@Composable
fun MediaPreviewScreen(state: AttachmentUiState, onBack: () -> Unit, onCaptionChange: (String) -> Unit, onQualityChange: (Boolean) -> Unit, onTimerToggle: () -> Unit, onSend: () -> Unit) {
    if (!state.isPreviewVisible) return
    val selected = state.media.filter { it.id in state.selection.selectedIds }
    val current = selected.firstOrNull()
    val sendScale by animateFloatAsState(if (state.isSending) 0.92f else 1f, label = "media-send-scale")
    Box(Modifier.fillMaxSize().background(Color(0xFF080A0D)).imePadding()) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back to gallery", tint = Color.White) }
                Column(Modifier.weight(1f)) {
                    Text(current?.title ?: "Media preview", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    Text("${selected.size} selected", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = onTimerToggle) { Icon(Icons.Default.Timer, contentDescription = "Self-destruct timer", tint = Color.White) }
            }
            Box(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
                AnimatedContent(targetState = current?.id, label = "selected-media-preview") { id ->
                    val item = selected.firstOrNull { it.id == id }
                    if (item == null) Text("Select media to preview", color = Color.LightGray)
                    else if (item.uri != null) AsyncImage(model = item.uri, contentDescription = item.title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                    else Box(Modifier.fillMaxSize().background(Color(item.tintArgb)), contentAlignment = Alignment.Center) { Text(item.title, color = Color.White, style = MaterialTheme.typography.headlineSmall) }
                }
            }
            Column(Modifier.fillMaxWidth().background(Color(0xE6192028), RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)).padding(horizontal = 14.dp, vertical = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onCaptionChange(state.selection.caption + "😊") }) { Icon(Icons.Default.EmojiEmotions, contentDescription = "Insert emoji", tint = Color.LightGray) }
                    Box(Modifier.weight(1f).background(Color(0xFF303841), RoundedCornerShape(22.dp)).padding(horizontal = 14.dp, vertical = 11.dp)) {
                        if (state.selection.caption.isEmpty()) Text("Add a caption...", color = Color(0xFFB8C0C8))
                        BasicTextField(value = state.selection.caption, onValueChange = onCaptionChange, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 15.sp), maxLines = 4)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.HighQuality, contentDescription = null, tint = if (state.selection.highQuality) PreviewBlue else Color.LightGray)
                    Spacer(Modifier.width(5.dp))
                    Text("HD", color = Color.White, style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.width(4.dp))
                    Switch(checked = state.selection.highQuality, onCheckedChange = onQualityChange)
                    Spacer(Modifier.weight(1f))
                    Text("Send ${selected.size}", color = Color.LightGray, style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.width(12.dp))
                    Surface(onClick = onSend, modifier = Modifier.size(54.dp).scale(sendScale), shape = CircleShape, color = PreviewBlue, contentColor = Color.White, enabled = selected.isNotEmpty() && !state.isSending) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Send, contentDescription = "Send selected media", modifier = Modifier.size(22.dp))
                            if (selected.size > 1) Surface(Modifier.align(Alignment.TopEnd).size(19.dp), shape = CircleShape, color = Color.White, contentColor = PreviewBlue) {
                                Box(contentAlignment = Alignment.Center) { Text(selected.size.toString(), fontSize = 10.sp, textAlign = TextAlign.Center) }
                            }
                        }
                    }
                }
            }
        }
    }
}
