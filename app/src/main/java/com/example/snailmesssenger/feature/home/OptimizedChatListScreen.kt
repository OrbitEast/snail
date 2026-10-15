package com.example.snailmesssenger.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val SearchMatchBlue = Color(0xFF2481CC)

@Composable
fun OptimizedChatListScreen(
    onOpenMenu: () -> Unit,
    onOpenChat: (String) -> Unit,
    onNewChat: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchAndContactViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var searchActive by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val showFloatingHeader by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 8 }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            SnailSearchTopBar(
                searchActive = searchActive,
                query = state.query,
                onOpenMenu = onOpenMenu,
                onActivateSearch = { searchActive = true },
                onQueryChange = viewModel::setQuery,
                onCloseSearch = {
                    searchActive = false
                    viewModel.clearQuery()
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewChat,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
            ) {
                Icon(Icons.Default.Edit, contentDescription = "New chat")
            }
        },
    ) { padding ->
        val showContactResults = searchActive && state.query.isNotBlank()
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            if (!showContactResults) {
                if (state.chats.isEmpty()) {
                    item(key = "empty-chats") {
                        Text(
                            "No conversations yet",
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    items(items = state.chats, key = { chat -> "chat-${chat.id}" }) { chat ->
                        ChatSearchResultRow(
                            chat = chat,
                            query = "",
                            onClick = { onOpenChat(chat.id) },
                        )
                    }
                }
            } else {
                if (state.matchingChats.isNotEmpty()) {
                    item(key = "header-matching-chats") {
                        SectionHeader("CHATS", showFloatingHeader)
                    }
                    items(state.matchingChats, key = { "search-chat-${it.id}" }) { chat ->
                        ChatSearchResultRow(chat = chat, query = state.query, onClick = { onOpenChat(chat.id) })
                    }
                }
                if (state.matchingSnailContacts.isNotEmpty()) {
                    item(key = "header-snail-contacts") { SectionHeader("CONTACTS ON SNAIL", showFloatingHeader) }
                    items(state.matchingSnailContacts, key = { "snail-contact-${it.id}" }) { contact ->
                        ContactRowItem(
                            contact = contact,
                            query = state.query,
                            onOpen = { onOpenChat(it.id) },
                        )
                        Divider(modifier = Modifier.padding(start = 78.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
                    }
                }
                if (state.matchingInviteContacts.isNotEmpty()) {
                    item(key = "header-invite-contacts") { SectionHeader("INVITE TO SNAIL", showFloatingHeader) }
                    items(state.matchingInviteContacts, key = { "invite-contact-${it.id}" }) { contact ->
                        ContactRowItem(contact = contact, query = state.query)
                        Divider(modifier = Modifier.padding(start = 78.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
                    }
                }
                if (state.matchingChats.isEmpty() && state.matchingSnailContacts.isEmpty() && state.matchingInviteContacts.isEmpty()) {
                    item(key = "empty-search") {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text("No results found", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text("Try a name, message, or @username.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, scrolled: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(if (scrolled) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f) else MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, color = SearchMatchBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
    }
}

@Composable
private fun ChatSearchResultRow(
    chat: ChatUiState,
    query: String,
    onClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.size(54.dp)) {
                Box(
                    modifier = Modifier.size(54.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        chat.title.split(" ").filter(String::isNotBlank).take(2).joinToString("") { it.first().uppercase() },
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                if (chat.isOnline) {
                    Box(
                        modifier = Modifier.align(Alignment.BottomEnd).size(14.dp)
                            .clip(CircleShape).background(MaterialTheme.colorScheme.surface).padding(2.dp)
                            .clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HighlightedSearchText(
                        text = chat.title,
                        query = query,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(chat.timestamp, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(3.dp))
                HighlightedSearchText(
                    text = chat.lastMessage,
                    query = query,
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    maxLines = 1,
                )
                if (query.isNotBlank()) {
                    HighlightedSearchText(
                        text = chat.username,
                        query = query,
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        maxLines = 1,
                    )
                }
            }
        }
        Divider(modifier = Modifier.padding(start = 82.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
    }
}

@Composable
fun HighlightedSearchText(
    text: String,
    query: String,
    style: androidx.compose.ui.text.TextStyle,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
) {
    androidx.compose.material3.Text(
        text = highlightedText(text, query),
        modifier = modifier,
        style = style,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}

private fun highlightedText(text: String, query: String): AnnotatedString {
    val term = query.trim().removePrefix("@")
    if (term.isBlank()) return AnnotatedString(text)
    return buildAnnotatedString {
        var cursor = 0
        while (cursor < text.length) {
            val index = text.indexOf(term, cursor, ignoreCase = true)
            if (index < 0) {
                append(text.substring(cursor))
                break
            }
            if (index > cursor) append(text.substring(cursor, index))
            pushStyle(SpanStyle(color = SearchMatchBlue, fontWeight = FontWeight.Bold))
            append(text.substring(index, index + term.length))
            pop()
            cursor = index + term.length
        }
    }
}
