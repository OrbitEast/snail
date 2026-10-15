package com.example.snailmesssenger.feature.call

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

/** Offline call-session simulation. This does not create a real phone or VoIP call. */
class CallViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CallUiState())
    val uiState: StateFlow<CallUiState> = _uiState.asStateFlow()
    private var sessionJob: kotlinx.coroutines.Job? = null

    fun startCall(name: String, handle: String, initials: String, kind: CallKind = CallKind.VOICE) {
        sessionJob?.cancel()
        _uiState.value = CallUiState(
            contactName = name,
            contactHandle = handle,
            avatarInitials = initials,
            status = CallStatus.OUTGOING_RINGING,
            kind = kind,
            isCameraEnabled = kind == CallKind.VIDEO,
        )
        sessionJob = viewModelScope.launch {
            delay(1_700)
            if (_uiState.value.status != CallStatus.OUTGOING_RINGING) return@launch
            _uiState.update { it.copy(status = CallStatus.CONNECTING) }
            delay(900)
            if (_uiState.value.status != CallStatus.CONNECTING) return@launch
            _uiState.update { it.copy(status = CallStatus.CONNECTED) }
            var tick = 0
            while (_uiState.value.status == CallStatus.CONNECTED) {
                delay(1_000)
                tick++
                val level = (0.15 + (sin(tick / 2.3) + 1.0) * 0.32 + Random.nextFloat() * 0.18)
                    .toFloat().coerceIn(0.08f, 1f)
                _uiState.update {
                    if (it.status == CallStatus.CONNECTED) it.copy(elapsedSeconds = it.elapsedSeconds + 1, audioLevel = level)
                    else it
                }
            }
        }
    }

    fun toggleMute() { _uiState.update { it.copy(isMuted = !it.isMuted) } }
    fun toggleSpeaker() { _uiState.update { it.copy(isSpeakerOn = !it.isSpeakerOn) } }
    fun toggleCamera() {
        _uiState.update { it.copy(isCameraEnabled = !it.isCameraEnabled, kind = CallKind.VIDEO) }
    }
    fun flipCamera() { _uiState.update { it.copy(isFrontCamera = !it.isFrontCamera) } }
    fun toggleControls() { _uiState.update { it.copy(controlsVisible = !it.controlsVisible) } }
    fun connectNow() { _uiState.update { it.copy(status = CallStatus.CONNECTED) } }
    fun endCall() {
        sessionJob?.cancel()
        _uiState.update { it.copy(status = CallStatus.ENDED, controlsVisible = true, audioLevel = 0f) }
    }
    fun reset() {
        sessionJob?.cancel()
        _uiState.value = CallUiState()
    }

    fun formattedDuration(): String {
        val total = _uiState.value.elapsedSeconds
        return "%02d:%02d".format(total / 60, total % 60)
    }
}
