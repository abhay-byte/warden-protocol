package com.wardenprotocol.game.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wardenprotocol.game.data.model.VaultSystems
import com.wardenprotocol.game.data.model.Databases
import com.wardenprotocol.game.ui.theme.SurfaceBlack

@Composable
fun VaultStatusPanel(
    systems: VaultSystems,
    databases: Databases,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "VAULT SYSTEMS",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            
            SystemStatusBar("Power Grid", systems.powerGrid)
            SystemStatusBar("Food Stores", systems.foodStores)
            SystemStatusBar("Medical Bay", systems.medicalBay)
            SystemStatusBar("Security System", systems.securitySystem)
            SystemStatusBar("Construction Gear", systems.constructionGear)
            SystemStatusBar("Atmosphere Scrubbers", systems.atmosphereScrubbers)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "DATABASES",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            
            SystemStatusBar("Cultural Archive", databases.culturalArchive)
            SystemStatusBar("Scientific Archive", databases.scientificArchive)
        }
    }
}
