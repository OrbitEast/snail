package com.example.snailmesssenger.feature.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snailmesssenger.data.model.ConversationKind
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/** UI model for a single conversation in the main inbox. */
@Immutable
data class ChatUiState(
    val id: String,
    val title: String,
    val username: String,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false,
    val avatarSeed: String = title,
    val isOutgoing: Boolean = false,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isMuted: Boolean = false,
    val kind: ConversationKind = ConversationKind.DIRECT,
    val memberCount: Int = 1,
)

@Immutable
data class ChatListUiState(
    val searchQuery: String = "",
    val chats: List<ChatUiState> = emptyList(),
)

class ChatViewModel : ViewModel() {
    private val allChats = MutableStateFlow(mockChats())
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<ChatListUiState> = combine(
        allChats,
        _searchQuery,
    ) { chats, query ->
        val normalized = query.trim()
        ChatListUiState(
            searchQuery = query,
            chats = if (normalized.isBlank()) chats else chats.filter {
                it.title.contains(normalized, ignoreCase = true) ||
                    it.username.contains(normalized, ignoreCase = true) ||
                    it.lastMessage.contains(normalized, ignoreCase = true)
            },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ChatListUiState(chats = allChats.value),
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    private fun mockChats(): List<ChatUiState> = listOf(
        ChatUiState("maya", "Maya Kapoor", "@mayakapoor", "Are we still meeting at 7?", "20:14", 2, true, "Maya", false),
        ChatUiState("arjun", "Arjun Mehta", "@arjunm", "The new build looks much better.", "19:48", 0, true, "Arjun", true),
        ChatUiState("riya", "Riya Sharma", "@riya", "Thanks! I will send it tonight.", "18:32", 4, false, "Riya", false),
        ChatUiState("dev-team", "Dev Notes", "@devnotes", "You: Pushed the navigation update", "17:06", 0, false, "Dev", true),
        ChatUiState("kabir", "Kabir Singh", "@kabir", "That animation is smooth 🔥", "16:40", 1, true, "Kabir", false),
        ChatUiState("ananya", "Ananya Das", "@ananyadas", "See you tomorrow!", "15:21", 0, false, "Ananya", false),
        ChatUiState("rohan", "Rohan Verma", "@rohanv", "Can you review the login screen?", "14:05", 3, true, "Rohan", false),
        ChatUiState("snail-news", "Snail Updates", "@snailupdates", "Privacy-first messaging, built for you.", "Mon", 0, false, "Snail", false),
        ChatUiState("neha", "Neha Jain", "@nehaj", "Got it 👍", "Sun", 0, false, "Neha", false),
        ChatUiState("vikram", "Vikram Roy", "@vikramroy", "Let's ship this version.", "Sat", 0, true, "Vikram", false),
    )
}
