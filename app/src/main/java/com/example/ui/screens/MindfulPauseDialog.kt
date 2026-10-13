package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.MintActivePill
import com.example.ui.theme.MintBadgeGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun MindfulPauseDialog(
    onDismiss: () -> Unit,
    onUrgeOvercome: (strategy: String) -> Unit
) {
    var isBreathingActive by remember { mutableStateOf(true) }
    var phaseName by remember { mutableStateOf("Inhale deeply...") }
    var secondsRemaining by remember { mutableIntStateOf(4) }
    var targetScale by remember { mutableStateOf(1.25f) }

    LaunchedEffect(isBreathingActive) {
        if (!isBreathingActive) return@LaunchedEffect
        while (isActive && isBreathingActive) {
            // Inhale 4s
            phaseName = "Inhale deeply..."
            targetScale = 1.35f
            for (s in 4 downTo 1) {
                secondsRemaining = s
                delay(1000L)
            }

            // Hold 7s
            phaseName = "Hold calmly..."
            targetScale = 1.35f
            for (s in 7 downTo 1) {
                secondsRemaining = s
                delay(1000L)
            }

            // Exhale 8s
            phaseName = "Exhale slowly..."
            targetScale = 0.85f
            for (s in 8 downTo 1) {
                secondsRemaining = s
                delay(1000L)
            }
        }
    }

    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
        label = "mindfulScale"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SelfImprovement,
                    contentDescription = null,
                    tint = ForestGreen,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mindful Pause (4-7-8)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Urges peak in 10-15 minutes. Regulate your autonomic nervous system before taking any action.",
                    fontSize = 12.sp,
                    color = Color(0xFF5C746C),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                // Breathing visualizer circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(170.dp)
                        .scale(animatedScale)
                ) {
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        MintBadgeGreen.copy(alpha = 0.4f),
                                        MintActivePill.copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(MintActivePill)
                            .border(2.dp, ForestGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = phaseName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${secondsRemaining}s",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = ForestGreen
                            )
                        }
                    }
                }

                // Actionable advice pills
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF1F7F4),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "⚡ Immediate Anti-Urge Moves:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "• Splash ice cold water on your eyes and face\n• Drop and do 20 pushups immediately\n• Put your phone in another room", fontSize = 11.sp, color = Color(0xFF4C6A60), lineHeight = 16.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onUrgeOvercome("Mindful Pause (4-7-8)")
                    onDismiss()
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                modifier = Modifier.testTag("mindful_victory_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "I Overcame The Urge!", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(14.dp)) {
                Text("Close")
            }
        },
        shape = RoundedCornerShape(26.dp),
        containerColor = Color.White
    )
}
