package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "streak_logs")
data class StreakLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: String, // "RELAPSE", "URGE_RESISTED", "CHECK_IN"
    val durationMs: Long = 0L,
    val triggerReason: String = "Stress", // "Stress", "Boredom", "Alone / Bed", "Social Media", "Late Night", "Routine"
    val note: String = "",
    val hourOfDay: Int = getHourFromTimestamp(timestamp),
    val moodRating: Int = 3
) {
    companion object {
        fun getHourFromTimestamp(ts: Long): Int {
            val cal = Calendar.getInstance()
            cal.timeInMillis = ts
            return cal.get(Calendar.HOUR_OF_DAY)
        }
    }
}
