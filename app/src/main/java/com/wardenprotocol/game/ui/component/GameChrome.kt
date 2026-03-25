package com.wardenprotocol.game.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.wardenprotocol.game.ui.theme.BackgroundBlack
import com.wardenprotocol.game.ui.theme.BackgroundBlue
import com.wardenprotocol.game.ui.theme.PanelStroke
import com.wardenprotocol.game.ui.theme.SignalCyan
import com.wardenprotocol.game.ui.theme.SurfaceBlack
import com.wardenprotocol.game.ui.theme.SurfaceElevated
import com.wardenprotocol.game.ui.theme.TextMuted
import com.wardenprotocol.game.ui.theme.TextPrimary
import com.wardenprotocol.game.ui.theme.TextSecondary
import com.wardenprotocol.game.ui.theme.VaultGreen
import kotlinx.coroutines.delay

private val panelShape = RoundedCornerShape(28.dp)

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
        Canvas(modifier = Modifier.fillMaxSize()) {
            val spacing = size.width / 6f
            for (index in 0..6) {
                val x = index * spacing
                drawLine(
                    color = SignalCyan.copy(alpha = 0.08f),
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1f
                )
            }
            for (index in 0..10) {
                val y = index * (size.height / 10f)
                drawLine(
                    color = VaultGreen.copy(alpha = 0.04f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
            }
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(VaultGreen.copy(alpha = 0.18f), Color.Transparent),
                    center = Offset(size.width * 0.82f, size.height * 0.16f),
                    radius = size.minDimension * 0.35f
                ),
                radius = size.minDimension * 0.35f,
                center = Offset(size.width * 0.82f, size.height * 0.16f)
            )
        }
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
            delayMillis = 20
        ),
        colors = CardDefaults.cardColors(containerColor = SurfaceBlack.copy(alpha = 0.92f)),
        border = BorderStroke(1.dp, PanelStroke),
        shape = panelShape
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(accent.copy(alpha = 0.16f))
                            .border(1.dp, accent.copy(alpha = 0.35f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = accent)
                    }
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
            delayMillis = 70
        ),
        color = accent.copy(alpha = 0.12f),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
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
            delayMillis = 110
        ),
        colors = CardDefaults.cardColors(
            containerColor = accent.copy(alpha = if (enabled) 0.15f else 0.08f)
        ),
        border = BorderStroke(1.dp, accent.copy(alpha = if (enabled) 0.35f else 0.15f)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = if (enabled) 0.22f else 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent)
            }
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
            delayMillis = 50
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
fun VaultSeal(
    modifier: Modifier = Modifier,
    accent: Color = VaultGreen
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2.3f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(accent.copy(alpha = 0.28f), Color.Transparent),
                center = center,
                radius = radius * 1.8f
            ),
            radius = radius * 1.8f,
            center = center
        )
        repeat(4) { index ->
            drawCircle(
                color = accent.copy(alpha = 0.18f - index * 0.03f),
                radius = radius - index * radius * 0.18f,
                center = center,
                style = Stroke(width = 18f - index * 3f, cap = StrokeCap.Round)
            )
        }
        drawCircle(color = accent.copy(alpha = 0.2f), radius = radius * 0.22f, center = center)
        drawCircle(color = accent, radius = radius * 0.09f, center = center)
    }
}

@Composable
fun DividerGlow(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, PanelStroke, Color.Transparent)
                )
            )
    )
}

@Composable
fun animatedEntranceModifier(
    modifier: Modifier = Modifier,
    delayMillis: Int = 0
): Modifier {
    var revealed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (delayMillis > 0) {
            delay(delayMillis.toLong())
        }
        revealed = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(durationMillis = 360),
        label = "element_alpha"
    )
    val offsetY by animateDpAsState(
        targetValue = if (revealed) 0.dp else 14.dp,
        animationSpec = tween(durationMillis = 420),
        label = "element_offset"
    )
    val density = LocalDensity.current

    return modifier.graphicsLayer {
        this.alpha = alpha
        translationY = with(density) { offsetY.toPx() }
    }
}
