package com.example.snailmesssenger.data.repository

import com.example.snailmesssenger.data.model.Conversation
import com.example.snailmesssenger.data.model.Message
import com.example.snailmesssenger.data.model.MockData

/**
 * Messaging surface for SNAIL Messenger.
 *
 * Phase 1 uses [DemoMessagingRepository] backed by [MockData].
 * Replace with a real implementation when a backend exists; the UI only
 * depends on this interface.
 */
interface MessagingRepository {
    fun getConversations(): List<Conversation>
    fun getConversation(id: String): Conversation?
    fun sendMessage(conversationId: String, text: String): Message
    fun startConversationWith(peerId: String): Conversation
}

/**
 * In-memory demo implementation over [MockData].
 * All state is lost when the process dies — by design.
 *
 * Singleton so the working conversation list is shared across screens.
 */
object DemoMessagingRepository : MessagingRepository {

    // Working copies so the source mock data isn't mutated.
    private val mutableConversations: MutableList<Conversation> =
        MockData.conversations.map { it.copy(messages = it.messages.toMutableList()) }.toMutableList()

    private var counter = 1000

    override fun getConversations(): List<Conversation> = mutableConversations.toList()

    override fun getConversation(id: String): Conversation? =
        mutableConversations.firstOrNull { it.id == id }

    override fun sendMessage(conversationId: String, text: String): Message {
        val index = mutableConversations.indexOfFirst { it.id == conversationId }
        require(index >= 0) { "Conversation not found." }
        val message = Message(
            id = "m${counter++}",
            senderId = "me",
            text = text,
            timestampMs = System.currentTimeMillis(),
        )
        val updated = mutableConversations[index].copy(
            messages = mutableConversations[index].messages + message,
        )
        mutableConversations[index] = updated
        return message
    }

    override fun startConversationWith(peerId: String): Conversation {
        mutableConversations.firstOrNull { it.peer.id == peerId }?.let { return it }
        val peer = MockData.conversations.map { it.peer }.firstOrNull { it.id == peerId }
            ?: throw IllegalArgumentException("Peer not found.")
        val conversation = Conversation(id = "c${counter++}", peer = peer)
        mutableConversations.add(0, conversation)
        return conversation
    }
}
