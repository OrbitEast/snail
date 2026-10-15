package com.example.snailmesssenger.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ContactRowItem(
    contact: SearchContact,
    query: String = "",
    onOpen: (SearchContact) -> Unit = {},
    onInvite: (SearchContact) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(50.dp)) {
            Box(
                modifier = Modifier.size(50.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = contact.displayName.split(" ").filter(String::isNotBlank)
                        .take(2).joinToString("") { it.first().uppercase() },
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }
            if (contact.isOnline && contact.isRegistered) {
                Box(
                    modifier = Modifier.align(Alignment.BottomEnd).size(13.dp)
                        .clip(CircleShape).background(MaterialTheme.colorScheme.surface).padding(2.dp)
                        .clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f),
        ) {
            HighlightedSearchText(
                text = contact.displayName,
                query = query,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
            )
            HighlightedSearchText(
                text = contact.username,
                query = query,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                maxLines = 1,
            )
        }
        if (contact.isRegistered) {
            Text(
                text = if (contact.isOnline) "online" else "on Snail",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp),
            )
            androidx.compose.foundation.clickable
        } else {
            Button(
                onClick = { onInvite(contact) },
                modifier = Modifier.padding(start = 8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 13.dp, vertical = 7.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Text("INVITE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
