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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ivarna.wardenprotocol.R
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
        title = stringResource(R.string.vault_status_title),
        subtitle = if (expanded) stringResource(R.string.vault_status_subtitle) else null,
        icon = Icons.Filled.Memory,
        accent = VaultGreen,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CompactVaultMetric(
                label = stringResource(R.string.vault_metric_core),
                value = stringResource(R.string.vault_pct_format, averageCore),
                tone = healthAccent(averageCore),
                modifier = Modifier.weight(1f)
            )
            CompactVaultMetric(
                label = stringResource(R.string.vault_metric_archives),
                value = stringResource(R.string.vault_pct_format, averageArchives),
                tone = healthAccent(averageArchives),
                modifier = Modifier.weight(1f)
            )
            CompactVaultMetric(
                label = stringResource(R.string.vault_metric_critical),
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
                text = if (expanded) stringResource(R.string.vault_hide_details) else stringResource(R.string.vault_show_details),
                color = SignalCyan
            )
        }

        if (expanded) {
            DividerGlow()
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SystemStatusBar(stringResource(R.string.vault_system_power_grid), systems.powerGrid)
                SystemStatusBar(stringResource(R.string.vault_system_food_stores), systems.foodStores)
                SystemStatusBar(stringResource(R.string.vault_system_medical_bay), systems.medicalBay)
                SystemStatusBar(stringResource(R.string.vault_system_security_system), systems.securitySystem)
                SystemStatusBar(stringResource(R.string.vault_system_construction_gear), systems.constructionGear)
                SystemStatusBar(stringResource(R.string.vault_system_atmosphere_scrubbers), systems.atmosphereScrubbers)

                Text(
                    text = stringResource(R.string.vault_section_databases),
                    style = MaterialTheme.typography.labelLarge,
                    color = SignalCyan
                )
                SystemStatusBar(stringResource(R.string.vault_system_cultural_archive), databases.culturalArchive)
                SystemStatusBar(stringResource(R.string.vault_system_scientific_archive), databases.scientificArchive)
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

@Composable
private fun conciseVaultStatusLine(
    averageCore: Int,
    unstableCount: Int,
    criticalCount: Int
): String {
    return when {
        criticalCount > 0 -> stringResource(R.string.vault_status_critical_format, criticalCount)
        unstableCount > 0 -> stringResource(R.string.vault_status_unstable_format, unstableCount)
        averageCore >= 85 -> stringResource(R.string.vault_status_stable)
        else -> stringResource(R.string.vault_status_functional)
    }
}

private fun healthAccent(value: Int) = when {
    value >= 70 -> VaultGreen
    value >= 40 -> WarningAmber
    value >= 15 -> WarningAmber
    else -> DangerRed
}
