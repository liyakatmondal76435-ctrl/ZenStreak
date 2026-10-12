package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.model.MotivationalContent
import com.example.ui.theme.CalmTeal
import com.example.ui.theme.GlowGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

enum class BreathingPhase(val label: String, val seconds: Int) {
    INHALE("Inhale deeply...", 4),
    HOLD_IN("Hold breath...", 4),
    EXHALE("Exhale slowly...", 4),
    HOLD_OUT("Rest & reset...", 4)
}

@Composable
fun SosScreen(
    onUrgeOvercome: (strategy: String, note: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isBreathingActive by remember { mutableStateOf(false) }
    var currentPhase by remember { mutableStateOf(BreathingPhase.INHALE) }
    var secondsRemainingInPhase by remember { mutableIntStateOf(4) }
    var completedCycles by remember { mutableIntStateOf(0) }
    var quoteIndex by remember { mutableIntStateOf(0) }
    var showGroundingDialog by remember { mutableStateOf(false) }
    var urgeVictoryCelebration by remember { mutableStateOf(false) }

    // Breathing loop
    LaunchedEffect(isBreathingActive) {
        if (!isBreathingActive) return@LaunchedEffect
        while (isActive && isBreathingActive) {
            // INHALE
            currentPhase = BreathingPhase.INHALE
            for (sec in BreathingPhase.INHALE.seconds downTo 1) {
                secondsRemainingInPhase = sec
                delay(1000L)
            }

            // HOLD IN
            currentPhase = BreathingPhase.HOLD_IN
            for (sec in BreathingPhase.HOLD_IN.seconds downTo 1) {
                secondsRemainingInPhase = sec
                delay(1000L)
            }

            // EXHALE
            currentPhase = BreathingPhase.EXHALE
            for (sec in BreathingPhase.EXHALE.seconds downTo 1) {
                secondsRemainingInPhase = sec
                delay(1000L)
            }

            // HOLD OUT
            currentPhase = BreathingPhase.HOLD_OUT
            for (sec in BreathingPhase.HOLD_OUT.seconds downTo 1) {
                secondsRemainingInPhase = sec
                delay(1000L)
            }

            completedCycles++
        }
    }

    // Dynamic scale for breathing circle animation
    val targetScale = when {
        !isBreathingActive -> 1.0f
        currentPhase == BreathingPhase.INHALE -> 1.35f
        currentPhase == BreathingPhase.HOLD_IN -> 1.35f
        currentPhase == BreathingPhase.EXHALE -> 0.85f
        else -> 0.85f
    }

    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = if (isBreathingActive) {
            tween(
                durationMillis = currentPhase.seconds * 1000,
                easing = FastOutSlowInEasing
            )
        } else {
            spring(dampingRatio = 0.7f, stiffness = 300f)
        },
        label = "breathingScale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "SOS Urge Breaker",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ground your physiology. Urges are temporary neural waves.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Breathing Visualizer Card
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("breathing_visualizer_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Air,
                            contentDescription = null,
                            tint = CalmTeal,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BOX BREATHING (4-4-4-4)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = CalmTeal,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "Cycles: $completedCycles",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // The Breathing Pulsing Circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(210.dp)
                        .scale(animatedScale)
                ) {
                    // Outer aura gradient
                    Box(
                        modifier = Modifier
                            .size(210.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        CalmTeal.copy(alpha = if (isBreathingActive) 0.35f else 0.15f),
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Middle circle
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        CalmTeal.copy(alpha = 0.3f)
                                    )
                                )
                            )
                            .border(
                                width = 2.dp,
                                color = if (isBreathingActive) CalmTeal else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isBreathingActive) currentPhase.label else "Ready",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isBreathingActive) "${secondsRemainingInPhase}s" else "Tap Start",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Play / Pause Button
                Button(
                    onClick = {
                        isBreathingActive = !isBreathingActive
                        if (!isBreathingActive) {
                            currentPhase = BreathingPhase.INHALE
                            secondsRemainingInPhase = 4
                        }
                    },
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isBreathingActive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("breathing_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isBreathingActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBreathingActive) "Pause Breathing" else "Start 4-4-4-4 Rhythm",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mark Urge Overcome Button
                FilledTonalButton(
                    onClick = {
                        onUrgeOvercome("Guided Breathing", "Used Box breathing to overcome impulse peak.")
                        urgeVictoryCelebration = true
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("urge_resisted_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = GlowGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("I Resisted The Urge! (Log Victory)")
                }

                if (urgeVictoryCelebration) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "🎉 Victory recorded in your local history! Strong discipline.",
                        style = MaterialTheme.typography.bodySmall,
                        color = GlowGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Random Motivational Quote Card
        val currentQuote = MotivationalContent.QUOTES[quoteIndex % MotivationalContent.QUOTES.size]
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("motivational_quote_card")
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = currentQuote.context.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                    }
                    IconButton(
                        onClick = { quoteIndex = (quoteIndex + 1) % MotivationalContent.QUOTES.size },
                        modifier = Modifier.size(32.dp).testTag("next_quote_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Autorenew,
                            contentDescription = "Next Quote",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                AnimatedContent(
                    targetState = currentQuote,
                    transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                    label = "quoteAnim"
                ) { quote ->
                    Column {
                        Text(
                            text = "\"${quote.quote}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "— ${quote.author}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Actionable Urge-Coping Tips & Grounding
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tactical Urge Coping",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            FilledTonalButton(
                onClick = { showGroundingDialog = true },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("grounding_trigger_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("5-4-3-2-1 Grounding")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tips List
        MotivationalContent.URGE_TIPS.forEach { tip ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = tip.iconEmoji, fontSize = 26.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = tip.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            ) {
                                Text(
                                    text = "${tip.timeMinutes}m",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tip.explanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }

    if (showGroundingDialog) {
        GroundingExerciseDialog(
            onDismiss = { showGroundingDialog = false },
            onCompleted = {
                onUrgeOvercome("5-4-3-2-1 Sensory Grounding", "Completed sensory grounding checklist.")
                urgeVictoryCelebration = true
            }
        )
    }
}
