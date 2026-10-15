package com.example.snailmesssenger.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.snailmesssenger.core.designsystem.SnailSectionHeading
import com.example.snailmesssenger.core.designsystem.SnailSectionRow
import com.example.snailmesssenger.core.designsystem.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataStorageScreen(
    onBack: () -> Unit,
) {
    var showClearedDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = { Text("Data and Storage", style = MaterialTheme.typography.titleLarge) },
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
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            SnailSectionHeading(text = "Storage Usage")
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "Storage Usage",
                subtitle = "Cache: 12.4 MB • Messages: 1.2 MB",
                icon = Icons.Default.Storage,
            )
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "Clear Local Cache",
                subtitle = "Free up temporary space",
                icon = Icons.Default.CleaningServices,
                onClick = { showClearedDialog = true },
            )

            Spacer(Modifier.height(Spacing.xl))

            SnailSectionHeading(text = "Network Usage")
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "Network Usage Stats",
                subtitle = "Text messaging only (minimal data)",
                icon = Icons.Default.Wifi,
            )

            Spacer(Modifier.height(Spacing.xxxl))
        }
    }

    if (showClearedDialog) {
        AlertDialog(
            onDismissRequest = { showClearedDialog = false },
            title = { Text("Cache Cleared") },
            text = { Text("Local temporary cache has been successfully cleaned.") },
            confirmButton = {
                TextButton(onClick = { showClearedDialog = false }) { Text("OK") }
            },
        )
    }
}
