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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.wardenprotocol.game.data.model.GameState
import com.wardenprotocol.game.data.model.SurfaceLocation
import com.wardenprotocol.game.ui.component.ActionButton
import com.wardenprotocol.game.ui.component.CommandPanel
import com.wardenprotocol.game.ui.component.SectionLabel
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
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            CommandPanel(
                title = location.name,
                subtitle = "${location.type.name.replace('_', ' ')} landing projection",
                icon = Icons.Filled.Public,
                accent = SignalCyan
            ) {
                Text(
                    text = "Command the vault with hard data, limited probes, and imperfect intel before you decide to surface.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
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
            }

            CommandPanel(
                title = "Surface Intel",
                subtitle = "Scanner-fed readout for the current target zone",
                icon = Icons.Filled.Explore,
                accent = SignalCyan
            ) {
                SurfaceIntelRow(
                    title = "Radiation",
                    value = if (gameState.vaultSystems.radiationScanner > 15) location.radiation.displayName else "Unknown",
                    icon = Icons.Filled.Science,
                    accent = radiationAccent(location.radiation.name),
                    known = gameState.vaultSystems.radiationScanner > 15
                )
                SurfaceIntelRow(
                    title = "Water",
                    value = if (gameState.vaultSystems.waterScanner > 15) location.water.displayName else "Unknown",
                    icon = Icons.Filled.WaterDrop,
                    accent = waterAccent(location.water.name),
                    known = gameState.vaultSystems.waterScanner > 15
                )
                SurfaceIntelRow(
                    title = "Food",
                    value = if (gameState.vaultSystems.agriculturalScanner > 15) location.food.displayName else "Unknown",
                    icon = Icons.Filled.Restaurant,
                    accent = foodAccent(location.food.name),
                    known = gameState.vaultSystems.agriculturalScanner > 15
                )
                SurfaceIntelRow(
                    title = "Shelter",
                    value = if (gameState.vaultSystems.structureScanner > 15) location.shelter.displayName else "Unknown",
                    icon = Icons.Filled.Home,
                    accent = shelterAccent(location.shelter.name),
                    known = gameState.vaultSystems.structureScanner > 15
                )
                SurfaceIntelRow(
                    title = "Resources",
                    value = if (gameState.vaultSystems.resourceScanner > 15) location.resources.displayName else "Unknown",
                    icon = Icons.Filled.Inventory2,
                    accent = resourceAccent(location.resources.name),
                    known = gameState.vaultSystems.resourceScanner > 15
                )
                SurfaceIntelRow(
                    title = "Threats",
                    value = if (gameState.vaultSystems.threatAssessment > 15) location.nativeHostility.displayName else "Unknown",
                    icon = Icons.Filled.Security,
                    accent = threatAccent(location.nativeHostility.name),
                    known = gameState.vaultSystems.threatAssessment > 15
                )
                if (probeRevealed && location.anomaly != null) {
                    SectionLabel(text = "Probe Discovery", accent = WarningAmber)
                    SurfaceIntelRow(
                        title = "Anomaly",
                        value = location.anomaly.displayName,
                        icon = Icons.Filled.AutoAwesome,
                        accent = WarningAmber,
                        known = true
                    )
                }
            }

            if (probeRevealed && location.probeData != null) {
                CommandPanel(
                    title = "Probe Analysis",
                    subtitle = "Expanded orbital report",
                    icon = Icons.Filled.Radar,
                    accent = WarningAmber
                ) {
                    Text(text = location.probeData.hiddenResources, style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                    Text(text = location.probeData.structuralIntegrity, style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                    Text(text = location.probeData.soilQuality, style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                    Text(text = location.probeData.recommendation, style = MaterialTheme.typography.titleMedium, color = WarningAmber)
                }
            }

            VaultStatusPanel(
                systems = gameState.vaultSystems,
                databases = gameState.databases
            )

            CommandPanel(
                title = "Command Actions",
                subtitle = "Choose your next move carefully",
                icon = Icons.Filled.Explore,
                accent = VaultGreen
            ) {
                ActionButton(
                    title = "Open the Vault",
                    subtitle = "Commit to this surface site and end the run with the current conditions.",
                    icon = Icons.Filled.Public,
                    accent = DangerRed,
                    onClick = onOpenVault
                )
                ActionButton(
                    title = "Continue Searching",
                    subtitle = "Advance another year, accept system decay, and roll a fresh event.",
                    icon = Icons.Filled.Explore,
                    accent = VaultGreen,
                    onClick = onContinueSearching
                )
                ActionButton(
                    title = "Deploy Probe",
                    subtitle = "Spend one probe to reveal hidden structural and anomaly intel.",
                    icon = Icons.Filled.Radar,
                    accent = WarningAmber,
                    onClick = onDeployProbe,
                    enabled = gameState.surfaceProbes > 0
                )
            }
        }
    }
}

private fun radiationAccent(name: String): Color = when (name) {
    "NONE", "LOW" -> VaultGreen
    "MODERATE" -> WarningAmber
    else -> DangerRed
}

private fun waterAccent(name: String): Color = when (name) {
    "ABUNDANT" -> VaultGreen
    "SCARCE" -> WarningAmber
    else -> DangerRed
}

private fun foodAccent(name: String): Color = when (name) {
    "FERTILE" -> VaultGreen
    "MARGINAL" -> WarningAmber
    else -> DangerRed
}

private fun shelterAccent(name: String): Color = when (name) {
    "EXCELLENT", "GOOD" -> VaultGreen
    "POOR" -> WarningAmber
    else -> DangerRed
}

private fun resourceAccent(name: String): Color = when (name) {
    "RICH" -> VaultGreen
    "MODERATE" -> WarningAmber
    else -> DangerRed
}

private fun threatAccent(name: String): Color = when (name) {
    "NONE" -> VaultGreen
    "BANDITS" -> WarningAmber
    else -> DangerRed
}

@Composable
private fun SurfaceIntelRow(
    title: String,
    value: String,
    icon: ImageVector,
    accent: Color,
    known: Boolean
) {
    Row(
        modifier = animatedEntranceModifier(
            modifier = Modifier.fillMaxWidth(),
            delayMillis = 140
        ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (known) accent else TextSecondary
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text(
                text = if (known) value else "Scanner offline",
                style = MaterialTheme.typography.bodyLarge,
                color = if (known) TextPrimary else TextSecondary
            )
        }
        Text(
            text = if (known) value.uppercase() else "LOCKED",
            style = MaterialTheme.typography.labelMedium,
            color = if (known) accent else TextSecondary
        )
    }
}
