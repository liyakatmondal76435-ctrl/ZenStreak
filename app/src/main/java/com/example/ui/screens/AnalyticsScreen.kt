package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ScallopedBadge
import com.example.ui.theme.ChartBarActive
import com.example.ui.theme.ChartBarInactive
import com.example.ui.theme.ChartDottedLine
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.HeartRateRed
import com.example.ui.theme.MintActivePill
import com.example.ui.theme.MintBadgeGreen
import com.example.ui.theme.MintCardOutline
import com.example.ui.theme.MintCardSurface
import com.example.ui.viewmodel.StreakUiState

enum class AnalyticsSubTab {
    CHARTS,
    STATISTICS,
    TRIGGERS
}

@Composable
fun AnalyticsScreen(
    uiState: StreakUiState,
    onSetBaselineClick: () -> Unit,
    onMindfulPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableStateOf(AnalyticsSubTab.STATISTICS) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Title "Analytics"
        Text(
            text = "Analytics",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = ForestGreen,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )

        // Segmented Pill Bar: [Charts] [Statistics] [Triggers] (Screenshots 1-6)
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Color(0xFFE2EFE8),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(
                    AnalyticsSubTab.CHARTS to "Charts",
                    AnalyticsSubTab.STATISTICS to "Statistics",
                    AnalyticsSubTab.TRIGGERS to "Triggers"
                ).forEach { (tab, label) ->
                    val isSelected = selectedSubTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(28.dp))
                            .background(if (isSelected) ForestGreen else Color.Transparent)
                            .clickable { selectedSubTab = tab }
                            .padding(vertical = 12.dp)
                            .testTag("analytics_subtab_${label.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color(0xFF4C6A60)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        when (selectedSubTab) {
            AnalyticsSubTab.CHARTS -> ChartsSubTabContent(uiState = uiState)
            AnalyticsSubTab.STATISTICS -> StatisticsSubTabContent(
                uiState = uiState,
                onSetBaselineClick = onSetBaselineClick,
                onMindfulPauseClick = onMindfulPauseClick
            )
            AnalyticsSubTab.TRIGGERS -> TriggersSubTabContent(uiState = uiState)
        }

        Spacer(modifier = Modifier.height(90.dp))
    }
}

// ---------------- TAB 1: CHARTS (Screenshot 3) ----------------
@Composable
private fun ChartsSubTabContent(uiState: StreakUiState) {
    var selectedRange by remember { mutableStateOf("Week") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Period Pills: [Day] [Week] [Month] [Year]
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFE5F1EB),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Day", "Week", "Month", "Year").forEach { range ->
                    val isSelected = selectedRange == range
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) ForestGreen else Color.Transparent)
                            .clickable { selectedRange = range }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = range,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color(0xFF4C6A60)
                        )
                    }
                }
            }
        }

        // Card with Interactive Chart (Screenshot 3)
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MintCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MintCardOutline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header row: Date Navigator & Chart Mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFF1F7F4)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, modifier = Modifier.size(16.dp), tint = ForestGreen)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "📅 12 Oct - 18 Oct", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, modifier = Modifier.size(16.dp), tint = ForestGreen)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = ForestGreen
                    ) {
                        Icon(imageVector = Icons.Default.BarChart, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp).size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Metric Total & Trend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(text = "${uiState.weeklyTotalCount}", fontSize = 36.sp, fontWeight = FontWeight.Black, color = ForestGreen)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "pcs.", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ForestGreen, modifier = Modifier.padding(bottom = 4.dp))
                        }
                        Text(text = "Total relapses", fontSize = 12.sp, color = Color(0xFF5C746C))
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFFE8E8)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.NorthEast, contentDescription = null, tint = CoralAlert, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Goal Target: 0", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CoralAlert)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Canvas Weekly Bar / Dot Visualizer matching Screenshot 3
                WeeklyDotChart(days = uiState.weeklyChartDays)

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom summary row: Average & Within Limit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFF1F7F4),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = "📉 Average", fontSize = 11.sp, color = Color(0xFF5C746C))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "${uiState.averagePerDay} pcs.", fontSize = 22.sp, fontWeight = FontWeight.Black, color = ForestGreen)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFF1F7F4),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = "🛡️ Clean days", fontSize = 11.sp, color = Color(0xFF5C746C))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = uiState.weeklyCleanDaysRatio, fontSize = 22.sp, fontWeight = FontWeight.Black, color = ForestGreen)
                        }
                    }
                }
            }
        }
    }
}

