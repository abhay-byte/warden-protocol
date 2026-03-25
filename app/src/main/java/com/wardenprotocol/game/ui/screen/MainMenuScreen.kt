package com.wardenprotocol.game.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wardenprotocol.game.data.model.RunRecord
import com.wardenprotocol.game.ui.component.ActionButton
import com.wardenprotocol.game.ui.component.CommandPanel
import com.wardenprotocol.game.ui.component.SectionLabel
import com.wardenprotocol.game.ui.component.StatusBadge
import com.wardenprotocol.game.ui.component.VaultSeal
import com.wardenprotocol.game.ui.component.WardenBackdrop
import com.wardenprotocol.game.ui.theme.DangerRed
import com.wardenprotocol.game.ui.theme.SignalCyan
import com.wardenprotocol.game.ui.theme.TextPrimary
import com.wardenprotocol.game.ui.theme.TextSecondary
import com.wardenprotocol.game.ui.theme.VaultGreen
import com.wardenprotocol.game.ui.theme.WarningAmber
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun MainMenuScreen(
    highScore: Int,
    runCount: Int,
    leaderboardPreview: List<RunRecord>,
    lastRun: RunRecord?,
    onNewGame: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    onOpenHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    WardenBackdrop(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            HeroDeck(
                highScore = highScore,
                runCount = runCount
            )

            ActionButton(
                title = "Launch New Mission",
                subtitle = "Enter the command bunker and evaluate the surface frontier.",
                icon = Icons.Filled.PlayArrow,
                accent = VaultGreen,
                onClick = onNewGame
            )
            ActionButton(
                title = "Hall of Wardens",
                subtitle = "View the highest scoring settlements and their final standings.",
                icon = Icons.Filled.EmojiEvents,
                accent = WarningAmber,
                onClick = onOpenLeaderboard
            )
            ActionButton(
                title = "Archive Previous Runs",
                subtitle = "Review recent colonies, failures, and survival outcomes.",
                icon = Icons.Filled.History,
                accent = SignalCyan,
                onClick = onOpenHistory
            )

            CommandPanel(
                title = "Command Snapshot",
                subtitle = "Quick telemetry from your bunker campaign",
                icon = Icons.Filled.Shield,
                accent = VaultGreen
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatusBadge(
                        icon = Icons.Filled.EmojiEvents,
                        label = "Top Score",
                        value = highScore.toString(),
                        accent = WarningAmber,
                        modifier = Modifier.weight(1f)
                    )
                    StatusBadge(
                        icon = Icons.Filled.Groups,
                        label = "Runs Logged",
                        value = runCount.toString(),
                        accent = SignalCyan,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (lastRun != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    SectionLabel(text = "Latest Outcome", accent = SignalCyan)
                    Text(
                        text = "${lastRun.classification} at ${lastRun.settlementName}",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "${lastRun.score} score  |  ${lastRun.locationName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = lastRun.summary,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
            }

            CommandPanel(
                title = "Leaderboard Preview",
                subtitle = "Best settlements on record",
                icon = Icons.Filled.EmojiEvents,
                accent = WarningAmber
            ) {
                if (leaderboardPreview.isEmpty()) {
                    Text(
                        text = "No finished runs yet. Launch a mission to begin populating the command archives.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                } else {
                    leaderboardPreview.take(3).forEachIndexed { index, entry ->
                        LeaderboardPreviewRow(rank = index + 1, entry = entry)
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroDeck(
    highScore: Int,
    runCount: Int
) {
    CommandPanel(
        title = "The Warden Protocol",
        subtitle = "Strategic vault command simulation",
        icon = Icons.Filled.Shield,
        accent = VaultGreen
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Guard the last thousand survivors. Scan the poisoned surface. Decide when humanity rises again.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatusBadge(
                        icon = Icons.Filled.EmojiEvents,
                        label = "High Score",
                        value = highScore.toString(),
                        accent = WarningAmber,
                        modifier = Modifier.weight(1f)
                    )
                    StatusBadge(
                        icon = Icons.Filled.History,
                        label = "Archived Runs",
                        value = runCount.toString(),
                        accent = SignalCyan,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            VaultSeal(modifier = Modifier.height(150.dp).weight(0.7f))
        }
    }
}

@Composable
private fun LeaderboardPreviewRow(
    rank: Int,
    entry: RunRecord
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#$rank",
            style = MaterialTheme.typography.titleMedium,
            color = WarningAmber
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.settlementName,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Text(
                text = "${entry.classification}  |  ${entry.locationName}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = entry.score.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = WarningAmber
            )
            Text(
                text = formatRunDate(entry.completedAtMillis),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

private fun formatRunDate(timestamp: Long): String {
    if (timestamp == 0L) return "Unknown"
    val formatter = DateTimeFormatter.ofPattern("dd MMM")
    return formatter.format(
        Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault())
    )
}
