package com.example.snailmesssenger.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.snailmesssenger.data.model.Conversation
import com.example.snailmesssenger.data.model.Message
import com.example.snailmesssenger.data.model.MockData
import com.example.snailmesssenger.data.model.User
import org.json.JSONArray
import org.json.JSONObject

/**
 * Messaging surface for SNAIL Messenger. The Phase 1 implementation stores
 * prototype conversations locally on this device; it does not sync to other users.
 */
interface MessagingRepository {
    fun getConversations(): List<Conversation>
    fun getConversation(id: String): Conversation?
    fun sendMessage(conversationId: String, text: String): Message
    fun sendAttachment(conversationId: String, uri: String, mimeType: String, name: String, caption: String = ""): Message
    fun editMessage(conversationId: String, messageId: String, newText: String)
    fun deleteMessage(conversationId: String, messageId: String)
    fun reactToMessage(conversationId: String, messageId: String, reaction: String)
    fun startConversationWith(peerId: String): Conversation
    fun markConversationRead(conversationId: String)
}

/** Device-local conversation store for the native prototype. */
object DemoMessagingRepository : MessagingRepository {

    private val mutableConversations: MutableList<Conversation> =
        MockData.conversations.map { it.copy(messages = it.messages.toList(), peer = it.peer.copy(isOnline = false)) }
            .toMutableList()
    private var preferences: SharedPreferences? = null
    private val _conversationsFlow = MutableStateFlow(mutableConversations.toList())
    val conversationsFlow: StateFlow<List<Conversation>> = _conversationsFlow

    fun initialize(context: Context) {
        if (preferences != null) return
        val store = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        preferences = store
        val saved = store.getString(KEY_CONVERSATIONS, null)
        if (saved != null) {
            val parsed = runCatching { deserializeConversations(JSONArray(saved)) }.getOrNull()
            if (parsed != null) {
                mutableConversations.clear()
                mutableConversations.addAll(parsed)
                _conversationsFlow.value = mutableConversations.toList()
            }
        } else {
            persistConversations()
        }
    }

    override fun getConversations(): List<Conversation> = mutableConversations.toList()

    override fun getConversation(id: String): Conversation? =
        mutableConversations.firstOrNull { it.id == id }

    override fun sendMessage(conversationId: String, text: String): Message {
        val normalizedText = text.trim()
        require(normalizedText.isNotEmpty()) { "Message cannot be empty." }
        val index = mutableConversations.indexOfFirst { it.id == conversationId }
        require(index >= 0) { "Conversation not found." }
        val message = Message(
            id = "m${System.currentTimeMillis()}",
            senderId = Message.ME_ID,
            text = normalizedText,
            timestampMs = System.currentTimeMillis(),
        )
        val updated = mutableConversations[index].copy(
            messages = mutableConversations[index].messages + message,
        )
        mutableConversations[index] = updated
        mutableConversations.removeAt(index)
        mutableConversations.add(0, updated)
        publishChanges()
        persistConversations()

        return message
    }

    override fun sendAttachment(
        conversationId: String,
        uri: String,
        mimeType: String,
        name: String,
        caption: String,
    ): Message {
        require(uri.isNotBlank()) { "Attachment URI is required." }
        val index = mutableConversations.indexOfFirst { it.id == conversationId }
        require(index >= 0) { "Conversation not found." }
        val normalizedName = name.trim().ifBlank { "Attachment" }
        val normalizedCaption = caption.trim().ifBlank {
            when {
                mimeType.startsWith("image/") -> "Photo"
                mimeType.startsWith("video/") -> "Video"
                else -> "File: $normalizedName"
            }
        }
        val message = Message(
            id = "m${java.util.UUID.randomUUID()}",
            senderId = Message.ME_ID,
            text = normalizedCaption,
            timestampMs = System.currentTimeMillis(),
            attachmentUri = uri,
            attachmentName = normalizedName,
            attachmentMimeType = mimeType,
        )
        val updated = mutableConversations[index].copy(
            messages = mutableConversations[index].messages + message,
        )
        mutableConversations[index] = updated
        mutableConversations.removeAt(index)
        mutableConversations.add(0, updated)
        publishChanges()
        persistConversations()
        return message
    }

    override fun editMessage(conversationId: String, messageId: String, newText: String) {
        val normalizedText = newText.trim()
        require(normalizedText.isNotEmpty()) { "Message cannot be empty." }
        val index = mutableConversations.indexOfFirst { it.id == conversationId }
        require(index >= 0) { "Conversation not found." }
        val target = mutableConversations[index].messages.firstOrNull { it.id == messageId }
            ?: throw IllegalArgumentException("Message not found.")
        require(target.isOutgoing && target.attachmentUri == null) { "Only your text messages can be edited." }
        val updated = mutableConversations[index].copy(
            messages = mutableConversations[index].messages.map { message ->
                if (message.id == messageId) message.copy(text = normalizedText, isEdited = true) else message
            },
        )
        mutableConversations[index] = updated
        publishChanges()
        persistConversations()
    }

