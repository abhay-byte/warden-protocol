package com.wardenprotocol.game.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wardenprotocol.game.data.model.ColonyOutcome
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

private val ProcessingBackground = Color(0xFF121414)
private val ProcessingPrimary = Color(0xFFFFD597)
private val ProcessingSecondary = Color(0xFF9EFF8B)
private val ProcessingCyan = Color(0xFF88E1FF)
private val ProcessingSurfaceHigh = Color(0xFF282A2A)
private val ProcessingSurfaceLow = Color(0xFF0D0F0F)
private val ProcessingText = Color(0xFFE2E2E2)
private val ProcessingMuted = Color(0xFFD7C4AC)

private data class ProcessingAnimationFrame(
    val pulse: Float = 0.7f,
    val sweep: Float = 0f,
    val progress: Float = 0.14f,
    val tickerDrift: Float = 0f,
    val coreRotation: Float = 0f,
    val coreOrbit: Float = 0f
) {
    companion object {
        fun fromElapsedMs(elapsedMs: Float): ProcessingAnimationFrame {
            val pulseCycle = ((elapsedMs % 1200f) / 1200f)
            val pulseWave = if (pulseCycle < 0.5f) pulseCycle * 2f else (1f - pulseCycle) * 2f
            val orbitCycle = ((elapsedMs % 900f) / 900f)
            val orbitWave = if (orbitCycle < 0.5f) orbitCycle * 2f else (1f - orbitCycle) * 2f
            val driftCycle = ((elapsedMs % 1800f) / 1800f)
            val driftWave = if (driftCycle < 0.5f) driftCycle * 2f else (1f - driftCycle) * 2f
            return ProcessingAnimationFrame(
                pulse = 0.35f + (pulseWave * 0.65f),
                sweep = -0.25f + (((elapsedMs % 2200f) / 2200f) * 1.5f),
                progress = 0.14f + (((elapsedMs % 2600f) / 2600f) * 0.82f),
                tickerDrift = -18f + (driftWave * 36f),
                coreRotation = ((elapsedMs % 1400f) / 1400f) * 360f,
                coreOrbit = orbitWave
            )
        }
    }
}

