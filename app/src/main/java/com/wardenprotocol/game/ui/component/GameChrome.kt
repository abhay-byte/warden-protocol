package com.wardenprotocol.game.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.wardenprotocol.game.ui.theme.BackgroundBlack
import com.wardenprotocol.game.ui.theme.BackgroundBlue
import com.wardenprotocol.game.ui.theme.Brass
import com.wardenprotocol.game.ui.theme.BrassDeep
import com.wardenprotocol.game.ui.theme.CyanGlow
import com.wardenprotocol.game.ui.theme.PanelStroke
import com.wardenprotocol.game.ui.theme.SurfaceBlack
import com.wardenprotocol.game.ui.theme.SurfaceElevated
import com.wardenprotocol.game.ui.theme.SurfaceMetal
import com.wardenprotocol.game.ui.theme.SurfaceSteel
import com.wardenprotocol.game.ui.theme.SteelHighlight
import com.wardenprotocol.game.ui.theme.TextMuted
import com.wardenprotocol.game.ui.theme.TextPrimary
import com.wardenprotocol.game.ui.theme.TextSecondary
import com.wardenprotocol.game.ui.theme.VaultGreen
import kotlinx.coroutines.delay

private val panelShape = RoundedCornerShape(24.dp)

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
                    colors = listOf(BackgroundBlack, Color(0xFF0B101A), BackgroundBlue, BackgroundBlack)
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val leftColumn = size.width * 0.16f
            val rightColumn = size.width * 0.84f

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF182333).copy(alpha = 0.9f), Color(0xFF0A0E16).copy(alpha = 0.6f))
                ),
                topLeft = Offset(0f, 0f),
                size = androidx.compose.ui.geometry.Size(leftColumn, size.height)
            )
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF182333).copy(alpha = 0.9f), Color(0xFF0A0E16).copy(alpha = 0.6f))
                ),
                topLeft = Offset(rightColumn, 0f),
                size = androidx.compose.ui.geometry.Size(size.width - rightColumn, size.height)
            )

            repeat(10) { index ->
                val y = size.height * (0.1f + index * 0.09f)
                drawLine(
                    color = SteelHighlight.copy(alpha = 0.06f),
                    start = Offset(leftColumn * 0.2f, y),
                    end = Offset(size.width - leftColumn * 0.2f, y),
                    strokeWidth = 1.5f
                )
            }

            repeat(8) { index ->
                val x = size.width * (0.22f + index * 0.08f)
                drawLine(
                    color = CyanGlow.copy(alpha = if (index % 3 == 0) 0.08f else 0.03f),
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1.2f
                )
            }

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(CyanGlow.copy(alpha = 0.11f), Color.Transparent)
                ),
                radius = size.minDimension * 0.42f,
                center = Offset(size.width * 0.5f, size.height * 0.34f)
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
            delayMillis = 15
        ),
        colors = CardDefaults.cardColors(containerColor = SurfaceBlack),
        border = BorderStroke(1.dp, PanelStroke.copy(alpha = 0.95f)),
        shape = panelShape
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(SurfaceMetal.copy(alpha = 0.55f), SurfaceBlack, Color(0xFF0C121C))
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(accent.copy(alpha = 0.25f), accent, accent.copy(alpha = 0.25f))
                            )
                        )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (icon != null) {
                        Surface(
                            shape = CircleShape,
                            color = SurfaceSteel,
                            border = BorderStroke(1.dp, SteelHighlight.copy(alpha = 0.35f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(accent.copy(alpha = 0.24f), Color.Transparent)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = accent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title.uppercase(),
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
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, SteelHighlight.copy(alpha = 0.22f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = SurfaceMetal,
                border = BorderStroke(1.dp, PanelStroke.copy(alpha = 0.9f))
            ) {
                Box(
                    modifier = Modifier.size(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
                }
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
        border = BorderStroke(1.dp, accent.copy(alpha = if (enabled) 0.5f else 0.2f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SurfaceSteel.copy(alpha = if (enabled) 0.34f else 0.12f),
                            SurfaceBlack,
                            SurfaceMetal.copy(alpha = if (enabled) 0.22f else 0.1f)
                        )
                    )
                )
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = SurfaceMetal,
                        border = BorderStroke(1.dp, accent.copy(alpha = if (enabled) 0.32f else 0.14f))
                    ) {
                        Box(
                            modifier = Modifier.size(38.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = title.uppercase(), style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(BrassDeep.copy(alpha = 0.55f), Brass.copy(alpha = 0.9f), BrassDeep.copy(alpha = 0.55f))
                            )
                        )
                )
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
            .background(SteelHighlight.copy(alpha = 0.18f))
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

@Composable
fun VaultCoreSeal(
    accent: Color = CyanGlow,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2f
        val center = center

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(SurfaceSteel, SurfaceBlack),
                center = center,
                radius = radius
            ),
            radius = radius
        )
        drawCircle(color = PanelStroke, radius = radius, style = Stroke(width = radius * 0.14f))
        drawCircle(color = SteelHighlight.copy(alpha = 0.55f), radius = radius * 0.86f, style = Stroke(width = radius * 0.04f))
        drawCircle(color = BrassDeep, radius = radius * 0.48f, style = Stroke(width = radius * 0.1f))
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(accent.copy(alpha = 0.95f), accent.copy(alpha = 0.18f), Color.Transparent),
                center = center,
                radius = radius * 0.5f
            ),
            radius = radius * 0.38f
        )
        repeat(6) { index ->
            val angle = Math.toRadians((index * 60.0) - 90.0)
            val bolt = Offset(
                x = center.x + kotlin.math.cos(angle).toFloat() * radius * 0.76f,
                y = center.y + kotlin.math.sin(angle).toFloat() * radius * 0.76f
            )
            drawCircle(color = Brass, radius = radius * 0.04f, center = bolt)
        }
        repeat(3) { index ->
            val orbitRadius = radius * (0.18f + index * 0.09f)
            drawCircle(
                color = accent.copy(alpha = 0.5f - index * 0.12f),
                radius = orbitRadius,
                center = center,
                style = Stroke(width = radius * 0.025f, cap = StrokeCap.Round)
            )
        }
        drawLine(
            color = Brass.copy(alpha = 0.8f),
            start = Offset(center.x - radius * 0.22f, center.y - radius * 0.15f),
            end = Offset(center.x + radius * 0.18f, center.y + radius * 0.19f),
            strokeWidth = radius * 0.06f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Brass.copy(alpha = 0.8f),
            start = Offset(center.x - radius * 0.17f, center.y + radius * 0.2f),
            end = Offset(center.x + radius * 0.22f, center.y - radius * 0.17f),
            strokeWidth = radius * 0.06f,
            cap = StrokeCap.Round
        )
        drawCircle(color = SteelHighlight.copy(alpha = 0.7f), radius = radius * 0.11f, center = center)
    }
}
