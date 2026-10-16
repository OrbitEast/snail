package com.example.snailmesssenger.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.HorizontalDivider
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
import com.example.snailmesssenger.data.model.MockData
import com.example.snailmesssenger.data.model.User
import com.example.snailmesssenger.data.repository.DemoMessagingRepository
import com.example.snailmesssenger.feature.security.AppLockManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySecurityScreen(onBack: () -> Unit, securityViewModel: SecurityViewModel = viewModel()) {
    var lastSeenOption by remember { mutableStateOf("My Contacts") }
    var photoOption by remember { mutableStateOf("Everybody") }
    val appLockState by AppLockManager.state.collectAsState()
    var showAppLockPinDialog by remember { mutableStateOf(false) }
    var appLockPin by remember { mutableStateOf("") }
    var appLockDialogMode by remember { mutableStateOf("enable") }
    var unsupportedFeature by remember { mutableStateOf<String?>(null) }
    var showBlockedUsersDialog by remember { mutableStateOf(false) }
    val blockedUserIds by DemoMessagingRepository.blockedUserIdsFlow.collectAsState()
    val knownUsers = remember(blockedUserIds) {
        (MockData.discoverableContacts + MockData.conversations.map { it.peer }).distinctBy { it.id }
    }
    var showPinDialog by remember { mutableStateOf(false) }
    var pin by remember { mutableStateOf("") }
    val securityState by securityViewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        topBar = { TopAppBar(title = { Text("Privacy and Security") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }) },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            SnailSectionHeading("Privacy")
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(title = "Last Seen & Online", subtitle = "$lastSeenOption · local preview", icon = Icons.Default.Visibility, onClick = { lastSeenOption = if (lastSeenOption == "My Contacts") "Nobody" else "My Contacts" })
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(title = "Profile Photo", subtitle = "$photoOption · local preview", icon = Icons.Default.Security, onClick = { photoOption = if (photoOption == "Everybody") "My Contacts" else "Everybody" })
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "Blocked Users",
                subtitle = "${blockedUserIds.size} blocked · stored on this device",
                icon = Icons.Default.Block,
                onClick = { showBlockedUsersDialog = true },
            )

            Spacer(Modifier.height(Spacing.xl))
            SnailSectionHeading("Security")
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "Passcode Lock",
                subtitle = if (appLockState.enabled) "Enabled · auto-locks after 30 seconds away" else "Disabled · set a 4–8 digit PIN",
                icon = Icons.Default.Lock,
                onClick = {
                    AppLockManager.clearMessage()
                    appLockPin = ""
                    appLockDialogMode = if (appLockState.enabled) "disable" else "enable"
                    showAppLockPinDialog = true
                },
            )
            if (appLockState.enabled) {
                SnailSectionRow(
                    title = "Lock App Now",
                    subtitle = "Require passcode immediately",
                    icon = Icons.Default.Lock,
                    onClick = { AppLockManager.lockNow() },
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))

            androidx.compose.foundation.layout.Row(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 14.dp)) {
                Column(Modifier.weight(1f)) {
                    Text("Two-Step Verification", style = MaterialTheme.typography.bodyLarge)
                    Text(if (securityState.twoStepEnabled) "Enabled · local PIN" else "Protect login with a local PIN", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = securityState.twoStepEnabled, onCheckedChange = { enabled ->
                    if (enabled) showPinDialog = true else securityViewModel.disableTwoStep()
                })
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(title = "Active Sessions", subtitle = "Mobile · Desktop · Web", icon = Icons.Default.Devices, onClick = { unsupportedFeature = "Active Sessions" })
            Spacer(Modifier.height(Spacing.xxxl))
        }
    }

    if (showBlockedUsersDialog) {
        val blockedProfiles: List<User> = blockedUserIds.map { blockedId ->
            knownUsers.firstOrNull { it.id == blockedId }
                ?: User(
                    id = blockedId,
                    username = "unknown",
                    displayName = blockedId,
                    bio = "",
                    isOnline = false,
                )
        }
        AlertDialog(
            onDismissRequest = { showBlockedUsersDialog = false },
            title = { Text("Blocked users") },
            text = {
                if (blockedProfiles.isEmpty()) {
                    Text(
                        "No blocked users. Block a contact from their profile in a conversation.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().height(340.dp),
                    ) {
                        items(blockedProfiles, key = { it.id }) { blocked ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(blocked.displayName, color = MaterialTheme.colorScheme.onSurface)
                                    Text(
                                        if (blocked.username == "unknown") "Local user ID" else "@${blocked.username}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                TextButton(onClick = { DemoMessagingRepository.unblockUser(blocked.id) }) {
                                    Text("Unblock")
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBlockedUsersDialog = false }) { Text("Done") }
            },
        )
    }

    if (showAppLockPinDialog) {
        AlertDialog(
            onDismissRequest = {
                showAppLockPinDialog = false
                appLockPin = ""
                AppLockManager.clearMessage()
            },
            title = { Text(if (appLockDialogMode == "enable") "Set App Passcode" else "Disable App Passcode") },
            text = {
                Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                    Text(
                        if (appLockDialogMode == "enable") {
                            "Choose a 4–8 digit passcode. SNAIL locks when you leave it for 30 seconds or lock it manually."
                        } else {
                            "Enter your current passcode to disable the app lock."
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    OutlinedTextField(
                        value = appLockPin,
                        onValueChange = {
                            if (it.length <= 8 && it.all(Char::isDigit)) {
                                appLockPin = it
                                AppLockManager.clearMessage()
                            }
                        },
                        label = { Text("4–8 digit PIN") },
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        isError = appLockState.message != null,
                    )
                    appLockState.message?.let { error ->
                        Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                    Text(
                        "Only a salted PIN hash is stored. Forgetting it cannot be recovered by the SNAIL prototype.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = appLockPin.length in 4..8,
                    onClick = {
                        val successful = if (appLockDialogMode == "enable") {
                            AppLockManager.enableWithPin(appLockPin)
                        } else {
                            AppLockManager.disableWithPin(appLockPin)
                        }
                        if (successful) {
                            showAppLockPinDialog = false
                            appLockPin = ""
                            AppLockManager.clearMessage()
                        }
                    },
                ) { Text(if (appLockDialogMode == "enable") "Enable lock" else "Disable lock") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAppLockPinDialog = false
                    appLockPin = ""
                    AppLockManager.clearMessage()
                }) { Text("Cancel") }
            },
        )
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
