package com.bestie.sipkitty.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bestie.sipkitty.ui.theme.AccentHeart
import com.bestie.sipkitty.ui.theme.GoldStar
import com.bestie.sipkitty.ui.theme.SakuraPink
import com.bestie.sipkitty.ui.theme.SoftPink
import com.bestie.sipkitty.ui.theme.TextPrimary
import com.bestie.sipkitty.ui.theme.WaterBlue
import kotlinx.coroutines.launch
import kotlin.math.sin

enum class KittenMood {
    THIRSTY,
    HAPPY,
    CELEBRATING
}

@Composable
fun KittenMascot(
    progress: Float,
    bestieName: String = "Bestie",
    modifier: Modifier = Modifier
) {
    val mood = when {
        progress >= 1.0f -> KittenMood.CELEBRATING
        progress >= 0.35f -> KittenMood.HAPPY
        else -> KittenMood.THIRSTY
    }

    val bounceScale = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()
    var clickMessage by remember { mutableStateOf<String?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "kitty_idle")
    val idleOffsetY by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_bounce"
    )

    val tailAngle by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tail_wag"
    )

    val speechText = clickMessage ?: when (mood) {
        KittenMood.THIRSTY -> "So thirsty... give me water, $bestieName! 🥺"
        KittenMood.HAPPY -> "Purrr~ feeling hydrated and cute! 🐾"
        KittenMood.CELEBRATING -> "Yay! Daily goal crushed! You're glowing! 👑✨"
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Speech Bubble
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SoftPink.copy(alpha = 0.55f)),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = speechText,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Interactive Kitten Canvas
        Box(
            modifier = Modifier
                .size(190.dp)
                .scale(bounceScale.value)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    coroutineScope.launch {
                        clickMessage = listOf(
                            "Meow! I love you, $bestieName! 💖",
                            "Sip sip hooray! 🥤",
                            "*purrrr* 🐾",
                            "More water = more energy! ✨"
                        ).random()

                        bounceScale.animateTo(
                            0.88f,
                            animationSpec = tween(100, easing = FastOutSlowInEasing)
                        )
                        bounceScale.animateTo(
                            1.12f,
                            animationSpec = tween(150, easing = FastOutSlowInEasing)
                        )
                        bounceScale.animateTo(
                            1.0f,
                            animationSpec = tween(120, easing = FastOutSlowInEasing)
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(180.dp)) {
                val cx = size.width / 2f
                val cy = size.height / 2f + idleOffsetY

                drawKitten(
                    cx = cx,
                    cy = cy,
                    mood = mood,
                    tailAngle = tailAngle
                )
            }
        }
    }
}

