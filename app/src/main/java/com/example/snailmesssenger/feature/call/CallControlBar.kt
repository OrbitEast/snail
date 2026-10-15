package com.example.snailmesssenger.feature.call

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val CallRed = Color(0xFFE5484D)

@Composable
fun CallControlBar(
    state: CallUiState,
    onMute: () -> Unit,
    onSpeaker: () -> Unit,
    onVideo: () -> Unit,
    onEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.background(Color(0xCC1A2028), CircleShape).padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CallControl(Icons.Default.run { if (state.isMuted) MicOff else Mic }, if (state.isMuted) "Unmute" else "Mute", state.isMuted, onMute)
        CallControl(Icons.Default.VolumeUp, if (state.isSpeakerOn) "Speaker on" else "Speaker", state.isSpeakerOn, onSpeaker)
        CallControl(Icons.Default.run { if (state.isCameraEnabled) Videocam else VideocamOff }, if (state.isCameraEnabled) "Video on" else "Video off", state.isCameraEnabled, onVideo)
        val endScale by animateFloatAsState(1f, label = "end-call-scale")
        Surface(
            onClick = onEnd,
            modifier = Modifier.size(58.dp),
            shape = CircleShape,
            color = CallRed,
            contentColor = Color.White,
        ) {
            androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center, modifier = Modifier.size(58.dp)) {
                Icon(Icons.Default.CallEnd, contentDescription = "End call", modifier = Modifier.size(27.dp))
            }
        }
    }
}

@Composable
private fun CallControl(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            onClick = onClick,
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = if (active) MaterialTheme.colorScheme.primary else Color(0xFF343C46),
            contentColor = Color.White,
        ) {
            androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = label, modifier = Modifier.size(22.dp))
            }
        }
        Spacer(Modifier.size(4.dp))
        Text(label, color = Color.White.copy(alpha = 0.82f), fontSize = 10.sp)
    }
}
