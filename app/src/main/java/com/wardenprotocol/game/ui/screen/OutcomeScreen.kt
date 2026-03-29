package com.wardenprotocol.game.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wardenprotocol.game.R
import com.wardenprotocol.game.data.model.ColonyOutcome
import com.wardenprotocol.game.data.model.OutcomeStats
import com.wardenprotocol.game.ui.theme.*

// Theme Colors consistent with Brutalist bunker interface
private val Primary = Color(0xFFFFD597)
private val PrimaryContainer = Color(0xFFFFB000)
private val WarningAmber = Color(0xFFFFB000)
private val Secondary = Color(0xFF9EFF8B)
private val DangerRed = Color(0xFF93000A)
private val SurfaceContainerHigh = Color(0xFF282A2A)
private val SurfaceContainerHighest = Color(0xFF333535)
private val SurfaceContainerLowest = Color(0xFF0D0F0F)
private val TextPrimary = Color(0xFFE2E2E2)
private val TextSecondary = Color(0xFFD7C4AC)

@Composable
fun OutcomeScreen(
    outcome: ColonyOutcome,
    isNewHighScore: Boolean,
    onPlayAgain: () -> Unit,
    onShowLeaderboard: () -> Unit,
    onShowHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF101316),
                        Color(0xFF121414),
                        Color(0xFF0D0F0F)
                    )
                )
            )
    ) {
        // CRT Scanlines & RGB Matrix Effect
        Canvas(modifier = Modifier.fillMaxSize()) {
            val scanlineCount = (size.height / 3f).toInt()
            for (i in 0 until scanlineCount) {
                val y = i * 3f
                drawLine(
                    color = Color.Black.copy(alpha = 0.15f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
            }
            
            val pixelDensity = 6f
            for (x in 0 until (size.width / pixelDensity).toInt()) {
                for (y in 0 until (size.height / pixelDensity).toInt()) {
                    if ((x + y) % 2 == 0) continue
                    drawRect(
                        color = Color.White.copy(alpha = 0.02f),
                        topLeft = Offset(x * pixelDensity, y * pixelDensity),
                        size = Size(1f, 1f)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Tactical Header (Sync with other screens)
            TacticalMissionHeader()

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Main Layout for larger screens could be row, but sticking to linear for mobile
                OutcomeHeroSection(outcome, isNewHighScore)

                PostSettlementChronicles(outcome.narrative)

                RunBreakdownSection(outcome.detailedStats)

                VaultStatusSection(outcome.detailedStats)

                GlobalActionSection(
                    onRestart = onPlayAgain,
                    onArchive = onShowLeaderboard,
                    onMenu = onShowHistory
                )

                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}

@Composable
private fun TacticalMissionHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(
                Icons.Filled.Terminal, 
                contentDescription = null, 
                tint = Primary, 
                modifier = Modifier.size(24.dp)
            )
            Text(
                "WARDEN_PROTOCOL_v1.0.4",
                style = MaterialTheme.typography.titleMedium,
                color = Primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1).sp
            )
        }
        Icon(
            Icons.Filled.SettingsInputComponent,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun OutcomeHeroSection(outcome: ColonyOutcome, isNewHighScore: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .border(1.dp, Primary.copy(alpha = 0.1f))
            .background(SurfaceContainerLowest)
            .clip(RoundedCornerShape(0.dp))
    ) {
        // Hero Image Backdrop (Grayscale/Dimmed)
        Image(
            painter = painterResource(id = R.drawable.loc_military_base),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.colorMatrix(
                androidx.compose.ui.graphics.ColorMatrix().apply { setToSaturation(0f) }
            ),
            alpha = 0.2f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Status Badge
                Row(
                    modifier = Modifier
                        .border(1.dp, Secondary.copy(alpha = 0.4f))
                        .background(Secondary.copy(alpha = 0.05f))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.size(8.dp).background(Secondary))
                    Text(
                        "MISSION_COMPLETE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Secondary,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isNewHighScore) {
                    Box(
                        modifier = Modifier
                            .background(PrimaryContainer)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "NEW HIGH SCORE! ${outcome.score} PTS",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Black,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                outcome.settlementName.uppercase(),
                style = MaterialTheme.typography.displaySmall,
                color = Primary,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Classification: ${outcome.classification}",
                    style = MaterialTheme.typography.labelLarge,
                    color = Primary.copy(alpha = 0.8f),
                    letterSpacing = 1.sp
                )
                Box(modifier = Modifier.height(1.dp).weight(1f).background(Primary.copy(alpha = 0.2f)))
                Text(
                    "ID: 0x8FA2-GAMMA",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun PostSettlementChronicles(narrative: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = 12f
                drawLine(
                    color = PrimaryContainer,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = strokeWidth
                )
            }
            .background(SurfaceContainerHigh)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Filled.HistoryEdu, contentDescription = null, tint = Primary, modifier = Modifier.size(24.dp))
            Text(
                "POST-SETTLEMENT CHRONICLES",
                style = MaterialTheme.typography.titleMedium,
                color = Primary,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            narrative,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary.copy(alpha = 0.9f),
            lineHeight = 28.sp
        )
    }
}

@Composable
private fun RunBreakdownSection(stats: OutcomeStats?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceContainerLowest.copy(alpha = 0.5f))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "RUN_BREAKDOWN_v04",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            BreakdownRow("Total Travel", "${4200} KM", Primary)
            BreakdownRow("Site Potential", "88%", Secondary)
            BreakdownRow("Final Survivors", stats?.survivors?.toString() ?: "0", PrimaryContainer)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceContainerHighest)
                .padding(12.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Telemetry Precision", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                Text("99.8%", style = MaterialTheme.typography.titleSmall, color = Secondary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun BreakdownRow(label: String, value: String, accent: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceContainerLowest)
            .drawBehind {
                drawLine(
                    color = accent,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = 4f
                )
            }
            .padding(16.dp)
    ) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        Text(value, style = MaterialTheme.typography.headlineLarge, color = accent, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun VaultStatusSection(stats: OutcomeStats?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = DangerRed,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 4f
                )
            }
            .background(SurfaceContainerHigh)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Filled.Memory, contentDescription = null, tint = DangerRed, modifier = Modifier.size(20.dp))
                Text("VAULT DIAGNOSTICS", style = MaterialTheme.typography.labelSmall, color = DangerRed, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Filled.ExpandMore, contentDescription = null, tint = TextSecondary)
        }

        MetricBar("Hull Integrity", stats?.securitySystem ?: 42, DangerRed)
        MetricBar("Core Temp", stats?.powerGrid ?: 78, Primary)
    }
}

@Composable
private fun MetricBar(label: String, value: Int, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = TextPrimary.copy(alpha = 0.7f))
            Text(
                if (value < 50) "$value% (CRITICAL)" else "OPTIMAL",
                style = MaterialTheme.typography.labelSmall,
                color = if (value < 50) DangerRed else Primary
            )
        }
        Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(SurfaceContainerLowest)) {
            Box(modifier = Modifier.fillMaxWidth(value / 100f).fillMaxHeight().background(color))
        }
    }
}

@Composable
private fun GlobalActionSection(onRestart: () -> Unit, onArchive: () -> Unit, onMenu: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = onRestart,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Filled.Refresh, contentDescription = null, tint = Color.Black)
                Text(
                    "RESTART MISSION",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onArchive,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(0.dp),
                border = borderStroke(1.dp, Primary.copy(alpha = 0.3f))
            ) {
                Text("ARCHIVE RESULTS", style = MaterialTheme.typography.labelLarge, color = Primary, fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = onMenu,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(0.dp),
                border = borderStroke(1.dp, Primary.copy(alpha = 0.3f))
            ) {
                Text("MAIN MENU", style = MaterialTheme.typography.labelLarge, color = Primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun borderStroke(width: androidx.compose.ui.unit.Dp, color: Color) = androidx.compose.foundation.BorderStroke(width, color)
