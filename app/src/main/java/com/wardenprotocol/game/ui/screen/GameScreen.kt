package com.wardenprotocol.game.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wardenprotocol.game.data.model.GameState
import com.wardenprotocol.game.data.model.SurfaceLocation
import kotlin.math.roundToInt

// Strict Tailwind Color Tokens 
private val BackgroundColor = Color(0xFF121414)
private val SurfaceContainerHighest = Color(0xFF333535)
private val SurfaceContainerHigh = Color(0xFF282A2A)
private val SurfaceContainer = Color(0xFF1E2020)
private val SurfaceContainerLow = Color(0xFF1A1C1C)
private val SurfaceContainerLowest = Color(0xFF0D0F0F)
private val Primary = Color(0xFFFFD597)
private val Secondary = Color(0xFF9EFF8B)
private val Error = Color(0xFFFFB4AB)
private val PrimaryFixedDim = Color(0xFFFFBA43)
private val OutlineVariant = Color(0xFF524533)
private val TextPrimary = Color(0xFFE2E2E2)
private val PrimaryContainer = Color(0xFFFFB000)
private val ErrorContainer = Color(0xFF93000A)
private val OnErrorContainer = Color(0xFFFFDAD6)

@Composable
fun GameScreen(
    gameState: GameState,
    location: SurfaceLocation,
    probeRevealed: Boolean,
    onOpenVault: () -> Unit,
    onContinueSearching: () -> Unit,
    onDeployProbe: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundColor,
        topBar = { SurfaceHeaderRow() },
        bottomBar = { SurfaceBottomNavTray(gameState.surfaceProbes > 0, onContinueSearching, onDeployProbe, onOpenVault) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            SurfaceScanlineOverlay() // Generic CRT underlying everything
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(Modifier.height(8.dp))
                
                // 1. Vault 01 Status
                VaultStatusPanel(gameState)
                
                // 2. Bunker Telemetry
                BunkerTelemetryPanel(gameState)
                
                // 3. Environment Telemetry
                EnvironmentTelemetryPanel(location)
                
                // 4. Main Surface Scan Target
                SurfaceScanTargetPanel(location)
                
                // 5. Transit Section
                TransitSectionPanel(location)
                
                // 6. System Alerts
                SystemAlertsPanel()
                
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SurfaceHeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundColor)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Terminal, contentDescription = null, tint = Primary)
            Text(
                "WARDEN_PROTOCOL_v1.0.4",
                style = MaterialTheme.typography.titleLarge,
                color = Primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1).sp
            )
        }
        Icon(Icons.Filled.SettingsInputComponent, contentDescription = null, tint = Primary.copy(0.6f))
    }
}

@Composable
private fun VaultStatusPanel(gameState: GameState) {
    val healthAvg = listOf(
        gameState.vaultSystems.powerGrid,
        gameState.vaultSystems.medicalBay,
        gameState.vaultSystems.securitySystem,
        gameState.vaultSystems.atmosphereScrubbers
    ).average().toFloat()
    
    val radiationText = if (gameState.vaultSystems.radiationScanner > 20) "0.02 mSv" else "ERR"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(SurfaceContainerLow)
            .border(width = 0.dp, color = Color.Transparent) // base
            .drawBehind {
                drawRect(Primary, size = size.copy(width = 4.dp.toPx())) // border-l-4
            }
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier
                        .size(8.dp)
                        .background(Secondary)
                        .graphicsLayer {
                            shadowElevation = 8.dp.toPx()
                            ambientShadowColor = Secondary
                            spotShadowColor = Secondary
                        }
                    )
                    Text("VAULT 01 STATUS", color = Primary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    StatusItem("HEALTH", "${healthAvg.roundToInt()}%", Secondary)
                    StatusItem("RADIATION", radiationText, Error)
                    StatusItem("FOOD RESERVES", "${gameState.vaultSystems.foodStores} DAYS", Primary)
                }
            }
            
            // Progress Bar
            Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(SurfaceContainerHighest)) {
                Box(modifier = Modifier
                    .fillMaxWidth(healthAvg / 100f)
                    .fillMaxHeight()
                    .background(Primary)
                    .graphicsLayer {
                        shadowElevation = 10.dp.toPx()
                        ambientShadowColor = Primary
                        spotShadowColor = Primary
                    }
                )
            }
        }
    }
}

