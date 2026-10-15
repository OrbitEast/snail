package com.example.snailmesssenger.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.snailmesssenger.feature.theme.NightMode
import com.example.snailmesssenger.feature.theme.ThemeViewModel
import androidx.compose.ui.Modifier
import com.example.snailmesssenger.core.designsystem.SnailSectionHeading
import com.example.snailmesssenger.core.designsystem.SnailSectionRow
import com.example.snailmesssenger.core.designsystem.Spacing
import com.example.snailmesssenger.data.preferences.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    preferences: UserPreferences,
    onBack: () -> Unit,
    onOpenChatSettings: () -> Unit = {},
) {
    val themeViewModel: ThemeViewModel = viewModel()
    val themeState by themeViewModel.state.collectAsState()
    val animationsEnabled by preferences.animationsEnabled.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = { Text("Appearance", style = MaterialTheme.typography.titleLarge) },
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
            SnailSectionHeading(text = "Theme & Colors")
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "App Theme",
                subtitle = themeState.nightMode.label,
                icon = Icons.Default.DarkMode,
                onClick = { showThemeDialog = true },
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "Chat Wallpaper",
                subtitle = "Customize colors and doodles",
                icon = Icons.Default.Palette,
                onClick = onOpenChatSettings,
            )

            Spacer(Modifier.height(Spacing.xl))

            SnailSectionHeading(text = "Motion")
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SnailSectionRow(
                title = "Smooth Animations",
                subtitle = if (animationsEnabled) "Telegram-style transitions enabled" else "Disabled",
                icon = Icons.Default.Tune,
                trailing = {
                    Switch(
                        checked = animationsEnabled,
                        onCheckedChange = { preferences.setAnimationsEnabled(it) },
                    )
                },
            )

            Spacer(Modifier.height(Spacing.xxxl))
        }
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Choose Theme") },
            text = {
                Column {
                    NightMode.entries.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Spacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = themeState.nightMode == option,
                                onClick = {
                                    themeViewModel.setNightMode(option)
                                    showThemeDialog = false
                                },
                            )
                            Spacer(Modifier.width(Spacing.sm))
                            Text(
                                text = option.label,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) { Text("Close") }
            },
        )
    }
}
