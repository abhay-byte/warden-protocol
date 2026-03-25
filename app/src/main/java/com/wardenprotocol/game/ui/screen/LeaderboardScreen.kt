package com.wardenprotocol.game.ui.screen

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wardenprotocol.game.data.model.RunRecord
import com.wardenprotocol.game.ui.component.ActionButton
import com.wardenprotocol.game.ui.component.CommandPanel
import com.wardenprotocol.game.ui.component.StatusBadge
import com.wardenprotocol.game.ui.component.WardenBackdrop
import com.wardenprotocol.game.ui.theme.SignalCyan
import com.wardenprotocol.game.ui.theme.TextPrimary
import com.wardenprotocol.game.ui.theme.TextSecondary
import com.wardenprotocol.game.ui.theme.VaultGreen
import com.wardenprotocol.game.ui.theme.WarningAmber
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun LeaderboardScreen(
    entries: List<RunRecord>,
    onBack: () -> Unit,
    onNewGame: () -> Unit,
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
            CommandPanel(
                title = "Hall of Wardens",
                subtitle = "Highest scoring survivors ever recorded",
                icon = Icons.Filled.EmojiEvents,
                accent = WarningAmber
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatusBadge(
                        icon = Icons.Filled.EmojiEvents,
                        label = "Top Entry",
                        value = entries.firstOrNull()?.score?.toString() ?: "--",
                        accent = WarningAmber,
                        modifier = Modifier.weight(1f)
                    )
                    StatusBadge(
                        icon = Icons.Filled.Groups,
                        label = "Runs Ranked",
                        value = entries.size.toString(),
                        accent = SignalCyan,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            ActionButton(
                title = "Return to Main Menu",
                subtitle = "Go back to the bunker command deck.",
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                accent = SignalCyan,
                onClick = onBack
            )
            ActionButton(
                title = "Start New Mission",
                subtitle = "Leave the archives and launch another run.",
                icon = Icons.Filled.PlayArrow,
                accent = VaultGreen,
                onClick = onNewGame
            )

            CommandPanel(
                title = "Leaderboard",
                subtitle = "Top archived outcomes ranked by score",
                icon = Icons.Filled.EmojiEvents,
                accent = WarningAmber
            ) {
                if (entries.isEmpty()) {
                    Text(
                        text = "No runs have been completed yet. Finish a mission to populate the hall.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                } else {
                    entries.forEachIndexed { index, entry ->
                        LeaderboardEntryRow(rank = index + 1, entry = entry)
                        if (index != entries.lastIndex) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardEntryRow(
    rank: Int,
    entry: RunRecord
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#$rank",
            style = MaterialTheme.typography.titleLarge,
            color = WarningAmber
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = entry.settlementName, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Text(
                text = "${entry.classification}  |  ${entry.locationName}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Text(
                text = "${entry.survivors} survivors after ${entry.yearsSinceWar} years",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = entry.score.toString(), style = MaterialTheme.typography.titleMedium, color = WarningAmber)
            Text(text = formatTimestamp(entry.completedAtMillis), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    if (timestamp == 0L) return "Unknown"
    return DateTimeFormatter.ofPattern("dd MMM yyyy")
        .format(Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()))
}
