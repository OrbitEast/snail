package com.example.snailmesssenger.feature.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.snailmesssenger.core.designsystem.SnailLogo

@Composable
fun TwoStepVerificationScreen(
    onVerified: () -> Unit,
    onBack: () -> Unit,
    viewModel: SecurityViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateSecurity()
    var pin by remember { mutableStateOf("") }
    var resetCode by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
        Spacer(Modifier.height(32.dp))
        SnailLogo(size = 104.dp)
        Spacer(Modifier.height(22.dp))
        Text("Two-Step Verification", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(
            "Enter your local security PIN to finish signing in.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
        )
        Spacer(Modifier.height(26.dp))

        OutlinedTextField(
            value = pin,
            onValueChange = { if (it.length <= 8 && it.all(Char::isDigit)) pin = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Security PIN") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation(),
            isError = state.error != null,
        )
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                loading = true
                if (viewModel.verifyPin(pin)) onVerified()
                loading = false
            },
            enabled = pin.length >= 4 && !loading,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (loading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            else Text("Verify")
        }
        TextButton(onClick = viewModel::requestLocalResetCode) { Text("Forgot PIN?") }

        if (state.resetCodeVisible) {
            Spacer(Modifier.height(10.dp))
            Text(
                "Local recovery code generated for this offline demo: ${state.generatedResetCode}",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp,
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = resetCode,
                onValueChange = { if (it.length <= 6 && it.all(Char::isDigit)) resetCode = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Recovery code") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = newPin,
                onValueChange = { if (it.length <= 8 && it.all(Char::isDigit)) newPin = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("New PIN (4–8 digits)") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
            )
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = { if (viewModel.resetWithCode(resetCode, newPin)) onVerified() },
                enabled = resetCode.length == 6 && newPin.length in 4..8,
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Reset PIN and Continue") }
        }
    }
}

@Composable
private fun SecurityViewModel.collectAsStateSecurity(): androidx.compose.runtime.State<SecurityUiState> =
    androidx.compose.runtime.collectAsState(this.uiState)
