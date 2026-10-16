package com.example.snailmesssenger.core.designsystem

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.animation.core.animateFloatAsState

/** Shared motion primitives for the SNAIL 2026 interaction language. */
object SnailMotion {
    val SpringSpec = spring<Float>(
        dampingRatio = 0.78f,
        stiffness = Spring.StiffnessMediumLow,
    )

    val QuickSpringSpec = spring<Float>(
        dampingRatio = 0.82f,
        stiffness = Spring.StiffnessMedium,
    )
}

@Composable
fun snailSelectionProgress(selected: Boolean): Float {
    val target = if (selected) 1f else 0f
    val progress by animateFloatAsState(
        targetValue = target,
        animationSpec = SnailMotion.SpringSpec,
        label = "snail-selection-progress",
    )
    return progress
}

@Composable
fun rememberSnailMotionKey(value: Any): String = remember(value) { value.toString() }
