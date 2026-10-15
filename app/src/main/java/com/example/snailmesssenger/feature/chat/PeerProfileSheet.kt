package com.example.snailmesssenger.feature.chat

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.snailmesssenger.core.designsystem.SnailAvatar
import com.example.snailmesssenger.core.designsystem.SnailSectionRow
import com.example.snailmesssenger.core.designsystem.Spacing
import com.example.snailmesssenger.data.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeerProfileSheet(
    peer: User,
    onDismiss: () -> Unit,
    onCallClick: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var notificationsMuted by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.xxxl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(Spacing.sm))
            // Handle bar
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)),
            )
            Spacer(Modifier.height(Spacing.xl))

            // Avatar
            SnailAvatar(
                displayId = peer.id,
                displayName = peer.displayName,
                size = 80,
                showOnlineIndicator = true,
                isOnline = peer.isOnline,
            )
            Spacer(Modifier.height(Spacing.md))

            // Name & Username
            Text(
                text = peer.displayName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = if (peer.isOnline) "online" else "last seen recently",
                style = MaterialTheme.typography.labelMedium,
                color = if (peer.isOnline) {
                    com.example.snailmesssenger.core.designsystem.SuccessGreen
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )

            Spacer(Modifier.height(Spacing.xl))

            // Action Buttons Row (Telegram style: Message, Call, Mute, More)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProfileActionButton(
                    icon = Icons.Default.Call,
                    label = "Call",
                    onClick = onCallClick,
                )
                Spacer(Modifier.width(Spacing.xl))
                ProfileActionButton(
                    icon = Icons.Default.Notifications,
                    label = if (notificationsMuted) "Unmute" else "Mute",
                    onClick = { notificationsMuted = !notificationsMuted },
                )
                Spacer(Modifier.width(Spacing.xl))
                ProfileActionButton(
                    icon = Icons.Default.PersonAdd,
                    label = "Add",
                    onClick = {},
                )
            }

            Spacer(Modifier.height(Spacing.xxl))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // Details Sections (Username, Bio, Shared Media)
            SnailSectionRow(
                title = "@${peer.username}",
                subtitle = "Username",
                icon = null,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            SnailSectionRow(
                title = peer.bio.ifBlank { "Taking it slow on SNAIL 🐌" },
                subtitle = "Bio",
                icon = null,
            )

            Spacer(Modifier.height(Spacing.md))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // Shared Media & Files Sections
            SnailSectionHeading(text = "Shared Media")
            SnailSectionRow(
                title = "Media, files and links",
                subtitle = "12 photos • 3 files • 5 links",
                icon = Icons.Default.PhotoLibrary,
                onClick = {},
            )
            SnailSectionRow(
                title = "Voice messages",
                subtitle = "8 voice notes",
                icon = Icons.Default.Mic,
                onClick = {},
            )

            Spacer(Modifier.height(Spacing.md))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            SnailSectionRow(
                title = "Block user",
                icon = Icons.Default.Block,
                onClick = {},
                destructive = true,
            )
        }
    }
}

@Composable
private fun ProfileActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun SnailSectionHeading(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.xl, vertical = Spacing.sm),
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
