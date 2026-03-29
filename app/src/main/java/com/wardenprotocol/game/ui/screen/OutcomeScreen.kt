package com.wardenprotocol.game.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wardenprotocol.game.R
import com.wardenprotocol.game.data.model.AiEndingReport
import com.wardenprotocol.game.data.model.LocationType
import com.wardenprotocol.game.data.model.ColonyOutcome
import com.wardenprotocol.game.data.model.OutcomeStats
import com.wardenprotocol.game.ui.component.locationArtworkRes
import com.wardenprotocol.game.ui.theme.*
import com.wardenprotocol.game.ui.viewmodel.EndingForecastState

// Theme Colors consistent with Brutalist bunker interface
private val Primary = Color(0xFFFFD597)
private val PrimaryContainer = Color(0xFFFFB000)
private val WarningAmber = Color(0xFFFFB000)
private val Secondary = Color(0xFF9EFF8B)
private val DangerRed = Color(0xFF93000A)
private val SignalBlue = Color(0xFF88E1FF)
private val SurfaceContainerHigh = Color(0xFF282A2A)
private val SurfaceContainerHighest = Color(0xFF333535)
private val SurfaceContainerLowest = Color(0xFF0D0F0F)
private val TextPrimary = Color(0xFFE2E2E2)
private val TextSecondary = Color(0xFFD7C4AC)

