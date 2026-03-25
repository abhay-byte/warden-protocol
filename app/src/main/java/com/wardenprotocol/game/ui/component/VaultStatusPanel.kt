package com.wardenprotocol.game.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wardenprotocol.game.data.model.Databases
import com.wardenprotocol.game.data.model.VaultSystems
import com.wardenprotocol.game.ui.theme.DangerRed
import com.wardenprotocol.game.ui.theme.SignalCyan
import com.wardenprotocol.game.ui.theme.SurfaceElevated
import com.wardenprotocol.game.ui.theme.TextPrimary
import com.wardenprotocol.game.ui.theme.TextSecondary
import com.wardenprotocol.game.ui.theme.VaultGreen
import com.wardenprotocol.game.ui.theme.WarningAmber
import kotlin.math.roundToInt

@Composable
fun VaultStatusPanel(
    systems: VaultSystems,
    databases: Databases,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val systemValues = listOf(
        systems.powerGrid,
        systems.foodStores,
        systems.medicalBay,
        systems.securitySystem,
        systems.constructionGear,
        systems.atmosphereScrubbers
    )
    val archiveValues = listOf(
        databases.culturalArchive,
        databases.scientificArchive
    )
    val averageCore = systemValues.average().roundToInt()
    val averageArchives = archiveValues.average().roundToInt()
    val unstableCount = (systemValues + archiveValues).count { it in 35..69 }
    val criticalCount = (systemValues + archiveValues).count { it < 35 }

    CommandPanel(
        title = "Vault Status",
        subtitle = if (expanded) "Full bunker diagnostics" else "Core health at a glance",
        icon = Icons.Filled.Memory,
        accent = VaultGreen,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CompactVaultMetric(
                label = "Core",
                value = "$averageCore%",
                tone = healthAccent(averageCore),
                modifier = Modifier.weight(1f)
            )
            CompactVaultMetric(
                label = "Archives",
                value = "$averageArchives%",
                tone = healthAccent(averageArchives),
                modifier = Modifier.weight(1f)
            )
            CompactVaultMetric(
                label = "Critical",
                value = criticalCount.toString(),
                tone = if (criticalCount == 0) VaultGreen else DangerRed,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = conciseVaultStatusLine(averageCore, unstableCount, criticalCount),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        TextButton(
            onClick = { expanded = !expanded },
            modifier = animatedEntranceModifier(Modifier, delayMillis = 45)
        ) {
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = SignalCyan
            )
            Text(
                text = if (expanded) "Hide system details" else "Expand system details",
                color = SignalCyan
            )
        }

        if (expanded) {
            DividerGlow()
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
}

@Composable
private fun CompactVaultMetric(
    label: String,
    value: String,
    tone: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = animatedEntranceModifier(modifier, delayMillis = 35),
        color = SurfaceElevated,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = tone
            )
        }
    }
}

private fun conciseVaultStatusLine(
    averageCore: Int,
    unstableCount: Int,
    criticalCount: Int
): String {
    return when {
        criticalCount > 0 -> "$criticalCount systems are critical. Command attention required before long-term survival degrades."
        unstableCount > 0 -> "$unstableCount systems are unstable. The bunker is holding, but maintenance pressure is rising."
        averageCore >= 85 -> "Vault infrastructure is strong. The colony can act from a stable bunker position."
        else -> "Vault operations remain functional, but overall resilience is trending downward."
    }
}

private fun healthAccent(value: Int) = when {
    value >= 70 -> VaultGreen
    value >= 40 -> WarningAmber
    value >= 15 -> WarningAmber
    else -> DangerRed
}
