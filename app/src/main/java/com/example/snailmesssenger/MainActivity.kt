package com.example.snailmesssenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import com.example.snailmesssenger.feature.theme.SnailTheme
import com.example.snailmesssenger.feature.theme.ThemeViewModel
import com.example.snailmesssenger.core.navigation.SnailNavGraph
import com.example.snailmesssenger.data.preferences.UserPreferences
import com.example.snailmesssenger.data.repository.DemoAuthRepository
import com.example.snailmesssenger.data.repository.DemoMessagingRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val context = this@MainActivity
        DemoAuthRepository.initialize(applicationContext)
        DemoMessagingRepository.initialize(applicationContext)
        setContent {
            val preferences = remember { UserPreferences(context) }
            val themeViewModel: ThemeViewModel = viewModel()
            val themeState by themeViewModel.state.collectAsState()

            SnailTheme(state = themeState) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SnailNavGraph(preferences = preferences)
                }
            }
        }
    }
}
