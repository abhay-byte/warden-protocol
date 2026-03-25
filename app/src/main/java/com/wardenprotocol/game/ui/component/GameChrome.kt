package com.wardenprotocol.game.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.wardenprotocol.game.ui.theme.BackgroundBlack
import com.wardenprotocol.game.ui.theme.BackgroundBlue
import com.wardenprotocol.game.ui.theme.PanelStroke
import com.wardenprotocol.game.ui.theme.SurfaceBlack
import com.wardenprotocol.game.ui.theme.SurfaceElevated
import com.wardenprotocol.game.ui.theme.TextMuted
import com.wardenprotocol.game.ui.theme.TextPrimary
import com.wardenprotocol.game.ui.theme.TextSecondary
import com.wardenprotocol.game.ui.theme.VaultGreen
import kotlinx.coroutines.delay

private val panelShape = RoundedCornerShape(22.dp)

@Composable
fun WardenBackdrop(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundBlack, BackgroundBlue, BackgroundBlack)
                )
            )
    ) {
        content()
    }
}

@Composable
fun CommandPanel(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    accent: Color = VaultGreen,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = animatedEntranceModifier(
            modifier = modifier
                .fillMaxWidth()
                .animateContentSize(),
            delayMillis = 15
        ),
        colors = CardDefaults.cardColors(containerColor = SurfaceBlack),
        border = BorderStroke(1.dp, PanelStroke.copy(alpha = 0.7f)),
        shape = panelShape
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
            content()
        }
    }
}

@Composable
fun StatusBadge(
    icon: ImageVector,
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = animatedEntranceModifier(
            modifier = modifier.animateContentSize(),
            delayMillis = 45
        ),
        color = SurfaceElevated,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, PanelStroke.copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
            }
            Column {
                Text(text = label, style = MaterialTheme.typography.labelMedium, color = TextMuted)
                Text(text = value, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ActionButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = animatedEntranceModifier(
            modifier = modifier
                .fillMaxWidth()
                .animateContentSize(),
            delayMillis = 70
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) SurfaceElevated else SurfaceBlack
        ),
        border = BorderStroke(1.dp, accent.copy(alpha = if (enabled) 0.4f else 0.18f)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}

@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color = VaultGreen,
    trailing: @Composable (RowScope.() -> Unit)? = null
) {
    Row(
        modifier = animatedEntranceModifier(
            modifier = modifier.fillMaxWidth(),
            delayMillis = 35
        ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = accent
        )
        if (trailing != null) {
            trailing()
        }
    }
}

@Composable
fun DividerGlow(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(PanelStroke.copy(alpha = 0.55f))
    )
}

@Composable
fun animatedEntranceModifier(
    modifier: Modifier = Modifier,
    delayMillis: Int = 0
): Modifier {
    var revealed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (delayMillis > 0) delay(delayMillis.toLong())
        revealed = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(durationMillis = 240),
        label = "element_alpha"
    )
    val offsetY by animateDpAsState(
        targetValue = if (revealed) 0.dp else 8.dp,
        animationSpec = tween(durationMillis = 260),
        label = "element_offset"
    )
    val density = LocalDensity.current
    return modifier.graphicsLayer {
        this.alpha = alpha
        translationY = with(density) { offsetY.toPx() }
    }
}
