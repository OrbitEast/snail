package com.example.snailmesssenger.core.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Lightweight liquid-glass treatment: layered translucency, a reflective edge,
 * and a soft elevation shadow. It avoids off-screen blur to remain smooth on
 * lower-end Android hardware.
 */
fun Modifier.snailGlass(
    shape: Shape = RoundedCornerShape(24.dp),
    baseColor: Color,
    elevation: Dp = 16.dp,
): Modifier = this
    .shadow(elevation = elevation, shape = shape, clip = false)
    .clip(shape)
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                baseColor.copy(alpha = 0.96f),
                baseColor.copy(alpha = 0.84f),
                baseColor.copy(alpha = 0.91f),
            ),
        ),
        shape = shape,
    )
    .border(
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.60f),
                    Color.White.copy(alpha = 0.14f),
                    Color.White.copy(alpha = 0.38f),
                ),
            ),
        ),
        shape = shape,
    )
