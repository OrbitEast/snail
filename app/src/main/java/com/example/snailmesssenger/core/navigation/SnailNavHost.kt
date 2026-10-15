package com.example.snailmesssenger.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.snailmesssenger.data.preferences.UserPreferences

/**
 * Backwards-compatible entry point. SnailNavGraph is the single source of navigation wiring.
 */
@Composable
fun SnailNavHost(
    navController: NavHostController = rememberNavController(),
    preferences: UserPreferences,
) {
    SnailNavGraph(navController = navController, preferences = preferences)
}
