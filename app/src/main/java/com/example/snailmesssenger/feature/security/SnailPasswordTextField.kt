package com.example.snailmesssenger.feature.security

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snailmesssenger.core.designsystem.SnailLogo

private val SnailBlue = Color(0xFF2481CC)
private val Hint = Color(0xFF8A96A3)
private val Border = Color(0xFFE0E0E0)

@Composable
fun SnailPasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    passwordVisible: Boolean,
    onToggleVisibility: () -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
) {
    val retract = value.isNotEmpty() && !passwordVisible
    val scale = Animatable(if (retract) 0.72f else 1f)
    val alpha = Animatable(if (retract) 0.82f else 1f)

    LaunchedEffect(retract) {
        scale.animateTo(
            if (retract) 0.72f else 1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        )
        alpha.animateTo(if (retract) 0.82f else 1f, spring(stiffness = Spring.StiffnessMedium))
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SnailLogo(
            size = 62.dp,
            modifier = Modifier
                .height(62.dp)
                .scale(scale.value)
                .alpha(alpha.value),
        )
        Text(
            text = if (retract) "Snail is hiding its eyes" else "Snail is watching",
            color = Hint,
            fontSize = 11.sp,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            placeholder = { Text("At least 6 characters", color = Hint) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = Hint,
                    )
                }
            },
            singleLine = true,
            isError = error != null,
            supportingText = if (error != null) ({ Text(error) }) else null,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = androidx.compose.material3.MaterialTheme.shapes.large,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SnailBlue,
                unfocusedBorderColor = Border,
                focusedLabelColor = SnailBlue,
                cursorColor = SnailBlue,
            ),
        )
    }
}
