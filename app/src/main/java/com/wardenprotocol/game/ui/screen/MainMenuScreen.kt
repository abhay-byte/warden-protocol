package com.wardenprotocol.game.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wardenprotocol.game.data.model.RunRecord
import com.wardenprotocol.game.ui.component.ActionButton
import com.wardenprotocol.game.ui.component.CommandPanel
import com.wardenprotocol.game.ui.component.SectionLabel
import com.wardenprotocol.game.ui.component.StatusBadge
import com.wardenprotocol.game.ui.component.WardenBackdrop
import com.wardenprotocol.game.ui.theme.SignalCyan
import com.wardenprotocol.game.ui.theme.TextPrimary
import com.wardenprotocol.game.ui.theme.TextSecondary
import com.wardenprotocol.game.ui.theme.VaultGreen
import com.wardenprotocol.game.ui.theme.WarningAmber

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
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CommandPanel(
                title = "The Warden Protocol",
                subtitle = "Protect the vault. Choose the future.",
                icon = Icons.Filled.Shield,
                accent = VaultGreen
            ) {
                Text(
                    text = "Scan the surface, manage bunker systems, and decide when the survivors leave the vault.",
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
                        label = "Runs",
                        value = runCount.toString(),
                        accent = SignalCyan,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            ActionButton(
                title = "New Mission",
                subtitle = "Start a fresh run.",
                icon = Icons.Filled.PlayArrow,
                accent = VaultGreen,
                onClick = onNewGame
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionButton(
                    title = "Leaderboard",
                    subtitle = "Top runs.",
                    icon = Icons.Filled.EmojiEvents,
                    accent = WarningAmber,
                    onClick = onOpenLeaderboard,
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    title = "History",
                    subtitle = "Past runs.",
                    icon = Icons.Filled.History,
                    accent = SignalCyan,
                    onClick = onOpenHistory,
                    modifier = Modifier.weight(1f)
                )
            }

            lastRun?.let { run ->
                CommandPanel(
                    title = "Latest Outcome",
                    subtitle = run.settlementName,
                    icon = Icons.Filled.History,
                    accent = SignalCyan
                ) {
                    Text(
                        text = run.classification,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "${run.score} score  |  ${run.locationName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = run.summary,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
            }

            if (leaderboardPreview.isNotEmpty()) {
                CommandPanel(
                    title = "Top Settlement",
                    subtitle = "Best run on record",
                    icon = Icons.Filled.EmojiEvents,
                    accent = WarningAmber
                ) {
                    val top = leaderboardPreview.first()
                    Text(
                        text = top.settlementName,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "${top.score} score  |  ${top.classification}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
