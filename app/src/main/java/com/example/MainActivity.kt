package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.repository.StreakRepository
import com.example.ui.components.ScreenTab
import com.example.ui.components.StreakBottomBar
import com.example.ui.components.StreakTopBar
import com.example.ui.screens.AppLockScreen
import com.example.ui.screens.CheckInDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ResetStreakDialog
import com.example.ui.screens.SettingsDialog
import com.example.ui.screens.SosScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.theme.ZenStreakTheme
import com.example.ui.viewmodel.StreakViewModel
import com.example.ui.viewmodel.StreakViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(applicationContext)
        val repository = StreakRepository(database.streakDao())
        val viewModelFactory = StreakViewModelFactory(repository)

        setContent {
            val streakViewModel: StreakViewModel = viewModel(factory = viewModelFactory)
            val uiState by streakViewModel.uiState.collectAsStateWithLifecycle()

            ZenStreakTheme(
                themeMode = uiState.settings.themeMode,
                dynamicColor = uiState.settings.dynamicColorEnabled
            ) {
                ZenStreakApp(
                    viewModel = streakViewModel
                )
            }
        }
    }
}

@Composable
fun ZenStreakApp(
    viewModel: StreakViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentTab by remember { mutableStateOf(ScreenTab.DASHBOARD) }

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showCheckInDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Handle back button on secondary tabs
    if (currentTab != ScreenTab.DASHBOARD) {
        BackHandler {
            currentTab = ScreenTab.DASHBOARD
        }
    }

    if (uiState.isAppLocked) {
        AppLockScreen(
            onUnlockAttempt = { pin ->
                viewModel.unlockApp(pin)
            }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize().testTag("app_scaffold"),
            topBar = {
                StreakTopBar(
                    title = "ZenStreak",
                    hasAppLock = uiState.settings.appLockEnabled,
                    onLockClick = { viewModel.lockApp() },
                    onSettingsClick = { showSettingsDialog = true }
                )
            },
            bottomBar = {
                StreakBottomBar(
                    selectedTab = currentTab,
                    onTabSelected = { currentTab = it }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                label = "screenTabTransition",
                modifier = Modifier.padding(innerPadding)
            ) { tab ->
                when (tab) {
                    ScreenTab.DASHBOARD -> DashboardScreen(
                        uiState = uiState,
                        onCheckInClick = { showCheckInDialog = true },
                        onResetClick = { showResetDialog = true },
                        onSosClick = { currentTab = ScreenTab.SOS },
                        onEditMottoClick = { showSettingsDialog = true }
                    )
                    ScreenTab.STATS -> StatsScreen(
                        uiState = uiState
                    )
                    ScreenTab.SOS -> SosScreen(
                        onUrgeOvercome = { strategy, note ->
                            viewModel.logUrgeResisted(strategy, note)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Urge conquered! Logged in history.")
                            }
                        }
                    )
                }
            }
        }
    }

    // Daily Check-In Dialog
    if (showCheckInDialog) {
        CheckInDialog(
            onDismiss = { showCheckInDialog = false },
            onConfirm = { moodRating, note ->
                viewModel.checkIn(moodRating, note)
                showCheckInDialog = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Daily victory affirmed!")
                }
            }
        )
    }

    // Reset Streak Dialog
    if (showResetDialog) {
        ResetStreakDialog(
            currentStreakDays = uiState.days,
            onDismiss = { showResetDialog = false },
            onConfirmReset = { trigger, note ->
                viewModel.resetStreak(trigger, note)
                showResetDialog = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Reset recorded. Starting with fresh resolve.")
                }
            }
        )
    }

    // Settings & Privacy Dialog
    if (showSettingsDialog) {
        SettingsDialog(
            settings = uiState.settings,
            currentMotto = uiState.currentMotto,
            onDismiss = { showSettingsDialog = false },
            onUpdateThemeMode = { viewModel.updateThemeMode(it) },
            onToggleDynamicColor = { viewModel.toggleDynamicColor(it) },
            onSetAppLock = { enabled, pin -> viewModel.setAppLock(enabled, pin) },
            onUpdateMotto = { viewModel.updateMotto(it) },
            onResetAllData = {
                viewModel.resetAllData()
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("All data cleared.")
                }
            }
        )
    }
}
