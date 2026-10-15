package com.example.snailmesssenger.feature.call

import androidx.compose.runtime.Immutable

enum class CallStatus { IDLE, OUTGOING_RINGING, CONNECTING, CONNECTED, ENDED }
enum class CallKind { VOICE, VIDEO }

@Immutable
data class CallUiState(
    val contactName: String = "Snail Contact",
    val contactHandle: String = "@snailuser",
    val avatarInitials: String = "SC",
    val status: CallStatus = CallStatus.IDLE,
    val kind: CallKind = CallKind.VOICE,
    val elapsedSeconds: Int = 0,
    val audioLevel: Float = 0.18f,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = false,
    val isCameraEnabled: Boolean = true,
    val isFrontCamera: Boolean = true,
    val controlsVisible: Boolean = true,
    val encryptionEmojis: List<String> = listOf("🐚", "🌿", "🌙", "⭐"),
)
