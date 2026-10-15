package com.example.snailmesssenger.feature.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snailmesssenger.core.designsystem.SnailLogo
import com.example.snailmesssenger.data.repository.DemoAuthRepository
import kotlinx.coroutines.launch

private val TelegramBlue = Color(0xFF2481CC)
private val LoginHint = Color(0xFF8A96A3)
private val LoginBorder = Color(0xFFE0E0E0)

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onCreateAccount: () -> Unit,
    onBackToWelcome: () -> Unit,
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showOptional by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val optionalGap by animateDpAsState(
        targetValue = if (showOptional) 16.dp else 0.dp,
        animationSpec = spring(stiffness = 500f),
        label = "optionalDetailsGap",
    )

    fun submit() {
        usernameError = if (username.isBlank()) "Username is required." else null
        passwordError = if (password.isBlank()) "Password is required." else null
        generalError = null
        if (usernameError != null || passwordError != null) return

        scope.launch {
            isLoading = true
            try {
                // Optional contact details are profile hints only; local auth remains username + password.
                DemoAuthRepository.login(username.trim(), password)
                onLoginSuccess()
            } catch (error: Exception) {
                generalError = error.message ?: "Couldn't log in. Check your details and try again."
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { if (!isLoading) submit() },
                containerColor = TelegramBlue,
                contentColor = Color.White,
                shape = androidx.compose.foundation.shape.CircleShape,
                modifier = Modifier
                    .padding(bottom = 12.dp, end = 4.dp)
                    .size(58.dp),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Log in",
                        modifier = Modifier.size(25.dp),
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBackToWelcome, modifier = Modifier.size(44.dp)) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            Spacer(Modifier.height(22.dp))
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = TelegramBlue.copy(alpha = 0.08f),
                modifier = Modifier.size(112.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    SnailLogo(size = 88.dp)
                }
            }
            Spacer(Modifier.height(22.dp))
            Text(
                text = "Welcome to Snail",
                fontSize = 25.sp,
                lineHeight = 31.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Please enter your username and password to log in.",
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = LoginHint,
                modifier = Modifier.fillMaxWidth(0.92f),
            )

            Spacer(Modifier.height(32.dp))
            LoginInput(
                value = username,
                onValueChange = { username = it; usernameError = null; generalError = null },
                label = "Username",
                placeholder = "Enter username",
                leadingIcon = { Icon(Icons.Default.PersonOutline, contentDescription = null) },
                error = usernameError,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            )
            Spacer(Modifier.height(16.dp))
            LoginInput(
                value = password,
                onValueChange = { password = it; passwordError = null; generalError = null },
                label = "Password",
                placeholder = "Enter password",
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                error = passwordError,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardAction = { submit() },
                trailingContent = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showPassword) "Hide password" else "Show password",
                            tint = LoginHint,
                        )
                    }
                },
            )

            Spacer(Modifier.height(12.dp))
            TextButton(
                onClick = { showOptional = !showOptional },
                modifier = Modifier.align(Alignment.Start),
            ) {
                Icon(
                    imageVector = if (showOptional) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TelegramBlue,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (showOptional) "Hide optional details" else "Optional details",
                    color = TelegramBlue,
                    fontWeight = FontWeight.Medium,
                )
            }

            AnimatedVisibility(visible = showOptional) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(spring(stiffness = 500f)),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    LoginInput(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email address (optional)",
                        placeholder = "name@example.com",
                        leadingIcon = { Icon(Icons.Default.MailOutline, contentDescription = null) },
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                    )
                    LoginInput(
                        value = phone,
                        onValueChange = { phone = it },
                        label = "Phone number (optional)",
                        placeholder = "Phone number",
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done,
                        singleLine = true,
                    )
                }
            }
            Spacer(Modifier.height(optionalGap))

            AnimatedVisibility(visible = generalError != null) {
                Text(
                    text = generalError.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )
            }

            Spacer(Modifier.height(18.dp))
            TextButton(onClick = onCreateAccount, modifier = Modifier.padding(bottom = 78.dp)) {
                Text(
                    text = "New to Snail? Create an account",
                    color = TelegramBlue,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun LoginInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: @Composable () -> Unit,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardAction: (() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder, color = LoginHint) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingContent,
        singleLine = singleLine,
        isError = error != null,
        supportingText = if (error != null) ({ Text(error) }) else null,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(onDone = { keyboardAction?.invoke() }),
        shape = MaterialTheme.shapes.large,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TelegramBlue,
            unfocusedBorderColor = LoginBorder,
            focusedLabelColor = TelegramBlue,
            cursorColor = TelegramBlue,
        ),
    )
}
