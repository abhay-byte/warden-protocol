package com.wardenprotocol.game.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wardenprotocol.game.data.model.EventChoice
import com.wardenprotocol.game.data.model.GameEvent

// High-Fidelity Color Tokens (Material 3 Surface Mappings)
private val BackgroundColor = Color(0xFF121414)
private val Primary = Color(0xFFFFD597)
private val Secondary = Color(0xFF9EFF8B)
private val Error = Color(0xFFFFB4AB)
private val OnError = Color(0xFF690005)
private val SurfaceContainerLow = Color(0xFF1A1C1C)
private val SurfaceContainerMedium = Color(0xFF1E2020)
private val SurfaceContainerHigh = Color(0xFF282A2A)
private val SurfaceContainerHighest = Color(0xFF333535)
private val SurfaceContainerLowest = Color(0xFF0D0F0F)
private val TextPrimary = Color(0xFFE2E2E2)
private val TextSecondary = Color(0xFFD7C4AC)

@Composable
fun EventScreen(
    event: GameEvent,
    onChoiceSelected: (EventChoice) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    // Emergency Flicker Animation
    val infiniteTransition = rememberInfiniteTransition()
    val flickerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 4000
                0.05f at 0
                0.05f at 1999
                0.3f at 2000
                0.3f at 2199
                0.05f at 2200
                0.05f at 6299
                0.4f at 6300
                0.4f at 6499
                0.1f at 6500
            },
            repeatMode = RepeatMode.Restart
        )
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundColor,
        topBar = { EventTopBar() }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF101316),
                            BackgroundColor,
                            SurfaceContainerLowest
                        )
                    )
                )
        ) {
            EventCRTOverlay()
            // Flicker Box moved to background layer and made non-interactive
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(flickerAlpha)
                    .background(Error)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Status Header
                StatusBreadcrumb(event.id)

                // Responsive Layout (Simplified for Mobile First)
                Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
                    // Left Side: Threat Visualizer & Telemetry
                    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        ThreatVisualizer(event)
                        TelemetryAnalysis(event.id)
                    }

                    // Right Side: Command Interface
                    Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
                        ApexThreatHeader(event.title, event.description)
                        ProtocolResponseSection(event, onChoiceSelected)
                    }
                }
                
                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

@Composable
private fun EventTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundColor)
            .tacticalGrid(alpha = 0.14f, horizontalSpacing = 3.dp, verticalSpacing = 4.dp)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Terminal,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "WARDEN_PROTOCOL_V1.0.4",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = Primary,
                letterSpacing = (-0.5).sp
            )
        }

        Icon(
            imageVector = Icons.Filled.Tune,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun StatusBreadcrumb(eventId: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRect(Error, size = size.copy(width = 4.dp.toPx()))
            }
            .padding(start = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "ALARM_LEVEL: CRITICAL",
                style = MaterialTheme.typography.labelSmall,
                color = Error,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                "SECTOR: SUBLEVEL_${eventId.take(2).uppercase()}_VAULT_ENTRY",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                letterSpacing = 2.sp
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Secondary)
                    .graphicsLayer {
                        shadowElevation = 8.dp.toPx()
                        ambientShadowColor = Secondary
                        spotShadowColor = Secondary
                    }
            )
            Text(
                "SCANNER_ONLINE",
                style = MaterialTheme.typography.labelSmall,
                color = Secondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ThreatVisualizer(event: GameEvent) {
    val imageKey = event.resolveEventImageKey()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.2f)
            .background(Color(0xFF0D0F0F))
            .border(1.dp, Color.White.copy(0.1f))
            .tacticalGrid(alpha = 0.12f)
            .padding(8.dp)
    ) {
        // Target Labels
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "TARGET_ACQUIRED",
                modifier = Modifier
                    .background(Error)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                color = OnError,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                imageKey.displayLabel.uppercase(),
                modifier = Modifier
                    .background(SurfaceContainerHighest)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                color = Primary,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Tactical Image
        Image(
            painter = painterResource(id = imageKey.drawableRes),
            contentDescription = "Threat Visual",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = 0.8f
                },
            contentScale = ContentScale.Crop
        )

        // Overlay Gradients
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, BackgroundColor.copy(0.8f)),
                        startY = 0f
                    )
                )
        )

        // HUD Coordinates
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("LAT: 44.232.1", color = Primary.copy(0.4f), style = MaterialTheme.typography.labelSmall)
            Text("LONG: -12.449.0", color = Primary.copy(0.4f), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun TelemetryAnalysis(eventId: String) {
    // Deterministic telemetry based on event ID
    val density = (eventId.hashCode() % 30 + 70).coerceIn(0, 100)
    val integrity = (eventId.hashCode() % 40 + 10).coerceIn(0, 100)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceContainerLow)
            .tacticalGrid(alpha = 0.07f)
            .drawBehind {
                drawRect(Primary, size = size.copy(width = 4.dp.toPx()))
            }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Filled.QueryStats, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
            Text(
                "Telemetry_Analysis",
                style = MaterialTheme.typography.labelSmall,
                color = Primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        TelemetryBar("BIOMASS_DENSITY", density, Primary)
        TelemetryBar("STRUCTURAL_INTEGRITY", integrity, if (integrity < 20) Error else Primary)
    }
}