    override fun deleteMessage(conversationId: String, messageId: String) {
        val index = mutableConversations.indexOfFirst { it.id == conversationId }
        if (index < 0) return
        val updated = mutableConversations[index].copy(
            messages = mutableConversations[index].messages.filterNot { it.id == messageId },
        )
        mutableConversations[index] = updated
        publishChanges()
        persistConversations()
    }

    override fun reactToMessage(conversationId: String, messageId: String, reaction: String) {
        val index = mutableConversations.indexOfFirst { it.id == conversationId }
        if (index < 0) return
        val updated = mutableConversations[index].copy(
            messages = mutableConversations[index].messages.map { message ->
                if (message.id == messageId) {
                    message.copy(reaction = if (message.reaction == reaction) null else reaction)
                } else message
            },
        )
        mutableConversations[index] = updated
        publishChanges()
        persistConversations()
    }

    override fun startConversationWith(peerId: String): Conversation {
        mutableConversations.firstOrNull { it.peer.id == peerId }?.let { return it }
        val peer = MockData.conversations.map { it.peer }.firstOrNull { it.id == peerId }
            ?: throw IllegalArgumentException("Peer not found.")
        val conversation = Conversation(
            id = "c${System.currentTimeMillis()}",
            peer = peer.copy(isOnline = false),
        )
        mutableConversations.add(0, conversation)
        publishChanges()
        persistConversations()
        return conversation
    }

    override fun markConversationRead(conversationId: String) {
        val index = mutableConversations.indexOfFirst { it.id == conversationId }
        if (index < 0 || mutableConversations[index].unreadCount == 0) return
        mutableConversations[index] = mutableConversations[index].copy(unreadCount = 0)
        publishChanges()
        persistConversations()
    }

    private fun publishChanges() {
        _conversationsFlow.value = mutableConversations.toList()
    }

    private fun persistConversations() {
        val array = JSONArray()
        mutableConversations.forEach { conversation ->
            val messages = JSONArray()
            conversation.messages.forEach { message ->
                messages.put(
                    JSONObject()
                        .put("id", message.id)
                        .put("senderId", message.senderId)
                        .put("text", message.text)
                        .put("timestampMs", message.timestampMs)
                        .put("attachmentUri", message.attachmentUri ?: JSONObject.NULL)
                        .put("attachmentName", message.attachmentName ?: JSONObject.NULL)
                        .put("attachmentMimeType", message.attachmentMimeType ?: JSONObject.NULL)
                        .put("reaction", message.reaction ?: JSONObject.NULL)
                        .put("isEdited", message.isEdited),
                )
            }
            array.put(
                JSONObject()
                    .put("id", conversation.id)
                    .put("peerId", conversation.peer.id)
                    .put("username", conversation.peer.username)
                    .put("displayName", conversation.peer.displayName)
                    .put("bio", conversation.peer.bio)
                    .put("unreadCount", conversation.unreadCount)
                    .put("messages", messages),
            )
        }
        preferences?.edit()?.putString(KEY_CONVERSATIONS, array.toString())?.apply()
    }

    private fun deserializeConversations(array: JSONArray): List<Conversation> =
        buildList {
            for (index in 0 until array.length()) {
                val item = array.optJSONObject(index) ?: continue
                val peer = User(
                    id = item.optString("peerId"),
                    username = item.optString("username"),
                    displayName = item.optString("displayName"),
                    bio = item.optString("bio", ""),
                    isOnline = false,
                )
                val messagesArray = item.optJSONArray("messages") ?: JSONArray()
                val messages = buildList {
                    for (messageIndex in 0 until messagesArray.length()) {
                        val message = messagesArray.optJSONObject(messageIndex) ?: continue
                        add(
                            Message(
                                id = message.optString("id"),
                                senderId = message.optString("senderId"),
                                text = message.optString("text"),
                                timestampMs = message.optLong("timestampMs"),
                                attachmentUri = message.optString("attachmentUri").takeUnless { it.isBlank() || it == "null" },
                                attachmentName = message.optString("attachmentName").takeUnless { it.isBlank() || it == "null" },
                                attachmentMimeType = message.optString("attachmentMimeType").takeUnless { it.isBlank() || it == "null" },
                                reaction = message.optString("reaction").takeUnless { it.isBlank() || it == "null" },
                                isEdited = message.optBoolean("isEdited", false),
                            ),
                        )
                    }
                }
                add(
                    Conversation(
                        id = item.optString("id"),
                        peer = peer,
                        messages = messages,
                        unreadCount = item.optInt("unreadCount", 0),
                    ),
                )
            }
        }

    private const val PREFS_NAME = "snail_local_messages"
    private const val KEY_CONVERSATIONS = "conversations_json"
}
