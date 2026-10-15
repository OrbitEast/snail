package com.example.snailmesssenger.feature.call

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun VideoCallScreen(
    state: CallUiState,
    durationText: String,
    onToggleControls: () -> Unit,
    onMute: () -> Unit,
    onSpeaker: () -> Unit,
    onVideo: () -> Unit,
    onFlipCamera: () -> Unit,
    onEnd: () -> Unit,
) {
    var pipX by remember { mutableStateOf(0f) }
    var pipY by remember { mutableStateOf(0f) }
    Box(
        Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF243D50), Color(0xFF0C1118), Color(0xFF07090C))))
            .pointerInput(Unit) { detectTapGestures(onTap = { onToggleControls() }) },
    ) {
        // Offline video placeholder; no camera permission or actual video stream is started.
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Surface(modifier = Modifier.size(136.dp), shape = RoundedCornerShape(36.dp), color = Color(0xFF2481CC), contentColor = Color.White) {
                Box(contentAlignment = Alignment.Center) { Text(state.avatarInitials, style = MaterialTheme.typography.headlineLarge) }
            }
            Spacer(Modifier.size(16.dp))
            Text(state.contactName, color = Color.White, style = MaterialTheme.typography.headlineMedium)
            Text(if (state.status == CallStatus.CONNECTED) durationText else "Video call • Connecting…", color = Color.LightGray)
        }

        Box(
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 48.dp, end = 16.dp)
                .offset { IntOffset(pipX.roundToInt(), pipY.roundToInt()) }
                .size(width = 112.dp, height = 158.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(if (state.isCameraEnabled) Brush.linearGradient(listOf(Color(0xFF516B7B), Color(0xFF222E3A))) else Brush.linearGradient(listOf(Color.DarkGray, Color.Black)))
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        pipX += dragAmount.x
                        pipY += dragAmount.y
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            if (state.isCameraEnabled) {
                Text(if (state.isFrontCamera) "YOU · FRONT CAMERA" else "YOU · REAR CAMERA", color = Color.White, fontSize = 10.sp)
            } else {
                Icon(Icons.Default.VideocamOff, contentDescription = "Camera off", tint = Color.White)
            }
        }

        AnimatedVisibility(
            visible = state.controlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 28.dp),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.background(Color(0xCC171D25), RoundedCornerShape(30.dp)).padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    VideoControl(if (state.isMuted) Icons.Default.MicOff else Icons.Default.Mic, if (state.isMuted) "Unmute" else "Mute", onMute, state.isMuted)
                    VideoControl(Icons.Default.VolumeUp, "Speaker", onSpeaker, state.isSpeakerOn)
                    VideoControl(if (state.isCameraEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff, "Camera", onVideo, state.isCameraEnabled)
                    VideoControl(Icons.Default.Cameraswitch, "Flip", onFlipCamera, false)
                    Surface(onClick = onEnd, modifier = Modifier.size(52.dp), shape = RoundedCornerShape(50), color = Color(0xFFE5484D), contentColor = Color.White) {
                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.CallEnd, contentDescription = "End call") }
                    }
                }
                Spacer(Modifier.size(8.dp))
                Text("End-to-end encrypted · local demo", color = Color(0xFFCDD5DD), fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun VideoControl(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    active: Boolean,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(onClick = onClick, modifier = Modifier.size(44.dp), shape = RoundedCornerShape(50), color = if (active) Color(0xFF2481CC) else Color(0xFF343C46), contentColor = Color.White) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, contentDescription = label) }
        }
        Spacer(Modifier.size(3.dp))
        Text(label, color = Color.White, fontSize = 9.sp)
    }
}