@Composable
private fun StatusItem(label: String, value: String, valueColor: Color) {
    Column {
        Text(label, color = TextPrimary.copy(alpha = 0.6f), style = MaterialTheme.typography.labelSmall)
        Text(value, color = valueColor, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun BunkerTelemetryPanel(gameState: GameState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(SurfaceContainer)
            .drawBehind { drawRect(OutlineVariant.copy(0.2f), size = size.copy(height = 1.dp.toPx())) }
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("BUNKER TELEMETRY", color = TextPrimary.copy(0.7f), style = MaterialTheme.typography.labelSmall, letterSpacing = 2.sp)
        
        TelemetryRow("Survivors", "1,000") // Mocking 1,000 as per screenshot exactness
        TelemetryRow("Years Sealed", "000")
        TelemetryRow("Active Probes", "0${gameState.surfaceProbes}")
    }
}

@Composable
private fun TelemetryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().drawBehind {
             drawLine(OutlineVariant.copy(0.1f), Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx())
        }.padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelMedium, color = TextPrimary.copy(0.7f))
        Text(value, style = MaterialTheme.typography.headlineMedium, color = Primary, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun EnvironmentTelemetryPanel(location: SurfaceLocation) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).background(SurfaceContainerLow).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("ENVIRONMENT TELEMETRY", color = TextPrimary.copy(0.7f), style = MaterialTheme.typography.labelSmall, letterSpacing = 2.sp)
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            EnvBox("Radiation", "HIGH", Error, Modifier.weight(1f))
            EnvBox("Water", "LOW", Primary, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            EnvBox("Food", "NONE", TextPrimary.copy(0.4f), Modifier.weight(1f))
            EnvBox("Shelter", "MINIMAL", PrimaryFixedDim, Modifier.weight(1f))
        }
    }
}

@Composable
private fun EnvBox(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.background(SurfaceContainerHighest).padding(12.dp)
    ) {
        Text(label.uppercase(), color = TextPrimary.copy(0.7f), style = MaterialTheme.typography.labelSmall)
        Text(value.uppercase(), color = color, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SurfaceScanTargetPanel(location: SurfaceLocation) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(SurfaceContainerHighest)
    ) {
        // Gradient backdrop simulating the mix-blend imagery
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color.Transparent, BackgroundColor))
        ))
        
        Column(modifier = Modifier.padding(32.dp)) {
            // Header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column {
                    Text("TARGET_LOCKED", color = BackgroundColor, modifier = Modifier.background(Primary).padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("SHATTERED", color = Primary, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black, letterSpacing = (-2).sp)
                    Text("SPIRE", color = Primary, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black, letterSpacing = (-2).sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("LAT: 34.0522 N", color = Primary.copy(0.6f), style = MaterialTheme.typography.labelSmall)
                    Text("LNG: 118.2437 W", color = Primary.copy(0.6f), style = MaterialTheme.typography.labelSmall)
                }
            }
            
            Spacer(Modifier.height(48.dp))
            
            // Description block
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundColor.copy(0.8f))
                    .drawBehind { drawRect(PrimaryContainer, size = size.copy(width = 2.dp.toPx())) }
                    .padding(24.dp)
            ) {
                Text(
                    "\"Visual confirms the structural collapse of the primary observation deck. Signal interference is peaking at 400MHz. Thermal bloom detected in the lower strata. Suggest immediate probe deployment before atmospheric shift.\"",
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 24.sp
                )
            }
            
            Spacer(Modifier.height(32.dp))
            
            // Image placeholders
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ImageFeedBox("IMAGERY_01", Modifier.weight(1f))
                ImageFeedBox("SPECTRAL_02", Modifier.weight(1f))
            }
        }
        
        // Corner Accents
        Box(modifier = Modifier.size(32.dp).align(Alignment.TopEnd).drawBehind {
            drawLine(Primary.copy(0.3f), Offset(0f, 0f), Offset(size.width, 0f), 2.dp.toPx())
            drawLine(Primary.copy(0.3f), Offset(size.width, 0f), Offset(size.width, size.height), 2.dp.toPx())
        })
        Box(modifier = Modifier.size(32.dp).align(Alignment.BottomStart).drawBehind {
            drawLine(Primary.copy(0.3f), Offset(0f, size.height), Offset(0f, 0f), 2.dp.toPx())
            drawLine(Primary.copy(0.3f), Offset(0f, size.height), Offset(size.width, size.height), 2.dp.toPx())
        })
    }
}

