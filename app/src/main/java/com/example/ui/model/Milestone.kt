package com.example.ui.model

data class Milestone(
    val id: String,
    val title: String,
    val subtitle: String,
    val targetDays: Double,
    val description: String,
    val benefit: String,
    val iconEmoji: String
)

object MilestoneCatalog {
    val ALL_MILESTONES = listOf(
        Milestone(
            id = "m_1d",
            title = "The Spark",
            subtitle = "24 Hours Clean",
            targetDays = 1.0,
            description = "The hardest step is the first day. Momentum has officially ignited.",
            benefit = "Dopamine receptors begin their initial sensitivity re-balancing.",
            iconEmoji = "🌱"
        ),
        Milestone(
            id = "m_3d",
            title = "Breaking Orbit",
            subtitle = "3 Days Clean",
            targetDays = 3.0,
            description = "Overcoming the initial withdrawal impulse peak.",
            benefit = "Sleep architecture begins settling; mental fog starts dissipating.",
            iconEmoji = "⚡"
        ),
        Milestone(
            id = "m_7d",
            title = "First Horizon",
            subtitle = "1 Week Clean",
            targetDays = 7.0,
            description = "One full cycle of weekdays and weekend conquered.",
            benefit = "Significant boost in self-efficacy, focus, and emotional equilibrium.",
            iconEmoji = "🛡️"
        ),
        Milestone(
            id = "m_14d",
            title = "Fortress Will",
            subtitle = "2 Weeks Clean",
            targetDays = 14.0,
            description = "Urges become less intense and more predictable.",
            benefit = "Prefrontal cortex gray matter and executive self-regulation strengthens.",
            iconEmoji = "⚔️"
        ),
        Milestone(
            id = "m_30d",
            title = "Silver Sovereign",
            subtitle = "1 Month Clean",
            targetDays = 30.0,
            description = "A milestone achieved by less than 10% of people trying.",
            benefit = "New neural pathways formed; daily baseline dopamine reset in progress.",
            iconEmoji = "🏆"
        ),
        Milestone(
            id = "m_60d",
            title = "Habit Crystallized",
            subtitle = "60 Days Clean",
            targetDays = 60.0,
            description = "Clean habits feel natural; the old impulse feels foreign.",
            benefit = "Substantial emotional resilience and decreased anxiety spikes.",
            iconEmoji = "💎"
        ),
        Milestone(
            id = "m_90d",
            title = "Golden Renewal",
            subtitle = "90 Days Clean",
            targetDays = 90.0,
            description = "The classic reboot threshold. Full neurochemical restoration.",
            benefit = "Long-term habit transformation. High mental clarity and purpose.",
            iconEmoji = "👑"
        ),
        Milestone(
            id = "m_180d",
            title = "Master of Self",
            subtitle = "6 Months Clean",
            targetDays = 180.0,
            description = "Half a year of unyielding inner sovereignty.",
            benefit = "Complete transformation of self-identity from striving to thriving.",
            iconEmoji = "🦅"
        ),
        Milestone(
            id = "m_365d",
            title = "Zen Sovereign",
            subtitle = "1 Year Clean",
            targetDays = 365.0,
            description = "365 days of triumph over impulsive gravity.",
            benefit = "Permanent lifestyle elevation and inspirational beacon for others.",
            iconEmoji = "🌟"
        )
    )
}
