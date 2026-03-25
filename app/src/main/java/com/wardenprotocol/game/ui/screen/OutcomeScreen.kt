package com.wardenprotocol.game.ui.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.wardenprotocol.game.data.model.ColonyOutcome
import com.wardenprotocol.game.ui.component.ActionButton
import com.wardenprotocol.game.ui.component.CommandPanel
import com.wardenprotocol.game.ui.component.DividerGlow
import com.wardenprotocol.game.ui.component.SectionLabel
import com.wardenprotocol.game.ui.component.StatusBadge
import com.wardenprotocol.game.ui.component.SystemStatusBar
import com.wardenprotocol.game.ui.component.WardenBackdrop
import com.wardenprotocol.game.ui.component.animatedEntranceModifier
import com.wardenprotocol.game.ui.theme.DangerRed
import com.wardenprotocol.game.ui.theme.SignalCyan
import com.wardenprotocol.game.ui.theme.TextPrimary
import com.wardenprotocol.game.ui.theme.TextSecondary
import com.wardenprotocol.game.ui.theme.VaultGreen
import com.wardenprotocol.game.ui.theme.WarningAmber
import kotlin.math.roundToInt

@Composable
fun OutcomeScreen(
    outcome: ColonyOutcome,
    isNewHighScore: Boolean,
    onPlayAgain: () -> Unit,
    onShowLeaderboard: () -> Unit,
    onShowHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mood = when {
        outcome.score >= 5000 -> OutcomeMood.Triumph
        outcome.score < 1500 -> OutcomeMood.Collapse
        else -> OutcomeMood.Unstable
    }
    val accent = when (mood) {
        OutcomeMood.Triumph -> VaultGreen
        OutcomeMood.Unstable -> WarningAmber
        OutcomeMood.Collapse -> SignalCyan
    }
    val animatedScore by animateIntAsState(
        targetValue = outcome.score,
        animationSpec = tween(durationMillis = 1400),
        label = "score_reveal"
    )

    WardenBackdrop(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            CommandPanel(
                title = outcome.classification,
                subtitle = outcome.settlementName,
                icon = Icons.Filled.Shield,
                accent = accent
            ) {
                OutcomeHero(
                    mood = mood,
                    accent = accent,
                    score = animatedScore,
                    isNewHighScore = isNewHighScore
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatusBadge(
                        icon = Icons.Filled.EmojiEvents,
                        label = "Score",
                        value = animatedScore.toString(),
                        accent = WarningAmber,
                        modifier = Modifier.weight(1f)
                    )
                    StatusBadge(
                        icon = Icons.Filled.History,
                        label = "High Score",
                        value = if (isNewHighScore) "NEW" else "ARCHIVED",
                        accent = SignalCyan,
                        modifier = Modifier.weight(1f)
                    )
                }
                Text(
                    text = outcome.narrative,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }

            outcome.detailedStats?.let { stats ->
                CommandPanel(
                    title = "Run Breakdown",
                    subtitle = "Final telemetry from the colony launch",
                    icon = Icons.Filled.EmojiEvents,
                    accent = WarningAmber
                ) {
                    StatRow("Survivors", "${stats.survivors} / 1000")
                    StatRow("Deaths", stats.deaths.toString())
                    StatRow("Years Underground", stats.yearsSinceWar.toString())
                    StatRow("Landing Site", stats.locationName)
                    StatRow("Travel Route", stats.travelRoute)
                    StatRow("Travel Time", stats.travelTime)
                    StatRow("Travel Risk", stats.travelRisk)
                    StatRow("Transit Deaths", stats.travelDeaths.toString())
                    Spacer(modifier = Modifier.height(6.dp))
                    StatRow("Radiation", stats.radiation)
                    StatRow("Water", stats.water)
                    StatRow("Food", stats.food)
                    StatRow("Shelter", stats.shelter)
                    StatRow("Resources", stats.resources)
                    StatRow("Threats", stats.threats)
                    OutcomeVaultStatus(stats)
                }
            }

            ActionButton(
                title = "Play Again",
                subtitle = "Start another bunker command run.",
                icon = Icons.Filled.PlayArrow,
                accent = VaultGreen,
                onClick = onPlayAgain
            )
            ActionButton(
                title = "Open Leaderboard",
                subtitle = "See how this settlement ranks against previous runs.",
                icon = Icons.Filled.EmojiEvents,
                accent = WarningAmber,
                onClick = onShowLeaderboard
            )
            ActionButton(
                title = "Open Run History",
                subtitle = "Review the archive of past outcomes.",
                icon = Icons.Filled.History,
                accent = SignalCyan,
                onClick = onShowHistory
            )
        }
    }
}

