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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.snailmesssenger.core.designsystem.SnailSectionHeading
import com.example.snailmesssenger.core.designsystem.SnailSectionRow
import com.example.snailmesssenger.core.designsystem.Spacing
import com.example.snailmesssenger.feature.security.SecurityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySecurityScreen(onBack: () -> Unit, securityViewModel: SecurityViewModel = viewModel()) {
    var lastSeenOption by remember { mutableStateOf("My Contacts") }
    var photoOption by remember { mutableStateOf("Everybody") }
    var passcodeEnabled by remember { mutableStateOf(false) }
    var unsupportedFeature by remember { mutableStateOf<String?>(null) }
    var showPinDialog by remember { mutableStateOf(false) }
    var pin by remember { mutableStateOf("") }
    val securityState by securityViewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        topBar = { TopAppBar(title = { Text("Privacy and Security") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }) },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            SnailSectionHeading("Privacy")
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(title = "Last Seen & Online", subtitle = "$lastSeenOption · local preview", icon = Icons.Default.Visibility, onClick = { lastSeenOption = if (lastSeenOption == "My Contacts") "Nobody" else "My Contacts" })
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(title = "Profile Photo", subtitle = "$photoOption · local preview", icon = Icons.Default.Security, onClick = { photoOption = if (photoOption == "Everybody") "My Contacts" else "Everybody" })
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(title = "Blocked Users", subtitle = "0 blocked", icon = Icons.Default.Block, onClick = { unsupportedFeature = "Blocked Users" })

            Spacer(Modifier.height(Spacing.xl))
            SnailSectionHeading("Security")
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(title = "Passcode Lock", subtitle = if (passcodeEnabled) "Enabled" else "Disabled", icon = Icons.Default.Lock, onClick = { passcodeEnabled = !passcodeEnabled })
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))

            androidx.compose.foundation.layout.Row(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 14.dp)) {
                Column(Modifier.weight(1f)) {
                    Text("Two-Step Verification", style = MaterialTheme.typography.bodyLarge)
                    Text(if (securityState.twoStepEnabled) "Enabled · local PIN" else "Protect login with a local PIN", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = securityState.twoStepEnabled, onCheckedChange = { enabled ->
                    if (enabled) showPinDialog = true else securityViewModel.disableTwoStep()
                })
            }
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(title = "Active Sessions", subtitle = "Mobile · Desktop · Web", icon = Icons.Default.Devices, onClick = { unsupportedFeature = "Active Sessions" })
            Spacer(Modifier.height(Spacing.xxxl))
        }
    }

    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false; pin = "" },
            title = { Text("Set Two-Step PIN") },
            text = {
                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 8 && it.all(Char::isDigit)) pin = it },
                    label = { Text("4–8 digit PIN") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = pin.isNotEmpty() && pin.length < 4,
                )
            },
            confirmButton = { TextButton(onClick = { if (securityViewModel.setTwoStepPin(pin)) { showPinDialog = false; pin = "" } }) { Text("Enable") } },
            dismissButton = { TextButton(onClick = { showPinDialog = false; pin = "" }) { Text("Cancel") } },
        )
    }

    unsupportedFeature?.let { feature ->
        AlertDialog(onDismissRequest = { unsupportedFeature = null }, title = { Text("$feature unavailable") }, text = { Text("This control needs a real account service. It remains local-only in the prototype.") }, confirmButton = { TextButton(onClick = { unsupportedFeature = null }) { Text("OK") } })
    }
}
