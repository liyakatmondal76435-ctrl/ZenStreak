package com.example.ui.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.GoldTrophy
import com.example.ui.theme.MintActivePill
import com.example.ui.theme.MintBadgeGreen
import com.example.ui.theme.MintCardOutline
import com.example.ui.theme.MintCardSurface
import com.example.ui.theme.TriggerAloneBg
import com.example.ui.theme.TriggerAloneText
import com.example.ui.theme.TriggerBoredomBg
import com.example.ui.theme.TriggerBoredomText
import com.example.ui.theme.TriggerMediaBg
import com.example.ui.theme.TriggerMediaText
import com.example.ui.theme.TriggerNightBg
import com.example.ui.theme.TriggerNightText
import com.example.ui.theme.TriggerRoutineBg
import com.example.ui.theme.TriggerRoutineText
import com.example.ui.theme.TriggerStressBg
import com.example.ui.theme.TriggerStressText
import com.example.ui.viewmodel.StreakUiState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    uiState: StreakUiState,
    onTriggerSelectedForActiveLog: (Long, String) -> Unit,
    onTimeUpdatedForActiveLog: (Long, Int, Int) -> Unit,
    onDeleteActiveLog: (Long) -> Unit,
    onDateShift: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val activeLog = uiState.activeLogEntryForEdit
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val activeTimeStr = remember(activeLog?.timestamp) {
        if (activeLog != null) timeFormat.format(Date(activeLog.timestamp)) else "20:11"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header matching Screenshot 2: App Title + Level Badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "NutYou",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = ForestGreen
            )

            // Trophy Badge (e.g. 🏆 8/26 matching Screenshot 2)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MintActivePill,
                modifier = Modifier.testTag("top_level_badge")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = GoldTrophy,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "8/26",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Card 1: Time Since Last Relapse (Screenshot 2)
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MintCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MintCardOutline),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_live_counter_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                // Label with green dot
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(ForestGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Time since last relapse",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4C6A60)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Huge bold counter: 02h 15m 18s
                Text(
                    text = uiState.formattedTimer,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = ForestGreen,
                    letterSpacing = (-0.5).sp,
                    modifier = Modifier.testTag("home_live_timer_text")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Warning / Daily limit line (Screenshot 2)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = CoralAlert,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Daily limit",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF4C6A60)
                        )
                    }
                    Text(
                        text = "${uiState.dailyRelapsesCount}/1",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = CoralAlert
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { 1.0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = CoralAlert,
                    trackColor = Color(0xFFFFD4D4)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Card 2: Frequency Metrics (Daily, Weekly, Monthly from Screenshot 2)
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MintCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MintCardOutline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FrequencyMetricColumn(count = "${uiState.dailyRelapsesCount}", label = "Daily")
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(36.dp)
                        .background(MintCardOutline)
                )
                FrequencyMetricColumn(count = "${uiState.weeklyRelapsesCount}", label = "Weekly")
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(36.dp)
                        .background(MintCardOutline)
                )
                FrequencyMetricColumn(count = "${uiState.monthlyRelapsesCount}", label = "Monthly")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Date Navigator Pill [<] [📅 13 October 2026 ▾] [>] (Screenshot 2)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onDateShift(-1) },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MintActivePill)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous Day",
                    tint = ForestGreen
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MintActivePill,
                modifier = Modifier.testTag("date_navigator_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.selectedDateFormatted,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            IconButton(
                onClick = { onDateShift(1) },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MintActivePill)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next Day",
                    tint = ForestGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card 3: Log Entry Editor & Triggers Grid (Screenshot 2)
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MintCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MintCardOutline),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_log_entry_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Change Time button
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF1F7F4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            if (activeLog != null) {
                                val cal = Calendar.getInstance()
                                cal.timeInMillis = activeLog.timestamp
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        onTimeUpdatedForActiveLog(activeLog.id, hour, minute)
                                    },
                                    cal.get(Calendar.HOUR_OF_DAY),
                                    cal.get(Calendar.MINUTE),
                                    true
                                ).show()
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🕒 Change time",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = ForestGreen
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = activeTimeStr,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Time",
                                tint = ForestGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Reason",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4C6A60)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Reason Grid: 2 columns matching Screenshot 2
                val currentTrigger = activeLog?.triggerReason ?: "Stress"
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TriggerPillChip(
                            label = "Stress",
                            emoji = "😟",
                            bgColor = TriggerStressBg,
                            textColor = TriggerStressText,
                            isSelected = currentTrigger == "Stress",
                            onClick = { activeLog?.let { onTriggerSelectedForActiveLog(it.id, "Stress") } },
                            modifier = Modifier.weight(1f)
                        )
                        TriggerPillChip(
                            label = "Boredom",
                            emoji = "⌛",
                            bgColor = TriggerBoredomBg,
                            textColor = TriggerBoredomText,
                            isSelected = currentTrigger == "Boredom",
                            onClick = { activeLog?.let { onTriggerSelectedForActiveLog(it.id, "Boredom") } },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TriggerPillChip(
                            label = "Alone / Bed",
                            emoji = "🛋️",
                            bgColor = TriggerAloneBg,
                            textColor = TriggerAloneText,
                            isSelected = currentTrigger == "Alone / Bed",
                            onClick = { activeLog?.let { onTriggerSelectedForActiveLog(it.id, "Alone / Bed") } },
                            modifier = Modifier.weight(1f)
                        )
                        TriggerPillChip(
                            label = "Routine",
                            emoji = "🔄",
                            bgColor = TriggerRoutineBg,
                            textColor = TriggerRoutineText,
                            isSelected = currentTrigger == "Routine",
                            onClick = { activeLog?.let { onTriggerSelectedForActiveLog(it.id, "Routine") } },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TriggerPillChip(
                            label = "Social Media",
                            emoji = "📱",
                            bgColor = TriggerMediaBg,
                            textColor = TriggerMediaText,
                            isSelected = currentTrigger == "Social Media",
                            onClick = { activeLog?.let { onTriggerSelectedForActiveLog(it.id, "Social Media") } },
                            modifier = Modifier.weight(1f)
                        )
                        TriggerPillChip(
                            label = "Late Night",
                            emoji = "🌙",
                            bgColor = TriggerNightBg,
                            textColor = TriggerNightText,
                            isSelected = currentTrigger == "Late Night",
                            onClick = { activeLog?.let { onTriggerSelectedForActiveLog(it.id, "Late Night") } },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Delete Entry Button (Screenshot 2)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFDEEEC),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showDeleteConfirmDialog = true }
                        .testTag("delete_entry_button")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = CoralAlert,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Delete entry",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = CoralAlert
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(90.dp)) // Space for floating bottom bar
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete this entry?") },
            text = { Text("This will remove this recorded incident from your streak history.") },
            confirmButton = {
                Button(
                    onClick = {
                        activeLog?.let { onDeleteActiveLog(it.id) }
                        showDeleteConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralAlert)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun FrequencyMetricColumn(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = ForestGreen
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF5C746C)
        )
    }
}

@Composable
private fun TriggerPillChip(
    label: String,
    emoji: String,
    bgColor: Color,
    textColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) ForestGreen else bgColor)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = ForestGreen,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else textColor
            )
        }
    }
}
