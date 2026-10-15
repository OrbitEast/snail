package com.example.snailmesssenger.feature.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.snailmesssenger.data.repository.DemoAuthRepository
import com.example.snailmesssenger.feature.security.SecurityViewModel
import com.example.snailmesssenger.feature.security.SnailPasswordTextField
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(
    onLoginSuccess: (twoStepRequired: Boolean) -> Unit,
    onCreateAccount: () -> Unit,
    onBackToWelcome: () -> Unit,
    securityViewModel: SecurityViewModel = viewModel(),
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
    val optionalGap by animateDpAsState(if (showOptional) 16.dp else 0.dp, spring(stiffness = 500f), label = "optionalGap")

    fun submit() {
        usernameError = if (username.trim().length < 3) "Username must contain at least 3 characters." else null
        passwordError = if (password.length < 6) "Password must contain at least 6 characters." else null
        generalError = null
        securityViewModel.updateUsername(username)
        securityViewModel.updatePassword(password, showPassword)
        if (!securityViewModel.validateCredentials()) return
        scope.launch {
            isLoading = true
            try {
                DemoAuthRepository.login(username.trim(), password)
                securityViewModel.refreshTwoStepState()
                onLoginSuccess(securityViewModel.uiState.value.twoStepEnabled)
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
            FloatingActionButton(onClick = { if (!isLoading) submit() }, containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White, modifier = Modifier.padding(bottom = 12.dp, end = 4.dp).size(58.dp)) {
                if (isLoading) CircularProgressIndicator(Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp) else Icon(Icons.Default.ArrowForward, "Log in", Modifier.size(25.dp))
            }
        },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding().windowInsetsPadding(WindowInsets.navigationBars).padding(innerPadding).verticalScroll(rememberScrollState()).padding(horizontal = 26.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth().padding(top = 4.dp)) { IconButton(onClick = onBackToWelcome, modifier = Modifier.size(44.dp)) { Icon(Icons.Default.ArrowBack, "Back") } }
            Spacer(Modifier.height(30.dp))
            Text("Welcome to Snail", fontSize = 25.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Please enter your username and password to log in.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth(0.92f))
            Spacer(Modifier.height(30.dp))
            ValidatedLoginInput(username, { username = it; usernameError = null; generalError = null }, "Username", "At least 3 characters", usernameError, username.length >= 3, { Icon(Icons.Default.PersonOutline, null) }, KeyboardType.Text, ImeAction.Next)
            Spacer(Modifier.height(16.dp))
            SnailPasswordTextField(value = password, onValueChange = { password = it; passwordError = null; generalError = null; securityViewModel.updatePassword(it, showPassword) }, passwordVisible = showPassword, onToggleVisibility = { showPassword = !showPassword; securityViewModel.setPasswordVisibility(showPassword) }, error = passwordError)
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = { showOptional = !showOptional }, Modifier.align(Alignment.Start)) { Icon(if (showOptional) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null, tint = MaterialTheme.colorScheme.primary); Text(if (showOptional) "Hide optional details" else "Optional details", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 8.dp)) }
            AnimatedVisibility(showOptional) { Column(Modifier.fillMaxWidth().animateContentSize(spring(stiffness = 500f)), verticalArrangement = Arrangement.spacedBy(16.dp)) { SimpleInput(email, { email = it }, "Email address (optional)", "name@example.com", Icons.Default.MailOutline, KeyboardType.Email); SimpleInput(phone, { phone = it }, "Phone number (optional)", "Phone number", Icons.Default.Phone, KeyboardType.Phone) } }
            Spacer(Modifier.height(optionalGap))
            generalError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth()) }
            Spacer(Modifier.height(18.dp))
            TextButton(onClick = onCreateAccount, Modifier.padding(bottom = 78.dp)) { Text("New to Snail? Create an account", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium) }
        }
    }
}

@Composable
private fun ValidatedLoginInput(value: String, onValueChange: (String) -> Unit, label: String, placeholder: String, error: String?, valid: Boolean, leadingIcon: @Composable () -> Unit, keyboardType: KeyboardType, imeAction: ImeAction) {
    OutlinedTextField(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(), label = { Text(label) }, placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) }, leadingIcon = leadingIcon, trailingIcon = if (value.isNotEmpty() && valid) ({ Text("✓", color = Color(0xFF35B66F), fontWeight = FontWeight.Bold) }) else null, isError = error != null, supportingText = error?.let { { Text(it) } }, singleLine = true, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant, focusedLabelColor = MaterialTheme.colorScheme.primary, cursorColor = MaterialTheme.colorScheme.primary))
}

@Composable
private fun SimpleInput(value: String, onValueChange: (String) -> Unit, label: String, placeholder: String, icon: androidx.compose.ui.graphics.vector.ImageVector, keyboardType: KeyboardType) {
    OutlinedTextField(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(), label = { Text(label) }, placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) }, leadingIcon = { Icon(icon, null) }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType), singleLine = true)
}