@Composable
private fun OutcomeHero(
    mood: OutcomeMood,
    accent: Color,
    score: Int,
    isNewHighScore: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            OutcomeSignalAnimation(
                mood = mood,
                accent = accent,
                modifier = Modifier.fillMaxSize()
            )
            Column(modifier = Modifier.padding(top = 12.dp, start = 12.dp)) {
                Text(
                    text = when (mood) {
                        OutcomeMood.Triumph -> "COLONY ASCENDANT"
                        OutcomeMood.Unstable -> "SETTLEMENT HOLDING"
                        OutcomeMood.Collapse -> "SYSTEM FAILURE"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = accent
                )
                Text(
                    text = score.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    color = TextPrimary
                )
                if (isNewHighScore) {
                    Text(
                        text = "New command record",
                        style = MaterialTheme.typography.bodyMedium,
                        color = WarningAmber
                    )
                }
            }
        }
    }
}

@Composable
private fun OutcomeSignalAnimation(
    mood: OutcomeMood,
    accent: Color,
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "outcome_signal")
    val pulse by infinite.animateFloat(
        initialValue = 0.15f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing)
        ),
        label = "pulse"
    )
    val sweep by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = LinearEasing)
        ),
        label = "sweep"
    )

    Canvas(modifier = modifier) {
        val center = Offset(size.width * 0.72f, size.height * 0.56f)
        val baseRadius = size.minDimension * 0.14f

        when (mood) {
            OutcomeMood.Triumph -> {
                repeat(4) { index ->
                    drawCircle(
                        color = accent.copy(alpha = (0.26f - index * 0.05f) * (1f - pulse.coerceAtMost(0.95f))),
                        radius = baseRadius + index * 34f + pulse * 28f,
                        center = center,
                        style = Stroke(width = 10f - index, cap = StrokeCap.Round)
                    )
                }
                repeat(5) { index ->
                    val startX = size.width * (0.12f + index * 0.12f)
                    val top = size.height * (0.72f - (index % 2) * 0.12f) - pulse * 40f
                    drawLine(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, accent.copy(alpha = 0.8f))
                        ),
                        start = Offset(startX, size.height * 0.92f),
                        end = Offset(startX, top),
                        strokeWidth = 12f,
                        cap = StrokeCap.Round
                    )
                }
            }

            OutcomeMood.Unstable -> {
                repeat(3) { index ->
                    drawCircle(
                        color = accent.copy(alpha = 0.16f - index * 0.03f),
                        radius = baseRadius + index * 30f + pulse * 18f,
                        center = center,
                        style = Stroke(width = 8f, cap = StrokeCap.Round)
                    )
                }
                val scanY = size.height * (0.25f + sweep * 0.5f)
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, accent.copy(alpha = 0.9f), Color.Transparent)
                    ),
                    start = Offset(size.width * 0.06f, scanY),
                    end = Offset(size.width * 0.94f, scanY),
                    strokeWidth = 8f,
                    cap = StrokeCap.Round
                )
            }

            OutcomeMood.Collapse -> {
                repeat(4) { index ->
                    drawCircle(
                        color = Color(0xFFff6b7c).copy(alpha = 0.22f - index * 0.04f),
                        radius = baseRadius + index * 18f + (1f - pulse) * 18f,
                        center = center,
                        style = Stroke(width = 7f, cap = StrokeCap.Round)
                    )
                }
                repeat(6) { index ->
                    val startX = size.width * (0.14f + index * 0.12f)
                    val endY = size.height * (0.24f + (index % 3) * 0.1f) + sweep * 42f
                    drawLine(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFff6b7c).copy(alpha = 0.92f), Color.Transparent)
                        ),
                        start = Offset(startX, size.height * 0.14f),
                        end = Offset(startX, endY),
                        strokeWidth = 10f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = animatedEntranceModifier(
            modifier = Modifier.fillMaxWidth(),
            delayMillis = 150
        ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
    }
}

