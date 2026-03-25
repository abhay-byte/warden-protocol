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
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wardenprotocol.game.data.model.GameState
import com.wardenprotocol.game.data.model.SurfaceLocation
import com.wardenprotocol.game.ui.component.ActionButton
import com.wardenprotocol.game.ui.component.CommandPanel
import com.wardenprotocol.game.ui.component.DividerGlow
import com.wardenprotocol.game.ui.component.StatusBadge
import com.wardenprotocol.game.ui.component.VaultStatusPanel
import com.wardenprotocol.game.ui.component.WardenBackdrop
import com.wardenprotocol.game.ui.component.animatedEntranceModifier
import com.wardenprotocol.game.ui.theme.DangerRed
import com.wardenprotocol.game.ui.theme.SignalCyan
import com.wardenprotocol.game.ui.theme.TextPrimary
import com.wardenprotocol.game.ui.theme.TextSecondary
import com.wardenprotocol.game.ui.theme.VaultGreen
import com.wardenprotocol.game.ui.theme.WarningAmber

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
    WardenBackdrop(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatusBadge(
                    icon = Icons.Filled.Groups,
                    label = "Survivors",
                    value = gameState.survivors.toString(),
                    accent = VaultGreen,
                    modifier = Modifier.weight(1f)
                )
                StatusBadge(
                    icon = Icons.Filled.TipsAndUpdates,
                    label = "Year",
                    value = gameState.yearsSinceWar.toString(),
                    accent = SignalCyan,
                    modifier = Modifier.weight(1f)
                )
                StatusBadge(
                    icon = Icons.Filled.Radar,
                    label = "Probes",
                    value = gameState.surfaceProbes.toString(),
                    accent = WarningAmber,
                    modifier = Modifier.weight(1f)
                )
            }

            CommandPanel(
                title = location.name,
                subtitle = location.type.name.replace('_', ' '),
                icon = Icons.Filled.Public,
                accent = SignalCyan
            ) {
                Text(
                    text = "Current surface target",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                SimpleIntelRow("Radiation", scannerValue(gameState.vaultSystems.radiationScanner, location.radiation.displayName), hazardAccent(location.radiation.name))
                SimpleIntelRow("Water", scannerValue(gameState.vaultSystems.waterScanner, location.water.displayName), hazardAccent(location.water.name))
                SimpleIntelRow("Food", scannerValue(gameState.vaultSystems.agriculturalScanner, location.food.displayName), hazardAccent(location.food.name))
                SimpleIntelRow("Shelter", scannerValue(gameState.vaultSystems.structureScanner, location.shelter.displayName), hazardAccent(location.shelter.name))
                SimpleIntelRow("Resources", scannerValue(gameState.vaultSystems.resourceScanner, location.resources.displayName), hazardAccent(location.resources.name))
                SimpleIntelRow("Threats", scannerValue(gameState.vaultSystems.threatAssessment, location.nativeHostility.displayName), hazardAccent(location.nativeHostility.name))

                if (probeRevealed && location.anomaly != null) {
                    DividerGlow()
                    SimpleIntelRow("Anomaly", location.anomaly.displayName, WarningAmber)
                }

                if (probeRevealed && location.probeData != null) {
                    DividerGlow()
                    Text(
                        text = "Probe Report",
                        style = MaterialTheme.typography.labelLarge,
                        color = WarningAmber
                    )
                    Text(text = location.probeData.recommendation, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                    Text(text = location.probeData.hiddenResources, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Text(text = location.probeData.structuralIntegrity, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Text(text = location.probeData.soilQuality, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }

            CommandPanel(
                title = "Next Action",
                subtitle = "Choose one",
                icon = Icons.Filled.Explore,
                accent = VaultGreen
            ) {
                ActionButton(
                    title = "Continue Searching",
                    subtitle = "Advance one year and scan another location.",
                    icon = Icons.Filled.Explore,
                    accent = VaultGreen,
                    onClick = onContinueSearching
                )
                ActionButton(
                    title = "Deploy Probe",
                    subtitle = "Reveal more details about this location.",
                    icon = Icons.Filled.Radar,
                    accent = WarningAmber,
                    onClick = onDeployProbe,
                    enabled = gameState.surfaceProbes > 0
                )
                ActionButton(
                    title = "Open The Vault",
                    subtitle = "Commit to this location and end the run.",
                    icon = Icons.Filled.Public,
                    accent = DangerRed,
                    onClick = onOpenVault
                )
            }

            VaultStatusPanel(
                systems = gameState.vaultSystems,
                databases = gameState.databases
            )
        }
    }
}

@Composable
private fun SimpleIntelRow(
    label: String,
    value: String,
    accent: Color
) {
    Row(
        modifier = animatedEntranceModifier(Modifier.fillMaxWidth(), delayMillis = 60),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = accent)
    }
}

private fun scannerValue(scannerHealth: Int, actualValue: String): String {
    return if (scannerHealth > 15) actualValue else "Unknown"
}

private fun hazardAccent(name: String): Color = when (name) {
    "NONE", "LOW", "ABUNDANT", "FERTILE", "EXCELLENT", "GOOD", "RICH" -> VaultGreen
    "SCARCE", "MARGINAL", "MODERATE", "BANDITS", "POOR" -> WarningAmber
    "UNKNOWN" -> TextSecondary
    else -> DangerRed
}
