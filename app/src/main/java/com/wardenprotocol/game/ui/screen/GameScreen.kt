package com.wardenprotocol.game.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wardenprotocol.game.data.model.*
import com.wardenprotocol.game.ui.component.*
import com.wardenprotocol.game.ui.theme.*

@Composable
fun GameScreen(
    gameState: GameState,
    location: SurfaceLocation,
    probeRevealed: Boolean,
    onOpenVault: () -> Unit,
    onContinueSearching: () -> Unit,
    onDeployProbe: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "YEAR: ${gameState.yearsSinceWar}",
                style = MaterialTheme.typography.labelLarge,
                color = VaultGreen
            )
            Text(
                text = "SURVIVORS: ${gameState.survivors}",
                style = MaterialTheme.typography.labelLarge,
                color = VaultGreen
            )
        }
        
        VaultStatusPanel(
            systems = gameState.vaultSystems,
            databases = gameState.databases
        )
        
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
                    text = "SURFACE LOCATION",
                    style = MaterialTheme.typography.labelLarge,
                    color = VaultGreen
                )
                
                Text(
                    text = "${location.type.name.replace('_', ' ')}: \"${location.name}\"",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LocationAttribute(
                    "Radiation",
                    if (gameState.vaultSystems.radiationScanner > 15) location.radiation.displayName else "? Unknown",
                    gameState.vaultSystems.radiationScanner > 15,
                    location.radiation == RadiationLevel.NONE || location.radiation == RadiationLevel.LOW
                )
                
                LocationAttribute(
                    "Water",
                    if (gameState.vaultSystems.waterScanner > 15) location.water.displayName else "? Unknown",
                    gameState.vaultSystems.waterScanner > 15,
                    location.water == WaterAvailability.ABUNDANT
                )
                
                LocationAttribute(
                    "Food",
                    if (gameState.vaultSystems.agriculturalScanner > 15) location.food.displayName else "? Unknown",
                    gameState.vaultSystems.agriculturalScanner > 15,
                    location.food == FoodPotential.FERTILE
                )
                
                LocationAttribute(
                    "Shelter",
                    if (gameState.vaultSystems.structureScanner > 15) location.shelter.displayName else "? Unknown",
                    gameState.vaultSystems.structureScanner > 15,
                    location.shelter == ShelterQuality.EXCELLENT || location.shelter == ShelterQuality.GOOD
                )
                
                LocationAttribute(
                    "Resources",
                    if (gameState.vaultSystems.resourceScanner > 15) location.resources.displayName else "? Unknown",
                    gameState.vaultSystems.resourceScanner > 15,
                    location.resources == ResourceRichness.RICH
                )
                
                LocationAttribute(
                    "Threats",
                    if (gameState.vaultSystems.threatAssessment > 15) location.nativeHostility.displayName else "? Unknown",
                    gameState.vaultSystems.threatAssessment > 15,
                    location.nativeHostility == Hostility.NONE
                )
                
                if (probeRevealed && location.anomaly != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ANOMALY DETECTED: ${location.anomaly.displayName}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = WarningAmber
                    )
                }
                
                if (probeRevealed && location.probeData != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "PROBE ANALYSIS",
                        style = MaterialTheme.typography.labelLarge,
                        color = VaultGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = location.probeData.hiddenResources,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = location.probeData.structuralIntegrity,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = location.probeData.soilQuality,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "⚠ ${location.probeData.recommendation}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = WarningAmber
                    )
                }
            }
        }
        
        Button(
            onClick = onOpenVault,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
        ) {
            Text("OPEN THE VAULT", style = MaterialTheme.typography.labelLarge)
        }
        
        Button(
            onClick = onContinueSearching,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
        ) {
            Text("CONTINUE SEARCHING", style = MaterialTheme.typography.labelLarge)
        }
        
        Button(
            onClick = onDeployProbe,
            modifier = Modifier.fillMaxWidth(),
            enabled = gameState.surfaceProbes > 0,
            colors = ButtonDefaults.buttonColors(containerColor = WarningAmber)
        ) {
            Text(
                "DEPLOY PROBE (${gameState.surfaceProbes} left)",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun LocationAttribute(
    label: String,
    value: String,
    isKnown: Boolean,
    isGood: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        
        val icon = when {
            !isKnown -> "?"
            isGood -> "✓"
            value.contains("None") || value.contains("Unknown") -> "?"
            else -> "⚠"
        }
        
        val color = when {
            !isKnown -> TextSecondary
            isGood -> VaultGreen
            value.contains("High") || value.contains("Lethal") || value.contains("Warlord") || value.contains("Cult") -> DangerRed
            else -> WarningAmber
        }
        
        Text(
            text = "$icon $value",
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )
    }
}
