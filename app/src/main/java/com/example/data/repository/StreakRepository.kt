package com.example.data.repository

import com.example.data.local.StreakConfig
import com.example.data.local.StreakDao
import com.example.data.local.StreakLog
import com.example.data.local.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StreakRepository(private val dao: StreakDao) {

    val configFlow: Flow<StreakConfig> = dao.getConfig().map { config ->
        config ?: StreakConfig()
    }

    val logsFlow: Flow<List<StreakLog>> = dao.getAllLogs()

    val settingsFlow: Flow<UserSettings> = dao.getSettings().map { settings ->
        settings ?: UserSettings()
    }

    suspend fun initializeIfNeeded() {
        val currentConfig = dao.getConfig().firstOrNull()
        if (currentConfig == null) {
            dao.saveConfig(
                StreakConfig(
                    id = 1,
                    currentStreakStartTimestamp = System.currentTimeMillis(),
                    longestStreakDurationMs = 0L,
                    motto = "One day at a time. I am in control of my choices.",
                    lastCheckInDate = "",
                    targetMilestoneDays = 7
                )
            )
        }
        val currentSettings = dao.getSettings().firstOrNull()
        if (currentSettings == null) {
            dao.saveSettings(
                UserSettings(
                    id = 1,
                    themeMode = "SYSTEM",
                    dynamicColorEnabled = true,
                    appLockEnabled = false,
                    pinCode = ""
                )
            )
        }
    }

    suspend fun resetStreak(triggerReason: String, note: String) {
        val currentConfig = dao.getConfig().firstOrNull() ?: StreakConfig()
        val now = System.currentTimeMillis()
        val streakDuration = (now - currentConfig.currentStreakStartTimestamp).coerceAtLeast(0L)
        val newLongest = maxOf(currentConfig.longestStreakDurationMs, streakDuration)

        // Log reset event
        dao.insertLog(
            StreakLog(
                timestamp = now,
                eventType = "RESET",
                durationMs = streakDuration,
                triggerReason = triggerReason.ifBlank { "Unspecified" },
                note = note.trim(),
                moodRating = 2
            )
        )

        // Reset streak start to now
        dao.saveConfig(
            currentConfig.copy(
                currentStreakStartTimestamp = now,
                longestStreakDurationMs = newLongest
            )
        )
    }

    suspend fun checkInToday(moodRating: Int, note: String) {
        val currentConfig = dao.getConfig().firstOrNull() ?: StreakConfig()
        val now = System.currentTimeMillis()
        val streakDuration = (now - currentConfig.currentStreakStartTimestamp).coerceAtLeast(0L)
        val todayStr = getTodayDateString()

        dao.insertLog(
            StreakLog(
                timestamp = now,
                eventType = "CHECK_IN",
                durationMs = streakDuration,
                triggerReason = "Daily Commitment",
                note = note.trim(),
                moodRating = moodRating
            )
        )

        dao.saveConfig(
            currentConfig.copy(
                lastCheckInDate = todayStr,
                longestStreakDurationMs = maxOf(currentConfig.longestStreakDurationMs, streakDuration)
            )
        )
    }

    suspend fun logUrgeResisted(strategyUsed: String, note: String) {
        val currentConfig = dao.getConfig().firstOrNull() ?: StreakConfig()
        val now = System.currentTimeMillis()
        val streakDuration = (now - currentConfig.currentStreakStartTimestamp).coerceAtLeast(0L)

        dao.insertLog(
            StreakLog(
                timestamp = now,
                eventType = "URGE_RESISTED",
                durationMs = streakDuration,
                triggerReason = strategyUsed,
                note = note.trim(),
                moodRating = 5
            )
        )
    }

    suspend fun updateMotto(newMotto: String) {
        val currentConfig = dao.getConfig().firstOrNull() ?: StreakConfig()
        dao.saveConfig(currentConfig.copy(motto = newMotto.trim()))
    }

    suspend fun updateSettings(settings: UserSettings) {
        dao.saveSettings(settings)
    }

    suspend fun resetAllData() {
        val now = System.currentTimeMillis()
        dao.clearAllLogs()
        dao.saveConfig(
            StreakConfig(
                id = 1,
                currentStreakStartTimestamp = now,
                longestStreakDurationMs = 0L,
                motto = "One day at a time. I am in control of my choices.",
                lastCheckInDate = "",
                targetMilestoneDays = 7
            )
        )
        dao.saveSettings(
            UserSettings(
                id = 1,
                themeMode = "SYSTEM",
                dynamicColorEnabled = true,
                appLockEnabled = false,
                pinCode = ""
            )
        )
    }

    companion object {
        fun getTodayDateString(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date())
        }
    }
}
