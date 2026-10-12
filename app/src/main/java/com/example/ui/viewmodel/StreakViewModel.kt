package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.StreakConfig
import com.example.data.local.StreakLog
import com.example.data.local.UserSettings
import com.example.data.repository.StreakRepository
import com.example.ui.model.Milestone
import com.example.ui.model.MilestoneCatalog
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StreakViewModel(private val repository: StreakRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(StreakUiState())
    val uiState: StateFlow<StreakUiState> = _uiState.asStateFlow()

    private var latestConfig = StreakConfig()
    private var latestLogs = emptyList<StreakLog>()
    private var latestSettings = UserSettings()

    init {
        viewModelScope.launch {
            repository.initializeIfNeeded()
        }

        // Combine flows from Room
        viewModelScope.launch {
            combine(
                repository.configFlow,
                repository.logsFlow,
                repository.settingsFlow
            ) { config, logs, settings ->
                Triple(config, logs, settings)
            }.collect { (config, logs, settings) ->
                latestConfig = config
                latestLogs = logs
                latestSettings = settings
                updateCalculatedState()
            }
        }

        // Timer ticking every second for the live streak counter
        viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                updateCalculatedState()
            }
        }
    }

    private fun updateCalculatedState() {
        val now = System.currentTimeMillis()
        val streakDurationMs = (now - latestConfig.currentStreakStartTimestamp).coerceAtLeast(0L)

        val totalSeconds = streakDurationMs / 1000
        val days = totalSeconds / (24 * 3600)
        val hours = (totalSeconds % (24 * 3600)) / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        val currentStreakDays = streakDurationMs.toDouble() / (1000.0 * 60 * 60 * 24)
        val longestDurationMs = maxOf(latestConfig.longestStreakDurationMs, streakDurationMs)
        val longestDays = longestDurationMs.toDouble() / (1000.0 * 60 * 60 * 24)

        val longestFormattedDays = longestDurationMs / (1000 * 3600 * 24)
        val longestFormattedHours = (longestDurationMs / (1000 * 3600)) % 24
        val longestStreakFormatted = "${longestFormattedDays}d ${longestFormattedHours}h"

        val todayDate = StreakRepository.getTodayDateString()
        val isCheckedInToday = latestConfig.lastCheckInDate == todayDate

        // Milestone calculations
        val unlockedIds = mutableSetOf<String>()
        var nextM: Milestone? = null
        var nextProg = 1f

        for (milestone in MilestoneCatalog.ALL_MILESTONES) {
            if (currentStreakDays >= milestone.targetDays) {
                unlockedIds.add(milestone.id)
            } else if (nextM == null) {
                nextM = milestone
                val prevTarget = MilestoneCatalog.ALL_MILESTONES
                    .filter { it.targetDays < milestone.targetDays }
                    .maxByOrNull { it.targetDays }?.targetDays ?: 0.0
                val progressRange = milestone.targetDays - prevTarget
                val currentProgress = (currentStreakDays - prevTarget).coerceAtLeast(0.0)
                nextProg = if (progressRange > 0) {
                    (currentProgress / progressRange).toFloat().coerceIn(0f, 1f)
                } else 0f
            }
        }

        // Stats from logs
        val checkInsCount = latestLogs.count { it.eventType == "CHECK_IN" }
        val resetsCount = latestLogs.count { it.eventType == "RESET" }
        val urgesResistedCount = latestLogs.count { it.eventType == "URGE_RESISTED" }

        // Trigger analysis
        val triggerMap = latestLogs
            .filter { it.eventType == "RESET" && it.triggerReason.isNotBlank() }
            .groupBy { it.triggerReason }
            .mapValues { it.value.size }

        // Total clean days (sum of current streak + past streaks before resets)
        val pastStreaksDurationMs = latestLogs
            .filter { it.eventType == "RESET" }
            .sumOf { it.durationMs }
        val totalCleanDays = (pastStreaksDurationMs + streakDurationMs).toDouble() / (1000.0 * 60 * 60 * 24)

        val startDateFormatted = formatTimestampToDate(latestConfig.currentStreakStartTimestamp)

        _uiState.value = _uiState.value.copy(
            currentStreakDurationMs = streakDurationMs,
            days = days,
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            longestStreakDays = longestDays,
            longestStreakFormatted = longestStreakFormatted,
            totalCleanDays = totalCleanDays,
            totalCheckIns = checkInsCount,
            totalResets = resetsCount,
            totalUrgesResisted = urgesResistedCount,
            currentMotto = latestConfig.motto,
            isCheckedInToday = isCheckedInToday,
            streakStartDateFormatted = startDateFormatted,
            allLogs = latestLogs,
            unlockedMilestoneIds = unlockedIds,
            nextMilestone = nextM,
            nextMilestoneProgress = nextProg,
            settings = latestSettings,
            triggerBreakdown = triggerMap,
            currentStreakStartTimestamp = latestConfig.currentStreakStartTimestamp
        )
    }

    fun checkIn(moodRating: Int, note: String) {
        viewModelScope.launch {
            repository.checkInToday(moodRating, note)
        }
    }

    fun resetStreak(triggerReason: String, note: String) {
        viewModelScope.launch {
            repository.resetStreak(triggerReason, note)
        }
    }

    fun logUrgeResisted(strategyUsed: String, note: String) {
        viewModelScope.launch {
            repository.logUrgeResisted(strategyUsed, note)
        }
    }

    fun updateMotto(newMotto: String) {
        viewModelScope.launch {
            repository.updateMotto(newMotto)
        }
    }

    fun updateThemeMode(mode: String) {
        viewModelScope.launch {
            repository.updateSettings(latestSettings.copy(themeMode = mode))
        }
    }

    fun toggleDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSettings(latestSettings.copy(dynamicColorEnabled = enabled))
        }
    }

    fun setAppLock(enabled: Boolean, pin: String) {
        viewModelScope.launch {
            repository.updateSettings(
                latestSettings.copy(
                    appLockEnabled = enabled,
                    pinCode = if (enabled) pin else ""
                )
            )
            _uiState.value = _uiState.value.copy(isAppLocked = false)
        }
    }

    fun unlockApp(pin: String): Boolean {
        if (!latestSettings.appLockEnabled || pin == latestSettings.pinCode) {
            _uiState.value = _uiState.value.copy(isAppLocked = false)
            return true
        }
        return false
    }

    fun lockApp() {
        if (latestSettings.appLockEnabled && latestSettings.pinCode.isNotBlank()) {
            _uiState.value = _uiState.value.copy(isAppLocked = true)
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
        }
    }

    private fun formatTimestampToDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
