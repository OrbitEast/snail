package com.example.snailmesssenger.data.model

/** A SNAIL Messenger user. */
data class User(
    val id: String,
    val username: String, // unique, no '@' prefix
    val displayName: String,
    val bio: String = "",
    val isOnline: Boolean = false,
)

/** The local conversation mode. */
enum class ConversationKind {
    DIRECT,
    GROUP,
    CHANNEL,
}

/** A conversation with either a single peer or a locally managed group/channel. */
data class Conversation(
    val id: String,
    val peer: User,
    val messages: List<Message> = emptyList(),
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isMuted: Boolean = false,
    val kind: ConversationKind = ConversationKind.DIRECT,
    val title: String? = null,
    val participantIds: List<String> = emptyList(),
    val createdBy: String? = null,
    val pinnedMessageId: String? = null,
    val isSavedMessages: Boolean = false,
)

/** A draft is stored locally per conversation and never sent until the user taps Send. */
data class MessageDraft(
    val text: String = "",
    val replyToMessageId: String? = null,
    val replyToText: String? = null,
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
    val pollQuestion: String? = null,
    val pollOptions: List<String> = emptyList(),
    val pollVotes: List<Int> = emptyList(),
    val pollSelectedOption: Int? = null,
) {
    val isOutgoing: Boolean get() = senderId == ME_ID

    companion object {
        const val ME_ID = "me"
    }
}