@Composable
fun OutcomeScreen(
    outcome: ColonyOutcome,
    isNewHighScore: Boolean,
    analysisState: EndingForecastState,
    onPlayAgain: () -> Unit,
    onShowLeaderboard: () -> Unit,
    onShowHistory: () -> Unit,
    onGoToMainMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF101316),
                        Color(0xFF121414),
                        Color(0xFF0D0F0F)
                    )
                )
            )
    ) {
        // Global Calibrated Baseline Grid
        Box(
            modifier = Modifier
                .fillMaxSize()
                .tacticalGrid(alpha = 0.05f, horizontalSpacing = 3.dp, verticalSpacing = 4.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Tactical Header (Sync with other screens)
            TacticalMissionHeader()

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Main Layout for larger screens could be row, but sticking to linear for mobile
                OutcomeHeroSection(outcome, isNewHighScore, analysisState)

                ForecastStatusPanel(analysisState, outcome.aiReport)

                PostSettlementChronicles(
                    narrative = outcome.aiReport?.detailedNarrative ?: outcome.narrative,
                    analysisState = analysisState
                )

                outcome.aiReport?.let { report ->
                    ForecastTimelineSection(report)
                    ForecastDriversSection(report)
                }

                RunBreakdownSection(
                    score = outcome.score,
                    stats = outcome.detailedStats,
                    aiReport = outcome.aiReport,
                    analysisState = analysisState
                )

                VaultStatusSection(outcome.detailedStats)

                GlobalActionSection(
                    analysisState = analysisState,
                    onRestart = onPlayAgain,
                    onLeaderboard = onShowLeaderboard,
                    onHistory = onShowHistory,
                    onMenu = onGoToMainMenu
                )

                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}

@Composable
private fun TacticalMissionHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .tacticalGrid(alpha = 0.14f, horizontalSpacing = 3.dp, verticalSpacing = 4.dp)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Icon(
                imageVector = Icons.Filled.Terminal,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                "WARDEN_PROTOCOL_V1.0.4",
                style = MaterialTheme.typography.titleMedium,
                color = Primary,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp
            )
        }
        Icon(
            imageVector = Icons.Filled.Tune,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun OutcomeHeroSection(
    outcome: ColonyOutcome,
    isNewHighScore: Boolean,
    analysisState: EndingForecastState
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .border(1.dp, Primary.copy(alpha = 0.1f))
            .background(SurfaceContainerLowest)
            .tacticalGrid(alpha = 0.10f)
            .clip(RoundedCornerShape(0.dp))
    ) {
        // Hero Image Backdrop (Grayscale/Dimmed)
        Image(
            painter = painterResource(id = resolveOutcomeLocationArt(outcome.detailedStats)),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.colorMatrix(
                ColorMatrix().apply { setToSaturation(0f) }
            ),
            alpha = 0.2f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Status Badge
                Row(
                    modifier = Modifier
                        .border(1.dp, Secondary.copy(alpha = 0.4f))
                        .background(Secondary.copy(alpha = 0.05f))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.size(8.dp).background(Secondary))
                    Text(
                        "MISSION_COMPLETE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Secondary,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Permanent Score Badge
                Row(
                    modifier = Modifier
                        .border(1.dp, Primary.copy(alpha = 0.3f))
                        .background(Primary.copy(alpha = 0.05f))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "${outcome.score} PTS",
                        style = MaterialTheme.typography.labelSmall,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                outcome.aiReport?.let { report ->
                    Row(
                        modifier = Modifier
                            .border(1.dp, SignalBlue.copy(alpha = 0.3f))
                            .background(SignalBlue.copy(alpha = 0.08f))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            if (report.scoreDelta >= 0) "AI +${report.scoreDelta}" else "AI ${report.scoreDelta}",
                            style = MaterialTheme.typography.labelSmall,
                            color = SignalBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (isNewHighScore) {
                    Box(
                        modifier = Modifier
                            .background(PrimaryContainer)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "NEW HIGH SCORE!",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Black,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                outcome.settlementName.uppercase(),
                style = MaterialTheme.typography.displaySmall,
                color = Primary,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Classification: ${outcome.classification}",
                    style = MaterialTheme.typography.labelLarge,
                    color = Primary.copy(alpha = 0.8f),
                    letterSpacing = 1.sp
                )
                Box(modifier = Modifier.height(1.dp).weight(1f).background(Primary.copy(alpha = 0.2f)))
                Text(
                    when (analysisState) {
                        EndingForecastState.Loading -> "Forecast: pending"
                        is EndingForecastState.Ready -> "Forecast: ${analysisState.provider}"
                        is EndingForecastState.Fallback -> "Forecast: fallback"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun ForecastStatusPanel(
    analysisState: EndingForecastState,
    aiReport: AiEndingReport?
) {
    val title = when (analysisState) {
        EndingForecastState.Loading -> "ENDING FORECAST ENGINE"
        is EndingForecastState.Ready -> "AI FORECAST LOCKED"
        is EndingForecastState.Fallback -> "DETERMINISTIC FALLBACK"
    }
    val description = when (analysisState) {
        EndingForecastState.Loading -> "Pulling a structured long-range settlement forecast from live run telemetry. The current deterministic ending remains visible until the response lands or times out."
        is EndingForecastState.Ready -> aiReport?.civilizationVerdict ?: "The AI forecast completed without a verdict summary."
        is EndingForecastState.Fallback -> analysisState.reason
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceContainerHighest.copy(alpha = 0.72f))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = if (analysisState is EndingForecastState.Fallback) Primary else SignalBlue,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.6.sp
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun PostSettlementChronicles(
    narrative: String,
    analysisState: EndingForecastState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = 12f
                drawLine(
                    color = PrimaryContainer,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = strokeWidth
                )
            }
            .background(SurfaceContainerHigh)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Filled.HistoryEdu, contentDescription = null, tint = Primary, modifier = Modifier.size(24.dp))
            Text(
                if (analysisState is EndingForecastState.Ready) "AI SETTLEMENT FORECAST" else "POST-SETTLEMENT CHRONICLES",
                style = MaterialTheme.typography.titleMedium,
                color = Primary,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            narrative,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary.copy(alpha = 0.9f),
            lineHeight = 28.sp
        )
    }
}

@Composable
private fun ForecastTimelineSection(report: AiEndingReport) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceContainerHigh)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "LONG-RANGE TIMELINE",
            style = MaterialTheme.typography.titleMedium,
            color = SignalBlue,
            fontWeight = FontWeight.Bold
        )
        report.timeline.forEach { entry ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLowest)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${entry.marker}  |  ${entry.title.uppercase()}",
                    style = MaterialTheme.typography.labelLarge,
                    color = Primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${entry.status}  |  ${entry.populationEstimate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SignalBlue
                )
                Text(
                    text = entry.narrative,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
private fun ForecastDriversSection(report: AiEndingReport) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutcomeListPanel(
            modifier = Modifier.weight(1f),
            title = "FAILURE CAUSES",
            accent = DangerRed,
            entries = report.failureCauses.ifEmpty { listOf("No explicit failure causes returned.") }
        )
        OutcomeListPanel(
            modifier = Modifier.weight(1f),
            title = "SURVIVAL DRIVERS",
            accent = Secondary,
            entries = report.survivalDrivers.ifEmpty { listOf("No explicit survival drivers returned.") }
        )
    }
}

@Composable
private fun OutcomeListPanel(
    title: String,
    accent: Color,
    entries: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(SurfaceContainerHigh)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = accent,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        entries.take(4).forEach { line ->
            Text(
                text = "• $line",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun RunBreakdownSection(
    score: Int,
    stats: OutcomeStats?,
    aiReport: AiEndingReport?,
    analysisState: EndingForecastState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceContainerLowest.copy(alpha = 0.5f))
            .tacticalGrid(alpha = 0.07f)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "RUN_BREAKDOWN_v04",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            BreakdownRow("Travel Route", stats?.travelRoute ?: "Unavailable", Primary)
            BreakdownRow("Travel Time", stats?.travelTime ?: "Unavailable", Secondary)
            BreakdownRow("Travel Risk", stats?.travelRisk ?: "Unknown", dangerTone(stats?.travelRisk), compact = true)
            BreakdownRow("Travel Deaths", stats?.travelDeaths?.toString() ?: "0", DangerRed, compact = true)
            BreakdownRow("Surface Conditions", buildConditionLine(stats), SignalBlue)
            BreakdownRow("Vault Systems", buildVaultLine(stats), PrimaryContainer)
        }

        ScoreReviewCard(score = score, aiReport = aiReport, analysisState = analysisState)
    }
}

@Composable
private fun ScoreReviewCard(
    score: Int,
    aiReport: AiEndingReport?,
    analysisState: EndingForecastState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceContainerHighest)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("SCORE REVIEW", style = MaterialTheme.typography.labelSmall, color = TextSecondary, letterSpacing = 1.sp)
        Text(
            text = if (aiReport != null) {
                "Final score $score after AI delta ${if (aiReport.scoreDelta >= 0) "+" else ""}${aiReport.scoreDelta}"
            } else {
                "Final score $score with deterministic scoring."
            },
            style = MaterialTheme.typography.titleMedium,
            color = if (aiReport != null) SignalBlue else Primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = when {
                aiReport != null -> aiReport.scoreReason
                analysisState is EndingForecastState.Loading -> "Waiting for the forecast engine. The current deterministic ending remains on screen unless the response arrives in time."
                analysisState is EndingForecastState.Fallback -> analysisState.reason
                else -> "Deterministic scoring held as the final result."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
        )
    }
}

@Composable
private fun BreakdownRow(label: String, value: String, accent: Color, compact: Boolean = false) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceContainerLowest)
            .drawBehind {
                drawLine(
                    color = accent,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = 4f
                )
            }
            .padding(16.dp)
    ) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        Text(
            text = value,
            style = if (compact) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge,
            color = accent,
            fontWeight = FontWeight.Black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun VaultStatusSection(stats: OutcomeStats?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = DangerRed,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 4f
                )
            }
            .background(SurfaceContainerHigh)
            .tacticalGrid(alpha = 0.07f)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Filled.Memory, contentDescription = null, tint = DangerRed, modifier = Modifier.size(20.dp))
                Text("VAULT DIAGNOSTICS", style = MaterialTheme.typography.labelSmall, color = DangerRed, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Filled.ExpandMore, contentDescription = null, tint = TextSecondary)
        }

        MetricBar("Hull Integrity", stats?.securitySystem ?: 42, DangerRed)
        MetricBar("Core Temp", stats?.powerGrid ?: 78, Primary)
    }
}

