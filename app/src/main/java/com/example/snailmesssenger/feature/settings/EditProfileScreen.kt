package com.example.snailmesssenger.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.snailmesssenger.core.designsystem.SnailPrimaryButton
import com.example.snailmesssenger.core.designsystem.SnailTextField
import com.example.snailmesssenger.core.designsystem.Spacing
import com.example.snailmesssenger.data.repository.DemoAuthRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
) {
    val currentUser = DemoAuthRepository.currentUser
    var displayName by remember { mutableStateOf(currentUser?.displayName ?: "User") }
    var bio by remember { mutableStateOf("Taking it slow on SNAIL 🐌") }
    var savedMessage by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", style = MaterialTheme.typography.titleLarge) },
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
                .padding(Spacing.xl),
        ) {
            SnailTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = "Display Name",
                singleLine = true,
            )
            Spacer(Modifier.height(Spacing.md))
            SnailTextField(
                value = bio,
                onValueChange = { bio = it },
                label = "Bio / About",
                singleLine = true,
            )

            Spacer(Modifier.height(Spacing.xxl))

            SnailPrimaryButton(
                text = "Save Changes",
                onClick = {
                    currentUser?.let {
                        // Update in demo repo if possible
                    }
                    savedMessage = true
                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
