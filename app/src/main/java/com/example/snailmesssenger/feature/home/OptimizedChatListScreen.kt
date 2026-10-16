package com.example.snailmesssenger.feature.home

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
        var selectedFolder by remember { mutableStateOf("All") }
        val showContactResults = searchActive
        val folderFilteredChats = remember(selectedFolder, state.chats) {
            when (selectedFolder) {
                "Unread" -> state.chats.filter { it.unreadCount > 0 }
                "Personal" -> state.chats.filter { !it.title.contains("Group", ignoreCase = true) }
                "Work" -> state.chats.filter { it.title.contains("Dev", ignoreCase = true) || it.title.contains("Work", ignoreCase = true) || it.title.contains("Team", ignoreCase = true) }
                else -> state.chats
            }
        }
        val visibleChats = if (state.query.isBlank()) folderFilteredChats else state.matchingChats
        val visibleSnailContacts = if (state.query.isBlank()) state.allSnailContacts else state.matchingSnailContacts
        val visibleInviteContacts = if (state.query.isBlank()) state.allInviteContacts else state.matchingInviteContacts

        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (!searchActive) {
                Row(
                    modifier = Modifier.fillMaxWidth().animateContentSize().horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf("All", "Personal", "Work", "Unread").forEach { folder ->
                        val isSelected = selectedFolder == folder
                        val unreadInFolder = when(folder) {
                            "Unread" -> state.chats.sumOf { it.unreadCount }
                            else -> 0
                        }
                        Surface(
                            onClick = { selectedFolder = folder },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Text(
                                    text = folder,
                                    fontSize = 13.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                )
                                if (unreadInFolder > 0) {
                                    Box(
                                        modifier = Modifier.clip(CircleShape)
                                            .background(if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary)
                                            .padding(horizontal = 6.dp, vertical = 1.dp),
                                    ) {
                                        Text(
                                            text = unreadInFolder.toString(),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 112.dp),
            ) {
            if (!showContactResults) {
                if (visibleChats.isEmpty()) {
                    item(key = "empty-chats") {
                        Text(
                            "No conversations yet",
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    items(items = visibleChats, key = { chat -> "chat-${chat.id}" }) { chat ->
                        ChatSearchResultRow(
                            chat = chat,
                            query = "",
                            onClick = { onOpenChat(chat.id) },
                        )
                    }
                }
            } else {
                if (visibleChats.isNotEmpty()) {
                    item(key = "header-matching-chats") {
                        SectionHeader("CHATS", showFloatingHeader)
                    }
                    items(visibleChats, key = { "search-chat-${it.id}" }) { chat ->
                        ChatSearchResultRow(chat = chat, query = state.query, onClick = { onOpenChat(chat.id) })
                    }
                }
                if (visibleSnailContacts.isNotEmpty()) {
                    item(key = "header-snail-contacts") { SectionHeader("CONTACTS ON SNAIL", showFloatingHeader) }
                    visibleSnailContacts.groupBy { it.displayName.firstOrNull()?.uppercaseChar() ?: '#' }
                        .toSortedMap().forEach { (letter, contactsForLetter) ->
                            item(key = "snail-letter-$letter") { LetterHeader(letter.toString()) }
                            items(contactsForLetter, key = { "snail-contact-${it.id}" }) { contact ->
                                ContactRowItem(
                                    contact = contact,
                                    query = state.query,
                                    onOpen = { onOpenChat(it.id) },
                                )
                                HorizontalDivider(modifier = Modifier.padding(start = 78.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
                            }
                        }
                }
                if (visibleInviteContacts.isNotEmpty()) {
                    item(key = "header-invite-contacts") { SectionHeader("INVITE TO SNAIL", showFloatingHeader) }
                    visibleInviteContacts.groupBy { it.displayName.firstOrNull()?.uppercaseChar() ?: '#' }
                        .toSortedMap().forEach { (letter, contactsForLetter) ->
                            item(key = "invite-letter-$letter") { LetterHeader(letter.toString()) }
                            items(contactsForLetter, key = { "invite-contact-${it.id}" }) { contact ->
                                ContactRowItem(contact = contact, query = state.query)
                                HorizontalDivider(modifier = Modifier.padding(start = 78.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
                            }
                        }
                }
                if (visibleChats.isEmpty() && visibleSnailContacts.isEmpty() && visibleInviteContacts.isEmpty() && state.query.isNotBlank()) {
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
}

@Composable
private fun LetterHeader(letter: String) {
    Text(
        text = letter,
        modifier = Modifier.fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.32f))
            .padding(horizontal = 16.dp, vertical = 7.dp),
        color = SearchMatchBlue,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
    )
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
                            .clip(CircleShape).background(Color(0xFF2DBE60)),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HighlightedSearchText(
                        text = chat.lastMessage,
                        query = query,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (chat.unreadCount > 0) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (chat.unreadCount > 0) FontWeight.Medium else FontWeight.Normal,
                        ),
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                    )
                    if (chat.unreadCount > 0) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier.clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 7.dp, vertical = 3.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = chat.unreadCount.coerceAtMost(99).let { if (it == 99) "99+" else it.toString() },
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            )
                        }
                    }
                }
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
        HorizontalDivider(modifier = Modifier.padding(start = 82.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
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