// ---------------- TAB 2: STATISTICS (Screenshots 1 & 5) ----------------
@Composable
private fun StatisticsSubTabContent(
    uiState: StreakUiState,
    onSetBaselineClick: () -> Unit,
    onMindfulPauseClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Card: Total Relapses with Scalloped Flower Badge (Screenshot 1 & 5)
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MintCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MintCardOutline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MintActivePill)
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Total relapses", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                            Text(text = "3 September 2026", fontSize = 12.sp, color = Color(0xFF5C746C))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Scalloped Badge (flower shape "7 pcs.")
                    ScallopedBadge(
                        countText = "${uiState.totalRelapsesCount}",
                        unitText = "pcs.",
                        size = 88.dp,
                        badgeColor = MintBadgeGreen
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF1F7F4),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                Text(text = "${uiState.averagePerDay}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = ForestGreen)
                                Text(text = "Average per day", fontSize = 11.sp, color = Color(0xFF5C746C))
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF1F7F4),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                Text(text = "${uiState.daysTracked}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = ForestGreen)
                                Text(text = "41 days tracked", fontSize = 11.sp, color = Color(0xFF5C746C))
                            }
                        }
                    }
                }
            }
        }

        // Card: Track reduction progress & Set Baseline (Screenshot 1 & 5)
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MintCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MintCardOutline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ShowChart,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MintActivePill)
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Track your reduction progress", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Set your past baseline to see your progress (-%), avoided relapses, and recovery index.",
                    fontSize = 13.sp,
                    color = Color(0xFF5C746C),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onSetBaselineClick,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("set_baseline_button")
                ) {
                    Text(text = "Set Baseline", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }

        // Two Circular Progress Rings (Screenshot 1 & 5)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Trophy Circle Ring
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = MintCardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MintCardOutline),
                modifier = Modifier
                    .weight(1f)
                    .height(160.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Canvas(modifier = Modifier.size(110.dp)) {
                        drawCircle(color = Color(0xFFE5F1EB), style = Stroke(width = 8.dp.toPx()))
                        drawArc(
                            color = ForestGreen,
                            startAngle = -90f,
                            sweepAngle = ((uiState.currentStreakDaysInt.toFloat() / uiState.targetStreakDays.toFloat()) * 360f).coerceIn(10f, 360f),
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx())
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "${uiState.currentStreakDaysInt}", fontSize = 28.sp, fontWeight = FontWeight.Black, color = ForestGreen)
                        Text(text = "${uiState.targetStreakDays} days goal", fontSize = 11.sp, color = Color(0xFF5C746C))
                    }
                }
            }

            // Dopamine Reset Level Ring (Dotted medical badge from Screenshot 1 & 5)
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = MintCardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MintCardOutline),
                modifier = Modifier
                    .weight(1f)
                    .height(160.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Canvas(modifier = Modifier.size(110.dp)) {
                        val stroke = Stroke(
                            width = 6.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 16f), 0f)
                        )
                        drawCircle(color = ForestGreen, style = stroke)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.LocalHospital, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "${uiState.dopamineRebootPercent}%", fontSize = 28.sp, fontWeight = FontWeight.Black, color = ForestGreen)
                        Text(text = "${uiState.dopamineStage} / 12 stages", fontSize = 11.sp, color = Color(0xFF5C746C))
                    }
                }
            }
        }

        // 4 Stat Cards Grid (Screenshot 5: Money Saved, Life Reclaimed, Average interval, Resisted Cravings)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatGridCard(
                icon = Icons.Default.FitnessCenter,
                title = "Vitality Reclaimed",
                value = "14 hrs",
                subtitle = "Mind clarity gained",
                modifier = Modifier.weight(1f)
            )
            StatGridCard(
                icon = Icons.Default.Favorite,
                title = "Life Reclaimed",
                value = "0m",
                subtitle = "Heartbeat calm",
                showHeartwave = true,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatGridCard(
                icon = Icons.Default.HourglassBottom,
                title = "Average interval",
                value = "4.2 days",
                subtitle = "Clean spacing",
                modifier = Modifier.weight(1f)
            )
            StatGridCard(
                icon = Icons.Default.Shield,
                title = "Resisted Cravings",
                value = "${uiState.resistedCravingsCount}",
                actionLabel = "Mindful Pause",
                onActionClick = onMindfulPauseClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ---------------- TAB 3: TRIGGERS (Screenshots 4 & 6) ----------------
@Composable
private fun TriggersSubTabContent(uiState: StreakUiState) {
    var selectedTimeFilter by remember { mutableStateOf("All time") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Filter pills: [All time] [30 days] [7 days] (Screenshot 6)
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFE5F1EB),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("All time", "30 days", "7 days").forEach { filter ->
                    val isSelected = selectedTimeFilter == filter
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) ForestGreen else Color.Transparent)
                            .clickable { selectedTimeFilter = filter }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = filter,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color(0xFF4C6A60)
                        )
                    }
                }
            }
        }

        // Primary Factor Card (Screenshot 4 & 6)
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MintCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MintCardOutline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFBCE7D9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "😟", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Primary Factor", fontSize = 12.sp, color = Color(0xFF5C746C))
                            Text(text = uiState.primaryTrigger, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFBCE7D9)
                    ) {
                        Text(
                            text = "${uiState.primaryTriggerPercent}%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = ForestGreen,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Subchips: [3 incidents] [🕒 Peak: Night (00:00 - 06:00)]
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFF1F7F4)) {
                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Text(text = "⚡ ${uiState.dailyRelapsesCount} incidents", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = ForestGreen)
                        }
                    }
                    Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFF1F7F4)) {
                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Text(text = "🕒 Peak: ${uiState.peakTimeWindow}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = ForestGreen)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Coping strategy advice card
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFFF1F7F4),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Coping Strategy", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = uiState.copingStrategy, fontSize = 12.sp, color = Color(0xFF4C6A60), lineHeight = 16.sp)
                        }
                    }
                }
            }
        }

        // Section: Time of Day Patterns (4 Vertical Cards from Screenshot 4 & 6)
        Column {
            Text(text = "Time of Day Patterns", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
            Text(text = "Dominant trigger for each part of the day", fontSize = 12.sp, color = Color(0xFF5C746C))

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.timeSlotPatterns.forEach { slot ->
                    TimeSlotPatternCard(slot = slot, modifier = Modifier.weight(1f))
                }
            }
        }

        // Section: Relapse Triggers Breakdown List (Screenshot 4 & 6)
        Column {
            Text(text = "Relapse Triggers", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MintCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MintCardOutline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    uiState.triggerBreakdownList.forEach { (name, count, pct) ->
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = when (name) {
                                            "Stress" -> "😟"
                                            "Boredom" -> "⌛"
                                            "Alone / Bed" -> "🛋️"
                                            "Social Media" -> "📱"
                                            else -> "🌙"
                                        },
                                        fontSize = 18.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                                }
                                Text(text = "$count times ($pct%) >", fontSize = 12.sp, color = Color(0xFF5C746C))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { (pct / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = ForestGreen,
                                trackColor = Color(0xFFE5F1EB)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeSlotPatternCard(
    slot: com.example.ui.model.TimeSlotPattern,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFF1F7F4),
        modifier = modifier.height(130.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = slot.iconEmoji, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = slot.timeRange, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5C746C))
            }

            if (slot.hasCravings) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = slot.dominantTrigger, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreen, maxLines = 1)
                    Text(text = "${slot.percentage}%", fontSize = 10.sp, color = Color(0xFF5C746C))
                }
            } else {
                Text(text = "No cravings...", fontSize = 9.sp, color = Color(0xFF88A399), maxLines = 2, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }
    }
}

