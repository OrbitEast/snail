package com.example.snailmesssenger.feature.call

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CallSessionScreen(
    conversationId: String,
    kindArg: String,
    onBack: () -> Unit,
    viewModel: CallViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val contact = when (conversationId.lowercase()) {
        "arjun" -> Triple("Arjun Mehta", "@arjunm", "AM")
        "riya" -> Triple("Riya Sharma", "@riyash", "RS")
        "kabir" -> Triple("Kabir Das", "@kabird", "KD")
        "rohan" -> Triple("Rohan Sen", "@rohansen", "RS")
        else -> Triple("Maya Kapoor", "@mayak", "MK")
    }
    val requestedKind = if (kindArg.equals("video", ignoreCase = true)) CallKind.VIDEO else CallKind.VOICE

    LaunchedEffect(conversationId, requestedKind) {
        viewModel.startCall(contact.first, contact.second, contact.third, requestedKind)
    }
    BackHandler(enabled = true) {
        if (state.status != CallStatus.ENDED) viewModel.endCall()
        onBack()
    }

    AnimatedContent(targetState = state.kind, label = "call-mode-transition") { kind ->
        if (kind == CallKind.VIDEO) {
            VideoCallScreen(
                state = state,
                durationText = viewModel.formattedDuration(),
                onToggleControls = viewModel::toggleControls,
                onMute = viewModel::toggleMute,
                onSpeaker = viewModel::toggleSpeaker,
                onVideo = viewModel::toggleCamera,
                onFlipCamera = viewModel::flipCamera,
                onEnd = { viewModel.endCall() },
            )
        } else {
            VoiceCallScreen(
                state = state,
                durationText = viewModel.formattedDuration(),
                onBack = onBack,
                onMute = viewModel::toggleMute,
                onSpeaker = viewModel::toggleSpeaker,
                onVideo = viewModel::toggleCamera,
                onEnd = viewModel::endCall,
            )
        }
    }

    if (state.status == CallStatus.ENDED) {
        LaunchedEffect(state.status) {
            kotlinx.coroutines.delay(850)
            onBack()
        }
    }
}
