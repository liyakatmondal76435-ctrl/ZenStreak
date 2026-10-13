package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streak_config")
data class StreakConfig(
    @PrimaryKey val id: Int = 1,
    val currentStreakStartTimestamp: Long = System.currentTimeMillis(),
    val longestStreakDurationMs: Long = 0L,
    val motto: String = "Master your urges, reclaim your vitality.",
    val lastCheckInDate: String = "",
    val baselinePerDay: Double = 1.0, // Past average per day
    val targetMilestoneDays: Int = 20, // e.g. 20 days goal shown in Screenshot 1/5
    val trackingStartTimestamp: Long = System.currentTimeMillis() - (41L * 24 * 3600 * 1000) // Default initial history anchor
)