@Composable
fun EndingProcessingScreen(
    outcome: ColonyOutcome,
    modifier: Modifier = Modifier
) {
    val animation by produceState(initialValue = ProcessingAnimationFrame()) {
        val startNanos = withFrameNanos { it }
        while (true) {
            val frameNanos = withFrameNanos { it }
            val elapsedMs = (frameNanos - startNanos) / 1_000_000f
            value = ProcessingAnimationFrame.fromElapsedMs(elapsedMs)
        }
    }
    val phase = ((animation.progress * 12).toInt() % 4)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF101316),
                        ProcessingBackground,
                        ProcessingSurfaceLow
                    )
                )
            )
    ) {
        ProcessingGridOverlay()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            ProcessingHeader()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            drawLine(
                                color = ProcessingSecondary,
                                start = Offset(0f, 0f),
                                end = Offset(0f, size.height),
                                strokeWidth = 6f
                            )
                        }
                        .padding(start = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "SETTLEMENT DOSSIER OPEN",
                        style = MaterialTheme.typography.labelLarge,
                        color = ProcessingSecondary,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "COMPILING LONG-RANGE OUTLOOK",
                        style = MaterialTheme.typography.displaySmall,
                        color = ProcessingText,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = "Command is reconstructing what the colony becomes after the vault opens. Decade markers, terminal state, and the final settlement chronicle are still being assembled.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = ProcessingMuted
                    )
                }

                ProcessingCoreLoader(
                    rotation = animation.coreRotation,
                    pulse = animation.pulse,
                    orbit = animation.coreOrbit
                )

                ProcessingSignalSweep(
                    sweep = animation.sweep,
                    pulse = animation.pulse,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProcessingMiniCard(
                        label = "SETTLEMENT",
                        value = outcome.settlementName,
                        accent = ProcessingPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    ProcessingMiniCard(
                        label = "CLASSIFICATION",
                        value = outcome.classification,
                        accent = ProcessingCyan,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProcessingMiniCard(
                        label = "SURVIVORS",
                        value = outcome.detailedStats?.survivors?.toString() ?: "0",
                        accent = ProcessingSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    ProcessingMiniCard(
                        label = "YEARS SEALED",
                        value = outcome.detailedStats?.yearsSinceWar?.toString() ?: "0",
                        accent = ProcessingPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    ProcessingMiniCard(
                        label = "TARGET SITE",
                        value = outcome.detailedStats?.locationName ?: "UNKNOWN",
                        accent = ProcessingCyan,
                        modifier = Modifier.weight(1.2f)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ProcessingSurfaceHigh)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Text(
                        text = "ARCHIVE COMPILATION",
                        style = MaterialTheme.typography.titleMedium,
                        color = ProcessingPrimary
                    )

                    ProcessingProgressRail(
                        progress = animation.progress,
                        sweep = animation.sweep,
                        pulse = animation.pulse
                    )

                    repeat(4) { index ->
                        val widthFraction = listOf(1f, 0.84f, 0.92f, 0.76f)[index]
                        val active = index <= phase
                        val barAlpha = animateFloatAsState(
                            targetValue = if (active) 1f else 0.34f,
                            animationSpec = tween(durationMillis = 320),
                            label = "processing_bar_$index"
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(max(0.32f, widthFraction * (0.45f + (animation.progress * 0.55f))))
                                .height(14.dp)
                                .alpha(barAlpha.value)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = if (active) {
                                            listOf(
                                                ProcessingSecondary.copy(alpha = 0.45f + (animation.pulse * 0.22f)),
                                                ProcessingPrimary.copy(alpha = 0.78f),
                                                ProcessingCyan.copy(alpha = 0.62f)
                                            )
                                        } else {
                                            listOf(
                                                ProcessingPrimary.copy(alpha = 0.10f),
                                                ProcessingSurfaceHigh,
                                                ProcessingSurfaceLow
                                            )
                                        }
                                    ),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .alpha(animation.pulse)
                                .background(ProcessingSecondary, RoundedCornerShape(99.dp))
                        )
                        Text(
                            text = processingStatusText(phase),
                            style = MaterialTheme.typography.bodyMedium,
                            color = ProcessingMuted
                        )
                    }

                    ProcessingWaitTicker(
                        message = "We'll get back to you. Just wait.",
                        drift = animation.tickerDrift,
                        pulse = animation.pulse
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ProcessingSurfaceLow.copy(alpha = 0.92f))
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProcessingLine("Travel Route", outcome.detailedStats?.travelRoute ?: "Unavailable")
                    ProcessingLine("Travel Risk", outcome.detailedStats?.travelRisk ?: "Unknown")
                    ProcessingLine("Vault Power", "${outcome.detailedStats?.powerGrid ?: 0}%")
                    ProcessingLine("Archive Integrity", "${(((outcome.detailedStats?.culturalArchive ?: 0) + (outcome.detailedStats?.scientificArchive ?: 0)) / 2)}%")
                }
            }
        }
    }
}

@Composable
private fun ProcessingHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ProcessingBackground)
            .drawBehind {
                val horizontalPx = 3.dp.toPx()
                val verticalPx = 4.dp.toPx()
                var y = 0f
                while (y < size.height) {
                    drawRect(
                        color = Color.Black.copy(alpha = 0.14f),
                        topLeft = Offset(0f, y),
                        size = Size(size.width, 1.dp.toPx())
                    )
                    y += horizontalPx
                }
                var x = 0f
                while (x < size.width) {
                    drawRect(
                        color = Color.Black.copy(alpha = 0.035f),
                        topLeft = Offset(x, 0f),
                        size = Size(1.dp.toPx(), size.height)
                    )
                    x += verticalPx
                }
            }
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Terminal,
                contentDescription = null,
                tint = ProcessingPrimary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "WARDEN_PROTOCOL_V1.0.4",
                style = MaterialTheme.typography.titleMedium,
                color = ProcessingPrimary,
                letterSpacing = (-0.5).sp
            )
        }

        Icon(
            imageVector = Icons.Filled.Tune,
            contentDescription = null,
            tint = ProcessingPrimary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun ProcessingMiniCard(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(ProcessingSurfaceHigh)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = ProcessingMuted.copy(alpha = 0.65f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = accent,
            maxLines = 2
        )
    }
}

