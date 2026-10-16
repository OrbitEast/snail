package com.example.snailmesssenger.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.snailmesssenger.data.model.Conversation
import com.example.snailmesssenger.data.model.ConversationKind
import com.example.snailmesssenger.data.model.Message
import com.example.snailmesssenger.data.model.MessageDraft
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
    fun sendMessage(
        conversationId: String,
        text: String,
        replyToMessageId: String? = null,
        replyToText: String? = null,
    ): Message
    fun sendScheduledMessage(
        conversationId: String,
        text: String,
        scheduledId: String,
        replyToMessageId: String? = null,
        replyToText: String? = null,
    ): Message
    fun createPoll(conversationId: String, question: String, options: List<String>): Message
    fun voteOnPoll(conversationId: String, messageId: String, optionIndex: Int)
    fun forwardMessage(targetConversationId: String, sourceMessage: Message): Message
    fun sendAttachment(conversationId: String, uri: String, mimeType: String, name: String, caption: String = ""): Message
    fun editMessage(conversationId: String, messageId: String, newText: String)
    fun deleteMessage(conversationId: String, messageId: String)
    fun reactToMessage(conversationId: String, messageId: String, reaction: String)
    fun setConversationPinned(conversationId: String, pinned: Boolean)
    fun setConversationArchived(conversationId: String, archived: Boolean)
    fun setConversationMuted(conversationId: String, muted: Boolean)
    fun createGroupOrChannel(title: String, memberIds: List<String>, isChannel: Boolean): Conversation
    fun startConversationWith(peerId: String): Conversation
    fun markConversationRead(conversationId: String)
    fun clearConversationHistory(conversationId: String)
    fun setPinnedMessage(conversationId: String, messageId: String?)
    fun getOrCreateSavedMessages(): Conversation
    fun updateConversationMembers(conversationId: String, memberIds: List<String>)
    fun getDraft(conversationId: String): MessageDraft
    fun saveDraft(conversationId: String, draft: MessageDraft)
    fun clearDraft(conversationId: String)
}

/** A one-time request to open a specific saved message. */
data class MessageJumpRequest(
    val conversationId: String,
    val messageId: String,
)

/** Device-local conversation store for the native prototype. */
object DemoMessagingRepository : MessagingRepository {

    private val mutableConversations: MutableList<Conversation> =
        MockData.conversations.map { it.copy(messages = it.messages.toList(), peer = it.peer.copy(isOnline = false)) }
            .toMutableList()
    private var preferences: SharedPreferences? = null
    private var applicationContext: Context? = null
    private var activeAccountKey: String? = null
    private val _conversationsFlow = MutableStateFlow(mutableConversations.toList())
    val conversationsFlow: StateFlow<List<Conversation>> = _conversationsFlow
    private val _pendingMessageJump = MutableStateFlow<MessageJumpRequest?>(null)
    val pendingMessageJump: StateFlow<MessageJumpRequest?> = _pendingMessageJump

    fun requestMessageJump(conversationId: String, messageId: String) {
        _pendingMessageJump.value = MessageJumpRequest(conversationId, messageId)
    }

    fun consumeMessageJump(request: MessageJumpRequest) {
        if (_pendingMessageJump.value == request) _pendingMessageJump.value = null
    }

    fun initialize(
        context: Context,
        accountId: String? = DemoAuthRepository.currentUser?.id,
    ) {
        val appContext = context.applicationContext
        applicationContext = appContext
        val accountKey = accountScopeKey(accountId)
        if (preferences != null && activeAccountKey == accountKey) return

        val safeKey = accountKey
            .filter { it.isLetterOrDigit() || it == '_' || it == '-' }
            .take(48)
            .ifBlank { "guest" }
        val store = appContext.getSharedPreferences("${PREFS_NAME}_${safeKey}", Context.MODE_PRIVATE)
        migrateLegacyLocalData(appContext, store)

        activeAccountKey = accountKey
        preferences = store
        mutableConversations.clear()
        mutableConversations.addAll(
            MockData.conversations.map {
                it.copy(messages = it.messages.toList(), peer = it.peer.copy(isOnline = false))
            },
        )

        val saved = store.getString(KEY_CONVERSATIONS, null)
        val parsed = saved?.let { runCatching { deserializeConversations(JSONArray(it)) }.getOrNull() }
        if (parsed != null) {
            mutableConversations.clear()
            mutableConversations.addAll(parsed)
        }
        _conversationsFlow.value = mutableConversations.toList()
        persistConversations()
    }

