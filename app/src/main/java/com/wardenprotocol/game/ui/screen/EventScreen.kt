package com.wardenprotocol.game.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wardenprotocol.game.data.model.EventChoice
import com.wardenprotocol.game.data.model.GameEvent
import com.wardenprotocol.game.ui.component.ChoiceButton
import com.wardenprotocol.game.ui.component.CommandPanel
import com.wardenprotocol.game.ui.component.WardenBackdrop
import com.wardenprotocol.game.ui.theme.TextSecondary
import com.wardenprotocol.game.ui.theme.WarningAmber

@Composable
fun EventScreen(
    event: GameEvent,
    onChoiceSelected: (EventChoice) -> Unit,
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
                title = event.title,
                subtitle = "Critical incident report",
                icon = Icons.Filled.Warning,
                accent = WarningAmber
            ) {
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }

            ChoiceButton(
                label = event.choiceA.label,
                description = event.choiceA.knownEffect + if (event.choiceA.hiddenRisk > 0) " | unknown risk" else "",
                onClick = { onChoiceSelected(event.choiceA) }
            )
            ChoiceButton(
                label = event.choiceB.label,
                description = event.choiceB.knownEffect + if (event.choiceB.hiddenRisk > 0) " | unknown risk" else "",
                onClick = { onChoiceSelected(event.choiceB) }
            )
            event.choiceC?.let { choiceC ->
                ChoiceButton(
                    label = choiceC.label,
                    description = choiceC.knownEffect + if (choiceC.hiddenRisk > 0) " | unknown risk" else "",
                    onClick = { onChoiceSelected(choiceC) }
                )
            }
        }
    }
}
