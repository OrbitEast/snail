package com.example.snailmesssenger.feature.call

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VoiceBlue = Color(0xFF2481CC)

@Composable
fun VoiceCallScreen(
    state: CallUiState,
    durationText: String,
    onBack: () -> Unit,
    onMute: () -> Unit,
    onSpeaker: () -> Unit,
    onVideo: () -> Unit,
    onEnd: () -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "voice-call-pulse")
    val pulse by transition.animateFloat(
        initialValue = 1f, targetValue = 1.22f,
        animationSpec = infiniteRepeatable(tween(1_000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "avatar-pulse",
    )
    val ringScale = 1f + state.audioLevel.coerceIn(0f, 1f) * 0.25f
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF26394B), Color(0xFF101820), Color(0xFF080B10))))) {
        Box(Modifier.align(Alignment.TopEnd).padding(top = 38.dp, end = 24.dp)) {
            Text(state.encryptionEmojis.take(4).joinToString(" "), fontSize = 18.sp)
        }
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart).padding(12.dp)) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Column(
            modifier = Modifier.align(Alignment.Center).padding(bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(210.dp)) {
                Box(Modifier.size(188.dp).scale(pulse * ringScale).background(VoiceBlue.copy(alpha = 0.10f), CircleShape))
                Box(Modifier.size(158.dp).scale(pulse).background(VoiceBlue.copy(alpha = 0.14f), CircleShape))
                Surface(modifier = Modifier.size(122.dp), shape = CircleShape, color = VoiceBlue, contentColor = Color.White, shadowElevation = 18.dp) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(state.avatarInitials.take(2).uppercase(), style = MaterialTheme.typography.headlineLarge)
                    }
                }
            }
            Spacer(Modifier.size(18.dp))
            Text(state.contactName, color = Color.White, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.size(8.dp))
            Text(
                text = when (state.status) {
                    CallStatus.OUTGOING_RINGING -> "Ringing…"
                    CallStatus.CONNECTING -> "Connecting…"
                    CallStatus.CONNECTED -> durationText
                    CallStatus.ENDED -> "Call ended"
                    CallStatus.IDLE -> "Ready to call"
                },
                color = if (state.status == CallStatus.CONNECTED) Color(0xFF8CD6A3) else Color(0xFFC1CBD4),
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(Modifier.size(7.dp))
            Text(state.contactHandle, color = Color(0xFF8C9AA8), style = MaterialTheme.typography.bodyMedium)
        }
        AnimatedVisibility(
            visible = state.status != CallStatus.ENDED,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 34.dp),
        ) {
            CallControlBar(state, onMute, onSpeaker, onVideo, onEnd)
        }
        if (state.status == CallStatus.ENDED) {
            Surface(
                onClick = onBack,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 40.dp),
                color = VoiceBlue,
                contentColor = Color.White,
                shape = CircleShape,
            ) { Text("Done", modifier = Modifier.padding(horizontal = 30.dp, vertical = 13.dp)) }
        }
    }
}
