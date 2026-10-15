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
                        .put("timestampMs", message.timestampMs),
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