@Composable
private fun TelemetryBar(label: String, value: Int, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = TextSecondary, style = MaterialTheme.typography.labelSmall)
            Text("$value.0%", color = color, style = MaterialTheme.typography.labelSmall)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(SurfaceContainerLowest)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(value / 100f)
                    .fillMaxHeight()
                    .background(color)
            )
        }
    }
}

@Composable
private fun ApexThreatHeader(title: String, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        val titleParts = title.split(":")
        Column {
            Text(
                text = (titleParts.getOrNull(0) ?: "INCIDENT").uppercase(),
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )
            if (titleParts.size > 1) {
                Text(
                    text = titleParts[1].trim().uppercase(),
                    style = MaterialTheme.typography.displayMedium,
                    color = Primary,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-2).sp
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0D0F0F))
                .border(1.dp, Color.White.copy(0.05f))
                .tacticalGrid(alpha = 0.07f)
                .padding(24.dp)
        ) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = Primary,
                lineHeight = 24.sp,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@Composable
private fun ProtocolResponseSection(event: GameEvent, onChoiceSelected: (EventChoice) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
            Text(
                "SELECT_PROTOCOL_RESPONSE",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ProtocolChoiceCard("01", event.choiceA) { onChoiceSelected(event.choiceA) }
            ProtocolChoiceCard("02", event.choiceB) { onChoiceSelected(event.choiceB) }
            event.choiceC?.let { choiceC ->
                ProtocolChoiceCard("03", choiceC, isCritical = true) { onChoiceSelected(choiceC) }
            }
        }
    }
}

@Composable
private fun ProtocolChoiceCard(
    index: String,
    choice: EventChoice,
    isCritical: Boolean = false,
    onClick: () -> Unit
) {
    val accent = if (isCritical) Error else Primary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .tacticalGrid(alpha = 0.12f, horizontalSpacing = 2.dp, verticalSpacing = 3.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh),
        shape = RoundedCornerShape(0.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(BackgroundColor)
                        .border(1.dp, accent.copy(0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(index, color = accent, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            choice.label.uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        choice.description,
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 20.sp
                    )

                    Column(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricLine(Icons.Filled.Bolt, "ENERGY_COST", "[REDACTED]", Primary)
                    }
                }

                Icon(
                    Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .align(Alignment.Top)
                )
            }
            
            // Interaction underline
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(accent.copy(0.1f))
            )
        }
    }
}

@Composable
private fun MetricLine(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
        Text(
            "$label: $value",
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun EventCRTOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .tacticalGrid(alpha = 0.05f, horizontalSpacing = 3.dp, verticalSpacing = 5.dp)
    )
}

private fun Modifier.tacticalGrid(
    alpha: Float = 0.15f,
    horizontalSpacing: androidx.compose.ui.unit.Dp = 3.dp,
    verticalSpacing: androidx.compose.ui.unit.Dp = 4.dp
): Modifier = this.drawBehind {
    val horizontalPx = horizontalSpacing.toPx()
    val verticalPx = verticalSpacing.toPx()
    
    // Horizontal Scanlines
    var y = 0f
    while (y < size.height) {
        drawRect(
            color = Color.Black.copy(alpha = alpha),
            topLeft = Offset(0f, y),
            size = Size(size.width, 1.dp.toPx())
        )
        y += horizontalPx
    }

    // Vertical Phosphor Columns
    var x = 0f
    while (x < size.width) {
        drawRect(
            color = Color.Black.copy(alpha = alpha * 0.25f),
            topLeft = Offset(x, 0f),
            size = Size(1.dp.toPx(), size.height)
        )
        x += verticalPx
    }
}
