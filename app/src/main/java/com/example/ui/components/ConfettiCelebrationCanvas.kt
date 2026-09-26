package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.ui.theme.MysticGold
import com.example.ui.theme.MysticGoldBright
import com.example.ui.theme.MysticPrimaryPurple
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private enum class ConfettiShape {
    RECTANGLE,
    CIRCLE,
    STAR,
    RIBBON
}

private data class ConfettiParticle(
    val startXRatio: Float,
    val startYRatio: Float,
    val velocityX: Float,
    val velocityY: Float,
    val gravity: Float,
    val swayAmplitude: Float,
    val swayFrequency: Float,
    val rotationSpeed: Float,
    val initialRotation: Float,
    val color: Color,
    val width: Float,
    val height: Float,
    val shape: ConfettiShape,
    val delayOffset: Float // 0f to 0.25f stagger
)

@Composable
fun ConfettiCelebrationCanvas(
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }
    val confettiColors = remember {
        listOf(
            MysticPrimaryPurple,
            Color(0xFFE8DEF8),
            MysticGold,
            MysticGoldBright,
            Color.White,
            Color(0xFFFFD54F),
            Color(0xFFCE93D8)
        )
    }

    val particles = remember {
        val random = Random(12345)
        // 140 particles spread across the entire screen from multiple fountains (left, center, right, top drop)
        List(140) { index ->
            val originType = index % 4 // 0: Left burst, 1: Center fountain, 2: Right burst, 3: Top rain
            val startX: Float
            val startY: Float
            val vx: Float
            val vy: Float

            when (originType) {
                0 -> { // Left corner explosion towards center-right
                    startX = random.nextFloat() * 0.2f
                    startY = 0.4f + random.nextFloat() * 0.3f
                    vx = 200f + random.nextFloat() * 700f
                    vy = -(400f + random.nextFloat() * 600f)
                }
                1 -> { // Center blast expanding in all directions
                    startX = 0.35f + random.nextFloat() * 0.3f
                    startY = 0.3f + random.nextFloat() * 0.2f
                    val angle = Math.toRadians((random.nextFloat() * 360.0)).toFloat()
                    val speed = 250f + random.nextFloat() * 750f
                    vx = cos(angle) * speed
                    vy = sin(angle) * speed - 250f // Bias upward
                }
                2 -> { // Right corner explosion towards center-left
                    startX = 0.8f + random.nextFloat() * 0.2f
                    startY = 0.4f + random.nextFloat() * 0.3f
                    vx = -(200f + random.nextFloat() * 700f)
                    vy = -(400f + random.nextFloat() * 600f)
                }
                else -> { // Full-width top rain showering down
                    startX = random.nextFloat()
                    startY = -0.05f - random.nextFloat() * 0.15f
                    vx = (random.nextFloat() - 0.5f) * 300f
                    vy = 200f + random.nextFloat() * 450f
                }
            }

            val shapeType = when (random.nextInt(4)) {
                0 -> ConfettiShape.RECTANGLE
                1 -> ConfettiShape.CIRCLE
                2 -> ConfettiShape.STAR
                else -> ConfettiShape.RIBBON
            }

            // Larger, bolder particle dimensions for maximum visibility
            val baseSize = when (shapeType) {
                ConfettiShape.RIBBON -> Pair(16f + random.nextFloat() * 18f, 7f + random.nextFloat() * 6f)
                ConfettiShape.RECTANGLE -> Pair(14f + random.nextFloat() * 16f, 10f + random.nextFloat() * 10f)
                ConfettiShape.STAR -> Pair(18f + random.nextFloat() * 14f, 18f + random.nextFloat() * 14f)
                ConfettiShape.CIRCLE -> Pair(12f + random.nextFloat() * 12f, 12f + random.nextFloat() * 12f)
            }

            ConfettiParticle(
                startXRatio = startX,
                startYRatio = startY,
                velocityX = vx,
                velocityY = vy,
                gravity = 650f + random.nextFloat() * 350f,
                swayAmplitude = 25f + random.nextFloat() * 45f,
                swayFrequency = 3f + random.nextFloat() * 5f,
                rotationSpeed = (random.nextFloat() - 0.5f) * 900f,
                initialRotation = random.nextFloat() * 360f,
                color = confettiColors[random.nextInt(confettiColors.size)],
                width = baseSize.first,
                height = baseSize.second,
                shape = shapeType,
                delayOffset = random.nextFloat() * 0.15f
            )
        }
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3200, easing = FastOutSlowInEasing)
        )
    }

    if (progress.value < 1f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height
            val globalTime = progress.value

            particles.forEach { particle ->
                // Account for staggered start
                val localTime = ((globalTime - particle.delayOffset) / (1f - particle.delayOffset)).coerceIn(0f, 1f)
                if (localTime <= 0f) return@forEach

                // Alpha fade out towards the end of lifetime
                val alpha = if (localTime > 0.75f) {
                    ((1f - localTime) / 0.25f).coerceIn(0f, 1f)
                } else {
                    (localTime / 0.1f).coerceIn(0f, 1f)
                }

                val originX = canvasW * particle.startXRatio
                val originY = canvasH * particle.startYRatio

                // Physics simulation: Initial velocity + gravity + horizontal sinusoidal wind sway
                val sway = sin(localTime * particle.swayFrequency * Math.PI.toFloat()) * particle.swayAmplitude
                val currentX = originX + (particle.velocityX * localTime) + sway
                val currentY = originY + (particle.velocityY * localTime) + (0.5f * particle.gravity * localTime * localTime)
                val currentRotation = particle.initialRotation + (particle.rotationSpeed * localTime)

                // Render only if within visible canvas bounds plus margin
                if (currentX in -60f..(canvasW + 60f) && currentY in -60f..(canvasH + 60f)) {
                    rotate(degrees = currentRotation, pivot = Offset(currentX, currentY)) {
                        val drawColor = particle.color.copy(alpha = (particle.color.alpha * alpha).coerceIn(0f, 1f))
                        when (particle.shape) {
                            ConfettiShape.CIRCLE -> {
                                drawCircle(
                                    color = drawColor,
                                    radius = particle.width / 2f,
                                    center = Offset(currentX, currentY)
                                )
                            }
                            ConfettiShape.RECTANGLE, ConfettiShape.RIBBON -> {
                                drawRect(
                                    color = drawColor,
                                    topLeft = Offset(currentX - particle.width / 2f, currentY - particle.height / 2f),
                                    size = Size(particle.width, particle.height)
                                )
                            }
                            ConfettiShape.STAR -> {
                                val path = createStarPath(currentX, currentY, particle.width / 2f, particle.width / 4f)
                                drawPath(path = path, color = drawColor)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun createStarPath(cx: Float, cy: Float, outerRadius: Float, innerRadius: Float): Path {
    val path = Path()
    val points = 5
    var angle = -Math.PI / 2.0
    val step = Math.PI / points

    val firstX = (cx + cos(angle) * outerRadius).toFloat()
    val firstY = (cy + sin(angle) * outerRadius).toFloat()
    path.moveTo(firstX, firstY)

    for (i in 1 until points * 2) {
        angle += step
        val r = if (i % 2 == 1) innerRadius else outerRadius
        val x = (cx + cos(angle) * r).toFloat()
        val y = (cy + sin(angle) * r).toFloat()
        path.lineTo(x, y)
    }
    path.close()
    return path
}
