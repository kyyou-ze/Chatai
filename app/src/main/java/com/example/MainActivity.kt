package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.ui.screens.MainChatScreen
import com.example.ui.screens.SetupApiKeyScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ChatViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val currentScreen by viewModel.currentScreen.collectAsState()

                // Observe Toast events
                LaunchedEffect(Unit) {
                    viewModel.toastEvent.collect { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    Crossfade(
                        targetState = currentScreen,
                        animationSpec = tween(400),
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            AppScreen.SPLASH -> {
                                SplashScreen()
                            }
                            AppScreen.SETUP_KEY -> {
                                SetupApiKeyScreen(
                                    onSaveAndContinue = { apiKey ->
                                        viewModel.saveApiKey(apiKey) { /* handled in VM */ }
                                    }
                                )
                            }
                            AppScreen.MAIN_CHAT -> {
                                MainChatScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

