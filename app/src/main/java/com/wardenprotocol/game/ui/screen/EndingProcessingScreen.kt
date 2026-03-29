package com.wardenprotocol.game.ui.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wardenprotocol.game.data.model.ColonyOutcome

private val ProcessingBackground = Color(0xFF121414)
private val ProcessingPrimary = Color(0xFFFFD597)
private val ProcessingSecondary = Color(0xFF9EFF8B)
private val ProcessingCyan = Color(0xFF88E1FF)
private val ProcessingSurfaceHigh = Color(0xFF282A2A)
private val ProcessingSurfaceLow = Color(0xFF0D0F0F)
private val ProcessingText = Color(0xFFE2E2E2)
private val ProcessingMuted = Color(0xFFD7C4AC)

@Composable
fun EndingProcessingScreen(
    outcome: ColonyOutcome,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "ending_processing")
    val pulse = transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

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

                    repeat(4) { index ->
                        val widthFraction = listOf(1f, 0.84f, 0.92f, 0.76f)[index]
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(widthFraction)
                                .height(14.dp)
                                .alpha(pulse.value)
                                .background(ProcessingPrimary.copy(alpha = 0.18f), RoundedCornerShape(4.dp))
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .alpha(pulse.value)
                                .background(ProcessingSecondary, RoundedCornerShape(99.dp))
                        )
                        Text(
                            text = "Do not close the report. The final colony record will appear automatically.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ProcessingMuted
                        )
                    }
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
