package com.example.snailmesssenger

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.SideEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import com.example.snailmesssenger.feature.theme.SnailTheme
import com.example.snailmesssenger.feature.theme.ThemeViewModel
import com.example.snailmesssenger.core.navigation.SnailNavGraph
import com.example.snailmesssenger.data.preferences.UserPreferences
import com.example.snailmesssenger.data.repository.DemoAuthRepository
import com.example.snailmesssenger.data.repository.DemoMessagingRepository
import com.example.snailmesssenger.feature.security.AppLockManager
import com.example.snailmesssenger.feature.security.AppLockScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val context = this@MainActivity
        DemoAuthRepository.initialize(applicationContext)
        DemoMessagingRepository.initialize(applicationContext)
        AppLockManager.initialize(applicationContext)
        setContent {
            val preferences = remember { UserPreferences(context) }
            val themeViewModel: ThemeViewModel = viewModel()
            val themeState by themeViewModel.state.collectAsState()
            val appLockState by AppLockManager.state.collectAsState()

            SideEffect {
                if (appLockState.enabled && appLockState.locked) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }

            SnailTheme(state = themeState) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        SnailNavGraph(preferences = preferences)
                        if (appLockState.enabled && appLockState.locked) {
                            AppLockScreen(
                                state = appLockState,
                                onUnlock = AppLockManager::unlock,
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        AppLockManager.onAppForegrounded()
    }

    override fun onStop() {
        AppLockManager.onAppBackgrounded()
        super.onStop()
    }
}
