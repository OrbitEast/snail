package com.example.snailmesssenger.feature.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.snailmesssenger.core.designsystem.SnailLogo
import com.example.snailmesssenger.core.designsystem.SnailPrimaryButton
import com.example.snailmesssenger.core.designsystem.SnailTextField
import com.example.snailmesssenger.core.designsystem.Spacing
import com.example.snailmesssenger.data.repository.DemoAuthRepository
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onCreateAccount: () -> Unit,
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val repository = remember { DemoAuthRepository }

    fun validate(): Boolean {
        usernameError = if (username.isBlank()) "Username is required." else null
        passwordError = if (password.isBlank()) "Password is required." else null
        return usernameError == null && passwordError == null
    }

    fun submit() {
        generalError = null
        if (!validate()) return
        scope.launch {
            isLoading = true
            try {
                repository.login(username, password)
                isLoading = false
                onLoginSuccess()
            } catch (e: Exception) {
                isLoading = false
                generalError = e.message ?: "Login failed. Please try again."
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.pageHorizontal),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(Spacing.xxxl))

        SnailLogo(size = 64.dp)
        Spacer(Modifier.height(Spacing.xl))
        Text(
            text = "Welcome back",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = "Log in to your local demo account.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(Spacing.xxxl))

        SnailTextField(
            value = username,
            onValueChange = { username = it; usernameError = null; generalError = null },
            label = "Username",
            placeholder = "Your username",
            isError = usernameError != null,
            errorMessage = usernameError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(onNext = {}),
        )

        Spacer(Modifier.height(Spacing.md))

        SnailTextField(
            value = password,
            onValueChange = { password = it; passwordError = null; generalError = null },
            label = "Password",
            placeholder = "Your password",
            isError = passwordError != null,
            errorMessage = passwordError,
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { submit() }),
            trailingIcon = {
                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
        )

        if (generalError != null) {
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = generalError!!,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        Spacer(Modifier.height(Spacing.xxl))

        SnailPrimaryButton(
            text = "Log In",
            onClick = { submit() },
            isLoading = isLoading,
        )

        Spacer(Modifier.height(Spacing.lg))

        TextButton(onClick = onCreateAccount) {
            Text(
                text = "Don't have an account? Create one",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(Modifier.height(Spacing.xxxl))
    }
}
