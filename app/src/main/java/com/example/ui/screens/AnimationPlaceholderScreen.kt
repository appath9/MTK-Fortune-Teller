package com.example.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.FortuneDataProvider
import com.example.ui.theme.MysticGold
import com.example.ui.theme.MysticGoldBright
import com.example.ui.theme.MysticSecondaryViolet
import com.example.ui.theme.MysticSurface
import com.example.ui.theme.MysticSurfaceBorder
import com.example.ui.theme.MysticTextPrimary
import com.example.ui.theme.MysticTextSecondary
import com.example.util.HapticHelper
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

val boardTileColors = listOf(
    Color(0xFF3A3742),
    Color(0xFF494552),
    Color(0xFF5A5365),
    Color(0xFF6B6178),
    Color(0xFF514B5E)
)

data class BurmeseGridCell(
    val bgHex: String,
    val text: String
)

val BURMESE_GRID_DATA = listOf(
    BurmeseGridCell("#E50000", "၃"),
    BurmeseGridCell("#4A4A4A", "၁၀"),
    BurmeseGridCell("#FFB700", "၅"),
    BurmeseGridCell("#4A4A4A", "၁၀"),
    BurmeseGridCell("#C0C0C0", "၇"),
    BurmeseGridCell("#FFFFFF", ""),
    BurmeseGridCell("#FFB700", "၅"),
    BurmeseGridCell("#FFFFFF", ""),
    BurmeseGridCell("#C0C0C0", "၇"),
    BurmeseGridCell("#00E600", "၈"),
    BurmeseGridCell("#FF00FF", "၆"),
    BurmeseGridCell("#FFB6C1", "၄"),
    BurmeseGridCell("#FFB700", "၅"),
    BurmeseGridCell("#E50000", "၃"),
    BurmeseGridCell("#FFFF00", "၁"),
    BurmeseGridCell("#4A4A4A", "၁၀"),
    BurmeseGridCell("#00E600", "၈"),
    BurmeseGridCell("#FF00FF", "၆"),
    BurmeseGridCell("#C0C0C0", "၇"),
    BurmeseGridCell("#FFFFFF", ""),
    BurmeseGridCell("#C0C0C0", "၉"),
    BurmeseGridCell("#FFB6C1", "၄"),
    BurmeseGridCell("#C0C0C0", "၉"),
    BurmeseGridCell("#FF00FF", "၆"),
    BurmeseGridCell("#C0C0C0", "၉"),
    BurmeseGridCell("#FFB6C1", "၄"),
    BurmeseGridCell("#FFFF00", "၁"),
    BurmeseGridCell("#00E600", "၈"),
    BurmeseGridCell("#FFB700", "၅"),
    BurmeseGridCell("#4A4A4A", "၁၀"),
    BurmeseGridCell("#FF00FF", "၆"),
    BurmeseGridCell("#E50000", "၃"),
    BurmeseGridCell("#00E600", "၈"),
    BurmeseGridCell("#FFB6C1", "၄"),
    BurmeseGridCell("#FFFF00", "၁"),
    BurmeseGridCell("#FF00FF", "၆"),
    BurmeseGridCell("#E50000", "၃"),
    BurmeseGridCell("#FFFF00", "၁"),
    BurmeseGridCell("#C0C0C0", "၉"),
    BurmeseGridCell("#FFFF00", "၁"),
    BurmeseGridCell("#C0C0C0", "၉"),
    BurmeseGridCell("#C0C0C0", "၇"),
    BurmeseGridCell("#C0C0C0", "၉"),
    BurmeseGridCell("#C0C0C0", "၇"),
    BurmeseGridCell("#FFB700", "၅"),
    BurmeseGridCell("#FFFFFF", ""),
    BurmeseGridCell("#C0C0C0", "၇"),
    BurmeseGridCell("#FFB6C1", "၄"),
    BurmeseGridCell("#4A4A4A", "၁၀"),
    BurmeseGridCell("#FFB700", "၅"),
    BurmeseGridCell("#FFFFFF", ""),
    BurmeseGridCell("#00E600", "၈"),
    BurmeseGridCell("#E50000", "၃"),
    BurmeseGridCell("#4A4A4A", "၁၀"),
    BurmeseGridCell("#C0C0C0", "၇"),
    BurmeseGridCell("#FFB6C1", "၄"),
    BurmeseGridCell("#C0C0C0", "၉"),
    BurmeseGridCell("#FFFFFF", ""),
    BurmeseGridCell("#C0C0C0", "၉"),
    BurmeseGridCell("#FFB6C1", "၄"),
    BurmeseGridCell("#C0C0C0", "၉"),
    BurmeseGridCell("#FF00FF", "၆"),
    BurmeseGridCell("#FFFF00", "၁"),
    BurmeseGridCell("#FFFFFF", ""),
    BurmeseGridCell("#4A4A4A", "၁၀"),
    BurmeseGridCell("#00E600", "၈"),
    BurmeseGridCell("#C0C0C0", "၇"),
    BurmeseGridCell("#FFB700", "၅"),
    BurmeseGridCell("#E50000", "၃"),
    BurmeseGridCell("#FFB6C1", "၄"),
    BurmeseGridCell("#FFFFFF", ""),
    BurmeseGridCell("#4A4A4A", "၁၀"),
    BurmeseGridCell("#FFFF00", "၁"),
    BurmeseGridCell("#FF00FF", "၆"),
    BurmeseGridCell("#E50000", "၃"),
    BurmeseGridCell("#FF00FF", "၆"),
    BurmeseGridCell("#FFFF00", "၁"),
    BurmeseGridCell("#00E600", "၈"),
    BurmeseGridCell("#E50000", "၃"),
    BurmeseGridCell("#00E600", "၈"),
    BurmeseGridCell("#FFB700", "၅")
)

