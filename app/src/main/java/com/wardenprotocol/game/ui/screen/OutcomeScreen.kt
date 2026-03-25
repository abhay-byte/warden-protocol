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
    var showStats by remember { mutableStateOf(false) }
    var showScore by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(400)
        showClassification = true
        delay(600)
        showNarrative = true
        delay(800)
        showStats = true
        delay(600)
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
            visible = showStats,
            enter = fadeIn()
        ) {
            outcome.detailedStats?.let { stats ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceBlack)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "FINAL STATISTICS",
                            style = MaterialTheme.typography.labelLarge,
                            color = VaultGreen
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        StatRow("Survivors", "${stats.survivors} / 1000")
                        StatRow("Deaths", "${stats.deaths}")
                        StatRow("Years Underground", "${stats.yearsSinceWar}")
                        StatRow("Location", stats.locationName)
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "LOCATION CONDITIONS",
                            style = MaterialTheme.typography.labelLarge,
                            color = VaultGreen
                        )
                        
                        StatRow("Radiation", stats.radiation)
                        StatRow("Water", stats.water)
                        StatRow("Food Potential", stats.food)
                        StatRow("Shelter", stats.shelter)
                        StatRow("Resources", stats.resources)
                        StatRow("Threats", stats.threats)
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "VAULT SYSTEMS",
                            style = MaterialTheme.typography.labelLarge,
                            color = VaultGreen
                        )
                        
                        StatRow("Power Grid", "${stats.powerGrid}%")
                        StatRow("Food Stores", "${stats.foodStores}%")
                        StatRow("Medical Bay", "${stats.medicalBay}%")
                        StatRow("Security", "${stats.securitySystem}%")
                        StatRow("Construction", "${stats.constructionGear}%")
                        StatRow("Atmosphere", "${stats.atmosphereScrubbers}%")
                        StatRow("Cultural Archive", "${stats.culturalArchive}%")
                        StatRow("Scientific Archive", "${stats.scientificArchive}%")
                    }
                }
            }
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

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
        )
    }
}
