package com.example.snailmessenger.core.navigation

import androidx.compose.animation.core.spring
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
import com.example.snailmessenger.data.preferences.UserPreferences
import com.example.snailmessenger.feature.auth.LoginScreen
import com.example.snailmessenger.feature.auth.RegisterScreen
import com.example.snailmessenger.feature.auth.WelcomeScreen
import com.example.snailmessenger.feature.chat.ChatDetailScreen
import com.example.snailmessenger.feature.home.FindPeopleScreen
import com.example.snailmessenger.feature.home.MainTabsScreen
import com.example.snailmessenger.feature.profile.ProfileScreen
import com.example.snailmessenger.feature.settings.AppearanceScreen
import com.example.snailmessenger.feature.settings.DataStorageScreen
import com.example.snailmessenger.feature.settings.EditProfileScreen
import com.example.snailmessenger.feature.settings.NotificationsScreen
import com.example.snailmessenger.feature.settings.PrivacySecurityScreen
import com.example.snailmessenger.feature.settings.SettingsScreen

private const val TRANSITION_MS = 280

@Composable
fun SnailNavHost(
    navController: NavHostController = rememberNavController(),
    preferences: UserPreferences,
) {
    val animationsEnabled by preferences.animationsEnabled.collectAsState()

    val enterTransition = if (animationsEnabled) {
        fadeIn(tween(TRANSITION_MS)) + slideInHorizontally(
            animationSpec = spring(stiffness = 420f),
            initialOffsetX = { it / 3 },
        )
    } else fadeIn(tween(0))

    val exitTransition = if (animationsEnabled) {
        fadeOut(tween(TRANSITION_MS)) + slideOutHorizontally(
            animationSpec = spring(stiffness = 420f),
            targetOffsetX = { -it / 3 },
        )
    } else fadeOut(tween(0))

    val popEnterTransition = if (animationsEnabled) {
        fadeIn(tween(TRANSITION_MS)) + slideInHorizontally(
            animationSpec = spring(stiffness = 420f),
            initialOffsetX = { -it / 3 },
        )
    } else fadeIn(tween(0))

    val popExitTransition = if (animationsEnabled) {
        fadeOut(tween(TRANSITION_MS)) + slideOutHorizontally(
            animationSpec = spring(stiffness = 420f),
            targetOffsetX = { it / 3 },
        )
    } else fadeOut(tween(0))

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
                onGetStarted = { navController.navigate(SnailRoute.REGISTER) { launchSingleTop = true } },
                onHaveAccount = { navController.navigate(SnailRoute.LOGIN) { launchSingleTop = true } },
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
                onCreateAccount = { navController.navigate(SnailRoute.REGISTER) { launchSingleTop = true } },
                onBackToWelcome = { navController.popBackStack() },
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
                onOpenChat = { id -> navController.navigate(SnailRoute.chat(id)) { launchSingleTop = true } },
                onFindPeople = { navController.navigate(SnailRoute.FIND_PEOPLE) { launchSingleTop = true } },
                onOpenPrivacySecurity = { navController.navigate(SnailRoute.PRIVACY_SECURITY) { launchSingleTop = true } },
                onOpenNotifications = { navController.navigate(SnailRoute.NOTIFICATIONS) { launchSingleTop = true } },
                onOpenDataStorage = { navController.navigate(SnailRoute.DATA_STORAGE) { launchSingleTop = true } },
                onOpenAppearance = { navController.navigate(SnailRoute.APPEARANCE) { launchSingleTop = true } },
                onOpenEditProfile = { navController.navigate(SnailRoute.EDIT_PROFILE) { launchSingleTop = true } },
                onLogout = {
                    navController.navigate(SnailRoute.WELCOME) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(SnailRoute.CHAT) { entry ->
            val conversationId = entry.arguments?.getString("conversationId") ?: return@composable
            ChatDetailScreen(conversationId = conversationId, onBack = { navController.popBackStack() })
        }
        composable(SnailRoute.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onOpenSettings = { navController.navigate(SnailRoute.SETTINGS) { launchSingleTop = true } },
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
                onOpenProfile = { navController.navigate(SnailRoute.PROFILE) { launchSingleTop = true } },
                onOpenPrivacySecurity = { navController.navigate(SnailRoute.PRIVACY_SECURITY) { launchSingleTop = true } },
                onOpenNotifications = { navController.navigate(SnailRoute.NOTIFICATIONS) { launchSingleTop = true } },
                onOpenDataStorage = { navController.navigate(SnailRoute.DATA_STORAGE) { launchSingleTop = true } },
                onOpenAppearance = { navController.navigate(SnailRoute.APPEARANCE) { launchSingleTop = true } },
                onOpenEditProfile = { navController.navigate(SnailRoute.EDIT_PROFILE) { launchSingleTop = true } },
                onLogout = {
                    navController.navigate(SnailRoute.WELCOME) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(SnailRoute.PRIVACY_SECURITY) { PrivacySecurityScreen(onBack = { navController.popBackStack() }) }
        composable(SnailRoute.NOTIFICATIONS) { NotificationsScreen(preferences = preferences, onBack = { navController.popBackStack() }) }
        composable(SnailRoute.DATA_STORAGE) { DataStorageScreen(onBack = { navController.popBackStack() }) }
        composable(SnailRoute.APPEARANCE) { AppearanceScreen(preferences = preferences, onBack = { navController.popBackStack() }) }
        composable(SnailRoute.EDIT_PROFILE) { EditProfileScreen(onBack = { navController.popBackStack() }) }
        composable(SnailRoute.FIND_PEOPLE) {
            FindPeopleScreen(
                onBack = { navController.popBackStack() },
                onStartConversation = { peerId ->
                    val conversation = com.example.snailmessenger.data.repository.DemoMessagingRepository.startConversationWith(peerId)
                    navController.popBackStack()
                    navController.navigate(SnailRoute.chat(conversation.id)) { launchSingleTop = true }
                },
            )
        }
    }
}
