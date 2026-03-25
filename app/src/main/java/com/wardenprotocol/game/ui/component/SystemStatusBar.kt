package com.wardenprotocol.game.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wardenprotocol.game.ui.theme.*

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
    
    Column(modifier = modifier) {
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
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = color,
            trackColor = SurfaceBlack
        )
    }
}
