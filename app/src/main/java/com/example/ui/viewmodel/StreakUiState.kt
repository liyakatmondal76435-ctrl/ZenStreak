package com.example.ui.viewmodel

import com.example.data.local.StreakConfig
import com.example.data.local.StreakLog
import com.example.data.local.UserSettings
import com.example.ui.model.Milestone
import com.example.ui.model.MilestoneCatalog

data class StreakUiState(
    val currentStreakDurationMs: Long = 0L,
    val days: Long = 0,
    val hours: Long = 0,
    val minutes: Long = 0,
    val seconds: Long = 0,
    val longestStreakDays: Double = 0.0,
    val longestStreakFormatted: String = "0d 0h",
    val totalCleanDays: Double = 0.0,
    val totalCheckIns: Int = 0,
    val totalResets: Int = 0,
    val totalUrgesResisted: Int = 0,
    val currentMotto: String = "One day at a time. I am in control of my choices.",
    val isCheckedInToday: Boolean = false,
    val streakStartDateFormatted: String = "",
    val allLogs: List<StreakLog> = emptyList(),
    val unlockedMilestoneIds: Set<String> = emptySet(),
    val nextMilestone: Milestone? = null,
    val nextMilestoneProgress: Float = 0f,
    val settings: UserSettings = UserSettings(),
    val isAppLocked: Boolean = false,
    val triggerBreakdown: Map<String, Int> = emptyMap(),
    val currentStreakStartTimestamp: Long = 0L
)
