package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.MintActivePill
import com.example.ui.theme.MintBadgeGreen
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

@Composable
fun LogIncidentDialog(
    onDismiss: () -> Unit,
    onLogRelapse: (trigger: String, note: String) -> Unit,
    onLogResisted: (trigger: String, note: String) -> Unit
) {
    var isRelapseMode by remember { mutableStateOf(true) }
    var selectedTrigger by remember { mutableStateOf("Stress") }
    var reflectionNote by remember { mutableStateOf("") }

    val triggers = listOf(
        Triple("Stress", "😟", TriggerStressBg to TriggerStressText),
        Triple("Boredom", "⌛", TriggerBoredomBg to TriggerBoredomText),
        Triple("Alone / Bed", "🛋️", TriggerAloneBg to TriggerAloneText),
        Triple("Routine", "🔄", TriggerRoutineBg to TriggerRoutineText),
        Triple("Social Media", "📱", TriggerMediaBg to TriggerMediaText),
        Triple("Late Night", "🌙", TriggerNightBg to TriggerNightText)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isRelapseMode) Icons.Default.WarningAmber else Icons.Default.Shield,
                    contentDescription = null,
                    tint = if (isRelapseMode) CoralAlert else ForestGreen,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isRelapseMode) "Log Relapse Incident" else "Log Resisted Urge (Victory!)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Toggle: [Relapse] vs [Resisted Urge]
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFE5F1EB),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isRelapseMode) CoralAlert else Color.Transparent)
                                .clickable { isRelapseMode = true }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⚠️ Relapse",
                                fontWeight = FontWeight.Bold,
                                color = if (isRelapseMode) Color.White else Color(0xFF4C6A60),
                                fontSize = 13.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (!isRelapseMode) ForestGreen else Color.Transparent)
                                .clickable { isRelapseMode = false }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🛡️ Resisted Urge",
                                fontWeight = FontWeight.Bold,
                                color = if (!isRelapseMode) Color.White else Color(0xFF4C6A60),
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Text(
                    text = "Select what triggered this moment:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4C6A60)
                )

                // 2-column trigger chip grid
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    triggers.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { (name, emoji, colorPair) ->
                                val isSelected = selectedTrigger == name
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isSelected) ForestGreen else colorPair.first)
                                        .border(
                                            width = if (isSelected) 2.dp else 0.dp,
                                            color = ForestGreen,
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        .clickable { selectedTrigger = name }
                                        .padding(vertical = 10.dp, horizontal = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = emoji, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = name,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else colorPair.second
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = reflectionNote,
                    onValueChange = { reflectionNote = it },
                    label = { Text("Reflection note (optional)") },
                    placeholder = { Text("What led to this or what strategy helped?") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isRelapseMode) {
                        onLogRelapse(selectedTrigger, reflectionNote)
                    } else {
                        onLogResisted(selectedTrigger, reflectionNote)
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRelapseMode) CoralAlert else ForestGreen
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("save_log_entry_button")
            ) {
                Text(text = if (isRelapseMode) "Record Relapse" else "Record Victory", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(26.dp),
        containerColor = Color.White
    )
}
