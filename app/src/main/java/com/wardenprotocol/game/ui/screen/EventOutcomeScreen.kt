package com.ivarna.wardenprotocol.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.ivarna.wardenprotocol.R

// Re-using same tokens for consistency
private val BackgroundColor = Color(0xFF121414)
private val Primary = Color(0xFFFFD597)
private val Secondary = Color(0xFF9EFF8B)
private val SurfaceContainerHigh = Color(0xFF282A2A)
private val SurfaceContainerHighest = Color(0xFF333535)
private val SurfaceContainerLowest = Color(0xFF0D0F0F)
private val TextPrimary = Color(0xFFE2E2E2)
private val TextSecondary = Color(0xFFD7C4AC)
private val SignalCyan = Color(0xFF88E1FF)

@Composable
fun EventOutcomeScreen(
    narrative: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundColor,
        topBar = { OutcomeTopBar() }
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
            EventOutcomeCRTOverlay()
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Tactical Header
                OutcomeTacticalHeader()

                // Report ID & Status Telemetry
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TelemetryMiniCard(stringResource(R.string.event_outcome_incident_id_label), stringResource(R.string.event_outcome_incident_id_format, narrative.length % 1000), Modifier.weight(1f))
                    TelemetryMiniCard(stringResource(R.string.event_outcome_status_label), stringResource(R.string.event_outcome_status_filed), Modifier.weight(1f), SignalCyan)
                }

                // Narrative Resolution Panel
                OutcomeNarrativePanel(narrative)

                // Command Action
                OutcomeActionSection(onDismiss)
            }
        }
    }
}

@Composable
private fun OutcomeTopBar() {
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
                text = stringResource(R.string.event_outcome_top_bar),
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

@Composable
private fun OutcomeTacticalHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = 2.dp.toPx()
                drawLine(Secondary, Offset(0f, 0f), Offset(0f, size.height), strokeWidth * 2)
            }
            .padding(start = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            stringResource(R.string.event_outcome_report_accepted),
            style = MaterialTheme.typography.labelSmall,
            color = Secondary,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            stringResource(R.string.event_outcome_command_response),
            style = MaterialTheme.typography.displaySmall,
            color = TextPrimary,
            fontWeight = FontWeight.Black,
            letterSpacing = (-1).sp
        )
        Text(
            stringResource(R.string.event_outcome_logged_msg),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun TelemetryMiniCard(label: String, value: String, modifier: Modifier = Modifier, accent: Color = Primary) {
    Column(
        modifier = modifier
            .background(SurfaceContainerHigh)
            .border(1.dp, Color.White.copy(0.05f))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary.copy(alpha = 0.5f), maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(value, style = MaterialTheme.typography.titleMedium, color = accent, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun OutcomeNarrativePanel(narrative: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.4f))
            .drawBehind {
                val stroke = 1.dp.toPx()
                drawLine(Color.White.copy(0.1f), Offset(0f, 0f), Offset(size.width, 0f), stroke)
                drawLine(Color.White.copy(0.1f), Offset(0f, size.height), Offset(size.width, size.height), stroke)
            }
            .padding(vertical = 32.dp, horizontal = 12.dp)
    ) {
        Text(
            text = narrative,
            style = MaterialTheme.typography.headlineSmall,
            color = Primary,
            lineHeight = 36.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Light,
            letterSpacing = (-0.5).sp
        )
    }
}

@Composable
private fun OutcomeActionSection(onDismiss: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDismiss() }
            .border(1.dp, SignalCyan.copy(alpha = 0.2f), RoundedCornerShape(0.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Black)
                    .border(1.dp, SignalCyan.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SignalCyan)
            }
            Column {
                Text(
                    stringResource(R.string.event_outcome_continue_scanning),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    stringResource(R.string.event_outcome_continue_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun EventOutcomeCRTOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        // Horizontal Line Effect
        val horizontalSpacing = 4.dp.toPx()
        var y = 0f
        while (y < height) {
            drawRect(
                color = Color.Black.copy(alpha = 0.12f),
                topLeft = Offset(0f, y),
                size = Size(width, 1.dp.toPx())
            )
            y += horizontalSpacing
        }

        // Vertical RGB Pixel Matrix Simulation
        val verticalSpacing = 3.dp.toPx()
        val columnWidth = 1.dp.toPx()
        var x = 0f
        while (x < width) {
            drawRect(color = Color(255, 0, 0, (255 * 0.02).toInt()), topLeft = Offset(x, 0f), size = Size(columnWidth, height))
            drawRect(color = Color(0, 255, 0, (255 * 0.01).toInt()), topLeft = Offset(x + columnWidth, 0f), size = Size(columnWidth, height))
            drawRect(color = Color(0, 0, 255, (255 * 0.02).toInt()), topLeft = Offset(x + 2 * columnWidth, 0f), size = Size(columnWidth, height))
            x += verticalSpacing
        }
    }
}