    private fun accountScopeKey(accountId: String?): String =
        accountId?.takeIf { it.isNotBlank() } ?: "guest"

    private fun ensureAccountScope() {
        val context = applicationContext ?: return
        val accountId = DemoAuthRepository.currentUser?.id
        if (preferences == null || activeAccountKey != accountScopeKey(accountId)) {
            initialize(context, accountId)
        }
    }

    /**
     * One-time migration from the previous shared local store. Migrated messages
     * and drafts are assigned to the account active on the first launch after upgrade;
     * the legacy keys are then removed so a second local account cannot inherit them.
     */
    private fun migrateLegacyLocalData(context: Context, destination: SharedPreferences) {
        val legacy = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val legacyValues = legacy.all
        if (legacyValues.isEmpty()) return

        if (!destination.contains(KEY_CONVERSATIONS)) {
            val editor = destination.edit()
            (legacyValues[KEY_CONVERSATIONS] as? String)?.let {
                editor.putString(KEY_CONVERSATIONS, it)
            }
            legacyValues.forEach { (key, value) ->
                if (key.startsWith(DRAFT_PREFIX) && value is String) {
                    editor.putString(key, value)
                }
            }
            editor.apply()
        }

        val legacyEditor = legacy.edit().remove(KEY_CONVERSATIONS)
        legacyValues.keys.filter { it.startsWith(DRAFT_PREFIX) }.forEach(legacyEditor::remove)
        legacyEditor.apply()
    }

    override fun getConversations(): List<Conversation> {
        ensureAccountScope()
        return mutableConversations.toList()
    }

    override fun getConversation(id: String): Conversation? {
        ensureAccountScope()
        return mutableConversations.firstOrNull { it.id == id }
    }

