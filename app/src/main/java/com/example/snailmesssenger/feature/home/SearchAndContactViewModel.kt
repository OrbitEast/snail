package com.example.snailmesssenger.feature.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.snailmesssenger.data.model.Conversation
import com.example.snailmesssenger.data.repository.DemoMessagingRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Immutable
data class SearchContact(
    val id: String,
    val displayName: String,
    val username: String,
    val phone: String,
    val isRegistered: Boolean,
    val isOnline: Boolean = false,
)

@Immutable
data class SearchAndContactUiState(
    val query: String = "",
    val chats: List<ChatUiState> = emptyList(),
    val matchingChats: List<ChatUiState> = emptyList(),
    val matchingSnailContacts: List<SearchContact> = emptyList(),
    val matchingInviteContacts: List<SearchContact> = emptyList(),
    val allSnailContacts: List<SearchContact> = emptyList(),
    val allInviteContacts: List<SearchContact> = emptyList(),
    val isSyncingContacts: Boolean = false,
)

@Immutable
private data class SearchIndexResult(
    val allChats: List<ChatUiState>,
    val chats: List<ChatUiState>,
    val snailContacts: List<SearchContact>,
    val inviteContacts: List<SearchContact>,
)

/**
 * Offline contact-sync simulation. It deliberately uses local mock data and does not
 * request READ_CONTACTS or access the device address book.
 */
class ContactSyncManager {
    private val _contacts = MutableStateFlow<List<SearchContact>>(emptyList())
    val contacts: StateFlow<List<SearchContact>> = _contacts.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    suspend fun syncLocalMockContacts() {
        if (_isSyncing.value) return
        _isSyncing.value = true
        try {
            // Simulate an IO-bound device-contact read without touching real user data.
            withContext(Dispatchers.IO) { delay(450) }
            _contacts.value = mockContacts().sortedBy { it.displayName.lowercase() }
        } finally {
            _isSyncing.value = false
        }
    }

    private fun mockContacts() = listOf(
        SearchContact("maya", "Maya Kapoor", "@mayakapoor", "+91 90000 10001", true, true),
        SearchContact("arjun", "Arjun Mehta", "@arjunm", "+91 90000 10002", true, true),
        SearchContact("riya", "Riya Sharma", "@riya", "+91 90000 10003", true),
        SearchContact("kabir", "Kabir Singh", "@kabir", "+91 90000 10004", true, true),
        SearchContact("ananya", "Ananya Das", "@ananyadas", "+91 90000 10005", true),
        SearchContact("rohan", "Rohan Verma", "@rohanv", "+91 90000 10006", true, true),
        SearchContact("neha", "Neha Jain", "@nehaj", "+91 90000 10007", true),
        SearchContact("vikram", "Vikram Roy", "@vikramroy", "+91 90000 10008", true),
        SearchContact("isha", "Isha Sen", "@ishasen", "+91 90000 10009", false),
        SearchContact("rahul", "Rahul Das", "@rahuld", "+91 90000 10010", false),
        SearchContact("priya", "Priya Bose", "@priyabose", "+91 90000 10011", false),
        SearchContact("samar", "Samar Dutta", "@samard", "+91 90000 10012", false),
        SearchContact("tanya", "Tanya Roy", "@tanyaroy", "+91 90000 10013", false),
    )
}

class SearchAndContactViewModel : ViewModel() {
    private val contactSyncManager = ContactSyncManager()
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val debouncedQuery = _query.debounce(250)

