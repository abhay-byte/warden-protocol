package com.wardenprotocol.game.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wardenprotocol.game.data.model.ColonyOutcome
import com.wardenprotocol.game.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun OutcomeScreen(
    outcome: ColonyOutcome,
    isNewHighScore: Boolean,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showClassification by remember { mutableStateOf(false) }
    var showNarrative by remember { mutableStateOf(false) }
    var showScore by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(400)
        showClassification = true
        delay(600)
        showNarrative = true
        delay(800)
        showScore = true
        delay(400)
        showButtons = true
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        AnimatedVisibility(
            visible = showClassification,
            enter = fadeIn()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = outcome.classification.uppercase(),
                    style = MaterialTheme.typography.displayLarge,
                    color = VaultGreen
                )
                
                Text(
                    text = outcome.settlementName,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextSecondary
                )
            }
        }
        
        AnimatedVisibility(
            visible = showNarrative,
            enter = fadeIn()
        ) {
            Text(
                text = outcome.narrative,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
        }
        
        AnimatedVisibility(
            visible = showScore,
            enter = fadeIn()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "CIVILIZATION SCORE",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
                
                Text(
                    text = "${outcome.score}",
                    style = MaterialTheme.typography.displayLarge,
                    color = VaultGreen
                )
                
                if (isNewHighScore) {
                    Text(
                        text = "NEW HIGH SCORE!",
                        style = MaterialTheme.typography.titleLarge,
                        color = WarningAmber
                    )
                }
            }
        }
        
        AnimatedVisibility(
            visible = showButtons,
            enter = fadeIn()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
                ) {
                    Text("PLAY AGAIN", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
