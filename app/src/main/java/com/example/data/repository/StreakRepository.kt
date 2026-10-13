package com.example.data.repository

import com.example.data.local.StreakConfig
import com.example.data.local.StreakDao
import com.example.data.local.StreakLog
import com.example.data.local.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
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
            val now = System.currentTimeMillis()
            val twoHoursAgo = now - (2L * 3600 * 1000 + 15 * 60 * 1000 + 18 * 1000) // 02h 15m 18s matching Screenshot 2!
            val initialConfig = StreakConfig(
                id = 1,
                currentStreakStartTimestamp = twoHoursAgo,
                longestStreakDurationMs = 14L * 24 * 3600 * 1000,
                motto = "Master your urges, reclaim your vitality.",
                lastCheckInDate = "",
                baselinePerDay = 1.0,
                targetMilestoneDays = 20,
                trackingStartTimestamp = now - (41L * 24 * 3600 * 1000)
            )
            dao.saveConfig(initialConfig)

            // Seed realistic past sample logs matching the screenshots (e.g. 7 incidents across 41 days, with Stress, Social/Alone, Boredom triggers)
            val cal = Calendar.getInstance()
            // 1 incident today (matching Screenshot 2: "1 Daily")
            dao.insertLog(
                StreakLog(
                    timestamp = twoHoursAgo,
                    eventType = "RELAPSE",
                    durationMs = 3L * 24 * 3600 * 1000,
                    triggerReason = "Stress",
                    note = "Late night work stress",
                    hourOfDay = 20
                )
            )

            // 2 other incidents this week (matching Screenshot 2: "3 Weekly")
            cal.timeInMillis = now
            cal.add(Calendar.DAY_OF_YEAR, -2)
            cal.set(Calendar.HOUR_OF_DAY, 23)
            dao.insertLog(
                StreakLog(
                    timestamp = cal.timeInMillis,
                    eventType = "RELAPSE",
                    durationMs = 5L * 24 * 3600 * 1000,
                    triggerReason = "Alone / Bed",
                    note = "Scrolling in bed",
                    hourOfDay = 23
                )
            )

            cal.add(Calendar.DAY_OF_YEAR, -3)
            cal.set(Calendar.HOUR_OF_DAY, 9)
            dao.insertLog(
                StreakLog(
                    timestamp = cal.timeInMillis,
                    eventType = "RELAPSE",
                    durationMs = 8L * 24 * 3600 * 1000,
                    triggerReason = "Stress",
                    note = "Morning anxiety",
                    hourOfDay = 9
                )
            )

            // Resisted urges (matching "Resisted Cravings" badge)
            cal.timeInMillis = now
            cal.add(Calendar.DAY_OF_YEAR, -1)
            dao.insertLog(
                StreakLog(
                    timestamp = cal.timeInMillis,
                    eventType = "URGE_RESISTED",
                    durationMs = 0L,
                    triggerReason = "Boredom",
                    note = "Cold shower and 20 pushups",
                    hourOfDay = 19
                )
            )
        }

        val currentSettings = dao.getSettings().firstOrNull()
        if (currentSettings == null) {
            dao.saveSettings(
                UserSettings(
                    id = 1,
                    themeMode = "LIGHT", // default light mint aesthetic matching screenshot
                    dynamicColorEnabled = false,
                    appLockEnabled = false,
                    pinCode = ""
                )
            )
        }
    }

    suspend fun logRelapse(timestamp: Long, triggerReason: String, note: String) {
        val currentConfig = dao.getConfig().firstOrNull() ?: StreakConfig()
        val durationAchieved = (timestamp - currentConfig.currentStreakStartTimestamp).coerceAtLeast(0L)
        val newLongest = maxOf(currentConfig.longestStreakDurationMs, durationAchieved)

        dao.insertLog(
            StreakLog(
                timestamp = timestamp,
                eventType = "RELAPSE",
                durationMs = durationAchieved,
                triggerReason = triggerReason.ifBlank { "Stress" },
                note = note.trim(),
                hourOfDay = StreakLog.getHourFromTimestamp(timestamp)
            )
        )

        dao.saveConfig(
            currentConfig.copy(
                currentStreakStartTimestamp = timestamp,
                longestStreakDurationMs = newLongest
            )
        )
    }

    suspend fun logUrgeResisted(timestamp: Long, triggerReason: String, note: String) {
        dao.insertLog(
            StreakLog(
                timestamp = timestamp,
                eventType = "URGE_RESISTED",
                durationMs = 0L,
                triggerReason = triggerReason,
                note = note.trim(),
                hourOfDay = StreakLog.getHourFromTimestamp(timestamp),
                moodRating = 5
            )
        )
    }

    suspend fun updateLogTimeAndTrigger(id: Long, newTimestamp: Long, newTrigger: String) {
        val allLogs = dao.getAllLogs().firstOrNull() ?: emptyList()
        val target = allLogs.find { it.id == id } ?: return
        dao.updateLog(
            target.copy(
                timestamp = newTimestamp,
                triggerReason = newTrigger,
                hourOfDay = StreakLog.getHourFromTimestamp(newTimestamp)
            )
        )
    }

    suspend fun deleteLog(id: Long) {
        dao.deleteLogById(id)
        // If we deleted the most recent relapse, adjust active streak timer to the previous relapse
        val remainingLogs = dao.getAllLogs().firstOrNull() ?: emptyList()
        val mostRecentRelapse = remainingLogs.filter { it.eventType == "RELAPSE" }.maxByOrNull { it.timestamp }
        val currentConfig = dao.getConfig().firstOrNull() ?: StreakConfig()

        if (mostRecentRelapse != null) {
            dao.saveConfig(currentConfig.copy(currentStreakStartTimestamp = mostRecentRelapse.timestamp))
        } else {
            dao.saveConfig(currentConfig.copy(currentStreakStartTimestamp = currentConfig.trackingStartTimestamp))
        }
    }

    suspend fun updateBaseline(baselinePerDay: Double) {
        val currentConfig = dao.getConfig().firstOrNull() ?: StreakConfig()
        dao.saveConfig(currentConfig.copy(baselinePerDay = baselinePerDay))
    }

    suspend fun updateTargetGoal(targetDays: Int) {
        val currentConfig = dao.getConfig().firstOrNull() ?: StreakConfig()
        dao.saveConfig(currentConfig.copy(targetMilestoneDays = targetDays))
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
                motto = "Master your urges, reclaim your vitality.",
                lastCheckInDate = "",
                baselinePerDay = 1.0,
                targetMilestoneDays = 20,
                trackingStartTimestamp = now
            )
        )
        dao.saveSettings(
            UserSettings(
                id = 1,
                themeMode = "LIGHT",
                dynamicColorEnabled = false,
                appLockEnabled = false,
                pinCode = ""
            )
        )
    }
}