@Composable
private fun ProcessingSignalSweep(
    sweep: Float,
    pulse: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(ProcessingSurfaceLow)
            .drawBehind {
                drawLine(
                    color = ProcessingPrimary.copy(alpha = 0.18f),
                    start = Offset(0f, size.height - 1.dp.toPx()),
                    end = Offset(size.width, size.height - 1.dp.toPx()),
                    strokeWidth = 2.dp.toPx()
                )
            }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerY = size.height / 2f
            val beamX = (size.width * sweep).coerceIn(0f, size.width)
            val glowWidth = 42.dp.toPx()

            drawLine(
                color = ProcessingMuted.copy(alpha = 0.22f),
                start = Offset(0f, centerY),
                end = Offset(size.width, centerY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawRect(
                color = ProcessingCyan.copy(alpha = 0.08f + (pulse * 0.07f)),
                topLeft = Offset((beamX - glowWidth).coerceAtLeast(0f), 0f),
                size = Size((glowWidth * 2f).coerceAtMost(size.width), size.height)
            )
            drawLine(
                color = ProcessingSecondary.copy(alpha = 0.72f + (pulse * 0.2f)),
                start = Offset(beamX, 0f),
                end = Offset(beamX, size.height),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(
                color = ProcessingPrimary.copy(alpha = 0.65f + (pulse * 0.25f)),
                radius = 5.dp.toPx(),
                center = Offset(beamX, centerY)
            )
        }

        Row(
            modifier = Modifier.align(Alignment.TopStart),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "LIVE SIGNAL SWEEP",
                style = MaterialTheme.typography.labelSmall,
                color = ProcessingSecondary,
                letterSpacing = 1.6.sp
            )
            Text(
                text = "ACTIVE",
                style = MaterialTheme.typography.labelSmall,
                color = ProcessingCyan,
                letterSpacing = 1.4.sp
            )
        }
    }
}

@Composable
private fun ProcessingCoreLoader(
    rotation: Float,
    pulse: Float,
    orbit: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ProcessingSurfaceLow.copy(alpha = 0.92f))
            .padding(vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radiusOuter = size.minDimension * 0.42f
                val radiusInner = size.minDimension * 0.26f
                val angleRadians = (rotation / 180f) * PI.toFloat()
                val orbitRadius = size.minDimension * (0.30f + (orbit * 0.05f))
                val orbitCenter = Offset(
                    x = center.x + (cos(angleRadians) * orbitRadius),
                    y = center.y + (sin(angleRadians) * orbitRadius)
                )

                drawCircle(
                    color = ProcessingPrimary.copy(alpha = 0.16f),
                    radius = radiusOuter,
                    center = center,
                    style = Stroke(width = 3.dp.toPx())
                )
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            ProcessingSecondary.copy(alpha = 0.05f),
                            ProcessingSecondary.copy(alpha = 0.85f),
                            ProcessingCyan.copy(alpha = 0.95f),
                            ProcessingPrimary.copy(alpha = 0.08f)
                        ),
                        center = center
                    ),
                    startAngle = rotation,
                    sweepAngle = 110f,
                    useCenter = false,
                    topLeft = Offset(center.x - radiusOuter, center.y - radiusOuter),
                    size = Size(radiusOuter * 2f, radiusOuter * 2f),
                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                )
                drawCircle(
                    color = ProcessingSecondary.copy(alpha = 0.22f + (pulse * 0.18f)),
                    radius = radiusInner,
                    center = center
                )
                drawCircle(
                    color = ProcessingPrimary.copy(alpha = 0.8f),
                    radius = size.minDimension * 0.08f,
                    center = center
                )
                drawCircle(
                    color = ProcessingCyan.copy(alpha = 0.92f),
                    radius = 6.dp.toPx(),
                    center = orbitCenter
                )
            }
        }

        Text(
            text = "FORECAST ENGINE ACTIVE",
            style = MaterialTheme.typography.labelLarge,
            color = ProcessingSecondary,
            letterSpacing = 2.sp
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { index ->
                val dotAlpha = ((pulse + (index * 0.18f)).coerceAtMost(1f))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .graphicsLayer {
                            translationY = if (((rotation / 30).toInt() + index) % 3 == 0) -8f else 0f
                        }
                        .alpha(0.45f + (dotAlpha * 0.45f))
                        .background(
                            if (index == 1) ProcessingPrimary else ProcessingCyan,
                            RoundedCornerShape(99.dp)
                        )
                )
            }
        }
    }
}

