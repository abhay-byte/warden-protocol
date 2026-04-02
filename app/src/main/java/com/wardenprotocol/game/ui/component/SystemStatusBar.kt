package com.ivarna.wardenprotocol.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MapsHomeWork
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ivarna.wardenprotocol.ui.theme.*

@Composable
fun SystemStatusBar(
    label: String,
    value: Int,
    modifier: Modifier = Modifier
) {
    val progress by animateFloatAsState(
        targetValue = value / 100f,
        animationSpec = tween(durationMillis = 1000, easing = EaseInOutCubic),
        label = "progress"
    )
    
    val color = when {
        value >= 70 -> VaultGreen
        value >= 40 -> WarningAmber
        value >= 15 -> CriticalOrange
        else -> DangerRed
    }

    val icon = systemIcon(label)

    Column(
        modifier = animatedEntranceModifier(
            modifier = modifier.animateContentSize(),
            delayMillis = 90
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.16f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
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
                        text = "$value%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = color
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .background(SurfaceElevated, RoundedCornerShape(999.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .background(color, RoundedCornerShape(999.dp))
                    )
                }
            }
        }
    }
}

private fun systemIcon(label: String): ImageVector {
    return when (label) {
        "Power Grid" -> Icons.Filled.Bolt
        "Food Stores" -> Icons.Filled.Inventory2
        "Medical Bay" -> Icons.Filled.HealthAndSafety
        "Security System" -> Icons.Filled.Security
        "Construction Gear" -> Icons.Filled.MapsHomeWork
        "Atmosphere Scrubbers" -> Icons.Filled.Spa
        "Cultural Archive" -> Icons.Filled.Psychology
        "Scientific Archive" -> Icons.Filled.WaterDrop
        else -> Icons.Filled.Bolt
    }
}