@Composable
private fun MetricBar(label: String, value: Int, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = TextPrimary.copy(alpha = 0.7f))
            Text(
                if (value < 50) "$value% (CRITICAL)" else "OPTIMAL",
                style = MaterialTheme.typography.labelSmall,
                color = if (value < 50) DangerRed else Primary
            )
        }
        Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(SurfaceContainerLowest)) {
            Box(modifier = Modifier.fillMaxWidth(value / 100f).fillMaxHeight().background(color))
        }
    }
}

@Composable
private fun GlobalActionSection(
    analysisState: EndingForecastState,
    onRestart: () -> Unit,
    onLeaderboard: () -> Unit,
    onHistory: () -> Unit,
    onMenu: () -> Unit
) {
    val actionsEnabled = analysisState !is EndingForecastState.Loading
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = onRestart,
            enabled = actionsEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .tacticalGrid(alpha = 0.18f, horizontalSpacing = 3.dp, verticalSpacing = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Filled.Refresh, contentDescription = null, tint = Color.Black)
                Text(
                    "RESTART MISSION",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onLeaderboard,
                enabled = actionsEnabled,
                modifier = Modifier.weight(1f).height(56.dp)
                    .tacticalGrid(alpha = 0.12f, horizontalSpacing = 4.dp, verticalSpacing = 6.dp),
                shape = RoundedCornerShape(0.dp),
                border = borderStroke(1.dp, Primary.copy(alpha = 0.3f))
            ) {
                Text("LEADERBOARD", style = MaterialTheme.typography.labelLarge, color = Primary, fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = onHistory,
                enabled = actionsEnabled,
                modifier = Modifier.weight(1f).height(56.dp)
                    .tacticalGrid(alpha = 0.12f, horizontalSpacing = 4.dp, verticalSpacing = 6.dp),
                shape = RoundedCornerShape(0.dp),
                border = borderStroke(1.dp, Primary.copy(alpha = 0.3f))
            ) {
                Text("RUN HISTORY", style = MaterialTheme.typography.labelLarge, color = Primary, fontWeight = FontWeight.Bold)
            }
        }

        OutlinedButton(
            onClick = onMenu,
            enabled = actionsEnabled,
            modifier = Modifier.fillMaxWidth().height(56.dp)
                .tacticalGrid(alpha = 0.12f, horizontalSpacing = 4.dp, verticalSpacing = 6.dp),
            shape = RoundedCornerShape(0.dp),
            border = borderStroke(1.dp, Primary.copy(alpha = 0.3f))
        ) {
            Text("MAIN MENU", style = MaterialTheme.typography.labelLarge, color = Primary, fontWeight = FontWeight.Bold)
        }
    }
}