@Composable
private fun ProcessingProgressRail(
    progress: Float,
    sweep: Float,
    pulse: Float
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SYNTHESIS PROGRESS",
                style = MaterialTheme.typography.labelSmall,
                color = ProcessingMuted,
                letterSpacing = 1.5.sp
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = ProcessingPrimary,
                letterSpacing = 1.2.sp
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
        ) {
            val progressWidth = size.width * progress
            val sweepX = (size.width * sweep).coerceIn(0f, size.width)

            drawRect(
                color = ProcessingSurfaceLow,
                size = size
            )
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        ProcessingSecondary.copy(alpha = 0.45f + (pulse * 0.1f)),
                        ProcessingPrimary.copy(alpha = 0.82f),
                        ProcessingCyan.copy(alpha = 0.6f)
                    )
                ),
                size = Size(progressWidth, size.height)
            )
            if (progressWidth > 0f) {
                drawLine(
                    color = ProcessingText.copy(alpha = 0.75f),
                    start = Offset(sweepX.coerceIn(0f, progressWidth), 0f),
                    end = Offset(sweepX.coerceIn(0f, progressWidth), size.height),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
private fun ProcessingWaitTicker(
    message: String,
    drift: Float,
    pulse: Float
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .background(ProcessingSurfaceLow.copy(alpha = 0.88f))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = message.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = ProcessingCyan.copy(alpha = 0.7f + (pulse * 0.2f)),
            letterSpacing = 1.6.sp,
            modifier = Modifier.graphicsLayer {
                translationX = drift
            }
        )
    }
}

private fun processingStatusText(phase: Int): String = when (phase) {
    0 -> "Indexing survivor records and casualty ledgers."
    1 -> "Projecting decade markers across the settlement lifespan."
    2 -> "Reconciling travel losses against long-term viability."
    else -> "Final colony chronicle will appear automatically."
}

@Composable
private fun ProcessingLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val icon = when (label) {
            "Travel Route" -> Icons.Filled.Schedule
            "Travel Risk" -> Icons.Filled.Memory
            "Vault Power" -> Icons.Filled.Terminal
            else -> Icons.Filled.Memory
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = ProcessingCyan, modifier = Modifier.size(16.dp))
            Text(text = label.uppercase(), style = MaterialTheme.typography.labelSmall, color = ProcessingMuted)
        }
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = ProcessingText)
    }
}

@Composable
private fun ProcessingGridOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val horizontalPx = 3.dp.toPx()
                val verticalPx = 4.dp.toPx()

                var y = 0f
                while (y < size.height) {
                    drawRect(
                        color = Color.Black.copy(alpha = 0.10f),
                        topLeft = Offset(0f, y),
                        size = Size(size.width, 1.dp.toPx())
                    )
                    y += horizontalPx
                }

                var x = 0f
                while (x < size.width) {
                    drawRect(
                        color = Color.Black.copy(alpha = 0.03f),
                        topLeft = Offset(x, 0f),
                        size = Size(1.dp.toPx(), size.height)
                    )
                    x += verticalPx
                }
            }
    )
}