@Composable
private fun OutcomeVaultStatus(stats: com.wardenprotocol.game.data.model.OutcomeStats) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val systemValues = listOf(
        stats.powerGrid,
        stats.foodStores,
        stats.medicalBay,
        stats.securitySystem,
        stats.constructionGear,
        stats.atmosphereScrubbers
    )
    val archiveValues = listOf(stats.culturalArchive, stats.scientificArchive)
    val averageCore = systemValues.average().roundToInt()
    val averageArchives = archiveValues.average().roundToInt()
    val criticalCount = (systemValues + archiveValues).count { it < 35 }
    val unstableCount = (systemValues + archiveValues).count { it in 35..69 }

    Spacer(modifier = Modifier.height(8.dp))
    DividerGlow()
    Spacer(modifier = Modifier.height(10.dp))

    SectionLabel(
        text = "Vault Status",
        accent = VaultGreen,
        trailing = {
            TextButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = SignalCyan
                )
                Text(
                    text = if (expanded) "Hide" else "Show",
                    color = SignalCyan
                )
            }
        }
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatusBadge(
            icon = Icons.Filled.Memory,
            label = "Core",
            value = "$averageCore%",
            accent = vaultHealthAccent(averageCore),
            modifier = Modifier.weight(1f)
        )
        StatusBadge(
            icon = Icons.Filled.History,
            label = "Archives",
            value = "$averageArchives%",
            accent = vaultHealthAccent(averageArchives),
            modifier = Modifier.weight(1f)
        )
        StatusBadge(
            icon = Icons.Filled.Shield,
            label = "Critical",
            value = criticalCount.toString(),
            accent = if (criticalCount == 0) VaultGreen else DangerRed,
            modifier = Modifier.weight(1f)
        )
    }

    Text(
        text = conciseVaultOutcomeLine(averageCore, unstableCount, criticalCount),
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondary
    )

    if (expanded) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SystemStatusBar("Power Grid", stats.powerGrid)
            SystemStatusBar("Food Stores", stats.foodStores)
            SystemStatusBar("Medical Bay", stats.medicalBay)
            SystemStatusBar("Security System", stats.securitySystem)
            SystemStatusBar("Construction Gear", stats.constructionGear)
            SystemStatusBar("Atmosphere Scrubbers", stats.atmosphereScrubbers)
            SectionLabel(text = "Database Archives", accent = SignalCyan)
            SystemStatusBar("Cultural Archive", stats.culturalArchive)
            SystemStatusBar("Scientific Archive", stats.scientificArchive)
        }
    }
}

private fun conciseVaultOutcomeLine(
    averageCore: Int,
    unstableCount: Int,
    criticalCount: Int
): String {
    return when {
        criticalCount > 0 -> "$criticalCount bunker systems finished in critical condition."
        unstableCount > 0 -> "$unstableCount bunker systems were unstable by the end of the run."
        averageCore >= 85 -> "The vault remained strong through launch and evacuation."
        else -> "The vault held, but long-term bunker resilience was slipping."
    }
}

private fun vaultHealthAccent(value: Int) = when {
    value >= 70 -> VaultGreen
    value >= 40 -> WarningAmber
    value >= 15 -> WarningAmber
    else -> DangerRed
}

private enum class OutcomeMood {
    Triumph,
    Unstable,
    Collapse
}
