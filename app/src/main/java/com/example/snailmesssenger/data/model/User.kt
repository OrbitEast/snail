package com.example.snailmesssenger.data.model

/** A SNAIL Messenger user. */
data class User(
    val id: String,
    val username: String, // unique, no '@' prefix
    val displayName: String,
    val bio: String = "",
    val isOnline: Boolean = false,
)

/** A one-to-one conversation with a single peer. */
data class Conversation(
    val id: String,
    val peer: User,
    val messages: List<Message> = emptyList(),
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isMuted: Boolean = false,
)

/** A single chat message. */
data class Message(
    val id: String,
    val senderId: String, // user id; "me" for outgoing
    val text: String,
    val timestampMs: Long,
    val attachmentUri: String? = null,
    val attachmentName: String? = null,
    val attachmentMimeType: String? = null,
    val reaction: String? = null,
    val isEdited: Boolean = false,
    val replyToMessageId: String? = null,
    val replyToText: String? = null,
    val isForwarded: Boolean = false,
) {
    val isOutgoing: Boolean get() = senderId == ME_ID

    companion object {
        const val ME_ID = "me"
    }
}
