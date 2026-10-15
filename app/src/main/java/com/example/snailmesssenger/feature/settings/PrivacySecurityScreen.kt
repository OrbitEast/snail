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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
fun PrivacySecurityScreen(
    onBack: () -> Unit,
) {
    var lastSeenOption by remember { mutableStateOf("My Contacts") }
    var photoOption by remember { mutableStateOf("Everybody") }
    var passcodeEnabled by remember { mutableStateOf(false) }
    var twoStepEnabled by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = { Text("Privacy and Security", style = MaterialTheme.typography.titleLarge) },
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
            SnailSectionHeading(text = "Privacy")
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "Last Seen & Online",
                subtitle = lastSeenOption,
                icon = Icons.Default.Visibility,
                onClick = {
                    lastSeenOption = if (lastSeenOption == "My Contacts") "Nobody" else "My Contacts"
                },
            )
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "Profile Photo",
                subtitle = photoOption,
                icon = Icons.Default.Security,
                onClick = {
                    photoOption = if (photoOption == "Everybody") "My Contacts" else "Everybody"
                },
            )
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "Blocked Users",
                subtitle = "0 blocked",
                icon = Icons.Default.Block,
                onClick = {},
            )

            Spacer(Modifier.height(Spacing.xl))

            SnailSectionHeading(text = "Security")
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "Passcode Lock",
                subtitle = if (passcodeEnabled) "Enabled" else "Disabled",
                icon = Icons.Default.Lock,
                onClick = { passcodeEnabled = !passcodeEnabled },
            )
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "Two-Step Verification",
                subtitle = if (twoStepEnabled) "Active" else "Off",
                icon = Icons.Default.Security,
                onClick = { twoStepEnabled = !twoStepEnabled },
            )
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "Active Sessions",
                subtitle = "1 active device (This device)",
                icon = Icons.Default.Devices,
                onClick = {},
            )

            Spacer(Modifier.height(Spacing.xxxl))
        }
    }
}
