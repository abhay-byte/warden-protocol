package com.wardenprotocol.game.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wardenprotocol.game.data.model.RunRecord
import com.wardenprotocol.game.ui.component.ActionButton
import com.wardenprotocol.game.ui.component.CommandPanel
import com.wardenprotocol.game.ui.component.RunArchiveCard
import com.wardenprotocol.game.ui.component.StatusBadge
import com.wardenprotocol.game.ui.component.WardenBackdrop
import com.wardenprotocol.game.ui.theme.SignalCyan
import com.wardenprotocol.game.ui.theme.TextSecondary
import com.wardenprotocol.game.ui.theme.VaultGreen

@Composable
fun HistoryScreen(
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
                title = "Run Archive",
                subtitle = "Chronological record of previous colonies",
                icon = Icons.Filled.History,
                accent = SignalCyan
            ) {
                StatusBadge(
                    icon = Icons.Filled.History,
                    label = "Archived Runs",
                    value = entries.size.toString(),
                    accent = SignalCyan
                )
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
                subtitle = "Jump straight from the archive into a new run.",
                icon = Icons.Filled.PlayArrow,
                accent = VaultGreen,
                onClick = onNewGame
            )

            CommandPanel(
                title = "Recent Outcomes",
                subtitle = "Latest runs first with archived site records",
                icon = Icons.Filled.AccessTime,
                accent = SignalCyan
            ) {
                if (entries.isEmpty()) {
                    Text(
                        text = "No run history has been recorded yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                } else {
                    entries.forEachIndexed { index, entry ->
                        RunArchiveCard(entry = entry)
                        if (index != entries.lastIndex) {
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}
