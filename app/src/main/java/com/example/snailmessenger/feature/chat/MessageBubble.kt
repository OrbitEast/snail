package com.example.snailmessenger.feature.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MessageBubble(message: Message, modifier: Modifier = Modifier) {
    val isDark = MaterialTheme.colorScheme.background.luminanceValue() < 0.5f
    val bubbleColor = when {
        message.isOutgoing && isDark -> Color(0xFF1D69A6)
        message.isOutgoing -> Color(0xFFE8F5D8)
        isDark -> Color(0xFF18212B)
        else -> Color.White
    }
    val textColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF222222)
    val shape = if (message.isOutgoing) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 2.dp),
        horizontalArrangement = if (message.isOutgoing) Arrangement.End else Arrangement.Start,
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(0.82f).clip(shape).background(bubbleColor)
                .padding(start = 11.dp, top = 7.dp, end = 8.dp, bottom = 6.dp),
        ) {
            Column {
                Text(text = message.text, color = textColor, fontSize = 16.sp, lineHeight = 21.sp)
                Row(modifier = Modifier.align(Alignment.End), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = message.timestamp,
                        color = if (isDark) Color(0xFFB8C4CF) else Color(0xFF8A96A3),
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                    )
                    if (message.isOutgoing) {
                        Spacer(Modifier.width(3.dp))
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = if (message.isRead) "Read" else "Sent",
                            tint = if (isDark) Color(0xFF8FD3FF) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.width(15.dp),
                        )
                    }
                }
            }
        }
    }
}

private fun Color.luminanceValue(): Float = 0.2126f * red + 0.7152f * green + 0.0722f * blue
