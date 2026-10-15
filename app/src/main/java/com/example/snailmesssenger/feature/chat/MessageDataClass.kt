package com.example.snailmesssenger.feature.chat

/** Local-only message model used by the offline conversation screen. */
data class Message(
    val id: String,
    val senderId: String,
    val text: String,
    val timestamp: String,
    val isOutgoing: Boolean,
    val isRead: Boolean = false,
)

data class ChatDetailUiState(
    val conversationId: String = "",
    val title: String = "Chat",
    val username: String = "",
    val isOnline: Boolean = false,
    val messages: List<Message> = emptyList(),
    val draft: String = "",
)