private fun DrawScope.drawKitten(
    cx: Float,
    cy: Float,
    mood: KittenMood,
    tailAngle: Float
) {
    val furColor = Color(0xFFFFFDF9) // Soft cream white kitten
    val outlineColor = Color(0xFF6D4C41) // Soft warm brown outline
    val earInnerColor = SakuraPink
    val strokeWidth = 5f

    // 1. Kitten Tail (Wagging behind body)
    val tailPath = Path().apply {
        moveTo(cx + 45f, cy + 40f)
        quadraticTo(
            cx + 70f + tailAngle,
            cy + 10f,
            cx + 55f + tailAngle,
            cy - 10f
        )
    }
    drawPath(
        path = tailPath,
        color = furColor,
        style = Stroke(width = 16f, cap = StrokeCap.Round)
    )
    drawPath(
        path = tailPath,
        color = outlineColor,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )

    // 2. Kitten Body
    val bodyRadiusX = 52f
    val bodyRadiusY = 48f
    drawOval(
        color = furColor,
        topLeft = Offset(cx - bodyRadiusX, cy - 10f),
        size = Size(bodyRadiusX * 2, bodyRadiusY * 2)
    )
    drawOval(
        color = outlineColor,
        topLeft = Offset(cx - bodyRadiusX, cy - 10f),
        size = Size(bodyRadiusX * 2, bodyRadiusY * 2),
        style = Stroke(width = strokeWidth)
    )

    // 3. Kitten Ears
    val leftEarPath = Path().apply {
        moveTo(cx - 45f, cy - 30f)
        lineTo(cx - 55f, cy - 75f)
        lineTo(cx - 15f, cy - 50f)
        close()
    }
    val rightEarPath = Path().apply {
        moveTo(cx + 15f, cy - 50f)
        lineTo(cx + 55f, cy - 75f)
        lineTo(cx + 45f, cy - 30f)
        close()
    }

    // Draw ears fill and inner pink
    drawPath(leftEarPath, color = furColor)
    drawPath(leftEarPath, color = outlineColor, style = Stroke(width = strokeWidth))
    val leftInnerEar = Path().apply {
        moveTo(cx - 40f, cy - 35f)
        lineTo(cx - 50f, cy - 65f)
        lineTo(cx - 22f, cy - 48f)
        close()
    }
    drawPath(leftInnerEar, color = earInnerColor)

    drawPath(rightEarPath, color = furColor)
    drawPath(rightEarPath, color = outlineColor, style = Stroke(width = strokeWidth))
    val rightInnerEar = Path().apply {
        moveTo(cx + 22f, cy - 48f)
        lineTo(cx + 50f, cy - 65f)
        lineTo(cx + 40f, cy - 35f)
        close()
    }
    drawPath(rightInnerEar, color = earInnerColor)

    // 4. Kitten Head
    val headRadius = 48f
    val headCenterY = cy - 25f
    drawCircle(
        color = furColor,
        radius = headRadius,
        center = Offset(cx, headCenterY)
    )
    drawCircle(
        color = outlineColor,
        radius = headRadius,
        center = Offset(cx, headCenterY),
        style = Stroke(width = strokeWidth)
    )

    // 5. Blushing Cheeks
    val blushColor = SakuraPink.copy(alpha = 0.85f)
    drawOval(
        color = blushColor,
        topLeft = Offset(cx - 40f, headCenterY + 4f),
        size = Size(18f, 11f)
    )
    drawOval(
        color = blushColor,
        topLeft = Offset(cx + 22f, headCenterY + 4f),
        size = Size(18f, 11f)
    )

    // 6. Eyes according to mood
    when (mood) {
        KittenMood.THIRSTY -> {
            // Sleepy / drooping closed curved lines ( ◡ _ ◡ )
            val leftEye = Path().apply {
                moveTo(cx - 30f, headCenterY + 2f)
                quadraticTo(cx - 20f, headCenterY + 10f, cx - 10f, headCenterY + 2f)
            }
            val rightEye = Path().apply {
                moveTo(cx + 10f, headCenterY + 2f)
                quadraticTo(cx + 20f, headCenterY + 10f, cx + 30f, headCenterY + 2f)
            }
            drawPath(leftEye, color = outlineColor, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
            drawPath(rightEye, color = outlineColor, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
        }
        KittenMood.HAPPY -> {
            // Happy cheerful closed arches ( ^ • ﻌ • ^ )
            val leftEye = Path().apply {
                moveTo(cx - 30f, headCenterY + 5f)
                quadraticTo(cx - 20f, headCenterY - 6f, cx - 10f, headCenterY + 5f)
            }
            val rightEye = Path().apply {
                moveTo(cx + 10f, headCenterY + 5f)
                quadraticTo(cx + 20f, headCenterY - 6f, cx + 30f, headCenterY + 5f)
            }
            drawPath(leftEye, color = outlineColor, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
            drawPath(rightEye, color = outlineColor, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
        }
        KittenMood.CELEBRATING -> {
            // Big shiny joyful eyes with white catchlight!
            drawCircle(color = outlineColor, radius = 7f, center = Offset(cx - 20f, headCenterY))
            drawCircle(color = Color.White, radius = 2.5f, center = Offset(cx - 22f, headCenterY - 2f))

            drawCircle(color = outlineColor, radius = 7f, center = Offset(cx + 20f, headCenterY))
            drawCircle(color = Color.White, radius = 2.5f, center = Offset(cx + 18f, headCenterY - 2f))
        }
    }

    // 7. Cute Pink Nose & Whiskers
    val noseY = headCenterY + 8f
    val nosePath = Path().apply {
        moveTo(cx - 4f, noseY)
        lineTo(cx + 4f, noseY)
        lineTo(cx, noseY + 4f)
        close()
    }
    drawPath(nosePath, color = SakuraPink)

    // Kitten mouth :3
    val mouthPath = Path().apply {
        moveTo(cx - 7f, noseY + 7f)
        quadraticTo(cx - 3f, noseY + 11f, cx, noseY + 6f)
        quadraticTo(cx + 3f, noseY + 11f, cx + 7f, noseY + 7f)
    }
    drawPath(mouthPath, color = outlineColor, style = Stroke(width = 3.5f, cap = StrokeCap.Round))

    // Whiskers
    drawLine(
        color = outlineColor.copy(alpha = 0.7f),
        start = Offset(cx - 36f, headCenterY + 2f),
        end = Offset(cx - 56f, headCenterY),
        strokeWidth = 2.5f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = outlineColor.copy(alpha = 0.7f),
        start = Offset(cx - 36f, headCenterY + 8f),
        end = Offset(cx - 54f, headCenterY + 12f),
        strokeWidth = 2.5f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = outlineColor.copy(alpha = 0.7f),
        start = Offset(cx + 36f, headCenterY + 2f),
        end = Offset(cx + 56f, headCenterY),
        strokeWidth = 2.5f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = outlineColor.copy(alpha = 0.7f),
        start = Offset(cx + 36f, headCenterY + 8f),
        end = Offset(cx + 54f, headCenterY + 12f),
        strokeWidth = 2.5f,
        cap = StrokeCap.Round
    )

    // 8. Front Paws
    drawRoundRect(
        color = furColor,
        topLeft = Offset(cx - 26f, cy + 50f),
        size = Size(20f, 18f),
        cornerRadius = CornerRadius(10f, 10f)
    )
    drawRoundRect(
        color = outlineColor,
        topLeft = Offset(cx - 26f, cy + 50f),
        size = Size(20f, 18f),
        cornerRadius = CornerRadius(10f, 10f),
        style = Stroke(width = strokeWidth)
    )

    drawRoundRect(
        color = furColor,
        topLeft = Offset(cx + 6f, cy + 50f),
        size = Size(20f, 18f),
        cornerRadius = CornerRadius(10f, 10f)
    )
    drawRoundRect(
        color = outlineColor,
        topLeft = Offset(cx + 6f, cy + 50f),
        size = Size(20f, 18f),
        cornerRadius = CornerRadius(10f, 10f),
        style = Stroke(width = strokeWidth)
    )

    // 9. Extra accessories based on mood
    if (mood == KittenMood.CELEBRATING) {
        // Golden Princess Crown on head
        val crownPath = Path().apply {
            moveTo(cx - 22f, headCenterY - 45f)
            lineTo(cx - 26f, headCenterY - 65f)
            lineTo(cx - 10f, headCenterY - 55f)
            lineTo(cx, headCenterY - 70f)
            lineTo(cx + 10f, headCenterY - 55f)
            lineTo(cx + 26f, headCenterY - 65f)
            lineTo(cx + 22f, headCenterY - 45f)
            close()
        }
        drawPath(crownPath, color = GoldStar)
        drawPath(crownPath, color = Color(0xFFF57F17), style = Stroke(width = 3.5f))

        // Floating sparkles / hearts
        drawCircle(color = AccentHeart, radius = 5f, center = Offset(cx - 55f, headCenterY - 30f))
        drawCircle(color = AccentHeart, radius = 4f, center = Offset(cx + 55f, headCenterY - 25f))
    } else if (mood == KittenMood.THIRSTY) {
        // Empty cute blue water bowl in front
        val bowlTop = cy + 46f
        drawArc(
            color = WaterBlue.copy(alpha = 0.4f),
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(cx - 25f, bowlTop),
            size = Size(50f, 32f)
        )
        drawArc(
            color = outlineColor,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(cx - 25f, bowlTop),
            size = Size(50f, 32f),
            style = Stroke(width = 3.5f)
        )
    }
}
