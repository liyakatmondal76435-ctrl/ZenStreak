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
import androidx.compose.foundation.layout.Box
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.repository.StreakRepository
import com.example.ui.components.FloatingPillBottomBar
import com.example.ui.components.NavigationTab
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.AppLockScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LogIncidentDialog
import com.example.ui.screens.MindfulPauseDialog
import com.example.ui.screens.SetBaselineDialog
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.IronWillTheme
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

            IronWillTheme(
                themeMode = uiState.settings.themeMode,
                dynamicColor = uiState.settings.dynamicColorEnabled
            ) {
                IronWillApp(viewModel = streakViewModel)
            }
        }
    }
}

@Composable
fun IronWillApp(viewModel: StreakViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentTab by remember { mutableStateOf(NavigationTab.HOME) }

    var showLogDialog by remember { mutableStateOf(false) }
    var showBaselineDialog by remember { mutableStateOf(false) }
    var showMindfulPauseDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    if (currentTab != NavigationTab.HOME) {
        BackHandler {
            currentTab = NavigationTab.HOME
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
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                FloatingPillBottomBar(
                    selectedTab = currentTab,
                    onTabSelected = { currentTab = it },
                    onPlusClick = { showLogDialog = true }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                    label = "tabAnim"
                ) { tab ->
                    when (tab) {
                        NavigationTab.HOME -> HomeScreen(
                            uiState = uiState,
                            onTriggerSelectedForActiveLog = { id, trigger ->
                                viewModel.updateActiveLogTrigger(id, trigger)
                            },
                            onTimeUpdatedForActiveLog = { id, hour, minute ->
                                viewModel.updateActiveLogTime(id, hour, minute)
                            },
                            onDeleteActiveLog = { id ->
                                viewModel.deleteLog(id)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Entry deleted from records.")
                                }
                            },
                            onDateShift = { days ->
                                viewModel.shiftSelectedDate(days)
                            }
                        )
                        NavigationTab.ANALYTICS -> AnalyticsScreen(
                            uiState = uiState,
                            onSetBaselineClick = { showBaselineDialog = true },
                            onMindfulPauseClick = { showMindfulPauseDialog = true }
                        )
                        NavigationTab.SETTINGS -> SettingsScreen(
                            settings = uiState.settings,
                            onUpdateThemeMode = { viewModel.updateThemeMode(it) },
                            onToggleDynamicColor = { viewModel.toggleDynamicColor(it) },
                            onSetAppLock = { enabled, pin -> viewModel.setAppLock(enabled, pin) },
                            onResetAllData = {
                                viewModel.resetAllData()
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("All records cleared.")
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Modal: Log Incident or Urge
    if (showLogDialog) {
        LogIncidentDialog(
            onDismiss = { showLogDialog = false },
            onLogRelapse = { trigger, note ->
                viewModel.logRelapse(trigger = trigger, note = note)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Relapse logged. Starting fresh with resolve.")
                }
            },
            onLogResisted = { trigger, note ->
                viewModel.logUrgeResisted(trigger = trigger, note = note)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Victory! Urge resisted.")
                }
            }
        )
    }

    // Modal: Set Baseline
    if (showBaselineDialog) {
        SetBaselineDialog(
            initialBaseline = uiState.baselinePerDay,
            initialTargetDays = uiState.targetStreakDays,
            onDismiss = { showBaselineDialog = false },
            onSave = { baseline, targetDays ->
                viewModel.setBaseline(baseline)
                viewModel.setTargetGoal(targetDays)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Baseline and streak target updated!")
                }
            }
        )
    }

    // Modal: Mindful Pause (4-7-8 Breathing & Urge Surfing)
    if (showMindfulPauseDialog) {
        MindfulPauseDialog(
            onDismiss = { showMindfulPauseDialog = false },
            onUrgeOvercome = { strategy ->
                viewModel.logUrgeResisted(trigger = "Mindful Pause", note = strategy)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Urge overcome! Recorded in history.")
                }
            }
        )
    }
}