private fun resolveOutcomeLocationArt(stats: OutcomeStats?): Int {
    val type = stats?.locationTypeName
        ?.takeIf { it.isNotBlank() }
        ?.let { raw -> runCatching { enumValueOf<LocationType>(raw) }.getOrNull() }
    return locationArtworkRes(type)
}

private fun buildConditionLine(stats: OutcomeStats?): String {
    if (stats == null) return "No surface telemetry available."
    return listOf(
        "Rad ${stats.radiation}",
        "Water ${stats.water}",
        "Food ${stats.food}",
        "Shelter ${stats.shelter}",
        "Resources ${stats.resources}",
        "Threats ${stats.threats}"
    ).joinToString(" | ")
}

private fun buildVaultLine(stats: OutcomeStats?): String {
    if (stats == null) return "No vault telemetry available."
    return listOf(
        "Power ${stats.powerGrid}%",
        "Food ${stats.foodStores}%",
        "Medical ${stats.medicalBay}%",
        "Security ${stats.securitySystem}%",
        "Construction ${stats.constructionGear}%",
        "Archives ${((stats.culturalArchive + stats.scientificArchive) / 2)}%"
    ).joinToString(" | ")
}

private fun dangerTone(risk: String?): Color = when (risk?.lowercase()) {
    "low" -> Secondary
    "moderate" -> Primary
    "high" -> DangerRed.copy(alpha = 0.75f)
    "extreme" -> DangerRed
    else -> TextPrimary
}

private fun borderStroke(width: androidx.compose.ui.unit.Dp, color: Color) = androidx.compose.foundation.BorderStroke(width, color)

private fun Modifier.tacticalGrid(
    alpha: Float = 0.15f,
    horizontalSpacing: androidx.compose.ui.unit.Dp = 3.dp,
    verticalSpacing: androidx.compose.ui.unit.Dp = 4.dp
): Modifier = this.drawBehind {
    val horizontalPx = horizontalSpacing.toPx()
    val verticalPx = verticalSpacing.toPx()
    
    // Horizontal Scanlines
    var y = 0f
    while (y < size.height) {
        drawRect(
            color = Color.Black.copy(alpha = alpha),
            topLeft = Offset(0f, y),
            size = Size(size.width, 1.dp.toPx())
        )
        y += horizontalPx
    }

    // Vertical Phosphor Columns
    var x = 0f
    while (x < size.width) {
        drawRect(
            color = Color.Black.copy(alpha = alpha * 0.25f),
            topLeft = Offset(x, 0f),
            size = Size(1.dp.toPx(), size.height)
        )
        x += verticalPx
    }
}
