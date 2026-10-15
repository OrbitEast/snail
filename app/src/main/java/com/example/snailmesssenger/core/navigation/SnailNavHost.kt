package com.example.snailmesssenger.core.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.snailmesssenger.core.designsystem.LocalThemePreference
import com.example.snailmesssenger.data.preferences.UserPreferences
import com.example.snailmesssenger.feature.auth.LoginScreen
import com.example.snailmesssenger.feature.auth.RegisterScreen
import com.example.snailmesssenger.feature.auth.WelcomeScreen
import com.example.snailmesssenger.feature.chat.ChatScreen
import com.example.snailmesssenger.feature.home.FindPeopleScreen
import com.example.snailmesssenger.feature.home.HomeScreen
import com.example.snailmesssenger.feature.home.MainTabsScreen
import com.example.snailmesssenger.feature.profile.ProfileScreen
import com.example.snailmesssenger.feature.settings.AppearanceScreen
import com.example.snailmesssenger.feature.settings.DataStorageScreen
import com.example.snailmesssenger.feature.settings.EditProfileScreen
import com.example.snailmesssenger.feature.settings.NotificationsScreen
import com.example.snailmesssenger.feature.settings.PrivacySecurityScreen
import com.example.snailmesssenger.feature.settings.SettingsScreen

private const val TRANSITION_MS = 240

/**
 * Root navigation graph.
 * Wraps destinations in the animation duration spec (200–280ms).
 */
@Composable
fun SnailNavHost(
    navController: NavHostController = rememberNavController(),
    preferences: UserPreferences,
) {
    val animationsEnabled by preferences.animationsEnabled.collectAsState()
    val themePreference by preferences.theme.collectAsState()

    val enterTransition = if (animationsEnabled) {
        fadeIn(animationSpec = tween(TRANSITION_MS)) +
            slideInHorizontally(initialOffsetX = { it / 4 })
    } else {
        fadeIn(animationSpec = tween(0))
    }

    val exitTransition = if (animationsEnabled) {
        fadeOut(animationSpec = tween(TRANSITION_MS)) +
            slideOutHorizontally(targetOffsetX = { -it / 4 })
    } else {
        fadeOut(animationSpec = tween(0))
    }

    val popEnterTransition = if (animationsEnabled) {
        fadeIn(animationSpec = tween(TRANSITION_MS)) +
            slideInHorizontally(initialOffsetX = { -it / 4 })
    } else {
        fadeIn(animationSpec = tween(0))
    }

    val popExitTransition = if (animationsEnabled) {
        fadeOut(animationSpec = tween(TRANSITION_MS)) +
            slideOutHorizontally(targetOffsetX = { it / 4 })
    } else {
        fadeOut(animationSpec = tween(0))
    }

    NavHost(
        navController = navController,
        startDestination = SnailRoute.WELCOME,
        enterTransition = { enterTransition },
        exitTransition = { exitTransition },
        popEnterTransition = { popEnterTransition },
        popExitTransition = { popExitTransition },
    ) {
        composable(SnailRoute.WELCOME) {
            WelcomeScreen(
                onGetStarted = {
                    navController.navigate(SnailRoute.REGISTER) {
                        launchSingleTop = true
                    }
                },
                onHaveAccount = {
                    navController.navigate(SnailRoute.LOGIN) {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(SnailRoute.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(SnailRoute.HOME) {
                        popUpTo(SnailRoute.WELCOME) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onCreateAccount = {
                    navController.navigate(SnailRoute.REGISTER) {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(SnailRoute.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(SnailRoute.HOME) {
                        popUpTo(SnailRoute.WELCOME) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBackToLogin = {
                    navController.navigate(SnailRoute.LOGIN) {
                        popUpTo(SnailRoute.WELCOME) { inclusive = false }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(SnailRoute.HOME) {
            MainTabsScreen(
                preferences = preferences,
                onOpenChat = { id ->
                    navController.navigate(SnailRoute.chat(id)) { launchSingleTop = true }
                },
                onFindPeople = {
                    navController.navigate(SnailRoute.FIND_PEOPLE) { launchSingleTop = true }
                },
                onOpenPrivacySecurity = {
                    navController.navigate(SnailRoute.PRIVACY_SECURITY) { launchSingleTop = true }
                },
                onOpenNotifications = {
                    navController.navigate(SnailRoute.NOTIFICATIONS) { launchSingleTop = true }
                },
                onOpenDataStorage = {
                    navController.navigate(SnailRoute.DATA_STORAGE) { launchSingleTop = true }
                },
                onOpenAppearance = {
                    navController.navigate(SnailRoute.APPEARANCE) { launchSingleTop = true }
                },
                onOpenEditProfile = {
                    navController.navigate(SnailRoute.EDIT_PROFILE) { launchSingleTop = true }
                },
                onLogout = {
                    navController.navigate(SnailRoute.WELCOME) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(SnailRoute.CHAT) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: return@composable
            ChatScreen(
                conversationId = conversationId,
                onBack = { navController.popBackStack() },
            )
        }
        composable(SnailRoute.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onOpenSettings = {
                    navController.navigate(SnailRoute.SETTINGS) { launchSingleTop = true }
                },
                onLogout = {
                    navController.navigate(SnailRoute.WELCOME) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(SnailRoute.SETTINGS) {
            SettingsScreen(
                preferences = preferences,
                onBack = { navController.popBackStack() },
                onOpenProfile = {
                    navController.navigate(SnailRoute.PROFILE) { launchSingleTop = true }
                },
                onOpenPrivacySecurity = {
                    navController.navigate(SnailRoute.PRIVACY_SECURITY) { launchSingleTop = true }
                },
                onOpenNotifications = {
                    navController.navigate(SnailRoute.NOTIFICATIONS) { launchSingleTop = true }
                },
                onOpenDataStorage = {
                    navController.navigate(SnailRoute.DATA_STORAGE) { launchSingleTop = true }
                },
                onOpenAppearance = {
                    navController.navigate(SnailRoute.APPEARANCE) { launchSingleTop = true }
                },
                onOpenEditProfile = {
                    navController.navigate(SnailRoute.EDIT_PROFILE) { launchSingleTop = true }
                },
                onLogout = {
                    navController.navigate(SnailRoute.WELCOME) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(SnailRoute.PRIVACY_SECURITY) {
            PrivacySecurityScreen(onBack = { navController.popBackStack() })
        }
        composable(SnailRoute.NOTIFICATIONS) {
            NotificationsScreen(preferences = preferences, onBack = { navController.popBackStack() })
        }
        composable(SnailRoute.DATA_STORAGE) {
            DataStorageScreen(onBack = { navController.popBackStack() })
        }
        composable(SnailRoute.APPEARANCE) {
            AppearanceScreen(preferences = preferences, onBack = { navController.popBackStack() })
        }
        composable(SnailRoute.EDIT_PROFILE) {
            EditProfileScreen(onBack = { navController.popBackStack() })
        }
        composable(SnailRoute.FIND_PEOPLE) {
            FindPeopleScreen(
                onBack = { navController.popBackStack() },
                onStartConversation = { peerId ->
                    val conversation = com.example.snailmesssenger.data.repository.DemoMessagingRepository
                        .startConversationWith(peerId)
                    navController.popBackStack()
                    navController.navigate(SnailRoute.chat(conversation.id)) { launchSingleTop = true }
                },
            )
        }
    }
}
