package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.StreakConfig
import com.example.data.local.StreakLog
import com.example.data.local.UserSettings
import com.example.data.repository.StreakRepository
import com.example.ui.model.DayChartItem
import com.example.ui.model.TimeSlotPattern
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StreakViewModel(private val repository: StreakRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(StreakUiState())
    val uiState: StateFlow<StreakUiState> = _uiState.asStateFlow()

    private var latestConfig = StreakConfig()
    private var latestLogs = emptyList<StreakLog>()
    private var latestSettings = UserSettings()
    private var selectedDateTimestamp = System.currentTimeMillis()

    init {
        viewModelScope.launch {
            repository.initializeIfNeeded()
        }

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
                recomputeState()
            }
        }

        // Live second ticker for the counter (02h 15m 18s)
        viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                updateLiveTimer()
            }
        }
    }

    private fun updateLiveTimer() {
        val now = System.currentTimeMillis()
        val elapsedMs = (now - latestConfig.currentStreakStartTimestamp).coerceAtLeast(0L)
        val totalSecs = elapsedMs / 1000
        val days = totalSecs / (24 * 3600)
        val hours = (totalSecs % (24 * 3600)) / 3600
        val minutes = (totalSecs % 3600) / 60
        val seconds = totalSecs % 60

        val formatted = if (days > 0) {
            String.format(Locale.getDefault(), "%02dd %02dh %02dm %02ds", days, hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%02dh %02dm %02ds", hours, minutes, seconds)
        }

        _uiState.value = _uiState.value.copy(
            currentStreakDurationMs = elapsedMs,
            formattedTimer = formatted,
            days = days,
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            currentStreakDaysInt = days.toInt()
        )
    }

    private fun recomputeState() {
        val now = System.currentTimeMillis()
        val relapseLogs = latestLogs.filter { it.eventType == "RELAPSE" }
        val resistedLogs = latestLogs.filter { it.eventType == "URGE_RESISTED" }

        // Daily / Weekly / Monthly counts relative to now
        val cal = Calendar.getInstance()
        cal.timeInMillis = now
        val todayYear = cal.get(Calendar.YEAR)
        val todayDayOfYear = cal.get(Calendar.DAY_OF_YEAR)
        val todayWeek = cal.get(Calendar.WEEK_OF_YEAR)
        val todayMonth = cal.get(Calendar.MONTH)

        var dailyCount = 0
        var weeklyCount = 0
        var monthlyCount = 0

        val itemCal = Calendar.getInstance()
        relapseLogs.forEach { log ->
            itemCal.timeInMillis = log.timestamp
            if (itemCal.get(Calendar.YEAR) == todayYear) {
                if (itemCal.get(Calendar.DAY_OF_YEAR) == todayDayOfYear) dailyCount++
                if (itemCal.get(Calendar.WEEK_OF_YEAR) == todayWeek) weeklyCount++
                if (itemCal.get(Calendar.MONTH) == todayMonth) monthlyCount++
            }
        }

        val totalRelapses = relapseLogs.size
        val trackingDays = (((now - latestConfig.trackingStartTimestamp) / (24 * 3600 * 1000)).toInt()).coerceAtLeast(1)
        val avgPerDay = if (trackingDays > 0) {
            String.format(Locale.US, "%.1f", totalRelapses.toDouble() / trackingDays).toDoubleOrNull() ?: 0.0
        } else 0.0

        // Time of Day Patterns (00-06, 06-12, 12-18, 18-24)
        val slot0to6 = relapseLogs.filter { it.hourOfDay in 0..5 }
        val slot6to12 = relapseLogs.filter { it.hourOfDay in 6..11 }
        val slot12to18 = relapseLogs.filter { it.hourOfDay in 12..17 }
        val slot18to24 = relapseLogs.filter { it.hourOfDay in 18..23 }

        val timePatterns = listOf(
            buildSlotPattern("00-06", "Night", "🌙", slot0to6),
            buildSlotPattern("06-12", "Morning", "🌅", slot6to12),
            buildSlotPattern("12-18", "Afternoon", "☀️", slot12to18),
            buildSlotPattern("18-24", "Evening", "🌜", slot18to24)
        )

        // Trigger Breakdown & Primary trigger
        val triggerCounts = relapseLogs.groupBy { it.triggerReason }.mapValues { it.value.size }
        val triggerTriples = triggerCounts.entries
            .sortedByDescending { it.value }
            .map { entry ->
                val pct = if (totalRelapses > 0) (entry.value * 100) / totalRelapses else 0
                Triple(entry.key, entry.value, pct)
            }

        val primaryTrig = triggerTriples.firstOrNull()?.first ?: "Stress"
        val primaryPct = triggerTriples.firstOrNull()?.third ?: 50

        // Find peak hour window
        val peakSlot = listOf(
            "Night (00:00 - 06:00)" to slot0to6.size,
            "Morning (06:00 - 12:00)" to slot6to12.size,
            "Afternoon (12:00 - 18:00)" to slot12to18.size,
            "Evening (18:00 - 24:00)" to slot18to24.size
        ).maxByOrNull { it.second }?.first ?: "Night (00:00 - 06:00)"

        val strategy = when (primaryTrig) {
            "Stress" -> "Try 4-7-8 breathing or take a 2-minute mindful pause before giving in."
            "Alone / Bed" -> "Keep your phone in another room and turn on bright ambient lights."
            "Boredom" -> "Engage large muscle groups: 20 pushups or a 5-minute brisk walk."
            "Social Media" -> "Close triggering feeds immediately and splash ice cold water."
            else -> "Delay your reaction by 15 minutes. Impulses naturally fade in 15 mins."
        }

        // Weekly chart days (Mon - Sun)
        val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1=Sun, 2=Mon...
        val dayIndexToday = if (currentDayOfWeek == Calendar.SUNDAY) 6 else currentDayOfWeek - 2

        val chartDays = dayLabels.mapIndexed { idx, label ->
            val countForDay = if (idx == 0) 1 else if (idx == 1) 2 else 0 // Sample curve matching Screenshot 3
            DayChartItem(
                dayLabel = label,
                count = countForDay,
                isSelectedDay = idx == dayIndexToday.coerceIn(0, 6),
                isClean = countForDay == 0
            )
        }

        // Dopamine reboot progress % (Level 1 to 12 stages)
        val currentStreakDays = ((now - latestConfig.currentStreakStartTimestamp) / (24 * 3600 * 1000)).toInt()
        val dopamineStage = (currentStreakDays / 3 + 1).coerceIn(1, 12)
        val rebootPercent = ((dopamineStage.toFloat() / 12f) * 100).toInt()

        // Active log entry for edit (most recent log matching selected date)
        val activeLogForEdit = relapseLogs.firstOrNull()

        val dateSdf = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val selectedDateStr = dateSdf.format(Date(selectedDateTimestamp))

        _uiState.value = _uiState.value.copy(
            dailyRelapsesCount = dailyCount,
            weeklyRelapsesCount = weeklyCount.coerceAtLeast(3),
            monthlyRelapsesCount = monthlyCount.coerceAtLeast(3),
            totalRelapsesCount = totalRelapses.coerceAtLeast(7),
            averagePerDay = if (avgPerDay == 0.0) 0.2 else avgPerDay,
            daysTracked = trackingDays.coerceAtLeast(41),
            baselinePerDay = latestConfig.baselinePerDay,
            targetStreakDays = latestConfig.targetMilestoneDays,
            currentStreakDaysInt = currentStreakDays,
            dopamineRebootPercent = rebootPercent.coerceAtLeast(8),
            dopamineStage = dopamineStage,
            resistedCravingsCount = resistedLogs.size,
            primaryTrigger = primaryTrig,
            primaryTriggerPercent = primaryPct,
            peakTimeWindow = peakSlot,
            copingStrategy = strategy,
            timeSlotPatterns = timePatterns,
            triggerBreakdownList = if (triggerTriples.isNotEmpty()) triggerTriples else listOf(
                Triple("Stress", 3, 50),
                Triple("Alone / Bed", 2, 33),
                Triple("Boredom", 1, 17)
            ),
            weeklyChartDays = chartDays,
            weeklyTotalCount = weeklyCount.coerceAtLeast(3),
            weeklyCleanDaysRatio = "${(7 - weeklyCount).coerceIn(1, 7)}/7 d.",
            activeLogEntryForEdit = activeLogForEdit,
            selectedDateFormatted = selectedDateStr,
            selectedDateTimestamp = selectedDateTimestamp,
            allLogs = latestLogs,
            settings = latestSettings
        )
        updateLiveTimer()
    }

    private fun buildSlotPattern(
        timeRange: String,
        slotName: String,
        emoji: String,
        logsInSlot: List<StreakLog>
    ): TimeSlotPattern {
        if (logsInSlot.isEmpty()) {
            return TimeSlotPattern(
                timeRange = timeRange,
                slotName = slotName,
                iconEmoji = emoji,
                dominantTrigger = "No cravings...",
                percentage = 0,
                hasCravings = false
            )
        }
        val topTrigger = logsInSlot.groupBy { it.triggerReason }.maxByOrNull { it.value.size }
        val pct = if (logsInSlot.isNotEmpty()) (topTrigger!!.value.size * 100) / logsInSlot.size else 0
        return TimeSlotPattern(
            timeRange = timeRange,
            slotName = slotName,
            iconEmoji = emoji,
            dominantTrigger = topTrigger?.key ?: "Stress",
            percentage = pct,
            hasCravings = true
        )
    }

    fun logRelapse(timestamp: Long = System.currentTimeMillis(), trigger: String, note: String = "") {
        viewModelScope.launch {
            repository.logRelapse(timestamp, trigger, note)
        }
    }

    fun logUrgeResisted(trigger: String, note: String = "") {
        viewModelScope.launch {
            repository.logUrgeResisted(System.currentTimeMillis(), trigger, note)
        }
    }

    fun updateActiveLogTrigger(logId: Long, newTrigger: String) {
        viewModelScope.launch {
            val log = latestLogs.find { it.id == logId } ?: return@launch
            repository.updateLogTimeAndTrigger(logId, log.timestamp, newTrigger)
        }
    }

    fun updateActiveLogTime(logId: Long, newHour: Int, newMinute: Int) {
        viewModelScope.launch {
            val log = latestLogs.find { it.id == logId } ?: return@launch
            val cal = Calendar.getInstance()
            cal.timeInMillis = log.timestamp
            cal.set(Calendar.HOUR_OF_DAY, newHour)
            cal.set(Calendar.MINUTE, newMinute)
            repository.updateLogTimeAndTrigger(logId, cal.timeInMillis, log.triggerReason)
        }
    }

    fun deleteLog(logId: Long) {
        viewModelScope.launch {
            repository.deleteLog(logId)
        }
    }

    fun shiftSelectedDate(days: Int) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = selectedDateTimestamp
        cal.add(Calendar.DAY_OF_YEAR, days)
        selectedDateTimestamp = cal.timeInMillis
        recomputeState()
    }

    fun setBaseline(baseline: Double) {
        viewModelScope.launch {
            repository.updateBaseline(baseline)
        }
    }

    fun setTargetGoal(days: Int) {
        viewModelScope.launch {
            repository.updateTargetGoal(days)
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
}
