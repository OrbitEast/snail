package com.example.snailmesssenger.feature.media

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

private val AttachmentBlue = Color(0xFF2481CC)

@Composable
fun SnailAttachmentBottomSheet(state: AttachmentUiState, onDismiss: () -> Unit, onShortcut: (AttachmentShortcut) -> Unit, onToggleMedia: (String) -> Unit, onPreview: (String?) -> Unit) {
    if (!state.isSheetVisible) return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = MaterialTheme.colorScheme.surface, dragHandle = {
        Box(Modifier.padding(top = 9.dp, bottom = 5.dp).size(width = 34.dp, height = 4.dp).background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f), CircleShape))
    }) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Share", style = MaterialTheme.typography.titleMedium)
                if (state.selection.selectedIds.isNotEmpty()) TextButton(onClick = { onPreview(null) }) { Text("PREVIEW ${state.selection.selectedIds.size}", color = AttachmentBlue) }
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ShortcutItem("Gallery", AttachmentShortcut.GALLERY, Color(0xFF7B61D1), Icons.Default.Collections, state.activeShortcut, onShortcut)
                ShortcutItem("File", AttachmentShortcut.FILE, Color(0xFFF09A36), Icons.Default.Description, state.activeShortcut, onShortcut)
                ShortcutItem("Location", AttachmentShortcut.LOCATION, Color(0xFF35A66F), Icons.Default.LocationOn, state.activeShortcut, onShortcut)
                ShortcutItem("Contact", AttachmentShortcut.CONTACT, Color(0xFF26A6C8), Icons.Default.Contacts, state.activeShortcut, onShortcut)
                ShortcutItem("Audio/Poll", AttachmentShortcut.AUDIO_POLL, Color(0xFFE2B33D), Icons.Default.AudioFile, state.activeShortcut, onShortcut)
            }
            if (state.activeShortcut == AttachmentShortcut.GALLERY) {
                Spacer(Modifier.height(15.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Recent photos", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { onPreview(state.media.firstOrNull()?.id) }) { Text("Open gallery") }
                }
                LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.fillMaxWidth().height(330.dp), verticalArrangement = Arrangement.spacedBy(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(state.media, key = { it.id }) { media ->
                        val index = state.selection.selectedIds.indexOf(media.id)
                        GalleryThumbnailItem(media, if (index >= 0) index + 1 else null, onClick = { if (index >= 0) onToggleMedia(media.id) else onToggleMedia(media.id) }, modifier = Modifier.fillMaxWidth().height(112.dp))
                    }
                }
            } else {
                val icon = when (state.activeShortcut) {
                    AttachmentShortcut.FILE -> Icons.Default.Description
                    AttachmentShortcut.LOCATION -> Icons.Default.LocationOn
                    AttachmentShortcut.CONTACT -> Icons.Default.Contacts
                    AttachmentShortcut.AUDIO_POLL -> Icons.Default.Poll
                    AttachmentShortcut.GALLERY -> Icons.Default.Collections
                }
                val message = when (state.activeShortcut) {
                    AttachmentShortcut.FILE -> "Local file picker placeholder"
                    AttachmentShortcut.LOCATION -> "Location sharing preview"
                    AttachmentShortcut.CONTACT -> "Contact sharing preview"
                    AttachmentShortcut.AUDIO_POLL -> "Audio and poll options"
                    AttachmentShortcut.GALLERY -> "Recent photos"
                }
                Surface(Modifier.fillMaxWidth().padding(vertical = 24.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), shape = MaterialTheme.shapes.large) {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(icon, contentDescription = null, tint = AttachmentBlue, modifier = Modifier.size(32.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(message, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ShortcutItem(label: String, shortcut: AttachmentShortcut, tint: Color, icon: ImageVector, active: AttachmentShortcut, onClick: (AttachmentShortcut) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(64.dp)) {
        Surface(onClick = { onClick(shortcut) }, modifier = Modifier.size(48.dp), shape = CircleShape, color = tint.copy(alpha = if (active == shortcut) 1f else 0.85f), contentColor = Color.White) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, contentDescription = label, modifier = Modifier.size(23.dp)) }
        }
        Spacer(Modifier.height(5.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
    }
}
