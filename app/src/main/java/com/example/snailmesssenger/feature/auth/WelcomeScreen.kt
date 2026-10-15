package com.example.snailmesssenger.feature.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.snailmesssenger.core.designsystem.SnailLogo
import com.example.snailmesssenger.core.designsystem.SnailPrimaryButton
import com.example.snailmesssenger.core.designsystem.SnailSecondaryButton
import com.example.snailmesssenger.core.designsystem.Spacing
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onHaveAccount: () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = Spacing.pageHorizontal),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(400)) + slideInVertically(
                animationSpec = tween(400),
                initialOffsetY = { it / 4 },
            ),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SnailLogo(size = 96.dp)
                Spacer(Modifier.height(Spacing.xxl))
                Text(
                    text = "Snail",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(Spacing.sm))
                Text(
                    text = "Private conversations. Your space.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Spacer(Modifier.weight(1f))

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(600, delayMillis = 200)),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                SnailPrimaryButton(text = "Get Started", onClick = onGetStarted)
                Spacer(Modifier.height(Spacing.md))
                SnailSecondaryButton(text = "I already have an account", onClick = onHaveAccount)
            }
        }

        Spacer(Modifier.height(Spacing.xxxl))
    }
}
