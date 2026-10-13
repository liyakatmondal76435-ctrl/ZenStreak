package com.example.ui.model

data class TimeSlotPattern(
    val timeRange: String, // "00-06", "06-12", "12-18", "18-24"
    val slotName: String,
    val iconEmoji: String,
    val dominantTrigger: String,
    val percentage: Int,
    val hasCravings: Boolean
)

data class DayChartItem(
    val dayLabel: String, // "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
    val count: Int,
    val isSelectedDay: Boolean = false,
    val isClean: Boolean = true
)