@Composable
private fun ImageFeedBox(label: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .height(128.dp)
            .background(SurfaceContainerLowest)
            .border(1.dp, OutlineVariant.copy(0.2f)),
        contentAlignment = Alignment.Center
    ) {
        // Mock noise background
        Box(modifier = Modifier.fillMaxSize().drawBehind {
            drawRect(Color.White.copy(0.05f))
        })
        Text(
            label,
            color = TextPrimary,
            modifier = Modifier.background(BackgroundColor.copy(0.8f)).padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun TransitSectionPanel(location: SurfaceLocation) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).background(SurfaceContainer).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("TRANSIT SECTION", color = TextPrimary.copy(0.7f), style = MaterialTheme.typography.labelSmall, letterSpacing = 2.sp)
        
        Column {
            Text("ACTIVE ROUTE", color = TextPrimary.copy(0.6f), style = MaterialTheme.typography.labelSmall)
            Text("Dead Man's Pass", color = Primary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(modifier = Modifier.weight(1f).background(SurfaceContainerLow).padding(12.dp)) {
                Text("RISK FACTOR", color = TextPrimary.copy(0.6f), style = MaterialTheme.typography.labelSmall)
                Text("85%", color = Error, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            }
            Column(modifier = Modifier.weight(1f).background(SurfaceContainerLow).padding(12.dp)) {
                Text("ATTRITION", color = TextPrimary.copy(0.6f), style = MaterialTheme.typography.labelSmall)
                Text("MOD", color = PrimaryFixedDim, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            }
        }
        
        Column(
            modifier = Modifier.fillMaxWidth().background(Primary.copy(0.05f))
                .drawBehind { drawRect(Primary, size = size.copy(width = 2.dp.toPx())) }
                .padding(12.dp)
        ) {
            Text("SCORE MULTIPLIER", color = Primary, style = MaterialTheme.typography.labelSmall)
            Text("x0.8", color = Primary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SystemAlertsPanel() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).background(SurfaceContainerLow).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("SYSTEM ALERTS", color = TextPrimary.copy(0.7f), style = MaterialTheme.typography.labelSmall, letterSpacing = 2.sp)
        
        AlertItem("SIGNAL_LOSS_DETECTED", Error)
    }
}

@Composable
private fun AlertItem(message: String, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(6.dp).background(color))
        Text(message, color = color, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun SurfaceBottomNavTray(
    probesAvailable: Boolean,
    onSearch: () -> Unit,
    onProbe: () -> Unit,
    onOpenVault: () -> Unit
) {
    Column {
        // Sticky action bar
        Row(
            modifier = Modifier.fillMaxWidth().height(96.dp).background(BackgroundColor).padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight().background(SurfaceContainerHigh).clickable { onSearch() }
                    .drawBehind { drawRect(SurfaceContainerHighest, topLeft = Offset(0f, size.height - 4.dp.toPx()), size = size.copy(height = 4.dp.toPx())) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Filled.GridView, contentDescription = null, tint = Primary)
                Text("SEARCH", color = Primary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top=4.dp))
            }
            
            // Probe
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight().background(SurfaceContainerHigh).clickable(enabled = probesAvailable) { onProbe() }
                    .drawBehind { drawRect(SurfaceContainerHighest, topLeft = Offset(0f, size.height - 4.dp.toPx()), size = size.copy(height = 4.dp.toPx())) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Filled.Radar, contentDescription = null, tint = Secondary)
                Text("PROBE", color = Secondary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top=4.dp))
            }
            
            // Open Vault
            Row(
                modifier = Modifier.weight(2f).fillMaxHeight().background(ErrorContainer).clickable { onOpenVault() }
                    .drawBehind { drawRect(Color(0xFF690005), topLeft = Offset(0f, size.height - 4.dp.toPx()), size = size.copy(height = 4.dp.toPx())) }
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("OPEN VAULT", color = OnErrorContainer, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, letterSpacing = (-1).sp)
                    Text("AUTHORIZATION REQUIRED", color = OnErrorContainer.copy(0.7f), style = MaterialTheme.typography.labelSmall)
                }
                Icon(Icons.Filled.PowerSettingsNew, contentDescription = null, tint = OnErrorContainer, modifier = Modifier.size(32.dp))
            }
        }
        
        // Mobile wrapper nav overlay
        Row(
            modifier = Modifier.fillMaxWidth().height(64.dp).background(BackgroundColor),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTab("COMMAND", Icons.Filled.GridView, PrimaryContainer, BackgroundColor, weight = 1f)
            NavTab("SURFACE", Icons.Filled.Radar, BackgroundColor, Primary.copy(0.5f), weight = 1f)
            NavTab("ARCHIVE", Icons.Filled.HistoryEdu, BackgroundColor, Primary.copy(0.5f), weight = 1f)
            NavTab("SYSTEM", Icons.Filled.DeveloperBoard, BackgroundColor, Primary.copy(0.5f), weight = 1f)
        }
    }
}

@Composable
private fun RowScope.NavTab(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, bg: Color, fg: Color, weight: Float) {
    Column(
        modifier = Modifier.weight(weight).fillMaxHeight().background(bg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = fg)
        Text(title, color = fg, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(top=2.dp))
    }
}

@Composable
private fun SurfaceScanlineOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        val horizontalSpacing = 3.dp.toPx()
        var y = 0f
        while (y < height) {
            drawRect(
                color = Color.Black.copy(alpha = 0.10f),
                topLeft = Offset(0f, y + horizontalSpacing / 2),
                size = Size(width, horizontalSpacing / 2)
            )
            y += horizontalSpacing
        }

        val verticalSpacing = 3.dp.toPx()
        val columnWidth = verticalSpacing / 3f
        var x = 0f
        while (x < width) {
            drawRect(color = Color(255, 0, 0, (255 * 0.03).toInt()), topLeft = Offset(x, 0f), size = Size(columnWidth, height))
            drawRect(color = Color(0, 255, 0, (255 * 0.01).toInt()), topLeft = Offset(x + columnWidth, 0f), size = Size(columnWidth, height))
            drawRect(color = Color(0, 0, 255, (255 * 0.03).toInt()), topLeft = Offset(x + 2 * columnWidth, 0f), size = Size(columnWidth, height))
            x += verticalSpacing
        }
    }
}
