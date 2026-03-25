package com.wardenprotocol.game.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wardenprotocol.game.data.model.GameEvent
import com.wardenprotocol.game.data.model.EventChoice
import com.wardenprotocol.game.ui.component.ChoiceButton
import com.wardenprotocol.game.ui.theme.*

@Composable
fun EventScreen(
    event: GameEvent,
    onChoiceSelected: (EventChoice) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceBlack)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = event.title.uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = WarningAmber
                )
                
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                ChoiceButton(
                    label = event.choiceA.label,
                    description = event.choiceA.knownEffect + if (event.choiceA.hiddenRisk > 0) " ⚠ Unknown risk" else "",
                    onClick = { onChoiceSelected(event.choiceA) }
                )
                
                ChoiceButton(
                    label = event.choiceB.label,
                    description = event.choiceB.knownEffect + if (event.choiceB.hiddenRisk > 0) " ⚠ Unknown risk" else "",
                    onClick = { onChoiceSelected(event.choiceB) }
                )
                
                event.choiceC?.let { choiceC ->
                    ChoiceButton(
                        label = choiceC.label,
                        description = choiceC.knownEffect + if (choiceC.hiddenRisk > 0) " ⚠ Unknown risk" else "",
                        onClick = { onChoiceSelected(choiceC) }
                    )
                }
            }
        }
    }
}
