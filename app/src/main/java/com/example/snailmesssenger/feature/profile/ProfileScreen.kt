package com.example.snailmesssenger.feature.profile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.snailmesssenger.core.designsystem.SnailAvatar
import com.example.snailmesssenger.core.designsystem.SnailPrimaryButton
import com.example.snailmesssenger.core.designsystem.SnailSectionHeading
import com.example.snailmesssenger.core.designsystem.SnailSectionRow
import com.example.snailmesssenger.core.designsystem.Spacing
import com.example.snailmesssenger.data.model.User
import com.example.snailmesssenger.data.repository.DemoAuthRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
    onLogout: () -> Unit,
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var user by remember { mutableStateOf(DemoAuthRepository.currentUser) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    AnimatedContent(
        targetState = Unit,
        transitionSpec = {
            (fadeIn(com.example.snailmesssenger.core.designsystem.SnailMotion.SpringSpec) +
                scaleIn(initialScale = 0.97f, animationSpec = com.example.snailmesssenger.core.designsystem.SnailMotion.SpringSpec)) togetherWith
                (fadeOut(com.example.snailmesssenger.core.designsystem.SnailMotion.QuickSpringSpec) +
                    scaleOut(targetScale = 0.97f, animationSpec = com.example.snailmesssenger.core.designsystem.SnailMotion.QuickSpringSpec))
        },
        label = "profile-entry-motion",
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            topBar = {
                TopAppBar(
                    title = { Text("Profile", style = MaterialTheme.typography.titleLarge) },
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
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(Spacing.xxl))
                SnailAvatar(
                    displayId = user?.id ?: "me",
                    displayName = user?.displayName ?: "You",
                    size = 96,
                )
                Spacer(Modifier.height(Spacing.lg))
                Text(
                    text = user?.displayName ?: "You",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = "@${user?.username ?: "you"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (user?.bio?.isNotBlank() == true) {
                    Spacer(Modifier.height(Spacing.sm))
                    Text(
                        text = user!!.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(Modifier.height(Spacing.xxl))
                SnailPrimaryButton(
                    text = "Edit profile",
                    onClick = { showEditDialog = true },
                    modifier = Modifier.padding(horizontal = Spacing.pageHorizontal),
                )

                Spacer(Modifier.height(Spacing.xxxl))
                SnailSectionHeading(text = "Account")
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                SnailSectionRow(
                    title = "Settings",
                    icon = Icons.Default.Settings,
                    onClick = onOpenSettings,
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                SnailSectionRow(
                    title = "Log out",
                    icon = Icons.AutoMirrored.Filled.Logout,
                    onClick = { showLogoutDialog = true },
                    destructive = true,
                )
                Spacer(Modifier.height(Spacing.xxxl))
            }
        }
    }

    if (showEditDialog && user != null) {
        EditProfileDialog(
            user = user!!,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                user = updated
                DemoAuthRepository.updateUser(updated)
                showEditDialog = false
            },
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log out") },
            text = { Text("This will end your local demo session.") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    DemoAuthRepository.logout()
                    onLogout()
                }) { Text("Log out") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun EditProfileDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (User) -> Unit,
) {
    var displayName by remember { mutableStateOf(user.displayName) }
    var bio by remember { mutableStateOf(user.bio) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit profile") },
        text = {
            Column {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Display name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Spacer(Modifier.height(Spacing.md))
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(user.copy(displayName = displayName.trim(), bio = bio.trim()))
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
