package com.wardenprotocol.game.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wardenprotocol.game.ui.component.ActionButton
import com.wardenprotocol.game.ui.component.CommandPanel
import com.wardenprotocol.game.ui.component.WardenBackdrop
import com.wardenprotocol.game.ui.theme.SignalCyan
import com.wardenprotocol.game.ui.theme.TextSecondary
import com.wardenprotocol.game.ui.theme.VaultGreen

@Composable
fun EventOutcomeScreen(
    narrative: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    WardenBackdrop(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            CommandPanel(
                title = "Outcome Logged",
                subtitle = "Command response report",
                icon = Icons.Filled.CheckCircle,
                accent = VaultGreen
            ) {
                Text(
                    text = narrative,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
            ActionButton(
                title = "Continue Mission",
                subtitle = "Return to scanning and choose the next surface target.",
                icon = Icons.Filled.CheckCircle,
                accent = SignalCyan,
                onClick = onDismiss
            )
        }
    }
}
