package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streak_config")
data class StreakConfig(
    @PrimaryKey val id: Int = 1,
    val currentStreakStartTimestamp: Long = System.currentTimeMillis(),
    val longestStreakDurationMs: Long = 0L,
    val motto: String = "One day at a time. I am in control of my choices.",
    val lastCheckInDate: String = "",
    val targetMilestoneDays: Int = 7
)
