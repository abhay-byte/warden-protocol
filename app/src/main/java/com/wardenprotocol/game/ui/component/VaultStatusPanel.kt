package com.ivarna.wardenprotocol.ui.component

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
import com.ivarna.wardenprotocol.data.model.Databases
import com.ivarna.wardenprotocol.data.model.VaultSystems
import com.ivarna.wardenprotocol.ui.theme.DangerRed
import com.ivarna.wardenprotocol.ui.theme.SignalCyan
import com.ivarna.wardenprotocol.ui.theme.SurfaceElevated
import com.ivarna.wardenprotocol.ui.theme.TextPrimary
import com.ivarna.wardenprotocol.ui.theme.TextSecondary
import com.ivarna.wardenprotocol.ui.theme.VaultGreen
import com.ivarna.wardenprotocol.ui.theme.WarningAmber
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
        subtitle = if (expanded) "Full bunker diagnostics" else null,
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
            style = MaterialTheme.typography.bodySmall,
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
                text = if (expanded) "Hide details" else "Show details",
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
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
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
        criticalCount > 0 -> "$criticalCount critical systems need command attention."
        unstableCount > 0 -> "$unstableCount systems are unstable, but the bunker is holding."
        averageCore >= 85 -> "Vault stable and ready for surface action."
        else -> "Vault functional, but resilience is slipping."
    }
}

private fun healthAccent(value: Int) = when {
    value >= 70 -> VaultGreen
    value >= 40 -> WarningAmber
    value >= 15 -> WarningAmber
    else -> DangerRed
}
