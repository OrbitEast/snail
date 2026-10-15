package com.example.snailmesssenger.core.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Original SNAIL Messenger mark.
 * A stylised snail drawn with a single continuous body path and a spiral shell.
 * Completely vector — no raster assets, no resemblance to any existing brand mark.
 */
@Composable
fun SnailLogo(
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    shellColor: Color = MaterialTheme.colorScheme.primary,
    showBackground: Boolean = true,
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        if (showBackground) {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(backgroundColor),
            )
        }
        Canvas(modifier = Modifier.size(size * 0.68f)) {
            drawSnail(this, shellColor)
        }
    }
}

private fun drawSnail(drawScope: DrawScope, color: Color) {
    with(drawScope) {
        val w = size.width
        val h = size.height

        // --- Shell: a filled circle with an inner spiral ring ---
        val shellCenter = Offset(w * 0.5f, h * 0.46f)
        val shellRadius = w * 0.28f
        drawCircle(
            color = color,
            radius = shellRadius,
            center = shellCenter,
        )
        // Spiral ring inside the shell
        val spiralPath = Path().apply {
            val cx = shellCenter.x
            val cy = shellCenter.y
            val r = shellRadius * 0.52f
            // Approximate an Archimedean spiral with line segments
            var angle = 0f
            val maxAngle = 3.2f * Math.PI.toFloat()
            val steps = 60
            val step = maxAngle / steps
            var px = cx + r * kotlin.math.cos(angle)
            var py = cy + r * 0.85f * kotlin.math.sin(angle)
            moveTo(px, py)
            for (i in 1..steps) {
                angle += step
                val rr = r * (1f - i.toFloat() / steps * 0.55f)
                px = cx + rr * kotlin.math.cos(angle)
                py = cy + rr * 0.85f * kotlin.math.sin(angle)
                lineTo(px, py)
            }
        }
        drawPath(
            path = spiralPath,
            color = Color.White.copy(alpha = 0.85f),
            style = Stroke(width = w * 0.03f),
        )

        // --- Body: a tapered, slightly S-curved path from tail to head ---
        val bodyPath = Path().apply {
            // Tail (left, low)
            moveTo(w * 0.08f, h * 0.78f)
            // Underside sweeping right toward head
            cubicTo(
                w * 0.15f, h * 0.92f,
                w * 0.55f, h * 0.92f,
                w * 0.72f, h * 0.82f,
            )
            // Rounding up to the head (right)
            cubicTo(
                w * 0.86f, h * 0.74f,
                w * 0.9f, h * 0.62f,
                w * 0.88f, h * 0.52f,
            )
            // Top of head curving back left
            cubicTo(
                w * 0.84f, h * 0.40f,
                w * 0.72f, h * 0.34f,
                w * 0.58f, h * 0.34f,
            )
            // Back along the neck/shoulder into the shell
            cubicTo(
                w * 0.48f, h * 0.34f,
                w * 0.40f, h * 0.44f,
                w * 0.34f, h * 0.52f,
            )
            // Bottom of shell back to tail
            cubicTo(
                w * 0.28f, h * 0.66f,
                w * 0.18f, h * 0.74f,
                w * 0.08f, h * 0.78f,
            )
            close()
        }
        drawPath(path = bodyPath, color = color)

        // --- Eye stalks (two short strokes from the head top) ---
        val stalkColor = color
        val stalkStroke = Stroke(width = w * 0.035f)
        drawLine(
            color = stalkColor,
            start = Offset(w * 0.82f, h * 0.36f),
            end = Offset(w * 0.80f, h * 0.22f),
            strokeWidth = w * 0.035f,
        )
        drawLine(
            color = stalkColor,
            start = Offset(w * 0.88f, h * 0.34f),
            end = Offset(w * 0.88f, h * 0.20f),
            strokeWidth = w * 0.035f,
        )
        // Eye tips
        drawCircle(color = stalkColor, radius = w * 0.025f, center = Offset(w * 0.80f, h * 0.20f))
        drawCircle(color = stalkColor, radius = w * 0.025f, center = Offset(w * 0.88f, h * 0.18f))
    }
}