val BURMESE_BALL_DIGITS = listOf("၀", "၁", "၂", "၃", "၄", "၅", "၆", "၇", "၈", "၉")
val ENGLISH_BALL_DIGITS = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")

data class BallData(
    val id: Int,
    val text: String,
    val x: Float,
    val y: Float
)

private class PhysicsBall(
    val id: Int,
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val radius: Float
)

object CosmicAudioSynthesizer {
    fun playChimeSound() {
        Thread {
            try {
                val sampleRate = 44100
                val durationSec = 0.65
                val numSamples = (durationSec * sampleRate).toInt()
                val pcmBuffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val progress = i.toDouble() / numSamples
                    val t = i.toDouble() / sampleRate

                    // Attack (first 5ms) + Exponential decay envelope
                    val attack = (progress * 50.0).coerceAtMost(1.0)
                    val decay = Math.exp(-4.2 * progress)
                    val env = attack * decay

                    // Dual oscillator with upward frequency ramp
                    val f1 = 528.0 + (progress * 140.0) // 528Hz Solfeggio frequency
                    val f2 = 792.0 + (progress * 210.0) // 792Hz harmonic fifth

                    val osc1 = Math.sin(2.0 * PI * f1 * t)
                    val osc2 = Math.sin(2.0 * PI * f2 * t) * 0.65

                    val mixed = ((osc1 + osc2) / 1.65) * env * 26000.0
                    pcmBuffer[i] = mixed.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(pcmBuffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(pcmBuffer, 0, pcmBuffer.size)
                audioTrack.play()
                Thread.sleep(700)
                audioTrack.release()
            } catch (_: Exception) {
                // Audio synthesis fallback
            }
        }.start()
    }
}

@Composable
fun AnimationPlaceholderScreen(
    currentLang: String,
    selectedQuestionId: String?,
    isSubmittingTransaction: Boolean = false,
    attestationStatusText: String? = null,
    onBallSelected: (Int) -> Unit = {},
    onNumberSquareClick: () -> Unit = { onBallSelected((0..9).random()) },
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBurmese = currentLang == "mm"
    val question = remember(selectedQuestionId) {
        selectedQuestionId?.let { FortuneDataProvider.getQuestionById(it) }
    }

    var isRunning by remember { mutableStateOf(true) }
    var lastShakeTime by remember { mutableStateOf(0L) }
    var isShakingPulse by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    // Automatically recover and re-enable selection when a transaction completes without navigation (e.g. cancelled/failed)
    LaunchedEffect(isSubmittingTransaction) {
        if (!isSubmittingTransaction && !isRunning) {
            isRunning = true
        }
    }

    val processSelection: (Int) -> Unit = remember(isSubmittingTransaction) {
        { chosenNumber ->
            if (isRunning && !isSubmittingTransaction) {
                isRunning = false
                CosmicAudioSynthesizer.playChimeSound()
                onBallSelected(chosenNumber)
            }
        }
    }

    DisposableEffect(lifecycleOwner, isRunning) {
        if (!isRunning) return@DisposableEffect onDispose {}

        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null || !isRunning) return
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]

