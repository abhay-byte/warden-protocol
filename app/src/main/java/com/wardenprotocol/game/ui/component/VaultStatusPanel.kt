package com.wardenprotocol.game.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wardenprotocol.game.data.model.Databases
import com.wardenprotocol.game.data.model.VaultSystems
import com.wardenprotocol.game.ui.theme.SignalCyan
import com.wardenprotocol.game.ui.theme.VaultGreen

@Composable
fun VaultStatusPanel(
    systems: VaultSystems,
    databases: Databases,
    modifier: Modifier = Modifier
) {
    CommandPanel(
        title = "Vault Systems",
        subtitle = "Operational integrity across the bunker core",
        icon = Icons.Filled.Memory,
        accent = VaultGreen,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SystemStatusBar("Power Grid", systems.powerGrid)
            SystemStatusBar("Food Stores", systems.foodStores)
            SystemStatusBar("Medical Bay", systems.medicalBay)
            SystemStatusBar("Security System", systems.securitySystem)
            SystemStatusBar("Construction Gear", systems.constructionGear)
            SystemStatusBar("Atmosphere Scrubbers", systems.atmosphereScrubbers)

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "DATABASE ARCHIVES",
                style = MaterialTheme.typography.labelLarge,
                color = SignalCyan
            )
            SystemStatusBar("Cultural Archive", databases.culturalArchive)
            SystemStatusBar("Scientific Archive", databases.scientificArchive)
        }
    }
}
