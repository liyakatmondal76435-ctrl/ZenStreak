package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MintBadgeGreen
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ScallopedBadge(
    countText: String,
    unitText: String = "pcs.",
    size: Dp = 80.dp,
    badgeColor: Color = MintBadgeGreen,
    textColor: Color = Color(0xFF13382D),
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val width = this.size.width
            val height = this.size.height
            val radius = width / 2f
            val centerX = width / 2f
            val centerY = height / 2f
            val numPetals = 12
            val path = Path()

            val angleStep = (2 * Math.PI / numPetals).toFloat()
            for (i in 0 until numPetals) {
                val angle = i * angleStep
                val nextAngle = (i + 1) * angleStep
                val midAngle = angle + angleStep / 2f

                val outerR = radius
                val innerR = radius * 0.82f

                val x1 = centerX + outerR * cos(angle)
                val y1 = centerY + outerR * sin(angle)
                val xMid = centerX + innerR * cos(midAngle)
                val yMid = centerY + innerR * sin(midAngle)
                val x2 = centerX + outerR * cos(nextAngle)
                val y2 = centerY + outerR * sin(nextAngle)

                if (i == 0) {
                    path.moveTo(x1, y1)
                }
                path.quadraticTo(xMid, yMid, x2, y2)
            }
            path.close()
            drawPath(path, color = badgeColor)
        }

        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = countText,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = textColor,
                lineHeight = 24.sp
            )
            Text(
                text = unitText,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor.copy(alpha = 0.8f)
            )
        }
    }
}
