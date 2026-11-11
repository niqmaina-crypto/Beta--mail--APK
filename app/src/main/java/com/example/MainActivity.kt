package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.screens.ComposeScreen
import com.example.ui.screens.EmailDetailScreen
import com.example.ui.screens.InboxScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SecurityAndSettingsScreen
import com.example.ui.theme.BetaMailTheme
import com.example.ui.viewmodel.BetaMailViewModel
import com.example.ui.viewmodel.ScreenDestination
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: BetaMailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.appThemeMode.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(Unit) {
                viewModel.snackbarMessage.collectLatest { msg ->
                    snackbarHostState.showSnackbar(msg)
                }
            }

            BetaMailTheme(themeMode = themeMode) {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        val isTablet = maxWidth >= 768.dp

                        if (isTablet && currentScreen != ScreenDestination.COMPOSE && currentScreen != ScreenDestination.SETTINGS_SECURITY) {
                            // Two-pane List-Detail layout on tablets and large screens
                            Row(modifier = Modifier.fillMaxSize()) {
                                Box(modifier = Modifier.width(380.dp)) {
                                    InboxScreen(viewModel = viewModel)
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize()
                                ) {
                                    val selectedId by viewModel.selectedEmailId.collectAsState()
                                    if (selectedId != null) {
                                        EmailDetailScreen(viewModel = viewModel)
                                    } else {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Select a conversation to read",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // Standard single-screen mobile navigation
                            Crossfade(
                                targetState = currentScreen,
                                label = "screen_transition"
                            ) { screen ->
                                when (screen) {
                                    ScreenDestination.INBOX -> InboxScreen(viewModel = viewModel)
                                    ScreenDestination.EMAIL_DETAIL -> EmailDetailScreen(viewModel = viewModel)
                                    ScreenDestination.COMPOSE -> ComposeScreen(viewModel = viewModel)
                                    ScreenDestination.SEARCH -> SearchScreen(viewModel = viewModel)
                                    ScreenDestination.SETTINGS_SECURITY -> SecurityAndSettingsScreen(viewModel = viewModel)
                                    ScreenDestination.TRASH -> InboxScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
