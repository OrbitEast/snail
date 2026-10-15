package com.example.snailmesssenger.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.snailmesssenger.core.designsystem.SnailAvatar
import com.example.snailmesssenger.core.designsystem.Spacing
import com.example.snailmesssenger.data.model.User
import com.example.snailmesssenger.data.repository.DemoMessagingRepository

/**
 * Local mock "Find people" screen.
 * This is NOT a real user directory — it uses mock contacts from MockData
 * to let the user start a demo conversation during development.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindPeopleScreen(
    onBack: () -> Unit,
    onStartConversation: (String) -> Unit,
) {
    val conversations by DemoMessagingRepository.conversationsFlow.collectAsState()
    val contacts = remember(conversations) { conversations.map { it.peer }.distinctBy { it.id } }
    var searchQuery by remember { mutableStateOf("") }
    val filteredContacts = contacts.filter { contact ->
        searchQuery.isBlank() ||
            contact.displayName.contains(searchQuery.trim(), ignoreCase = true) ||
            contact.username.contains(searchQuery.trim().removePrefix("@"), ignoreCase = true)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = { Text("Find people", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Text(
                text = "Find someone to start a chat",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = Spacing.xl, vertical = Spacing.sm),
            )
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.xl, vertical = Spacing.sm),
                singleLine = true,
                label = { Text("Search contacts") },
                placeholder = { Text("@username") },
            )
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (filteredContacts.isEmpty()) {
                    item {
                        Text(
                            text = "No contacts match that search.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(Spacing.xl),
                        )
                    }
                } else {
                    items(filteredContacts, key = { it.id }) { contact ->
                        PersonRow(contact = contact, onClick = { onStartConversation(contact.id) })
                        Divider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            modifier = Modifier.padding(start = 84.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonRow(contact: User, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.xl, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SnailAvatar(
            displayId = contact.id,
            displayName = contact.displayName,
            size = 48,
        )
        Spacer(Modifier.width(Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.displayName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "@${contact.username}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