@Composable
private fun StatGridCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    subtitle: String = "",
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    showHeartwave: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MintCardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MintCardOutline),
        modifier = modifier.height(140.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                Text(text = title, fontSize = 11.sp, color = Color(0xFF5C746C), fontWeight = FontWeight.SemiBold)
            }

            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = ForestGreen)

            if (showHeartwave) {
                Canvas(modifier = Modifier.fillMaxWidth().height(18.dp)) {
                    val path = Path()
                    val w = size.width
                    val h = size.height
                    path.moveTo(0f, h * 0.7f)
                    path.lineTo(w * 0.3f, h * 0.7f)
                    path.lineTo(w * 0.45f, 0f)
                    path.lineTo(w * 0.6f, h)
                    path.lineTo(w * 0.75f, h * 0.7f)
                    path.lineTo(w, h * 0.7f)
                    drawPath(path, color = Color(0xFF8BB7A8), style = Stroke(width = 2.dp.toPx()))
                }
            } else if (actionLabel != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MintActivePill,
                    modifier = Modifier.clickable { onActionClick?.invoke() }
                ) {
                    Text(
                        text = actionLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            } else {
                Text(text = subtitle, fontSize = 10.sp, color = Color(0xFF88A399))
            }
        }
    }
}

@Composable
private fun WeeklyDotChart(days: List<com.example.ui.model.DayChartItem>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            days.forEach { item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f)
                ) {
                    if (item.count > 0) {
                        Box(
                            modifier = Modifier
                                .size(if (item.isSelectedDay) 26.dp else 22.dp)
                                .clip(CircleShape)
                                .background(if (item.isSelectedDay) ForestGreen else Color(0xFFDF6858)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "${item.count}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    } else {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE5F1EB))
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    Text(
                        text = item.dayLabel,
                        fontSize = 11.sp,
                        fontWeight = if (item.isSelectedDay) FontWeight.Bold else FontWeight.Normal,
                        color = if (item.isSelectedDay) ForestGreen else Color(0xFF5C746C)
                    )
                    if (item.isSelectedDay) {
                        Box(
                            modifier = Modifier
                                .width(14.dp)
                                .height(3.dp)
                                .clip(RoundedCornerShape(1.5.dp))
                                .background(ForestGreen)
                        )
                    }
                }
            }
        }
    }
}