    private val indexedResults = combine(
        DemoMessagingRepository.conversationsFlow,
        contactSyncManager.contacts,
        debouncedQuery,
    ) { conversations, contacts, rawQuery ->
        val chats = toChatUiStates(conversations)
        withContext(Dispatchers.Default) {
            val term = rawQuery.trim().removePrefix("@")
            val chatMatches = if (term.isBlank()) chats else chats.filter { chat ->
                chat.title.contains(rawQuery.trim(), ignoreCase = true) ||
                    chat.lastMessage.contains(rawQuery.trim(), ignoreCase = true) ||
                    chat.username.removePrefix("@").contains(term, ignoreCase = true)
            }
            val contactMatches = if (term.isBlank()) contacts else contacts.filter { contact ->
                contact.displayName.contains(rawQuery.trim(), ignoreCase = true) ||
                    contact.username.removePrefix("@").contains(term, ignoreCase = true) ||
                    contact.phone.contains(term, ignoreCase = true)
            }
            SearchIndexResult(
                allChats = chats,
                chats = chatMatches,
                snailContacts = contactMatches.filter { it.isRegistered },
                inviteContacts = contactMatches.filterNot { it.isRegistered },
            )
        }
    }.flowOn(Dispatchers.Default).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchIndexResult(
            allChats = toChatUiStates(DemoMessagingRepository.getConversations()),
            chats = toChatUiStates(DemoMessagingRepository.getConversations()),
            snailContacts = emptyList(),
            inviteContacts = emptyList(),
        ),
    )

    val uiState: StateFlow<SearchAndContactUiState> = combine(
        _query,
        indexedResults,
        contactSyncManager.contacts,
        contactSyncManager.isSyncing,
    ) { currentQuery, results, contacts, syncing ->
        SearchAndContactUiState(
            query = currentQuery,
            chats = results.allChats,
            matchingChats = results.chats,
            matchingSnailContacts = results.snailContacts,
            matchingInviteContacts = results.inviteContacts,
            allSnailContacts = contacts.filter { it.isRegistered },
            allInviteContacts = contacts.filterNot { it.isRegistered },
            isSyncingContacts = syncing,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchAndContactUiState(
            chats = toChatUiStates(DemoMessagingRepository.getConversations()),
        ),
    )

    init {
        viewModelScope.launch { contactSyncManager.syncLocalMockContacts() }
    }

    fun setQuery(value: String) {
        _query.value = value
    }

    fun clearQuery() {
        _query.value = ""
    }

    fun refreshContacts() {
        viewModelScope.launch { contactSyncManager.syncLocalMockContacts() }
    }

}

private fun toChatUiStates(conversations: List<Conversation>): List<ChatUiState> =
    conversations
        .sortedWith(
            compareByDescending<Conversation> { it.isPinned }
                .thenByDescending { it.messages.lastOrNull()?.timestampMs ?: 0L },
        )
        .map { conversation ->
            val last = conversation.messages.lastOrNull()
            val rawPreview = when {
                last == null -> "Start a conversation"
                last.attachmentUri != null && last.text == "Photo" -> "📷 Photo"
                last.attachmentUri != null && last.text == "Video" -> "🎬 Video"
                last.attachmentUri != null -> last.text.ifBlank { "📎 Attachment" }
                else -> last.text
            }
            ChatUiState(
                id = conversation.id,
                title = conversation.peer.displayName,
                username = "@${conversation.peer.username}",
                lastMessage = if (last?.isOutgoing == true) "You: $rawPreview" else rawPreview,
                timestamp = last?.let { formatChatTimestamp(it.timestampMs) }.orEmpty(),
                unreadCount = conversation.unreadCount,
                isOnline = conversation.peer.isOnline,
                avatarSeed = conversation.peer.id,
                isOutgoing = last?.isOutgoing == true,
                isPinned = conversation.isPinned,
                isArchived = conversation.isArchived,
                isMuted = conversation.isMuted,
            )
        }

private fun formatChatTimestamp(timestampMs: Long): String {
    if (timestampMs <= 0L) return ""
    val now = Calendar.getInstance()
    val then = Calendar.getInstance().apply { timeInMillis = timestampMs }
    val sameDay = now.get(Calendar.YEAR) == then.get(Calendar.YEAR) &&
        now.get(Calendar.DAY_OF_YEAR) == then.get(Calendar.DAY_OF_YEAR)
    if (sameDay) return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestampMs))
    now.add(Calendar.DAY_OF_YEAR, -1)
    val yesterday = now.get(Calendar.YEAR) == then.get(Calendar.YEAR) &&
        now.get(Calendar.DAY_OF_YEAR) == then.get(Calendar.DAY_OF_YEAR)
    return if (yesterday) "Yesterday"
    else SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date(timestampMs))
}
