package com.example.snailmessenger.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChatDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ChatDetailUiState())
    val uiState: StateFlow<ChatDetailUiState> = _uiState.asStateFlow()

    fun loadConversation(conversationId: String) {
        if (_uiState.value.conversationId == conversationId && _uiState.value.messages.isNotEmpty()) return

        val peer = when (conversationId) {
            "maya" -> "Maya Kapoor" to "mayakapoor"
            "arjun" -> "Arjun Mehta" to "arjunm"
            "riya" -> "Riya Sharma" to "riya"
            "kabir" -> "Kabir Singh" to "kabir"
            "rohan" -> "Rohan Verma" to "rohanv"
            else -> "Maya Kapoor" to "mayakapoor"
        }

        _uiState.value = ChatDetailUiState(
            conversationId = conversationId,
            title = peer.first,
            username = peer.second,
            isOnline = true,
            messages = mockMessages(conversationId),
        )
    }

    fun updateDraft(text: String) {
        _uiState.update { it.copy(draft = text) }
    }

    fun sendMessage(text: String = _uiState.value.draft) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        _uiState.update { state ->
            state.copy(
                messages = state.messages + Message(
                    id = "local-${System.currentTimeMillis()}",
                    senderId = "me",
                    text = trimmed,
                    timestamp = "now",
                    isOutgoing = true,
                    isRead = true,
                ),
                draft = "",
            )
        }
    }

    private fun mockMessages(conversationId: String): List<Message> = listOf(
        Message("1", conversationId, "Hey! How is the new Snail build going?", "19:41", false),
        Message("2", "me", "Pretty good. I just finished the login flow.", "19:43", true, true),
        Message("3", conversationId, "Nice. The transitions looked really smooth.", "19:44", false),
        Message("4", "me", "Next I'm polishing the chat experience.", "19:45", true, true),
        Message("5", conversationId, "The message bubbles should feel clean and lightweight.", "19:46", false),
        Message("6", "me", "Agreed. Keeping everything local for now.", "19:47", true, true),
        Message("7", conversationId, "Perfect. Send me the latest build when it's ready.", "19:49", false),
        Message("8", "me", "Will do 👍", "19:50", true, true),
    )
}
