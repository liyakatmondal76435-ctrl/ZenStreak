package com.example.ui.viewmodel

import com.example.data.local.StreakLog
import com.example.data.local.UserSettings
import com.example.ui.model.DayChartItem
import com.example.ui.model.TimeSlotPattern

data class StreakUiState(
    val currentStreakDurationMs: Long = 0L,
    val formattedTimer: String = "02h 15m 18s",
    val days: Long = 0,
    val hours: Long = 2,
    val minutes: Long = 15,
    val seconds: Long = 18,
    val dailyRelapsesCount: Int = 1,
    val weeklyRelapsesCount: Int = 3,
    val monthlyRelapsesCount: Int = 3,
    val totalRelapsesCount: Int = 7,
    val averagePerDay: Double = 0.2,
    val daysTracked: Int = 41,
    val baselinePerDay: Double = 1.0,
    val targetStreakDays: Int = 20,
    val currentStreakDaysInt: Int = 0,
    val dopamineRebootPercent: Int = 8,
    val dopamineStage: Int = 1,
    val vitalityReclaimedMins: Int = 0,
    val resistedCravingsCount: Int = 0,
    val primaryTrigger: String = "Stress",
    val primaryTriggerPercent: Int = 50,
    val peakTimeWindow: String = "Night (00:00 - 06:00)",
    val copingStrategy: String = "Try 4-7-8 breathing or take a cold shower before bedtime.",
    val timeSlotPatterns: List<TimeSlotPattern> = emptyList(),
    val triggerBreakdownList: List<Triple<String, Int, Int>> = emptyList(), // Name, count, percent
    val weeklyChartDays: List<DayChartItem> = emptyList(),
    val weeklyTotalCount: Int = 3,
    val weeklyCleanDaysRatio: String = "6/7 d.",
    val activeLogEntryForEdit: StreakLog? = null,
    val selectedDateFormatted: String = "13 October 2026",
    val selectedDateTimestamp: Long = System.currentTimeMillis(),
    val allLogs: List<StreakLog> = emptyList(),
    val settings: UserSettings = UserSettings(),
    val isAppLocked: Boolean = false
)
