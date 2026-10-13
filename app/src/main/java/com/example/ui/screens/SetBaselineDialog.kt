package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForestGreen

@Composable
fun SetBaselineDialog(
    initialBaseline: Double,
    initialTargetDays: Int,
    onDismiss: () -> Unit,
    onSave: (baseline: Double, targetDays: Int) -> Unit
) {
    var baselineInput by remember { mutableStateOf(initialBaseline.toString()) }
    var targetDaysInput by remember { mutableStateOf(initialTargetDays.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = ForestGreen,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Set Baseline & Goal",
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
                Text(
                    text = "Configure your past habit baseline and streak target to accurately calculate your recovery reduction index.",
                    fontSize = 12.sp,
                    color = Color(0xFF5C746C)
                )

                OutlinedTextField(
                    value = baselineInput,
                    onValueChange = { baselineInput = it },
                    label = { Text("Past Average Relapses / Day") },
                    placeholder = { Text("e.g. 1.0 or 0.5") },
                    modifier = Modifier.fillMaxWidth().testTag("baseline_input"),
                    shape = RoundedCornerShape(14.dp)
                )

                OutlinedTextField(
                    value = targetDaysInput,
                    onValueChange = { targetDaysInput = it },
                    label = { Text("Target Clean Streak (Days)") },
                    placeholder = { Text("e.g. 20 or 30 or 90") },
                    modifier = Modifier.fillMaxWidth().testTag("target_days_input"),
                    shape = RoundedCornerShape(14.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val b = baselineInput.toDoubleOrNull() ?: initialBaseline
                    val t = targetDaysInput.toIntOrNull() ?: initialTargetDays
                    onSave(b, t)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("save_baseline_button")
            ) {
                Text("Save Baseline", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(14.dp)) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(26.dp),
        containerColor = Color.White
    )
}
