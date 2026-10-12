package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streak_logs")
data class StreakLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: String, // "RESET", "CHECK_IN", "URGE_RESISTED", "MILESTONE"
    val durationMs: Long = 0L,
    val triggerReason: String = "None",
    val note: String = "",
    val moodRating: Int = 5 // 1 to 5 scale
)