                    val gForce = sqrt((x * x + y * y + z * z).toDouble()) / SensorManager.GRAVITY_EARTH
                    if (gForce > 2.2) {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastShakeTime > 1500) {
                            lastShakeTime = currentTime
                            isShakingPulse = true
                            HapticHelper.playShakeHaptic(context)
                            val randomNum = (0..9).random()
                            processSelection(randomNum)
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (sensorManager != null && accelerometer != null) {
                        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI)
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    sensorManager?.unregisterListener(sensorEventListener)
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager?.unregisterListener(sensorEventListener)
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("animation_placeholder_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top navigation bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.04f))
                    .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                    .testTag("back_to_questions_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MysticTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = if (isBurmese) "ဂဏန်းဗေဒင် ရွေးချယ်ခြင်း" else "Cosmic Vortex Oracle",
                    color = MysticTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isBurmese) "စကြာဝဠာ၏ ရွေးချယ်မှုကို ခံယူပါ" else "Let the universe choose for you",
                    color = MysticTextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        // Active Question Banner
        if (question != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MysticSurface.copy(alpha = 0.85f))
                    .border(1.dp, MysticGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = if (isBurmese) question.mm else question.en,
                    color = MysticGold,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Loading status indicator when transaction/attestation is submitting
        if (isSubmittingTransaction || attestationStatusText != null) {
            Spacer(modifier = Modifier.height(10.dp))
            val statusDisplay = attestationStatusText ?: (
                if (isBurmese) "Solana Devnet သို့ ပေးပို့နေပါသည်... 🔮" else "Sealing fate on Solana Devnet... 🔮"
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF231E2E))
                    .border(1.dp, Color(0xFF14F195), RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("solana_submitting_indicator"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFF14F195),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = statusDisplay,
                        color = Color(0xFF14F195),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Cosmic Vortex Simulation Arena (9x9 Background Grid + Physics Swirling Balls)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            CosmicVortexArena(
                isBurmese = isBurmese,
                isRunning = isRunning,
                isShakingPulse = isShakingPulse,
                onBallClicked = { chosenNumber ->
                    processSelection(chosenNumber)
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Merged Instruction Card — Shake Your Phone Recommendation with "Or tap a number" Subtitle
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF261F38),
                            Color(0xFF1B1726)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MysticGoldBright.copy(alpha = 0.8f),
                            MysticSecondaryViolet.copy(alpha = 0.5f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .testTag("shake_instruction_label")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MysticGold.copy(alpha = 0.15f))
                        .border(1.dp, MysticGoldBright.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Vibration,
                        contentDescription = null,
                        tint = MysticGoldBright,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isBurmese) "ဖုန်းကို လှုပ်ခါပါ" else "Shake your phone",
                            color = MysticTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MysticGold.copy(alpha = 0.2f))
                                .border(0.5.dp, MysticGoldBright.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isBurmese) "အကြံပြုချက်" else "RECOMMENDED",
                                color = MysticGoldBright,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isBurmese) "သို့မဟုတ် ဂဏန်းတစ်ခုကို နှိပ်ပါ" else "Or tap a number",
                        color = MysticTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CosmicVortexArena(
    isBurmese: Boolean,
    isRunning: Boolean,
    isShakingPulse: Boolean,
    onBallClicked: (Int) -> Unit
) {
    val density = LocalDensity.current
    val ballDiameter = 78.dp
    val ballRadiusPx = with(density) { (ballDiameter / 2).toPx() }

    val ballDigits = if (isBurmese) BURMESE_BALL_DIGITS else ENGLISH_BALL_DIGITS

    var widthPx by remember { mutableFloatStateOf(0f) }
    var heightPx by remember { mutableFloatStateOf(0f) }

    val scaleAnim by animateFloatAsState(
        targetValue = if (isShakingPulse) 1.05f else 1f,
        animationSpec = tween(300),
        label = "shake_pulse_scale"
    )

    // State list for rendering positions of all 10 balls
    val renderedBalls = remember {
        mutableStateListOf<BallData>().apply {
            repeat(10) { i ->
                add(BallData(id = i, text = ballDigits[i], x = 0f, y = 0f))
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .scale(scaleAnim)
            .onSizeChanged { size ->
                widthPx = size.width.toFloat()
                heightPx = size.height.toFloat()
            }
            .shadow(20.dp, RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF0D0C14))
            .border(
                width = if (isShakingPulse) 3.dp else 2.dp,
                brush = if (isShakingPulse) {
                    Brush.linearGradient(colors = listOf(Color.White, MysticGoldBright, Color.White))
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            MysticGoldBright,
                            MysticSecondaryViolet,
                            MysticGold
                        )
                    )
                },
                shape = RoundedCornerShape(22.dp)
            )
            .clipToBounds()
            .testTag("cosmic_vortex_arena")
    ) {
        // 1. Background 9x9 CSS Grid (81 cells)
        LazyVerticalGrid(
            columns = GridCells.Fixed(9),
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp)),
            userScrollEnabled = false
        ) {
            itemsIndexed(BURMESE_GRID_DATA) { index, cell ->
                val cellColor = boardTileColors[index % boardTileColors.size]
                val textColor = remember(cellColor) {
                    // High contrast text based on luminance
                    val luminance = (0.299 * cellColor.red + 0.587 * cellColor.green + 0.114 * cellColor.blue)
                    if (luminance > 0.55) Color(0xFF101010) else Color(0xFFFAFAFA)
                }

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .background(cellColor)
                        .border(0.5.dp, Color(0x33000000)),
                    contentAlignment = Alignment.Center
                ) {
                    if (cell.text.isNotEmpty()) {
                        Text(
                            text = cell.text,
                            color = textColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Atmospheric semi-transparent cosmic vortex glow overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x33673AB7),
                            Color(0x770D0C14)
                        )
                    )
                )
        )

        // 2 & 3. Physics Simulation Engine Loop
        LaunchedEffect(widthPx, heightPx, isRunning) {
            if (widthPx <= 0f || heightPx <= 0f || !isRunning) return@LaunchedEffect

            // Initialize 10 balls in a graceful orbital ring
            val physicsBalls = List(10) { i ->
                val angle = (i * 2.0 * PI / 10.0).toFloat()
                val ringDist = widthPx * 0.32f
                val initX = (widthPx / 2f) + ringDist * cos(angle)
                val initY = (heightPx / 2f) + ringDist * sin(angle)
                val initVx = -sin(angle) * 4.5f
                val initVy = cos(angle) * 4.5f
                PhysicsBall(
                    id = i,
                    x = initX.coerceIn(ballRadiusPx, widthPx - ballRadiusPx),
                    y = initY.coerceIn(ballRadiusPx, heightPx - ballRadiusPx),
                    vx = initVx,
                    vy = initVy,
                    radius = ballRadiusPx
                )
            }

            var startNano = 0L

            while (isRunning) {
                withFrameNanos { nowNanos ->
                    if (startNano == 0L) startNano = nowNanos
                    val timeSec = (nowNanos - startNano) / 1_000_000_000.0f

                    // Vortex Center: Figure-8 wiggling motion
                    val vortexX = (widthPx / 2f) + cos(timeSec * 1.5f) * (widthPx * 0.22f)
                    val vortexY = (heightPx / 2f) + sin(timeSec * 2.1f) * (heightPx * 0.22f)

                    // Update each ball with forces & clamp speed
                    for (ball in physicsBalls) {
                        val dx = vortexX - ball.x
                        val dy = vortexY - ball.y
                        val dist = sqrt(dx * dx + dy * dy).coerceAtLeast(10f)

                        // Inward radial pull
                        val radialAccel = 0.16f
                        val radFx = (dx / dist) * radialAccel
                        val radFy = (dy / dist) * radialAccel

                        // Tangential swirl force (clockwise)
                        val swirlAccel = 0.32f
                        val tanFx = (-dy / dist) * swirlAccel
                        val tanFy = (dx / dist) * swirlAccel

                        ball.vx += radFx + tanFx
                        ball.vy += radFy + tanFy

                        // Clamping speed strictly between 3.2 and 6.8 pixels per frame
                        val currentSpeed = sqrt(ball.vx * ball.vx + ball.vy * ball.vy)
                        val minSpeed = 3.2f
                        val maxSpeed = 6.8f
                        if (currentSpeed > 0f) {
                            val clamped = currentSpeed.coerceIn(minSpeed, maxSpeed)
                            ball.vx = (ball.vx / currentSpeed) * clamped
                            ball.vy = (ball.vy / currentSpeed) * clamped
                        }

                        // Apply velocity
                        ball.x += ball.vx
                        ball.y += ball.vy

                        // Wall boundary bounces
                        if (ball.x - ball.radius < 0f) {
                            ball.x = ball.radius
                            ball.vx = abs(ball.vx)
                        } else if (ball.x + ball.radius > widthPx) {
                            ball.x = widthPx - ball.radius
                            ball.vx = -abs(ball.vx)
                        }

                        if (ball.y - ball.radius < 0f) {
                            ball.y = ball.radius
                            ball.vy = abs(ball.vy)
                        } else if (ball.y + ball.radius > heightPx) {
                            ball.y = heightPx - ball.radius
                            ball.vy = -abs(ball.vy)
                        }
                    }

                    // Ball-to-ball elastic collisions (no overlapping)
                    for (i in 0 until physicsBalls.size) {
                        for (j in i + 1 until physicsBalls.size) {
                            val b1 = physicsBalls[i]
                            val b2 = physicsBalls[j]
                            val cdx = b2.x - b1.x
                            val cdy = b2.y - b1.y
                            val dist = sqrt(cdx * cdx + cdy * cdy)
                            val minDist = b1.radius + b2.radius

                            if (dist < minDist && dist > 0.001f) {
                                // Separate balls to prevent overlap
                                val overlap = (minDist - dist) * 0.5f
                                val nx = cdx / dist
                                val ny = cdy / dist

                                b1.x -= nx * overlap
                                b1.y -= ny * overlap
                                b2.x += nx * overlap
                                b2.y += ny * overlap

                                // Elastic collision velocity impulse
                                val kx = b1.vx - b2.vx
                                val ky = b1.vy - b2.vy
                                val p = nx * kx + ny * ky
                                if (p > 0f) {
                                    b1.vx -= p * nx
                                    b1.vy -= p * ny
                                    b2.vx += p * nx
                                    b2.vy += p * ny
                                }
                            }
                        }
                    }

                    // Update UI state for rendering
                    physicsBalls.forEachIndexed { index, ball ->
                        if (index < renderedBalls.size) {
                            val cur = renderedBalls[index]
                            renderedBalls[index] = cur.copy(
                                text = ballDigits[cur.id],
                                x = ball.x - ball.radius,
                                y = ball.y - ball.radius
                            )
                        }
                    }
                }
            }
        }

        // Render the 10 floating interactive target numbers
        renderedBalls.forEach { ball ->
            val displayText = ballDigits.getOrElse(ball.id) { ball.id.toString() }

            Box(
                modifier = Modifier
                    .offset { IntOffset(ball.x.roundToInt(), ball.y.roundToInt()) }
                    .size(78.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFB79AF4).copy(alpha = 0.22f))
                    .border(
                        1.5.dp,
                        Color(0xFFD2B9FF).copy(alpha = 0.82f),
                        CircleShape
                    )
                    .defaultMinSize(48.dp, 48.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = MysticGoldBright),
                        onClick = { onBallClicked(ball.id) }
                    )
                    .testTag("cosmic_ball_${ball.id}"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = displayText,
                    fontSize = 24.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Normal,
                    color = MysticTextSecondary.copy(alpha = 0.45f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