    override fun sendMessage(
        conversationId: String,
        text: String,
        replyToMessageId: String?,
        replyToText: String?,
    ): Message {
        ensureAccountScope()
        val normalizedText = text.trim()
        require(normalizedText.isNotEmpty()) { "Message cannot be empty." }
        val index = mutableConversations.indexOfFirst { it.id == conversationId }
        require(index >= 0) { "Conversation not found." }
        val message = Message(
            id = "m${java.util.UUID.randomUUID()}",
            senderId = Message.ME_ID,
            text = normalizedText,
            timestampMs = System.currentTimeMillis(),
            replyToMessageId = replyToMessageId,
            replyToText = replyToText,
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

    override fun sendScheduledMessage(
        conversationId: String,
        text: String,
        scheduledId: String,
        replyToMessageId: String?,
        replyToText: String?,
    ): Message {
        ensureAccountScope()
        val stableId = "scheduled:$scheduledId"
        mutableConversations.firstNotNullOfOrNull { conversation ->
            conversation.messages.firstOrNull { it.id == stableId }
        }?.let { return it }

        val normalizedText = text.trim()
        require(normalizedText.isNotEmpty()) { "Scheduled message cannot be empty." }
        val index = mutableConversations.indexOfFirst { it.id == conversationId }
        require(index >= 0) { "Conversation not found." }
        val message = Message(
            id = stableId,
            senderId = Message.ME_ID,
            text = normalizedText,
            timestampMs = System.currentTimeMillis(),
            replyToMessageId = replyToMessageId,
            replyToText = replyToText,
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

    override fun createPoll(conversationId: String, question: String, options: List<String>): Message {
        ensureAccountScope()
        val normalizedQuestion = question.trim()
        val normalizedOptions = options.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
        require(normalizedQuestion.isNotEmpty()) { "Poll question cannot be empty." }
        require(normalizedOptions.size in 2..10) { "A poll must have 2–10 different options." }
        val index = mutableConversations.indexOfFirst { it.id == conversationId }
        require(index >= 0) { "Conversation not found." }

        val poll = Message(
            id = "m${java.util.UUID.randomUUID()}",
            senderId = Message.ME_ID,
            text = normalizedQuestion,
            timestampMs = System.currentTimeMillis(),
            pollQuestion = normalizedQuestion,
            pollOptions = normalizedOptions,
            pollVotes = List(normalizedOptions.size) { 0 },
        )
        val updated = mutableConversations[index].copy(
            messages = mutableConversations[index].messages + poll,
        )
        mutableConversations[index] = updated
        mutableConversations.removeAt(index)
        mutableConversations.add(0, updated)
        publishChanges()
        persistConversations()
        return poll
    }

    override fun voteOnPoll(conversationId: String, messageId: String, optionIndex: Int) {
        ensureAccountScope()
        val conversationIndex = mutableConversations.indexOfFirst { it.id == conversationId }
        require(conversationIndex >= 0) { "Conversation not found." }
        val conversation = mutableConversations[conversationIndex]
        val poll = conversation.messages.firstOrNull { it.id == messageId }
            ?: throw IllegalArgumentException("Poll not found.")
        require(poll.pollQuestion != null && optionIndex in poll.pollOptions.indices) {
            "Invalid poll option."
        }
        if (poll.pollSelectedOption == optionIndex) return

        val counts = if (poll.pollVotes.size == poll.pollOptions.size) {
            poll.pollVotes.toMutableList()
        } else {
            MutableList(poll.pollOptions.size) { 0 }
        }
        poll.pollSelectedOption?.takeIf { it in counts.indices }?.let { previous ->
            counts[previous] = (counts[previous] - 1).coerceAtLeast(0)
        }
        counts[optionIndex] = counts[optionIndex] + 1

        mutableConversations[conversationIndex] = conversation.copy(
            messages = conversation.messages.map { message ->
                if (message.id == messageId) {
                    message.copy(pollVotes = counts, pollSelectedOption = optionIndex)
                } else message
            },
        )
        publishChanges()
        persistConversations()
    }

    override fun forwardMessage(targetConversationId: String, sourceMessage: Message): Message {
        ensureAccountScope()
        val index = mutableConversations.indexOfFirst { it.id == targetConversationId }
        require(index >= 0) { "Target conversation not found." }
        val message = Message(
            id = "m${java.util.UUID.randomUUID()}",
            senderId = Message.ME_ID,
            text = sourceMessage.text,
            timestampMs = System.currentTimeMillis(),
            attachmentUri = sourceMessage.attachmentUri,
            attachmentName = sourceMessage.attachmentName,
            attachmentMimeType = sourceMessage.attachmentMimeType,
            replyToMessageId = null,
            replyToText = null,
            isForwarded = true,
            pollQuestion = sourceMessage.pollQuestion,
            pollOptions = sourceMessage.pollOptions,
            pollVotes = List(sourceMessage.pollOptions.size) { 0 },
            pollSelectedOption = null,
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
        ensureAccountScope()
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
        ensureAccountScope()
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
        ensureAccountScope()
        val index = mutableConversations.indexOfFirst { it.id == conversationId }
        if (index < 0) return
        val updated = mutableConversations[index].copy(
            messages = mutableConversations[index].messages.filterNot { it.id == messageId },
            pinnedMessageId = if (mutableConversations[index].pinnedMessageId == messageId) null
                else mutableConversations[index].pinnedMessageId,
        )
        mutableConversations[index] = updated
        publishChanges()
        persistConversations()
    }

    override fun reactToMessage(conversationId: String, messageId: String, reaction: String) {
        ensureAccountScope()
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

    override fun setConversationPinned(conversationId: String, pinned: Boolean) {
        ensureAccountScope()
        updateConversationFlag(conversationId) { it.copy(isPinned = pinned) }
    }

    override fun setConversationArchived(conversationId: String, archived: Boolean) {
        ensureAccountScope()
        updateConversationFlag(conversationId) { it.copy(isArchived = archived) }
    }

    override fun setConversationMuted(conversationId: String, muted: Boolean) {
        ensureAccountScope()
        updateConversationFlag(conversationId) { it.copy(isMuted = muted) }
    }

    private fun updateConversationFlag(conversationId: String, transform: (Conversation) -> Conversation) {
        val index = mutableConversations.indexOfFirst { it.id == conversationId }
        if (index < 0) return
        mutableConversations[index] = transform(mutableConversations[index])
        publishChanges()
        persistConversations()
    }

    override fun createGroupOrChannel(
        title: String,
        memberIds: List<String>,
        isChannel: Boolean,
    ): Conversation {
        ensureAccountScope()
        val normalizedTitle = title.trim()
        require(normalizedTitle.length in 2..48) { "Name must be between 2 and 48 characters." }
        val knownUsers = (MockData.discoverableContacts + MockData.conversations.map { it.peer })
            .distinctBy { it.id }
        val requestedIds = memberIds.distinct()
        require(requestedIds.isNotEmpty()) { "Choose at least one member." }
        val resolvedIds = requestedIds.filter { requestedId ->
            knownUsers.any { it.id == requestedId }
        }
        require(resolvedIds.size == requestedIds.size) { "One or more selected members could not be found." }

        val groupId = "c${java.util.UUID.randomUUID()}"
        val titleSlug = normalizedTitle.lowercase()
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')
            .take(18)
            .ifBlank { "snail_chat" }
        val kind = if (isChannel) ConversationKind.CHANNEL else ConversationKind.GROUP
        val peer = User(
            id = groupId,
            username = "${titleSlug}_${java.util.UUID.randomUUID().toString().take(5)}",
            displayName = normalizedTitle,
            bio = if (isChannel) "SNAIL channel" else "SNAIL group",
            isOnline = false,
        )
        val conversation = Conversation(
            id = groupId,
            peer = peer,
            kind = kind,
            title = normalizedTitle,
            participantIds = (listOf("me") + resolvedIds).distinct(),
            createdBy = "me",
        )
        mutableConversations.add(0, conversation)
        publishChanges()
        persistConversations()
        return conversation
    }

    override fun startConversationWith(peerId: String): Conversation {
        ensureAccountScope()
        mutableConversations.firstOrNull { it.peer.id == peerId }?.let { existing ->
            if (existing.isArchived) setConversationArchived(existing.id, false)
            return getConversation(existing.id) ?: existing
        }
        val peer = MockData.conversations.map { it.peer }.firstOrNull { it.id == peerId }
            ?: MockData.discoverableContacts.firstOrNull { it.id == peerId }
            ?: throw IllegalArgumentException("Peer not found.")
        val conversation = Conversation(
            id = "c${java.util.UUID.randomUUID()}",
            peer = peer.copy(isOnline = false),
        )
        mutableConversations.add(0, conversation)
        publishChanges()
        persistConversations()
        return conversation
    }

    override fun updateConversationMembers(conversationId: String, memberIds: List<String>) {
        ensureAccountScope()
        val conversationIndex = mutableConversations.indexOfFirst { it.id == conversationId }
        require(conversationIndex >= 0) { "Conversation not found." }
        val existing = mutableConversations[conversationIndex]
        require(existing.kind == ConversationKind.GROUP || existing.kind == ConversationKind.CHANNEL) {
            "Only groups and channels have editable members."
        }
        val directory = (MockData.discoverableContacts + MockData.conversations.map { it.peer })
            .distinctBy { it.id }
        val uniqueIds = memberIds.filterNot { it == "me" }.distinct()
        require(uniqueIds.all { id -> directory.any { it.id == id } }) {
            "One or more selected members could not be found."
        }
        require(uniqueIds.isNotEmpty()) { "Keep at least one member or subscriber." }
        mutableConversations[conversationIndex] = existing.copy(
            participantIds = listOf("me") + uniqueIds,
        )
        publishChanges()
        persistConversations()
    }

    override fun getOrCreateSavedMessages(): Conversation {
        ensureAccountScope()
        mutableConversations.firstOrNull { it.isSavedMessages }?.let { return it }
        val conversation = Conversation(
            id = "saved-messages",
            peer = User(
                id = "saved-messages",
                username = "saved",
                displayName = "Saved Messages",
                bio = "Your personal space for notes and messages.",
                isOnline = false,
            ),
            kind = ConversationKind.DIRECT,
            title = "Saved Messages",
            participantIds = listOf("me"),
            isPinned = true,
            isSavedMessages = true,
        )
        mutableConversations.add(0, conversation)
        publishChanges()
        persistConversations()
        return conversation
    }

    override fun getDraft(conversationId: String): MessageDraft {
        ensureAccountScope()
        val raw = preferences?.getString("$DRAFT_PREFIX$conversationId", null) ?: return MessageDraft()
        val json = runCatching { JSONObject(raw) }.getOrNull() ?: return MessageDraft()
        return MessageDraft(
            text = json.optString("text"),
            replyToMessageId = json.optString("replyToMessageId")
                .takeUnless { it.isBlank() || it == "null" },
            replyToText = json.optString("replyToText")
                .takeUnless { it.isBlank() || it == "null" },
        )
    }

    override fun saveDraft(conversationId: String, draft: MessageDraft) {
        ensureAccountScope()
        val key = "$DRAFT_PREFIX$conversationId"
        if (draft.text.isBlank() && draft.replyToMessageId == null && draft.replyToText.isNullOrBlank()) {
            preferences?.edit()?.remove(key)?.apply()
            return
        }
        val json = JSONObject()
            .put("text", draft.text)
            .put("replyToMessageId", draft.replyToMessageId ?: JSONObject.NULL)
            .put("replyToText", draft.replyToText ?: JSONObject.NULL)
        preferences?.edit()?.putString(key, json.toString())?.apply()
    }

    override fun clearDraft(conversationId: String) {
        ensureAccountScope()
        preferences?.edit()?.remove("$DRAFT_PREFIX$conversationId")?.apply()
    }

    override fun setPinnedMessage(conversationId: String, messageId: String?) {
        ensureAccountScope()
        val conversationIndex = mutableConversations.indexOfFirst { it.id == conversationId }
        require(conversationIndex >= 0) { "Conversation not found." }
        if (messageId != null) {
            require(mutableConversations[conversationIndex].messages.any { it.id == messageId }) {
                "Message to pin could not be found."
            }
        }
        mutableConversations[conversationIndex] =
            mutableConversations[conversationIndex].copy(pinnedMessageId = messageId)
        publishChanges()
        persistConversations()
    }

    override fun clearConversationHistory(conversationId: String) {
        ensureAccountScope()
        val index = mutableConversations.indexOfFirst { it.id == conversationId }
        if (index < 0) return
        mutableConversations[index] = mutableConversations[index].copy(
            messages = emptyList(),
            unreadCount = 0,
            pinnedMessageId = null,
        )
        clearDraft(conversationId)
        publishChanges()
        persistConversations()
    }

    override fun markConversationRead(conversationId: String) {
        ensureAccountScope()
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
                        .put("isEdited", message.isEdited)
                        .put("replyToMessageId", message.replyToMessageId ?: JSONObject.NULL)
                        .put("replyToText", message.replyToText ?: JSONObject.NULL)
                        .put("isForwarded", message.isForwarded)
                        .put("pollQuestion", message.pollQuestion ?: JSONObject.NULL)
                        .put("pollOptions", JSONArray(message.pollOptions))
                        .put("pollVotes", JSONArray(message.pollVotes))
                        .put("pollSelectedOption", message.pollSelectedOption ?: JSONObject.NULL),
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
                    .put("isPinned", conversation.isPinned)
                    .put("isArchived", conversation.isArchived)
                    .put("isMuted", conversation.isMuted)
                    .put("kind", conversation.kind.name)
                    .put("title", conversation.title ?: JSONObject.NULL)
                    .put("participantIds", JSONArray(conversation.participantIds))
                    .put("createdBy", conversation.createdBy ?: JSONObject.NULL)
                    .put("pinnedMessageId", conversation.pinnedMessageId ?: JSONObject.NULL)
                    .put("isSavedMessages", conversation.isSavedMessages)
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
                                replyToMessageId = message.optString("replyToMessageId").takeUnless { it.isBlank() || it == "null" },
                                replyToText = message.optString("replyToText").takeUnless { it.isBlank() || it == "null" },
                                isForwarded = message.optBoolean("isForwarded", false),
                                pollQuestion = message.optString("pollQuestion").takeUnless { it.isBlank() || it == "null" },
                                pollOptions = message.optJSONArray("pollOptions")?.let { options ->
                                    buildList { for (optionIndex in 0 until options.length()) add(options.optString(optionIndex)) }
                                }.orEmpty(),
                                pollVotes = message.optJSONArray("pollVotes")?.let { votes ->
                                    buildList { for (voteIndex in 0 until votes.length()) add(votes.optInt(voteIndex)) }
                                }.orEmpty(),
                                pollSelectedOption = message.optString("pollSelectedOption").toIntOrNull(),
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
                        isPinned = item.optBoolean("isPinned", false),
                        isArchived = item.optBoolean("isArchived", false),
                        isMuted = item.optBoolean("isMuted", false),
                        kind = runCatching {
                            ConversationKind.valueOf(item.optString("kind", "DIRECT"))
                        }.getOrDefault(ConversationKind.DIRECT),
                        title = item.optString("title").takeUnless { it.isBlank() || it == "null" },
                        participantIds = item.optJSONArray("participantIds")?.let { ids ->
                            buildList { for (memberIndex in 0 until ids.length()) add(ids.optString(memberIndex)) }
                        }.orEmpty(),
                        createdBy = item.optString("createdBy").takeUnless { it.isBlank() || it == "null" },
                        pinnedMessageId = item.optString("pinnedMessageId").takeUnless { it.isBlank() || it == "null" },
                        isSavedMessages = item.optBoolean("isSavedMessages", false),
                    ),
                )
            }
        }

    private const val PREFS_NAME = "snail_local_messages"
    private const val KEY_CONVERSATIONS = "conversations_json"
    private const val DRAFT_PREFIX = "draft_"
}
