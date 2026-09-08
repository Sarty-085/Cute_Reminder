package com.bestie.sipkitty.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bestie.sipkitty.ui.theme.CardSurface
import com.bestie.sipkitty.ui.theme.TextPrimary
import com.bestie.sipkitty.ui.theme.TextSecondary
import com.bestie.sipkitty.ui.theme.WaterBlue
import com.bestie.sipkitty.ui.theme.WaterBlueWave
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun WaterReservoir(
    currentMl: Int,
    targetMl: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (targetMl > 0) (currentMl.toFloat() / targetMl).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "water_fill_anim"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "wave_anim")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    val bubbleOffsetY by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubble_rise"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .shadow(6.dp, shape = RoundedCornerShape(26.dp)),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Wave and water background
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(26.dp))
            ) {
                val width = size.width
                val height = size.height
                val waterLevel = height * (1f - animatedProgress)

                // Background subtle fill container
                drawRoundRect(
                    color = Color(0xFFF0F9FF),
                    size = size,
                    cornerRadius = CornerRadius(26.dp.toPx(), 26.dp.toPx())
                )

                if (animatedProgress > 0.005f) {
                    // Back wave
                    val backWavePath = Path().apply {
                        moveTo(0f, height)
                        lineTo(0f, waterLevel)
                        val waveAmplitude = 14f * animatedProgress
                        for (x in 0..width.toInt() step 10) {
                            val xF = x.toFloat()
                            val y = waterLevel + waveAmplitude * sin((xF / width * 2 * PI + wavePhase + 1f)).toFloat()
                            lineTo(xF, y)
                        }
                        lineTo(width, height)
                        close()
                    }
                    drawPath(backWavePath, color = WaterBlueWave.copy(alpha = 0.5f))

                    // Front wave
                    val frontWavePath = Path().apply {
                        moveTo(0f, height)
                        lineTo(0f, waterLevel)
                        val waveAmplitude = 12f * animatedProgress
                        for (x in 0..width.toInt() step 10) {
                            val xF = x.toFloat()
                            val y = waterLevel + waveAmplitude * sin((xF / width * 2 * PI + wavePhase)).toFloat()
                            lineTo(xF, y)
                        }
                        lineTo(width, height)
                        close()
                    }
                    drawPath(frontWavePath, color = WaterBlue.copy(alpha = 0.85f))

                    // Bubbles rising
                    val bubbleX1 = width * 0.25f
                    val bubbleY1 = waterLevel + (height - waterLevel) * bubbleOffsetY
                    drawCircle(Color.White.copy(alpha = 0.7f), radius = 5f, center = Offset(bubbleX1, bubbleY1))

                    val bubbleX2 = width * 0.65f
                    val bubbleY2 = waterLevel + (height - waterLevel) * ((bubbleOffsetY + 0.4f) % 1f)
                    drawCircle(Color.White.copy(alpha = 0.6f), radius = 8f, center = Offset(bubbleX2, bubbleY2))

                    val bubbleX3 = width * 0.82f
                    val bubbleY3 = waterLevel + (height - waterLevel) * ((bubbleOffsetY + 0.7f) % 1f)
                    drawCircle(Color.White.copy(alpha = 0.5f), radius = 4f, center = Offset(bubbleX3, bubbleY3))
                }

                // Border outline
                drawRoundRect(
                    color = Color(0xFFE2E8F0),
                    size = size,
                    cornerRadius = CornerRadius(26.dp.toPx(), 26.dp.toPx()),
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Stats overlay on top of water
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Today's Hydration",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$currentMl / $targetMl ml",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                val percent = (progress * 100).toInt()
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (progress >= 1f) Color(0xFFE8F5E9) else Color(0xFFE1F5FE)
                    )
                ) {
                    Text(
                        text = if (progress >= 1f) "Goal Reached! 🌟 $percent%" else "$percent% completed 💧",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (progress >= 1f) Color(0xFF2E7D32) else WaterBlue,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
