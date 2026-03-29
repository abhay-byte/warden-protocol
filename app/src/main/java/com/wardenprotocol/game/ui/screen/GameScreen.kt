package com.wardenprotocol.game.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wardenprotocol.game.R
import com.wardenprotocol.game.data.model.FoodPotential
import com.wardenprotocol.game.data.model.GameState
import com.wardenprotocol.game.data.model.Hostility
import com.wardenprotocol.game.data.model.LocationType
import com.wardenprotocol.game.data.model.RadiationLevel
import com.wardenprotocol.game.data.model.ResourceRichness
import com.wardenprotocol.game.data.model.ShelterQuality
import com.wardenprotocol.game.data.model.SurfaceAnomaly
import com.wardenprotocol.game.data.model.SurfaceLocation
import com.wardenprotocol.game.data.model.TravelRisk
import com.wardenprotocol.game.data.model.WaterAvailability
import kotlin.math.roundToInt

private val BackgroundColor = Color(0xFF121414)
private val SurfaceContainerHighest = Color(0xFF333535)
private val SurfaceContainerHigh = Color(0xFF282A2A)
private val SurfaceContainer = Color(0xFF1E2020)
private val SurfaceContainerLow = Color(0xFF1A1C1C)
private val SurfaceContainerLowest = Color(0xFF0D0F0F)
private val Primary = Color(0xFFFFD597)
private val Secondary = Color(0xFF9EFF8B)
private val Error = Color(0xFFFF7A70)
private val PrimaryFixedDim = Color(0xFFFFBA43)
private val OutlineVariant = Color(0xFF524533)
private val TextPrimary = Color(0xFFE2E2E2)
private val TextMuted = Color(0xFF949494)
private val PrimaryContainer = Color(0xFFFFB000)
private val ErrorContainer = Color(0xFF3E1519)
private val OnErrorContainer = Color(0xFFFFDAD6)
private val SignalBlue = Color(0xFF88E1FF)

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
        topBar = { SurfaceHeaderRow(gameState, location) },
        bottomBar = {
            SurfaceBottomCommandTray(
                probesAvailable = gameState.surfaceProbes > 0,
                probeRevealed = probeRevealed,
                location = location,
                onSearch = onContinueSearching,
                onProbe = onDeployProbe,
                onOpenVault = onOpenVault
            )
        }
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
            SurfaceScanlineOverlay()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Spacer(Modifier.height(8.dp))
                VaultStatusPanel(gameState)
                BunkerTelemetryPanel(gameState, location)
                EnvironmentTelemetryPanel(gameState, location)
                ScannerDiagnosticsPanel(gameState)
                SurfaceScanTargetPanel(location, probeRevealed)
                TransitSectionPanel(location)
                SystemAlertsPanel(gameState, location)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SurfaceHeaderRow(gameState: GameState, location: SurfaceLocation) {
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
            Column {
                Text(
                    "WARDEN_PROTOCOL_v1.0.4",
                    style = MaterialTheme.typography.titleLarge,
                    color = Primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp
                )
                Text(
                    "${location.type.displayName()} / YEAR ${gameState.yearsSinceWar}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary.copy(alpha = 0.55f)
                )
            }
        }

        Row(
            modifier = Modifier
                .background(SurfaceContainerHigh, RoundedCornerShape(999.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Shield, contentDescription = null, tint = severityColor(location))
            Text(
                openingRiskLabel(location),
                style = MaterialTheme.typography.labelMedium,
                color = severityColor(location),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun VaultStatusPanel(gameState: GameState) {
    val coreAverage = listOf(
        gameState.vaultSystems.powerGrid,
        gameState.vaultSystems.foodStores,
        gameState.vaultSystems.medicalBay,
        gameState.vaultSystems.securitySystem,
        gameState.vaultSystems.constructionGear,
        gameState.vaultSystems.atmosphereScrubbers
    ).average().toFloat()

    val archiveAverage = listOf(
        gameState.databases.culturalArchive,
        gameState.databases.scientificArchive
    ).average().roundToInt()

    val criticalCount = listOf(
        gameState.vaultSystems.powerGrid,
        gameState.vaultSystems.foodStores,
        gameState.vaultSystems.medicalBay,
        gameState.vaultSystems.securitySystem,
        gameState.vaultSystems.constructionGear,
        gameState.vaultSystems.atmosphereScrubbers,
        gameState.vaultSystems.radiationScanner,
        gameState.vaultSystems.waterScanner,
        gameState.vaultSystems.agriculturalScanner,
        gameState.vaultSystems.structureScanner,
        gameState.vaultSystems.resourceScanner,
        gameState.vaultSystems.threatAssessment,
        gameState.databases.culturalArchive,
        gameState.databases.scientificArchive
    ).count { it <= 15 }

    TacticalPanel(
        modifier = Modifier.padding(horizontal = 16.dp),
        accent = Secondary
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlowingDot(Secondary)
                    Text(
                        "VAULT 01 STATUS",
                        color = Primary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    StatusItem("CORE", "${coreAverage.roundToInt()}%", healthColor(coreAverage.roundToInt()))
                    StatusItem("ARCHIVE", "$archiveAverage%", healthColor(archiveAverage))
                    StatusItem("CRITICAL", criticalCount.toString(), if (criticalCount == 0) Secondary else Error)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(SurfaceContainerHighest)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(coreAverage / 100f)
                        .fillMaxSize()
                        .background(Primary)
                )
            }

            Text(
                text = vaultStatusLine(coreAverage.roundToInt(), criticalCount),
                color = TextPrimary.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun StatusItem(label: String, value: String, valueColor: Color) {
    Column {
        Text(label, color = TextPrimary.copy(alpha = 0.6f), style = MaterialTheme.typography.labelSmall)
        Text(value, color = valueColor, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun BunkerTelemetryPanel(gameState: GameState, location: SurfaceLocation) {
    TacticalPanel(
        modifier = Modifier.padding(horizontal = 16.dp),
        accent = Primary
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Text(
                "BUNKER TELEMETRY",
                color = TextPrimary.copy(0.7f),
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 2.sp
            )

            TelemetryRow("Survivors", gameState.survivors.toString())
            TelemetryRow("Years Sealed", gameState.yearsSinceWar.toString().padStart(3, '0'))
            TelemetryRow("Sites Scanned", gameState.surfaceLocationsScanned.toString().padStart(2, '0'))
            TelemetryRow("Active Probes", gameState.surfaceProbes.toString().padStart(2, '0'))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLow)
                    .drawBehind { drawRect(PrimaryContainer, size = size.copy(width = 2.dp.value * density)) }
                    .padding(12.dp)
            ) {
                Text(
                    text = buildOverviewLine(location),
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun TelemetryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    OutlineVariant.copy(0.18f),
                    Offset(0f, size.height),
                    Offset(size.width, size.height),
                    1.dp.toPx()
                )
            }
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelMedium, color = TextPrimary.copy(0.7f))
        Text(value, style = MaterialTheme.typography.headlineMedium, color = Primary, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun EnvironmentTelemetryPanel(
    gameState: GameState,
    location: SurfaceLocation
) {
    TacticalPanel(
        modifier = Modifier.padding(horizontal = 16.dp),
        accent = SignalBlue
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                "ENVIRONMENT TELEMETRY",
                color = TextPrimary.copy(0.7f),
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 2.sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                EnvBox(
                    "Radiation",
                    scannerReading(
                        gameState.vaultSystems.radiationScanner,
                        location.radiation.displayName,
                        radiationTone(location.radiation)
                    ),
                    Modifier.weight(1f)
                )
                EnvBox(
                    "Water",
                    scannerReading(
                        gameState.vaultSystems.waterScanner,
                        location.water.displayName,
                        waterTone(location.water)
                    ),
                    Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                EnvBox(
                    "Food",
                    scannerReading(
                        gameState.vaultSystems.agriculturalScanner,
                        location.food.displayName,
                        foodTone(location.food)
                    ),
                    Modifier.weight(1f)
                )
                EnvBox(
                    "Shelter",
                    scannerReading(
                        gameState.vaultSystems.structureScanner,
                        location.shelter.displayName,
                        shelterTone(location.shelter)
                    ),
                    Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                EnvBox(
                    "Resources",
                    scannerReading(
                        gameState.vaultSystems.resourceScanner,
                        location.resources.displayName,
                        resourceTone(location.resources)
                    ),
                    Modifier.weight(1f)
                )
                EnvBox(
                    "Threat",
                    scannerReading(
                        gameState.vaultSystems.threatAssessment,
                        location.nativeHostility.displayName,
                        hostilityTone(location.nativeHostility)
                    ),
                    Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun EnvBox(label: String, reading: ScannerReading, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(SurfaceContainerHighest)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(label.uppercase(), color = TextPrimary.copy(0.7f), style = MaterialTheme.typography.labelSmall)
        Text(
            reading.value.uppercase(),
            color = reading.color,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            reading.supportingLine,
            color = TextMuted,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun ScannerDiagnosticsPanel(gameState: GameState) {
    TacticalPanel(
        modifier = Modifier.padding(horizontal = 16.dp),
        accent = Secondary
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                "SCANNER ARRAY",
                color = TextPrimary.copy(0.7f),
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 2.sp
            )

            SignalBar("Radiation Scanner", gameState.vaultSystems.radiationScanner)
            SignalBar("Water Scanner", gameState.vaultSystems.waterScanner)
            SignalBar("Agricultural Scanner", gameState.vaultSystems.agriculturalScanner)
            SignalBar("Structure Scanner", gameState.vaultSystems.structureScanner)
            SignalBar("Resource Scanner", gameState.vaultSystems.resourceScanner)
            SignalBar("Threat Assessment", gameState.vaultSystems.threatAssessment)
        }
    }
}

@Composable
private fun SignalBar(label: String, value: Int) {
    val tone = healthColor(value)

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = TextPrimary.copy(0.75f), style = MaterialTheme.typography.bodySmall)
            Text("$value%", color = tone, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(SurfaceContainerHighest)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((value / 100f).coerceIn(0f, 1f))
                    .fillMaxSize()
                    .background(tone)
            )
        }
    }
}

@Composable
private fun SurfaceScanTargetPanel(location: SurfaceLocation, probeRevealed: Boolean) {
    var expanded by rememberSaveable(location.name) { mutableStateOf(false) }
    val locationImage = getLocationImage(location.type)
    val titleLines = splitName(location.name)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(SurfaceContainerHighest)
    ) {
        Image(
            painter = painterResource(id = locationImage),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.35f),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, BackgroundColor.copy(alpha = 0.55f), BackgroundColor)
                    )
                )
        )

        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "TARGET_LOCKED",
                        color = BackgroundColor,
                        modifier = Modifier
                            .background(Primary)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )

                    titleLines.forEach { line ->
                        Text(
                            text = line.uppercase(),
                            color = Primary,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-2).sp
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(location.type.displayName().uppercase(), color = Primary.copy(0.72f), style = MaterialTheme.typography.labelSmall)
                    Text(location.travelProfile.routeName.uppercase(), color = Primary.copy(0.58f), style = MaterialTheme.typography.labelSmall)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundColor.copy(0.82f))
                    .drawBehind { drawRect(PrimaryContainer, size = size.copy(width = 2.dp.toPx())) }
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "\"${location.shortDescription}\"",
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 22.sp
                    )

                    Text(
                        text = location.longDescription,
                        color = TextPrimary.copy(0.82f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = if (expanded) Int.MAX_VALUE else 5,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = if (expanded) "COLLAPSE FIELD REPORT" else "EXPAND FIELD REPORT",
                        color = SignalBlue,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { expanded = !expanded }
                    )
                }
            }

            if (probeRevealed) {
                ProbeTelemetryGrid(location)
            } else {
                LockedProbeBanner()
            }
        }

        CornerAccent(Modifier.align(Alignment.TopEnd), top = true)
        CornerAccent(Modifier.align(Alignment.BottomStart), top = false)
    }
}

@Composable
private fun ProbeTelemetryGrid(location: SurfaceLocation) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "DEEP FIELD TELEMETRY",
            color = Secondary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ProbeBox(
                "ANOMALY",
                location.anomaly?.displayName ?: "No dominant anomaly",
                location.anomaly?.let(::anomalyTone) ?: TextPrimary,
                Modifier.weight(1f)
            )
            ProbeBox(
                "RECOMMENDATION",
                location.probeData?.recommendation ?: "Probe uplink incomplete.",
                Secondary,
                Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ProbeBox(
                "RESOURCES",
                location.probeData?.hiddenResources ?: "No deeper resource packet received.",
                Primary,
                Modifier.weight(1f)
            )
            ProbeBox(
                "STRUCTURE",
                location.probeData?.structuralIntegrity ?: "No structural packet received.",
                SignalBlue,
                Modifier.weight(1f)
            )
        }

        ProbeBox(
            "SOIL QUALITY",
            location.probeData?.soilQuality ?: "No soil packet received.",
            PrimaryFixedDim,
            Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ProbeBox(
    label: String,
    value: String,
    tone: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(BackgroundColor.copy(alpha = 0.84f))
            .border(1.dp, OutlineVariant.copy(alpha = 0.28f))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(label, color = TextPrimary.copy(0.62f), style = MaterialTheme.typography.labelSmall)
        Text(value, color = tone, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun LockedProbeBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundColor.copy(alpha = 0.82f))
            .border(1.dp, OutlineVariant.copy(alpha = 0.28f))
            .padding(14.dp)
    ) {
        Text(
            text = "PROBE LINK READY: deploy a probe to reveal anomaly classification plus structural, resource, soil, and settlement viability telemetry.",
            color = TextPrimary.copy(alpha = 0.82f),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun CornerAccent(modifier: Modifier, top: Boolean) {
    Box(
        modifier = modifier
            .size(32.dp)
            .drawBehind {
                if (top) {
                    drawLine(Primary.copy(0.3f), Offset(0f, 0f), Offset(size.width, 0f), 2.dp.toPx())
                    drawLine(Primary.copy(0.3f), Offset(size.width, 0f), Offset(size.width, size.height), 2.dp.toPx())
                } else {
                    drawLine(Primary.copy(0.3f), Offset(0f, size.height), Offset(0f, 0f), 2.dp.toPx())
                    drawLine(Primary.copy(0.3f), Offset(0f, size.height), Offset(size.width, size.height), 2.dp.toPx())
                }
            }
    )
}

@Composable
private fun TransitSectionPanel(location: SurfaceLocation) {
    TacticalPanel(
        modifier = Modifier.padding(horizontal = 16.dp),
        accent = Primary
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                "TRANSIT SECTION",
                color = TextPrimary.copy(0.7f),
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 2.sp
            )

            Column {
                Text("ACTIVE ROUTE", color = TextPrimary.copy(0.6f), style = MaterialTheme.typography.labelSmall)
                Text(location.travelProfile.routeName, color = Primary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(location.travelProfile.durationText, color = TextMuted, style = MaterialTheme.typography.bodySmall)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TransitMetric(
                    "RISK",
                    location.travelProfile.riskLevel.displayName,
                    travelRiskTone(location.travelProfile.riskLevel),
                    Modifier.weight(1f)
                )
                TransitMetric(
                    "ATTRITION",
                    "${location.travelProfile.minLossPercent}-${location.travelProfile.maxLossPercent}%",
                    travelRiskTone(location.travelProfile.riskLevel),
                    Modifier.weight(1f)
                )
            }

            TransitMetric(
                "SCORE PENALTY",
                "-${location.travelProfile.scorePenalty}",
                Primary,
                Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Primary.copy(0.05f))
                    .drawBehind { drawRect(Primary, size = size.copy(width = 2.dp.value * density)) }
                    .padding(12.dp)
            ) {
                Text(
                    location.travelProfile.riskSummary,
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun TransitMetric(
    label: String,
    value: String,
    tone: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(SurfaceContainerLow)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label, color = TextPrimary.copy(0.6f), style = MaterialTheme.typography.labelSmall)
        Text(value, color = tone, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun SystemAlertsPanel(
    gameState: GameState,
    location: SurfaceLocation
) {
    val alerts = buildAlerts(gameState, location)

    TacticalPanel(
        modifier = Modifier.padding(horizontal = 16.dp),
        accent = Error
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                "SYSTEM ALERTS",
                color = TextPrimary.copy(0.7f),
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 2.sp
            )

            alerts.forEach { alert ->
                AlertItem(alert.title, alert.description, alert.color)
            }
        }
    }
}

@Composable
private fun AlertItem(title: String, description: String, color: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(7.dp)
                .background(color)
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title.uppercase(), color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text(description, color = TextPrimary.copy(0.78f), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun SurfaceBottomCommandTray(
    probesAvailable: Boolean,
    probeRevealed: Boolean,
    location: SurfaceLocation,
    onSearch: () -> Unit,
    onProbe: () -> Unit,
    onOpenVault: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .background(BackgroundColor)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CommandTile(
            title = "SEARCH",
            subtitle = "Advance year",
            icon = Icons.Filled.GridView,
            tone = Primary,
            onClick = onSearch,
            modifier = Modifier.weight(1f)
        )

        CommandTile(
            title = if (probeRevealed) "PROBE USED" else "PROBE",
            subtitle = if (probeRevealed) "Deep scan captured" else "Reveal anomaly",
            icon = Icons.Filled.Radar,
            tone = Secondary,
            enabled = probesAvailable && !probeRevealed,
            onClick = onProbe,
            modifier = Modifier.weight(1f)
        )

        OpenVaultTile(
            location = location,
            onClick = onOpenVault,
            modifier = Modifier.weight(1.7f)
        )
    }
}

@Composable
private fun CommandTile(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tone: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val contentTone = if (enabled) tone else TextMuted

    Column(
        modifier = modifier
            .background(SurfaceContainerHigh)
            .clickable(enabled = enabled) { onClick() }
            .drawBehind {
                drawRect(
                    SurfaceContainerHighest,
                    topLeft = Offset(0f, size.height - 4.dp.toPx()),
                    size = size.copy(height = 4.dp.toPx())
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = contentTone)
        Text(
            title,
            color = contentTone,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            subtitle,
            color = TextPrimary.copy(alpha = 0.55f),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun OpenVaultTile(
    location: SurfaceLocation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(ErrorContainer)
            .clickable { onClick() }
            .drawBehind {
                drawRect(
                    Color(0xFF690005),
                    topLeft = Offset(0f, size.height - 4.dp.toPx()),
                    size = size.copy(height = 4.dp.toPx())
                )
            }
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "OPEN VAULT",
                color = OnErrorContainer,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )
            Text(
                "Commit to ${location.name}",
                color = OnErrorContainer.copy(0.7f),
                style = MaterialTheme.typography.labelSmall
            )
        }

        Icon(
            Icons.Filled.PowerSettingsNew,
            contentDescription = null,
            tint = severityColor(location),
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
private fun TacticalPanel(
    modifier: Modifier = Modifier,
    accent: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceContainerLow)
            .border(1.dp, OutlineVariant.copy(alpha = 0.2f))
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .align(Alignment.TopStart)
                .background(accent.copy(alpha = 0.7f))
        )

        Column(
            modifier = Modifier.padding(top = 6.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Composable
private fun GlowingDot(color: Color) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .background(color)
            .graphicsLayer {
                shadowElevation = 8.dp.toPx()
                ambientShadowColor = color
                spotShadowColor = color
            }
    )
}

private data class ScannerReading(
    val value: String,
    val supportingLine: String,
    val color: Color
)

private data class AlertRow(
    val title: String,
    val description: String,
    val color: Color
)

private fun scannerReading(
    systemValue: Int,
    displayValue: String,
    tone: Color
): ScannerReading {
    return if (systemValue <= 15) {
        ScannerReading(
            value = "Unknown",
            supportingLine = "Scanner offline at $systemValue%",
            color = TextMuted
        )
    } else {
        ScannerReading(
            value = displayValue,
            supportingLine = "Integrity $systemValue%",
            color = tone
        )
    }
}

private fun buildOverviewLine(location: SurfaceLocation): String {
    val hazard = when {
        location.radiation == RadiationLevel.LETHAL -> "lethal radiation saturation"
        location.water == WaterAvailability.NONE -> "no dependable water source"
        location.nativeHostility == Hostility.WARLORD -> "heavy hostile presence"
        else -> "mixed but survivable conditions"
    }
    return "${location.name} is the current target. Transit risk is ${location.travelProfile.riskLevel.displayName.lowercase()} with projected losses of ${location.travelProfile.minLossPercent}-${location.travelProfile.maxLossPercent}% before settlement. Primary concern: $hazard."
}

private fun buildAlerts(gameState: GameState, location: SurfaceLocation): List<AlertRow> {
    val alerts = mutableListOf<AlertRow>()
    val systems = gameState.vaultSystems

    fun addIfLow(name: String, value: Int, failureText: String) {
        if (value <= 15) {
            alerts += AlertRow(
                title = "$name failure threshold reached",
                description = failureText,
                color = Error
            )
        } else if (value <= 35) {
            alerts += AlertRow(
                title = "$name unstable",
                description = "$name is down to $value% integrity. Expect worsening read fidelity.",
                color = PrimaryFixedDim
            )
        }
    }

    addIfLow("Radiation scanner", systems.radiationScanner, "Radiation telemetry is now blind on surface targets.")
    addIfLow("Water scanner", systems.waterScanner, "Water availability reports are no longer trustworthy.")
    addIfLow("Agricultural scanner", systems.agriculturalScanner, "Food viability estimates are now obscured.")
    addIfLow("Structure scanner", systems.structureScanner, "Shelter assessments are no longer dependable.")
    addIfLow("Resource scanner", systems.resourceScanner, "Resource density estimates are now obscured.")
    addIfLow("Threat assessment", systems.threatAssessment, "Hostility telemetry is degraded or lost.")

    if (gameState.surfaceProbes == 0) {
        alerts += AlertRow(
            title = "Probe inventory exhausted",
            description = "No additional deep scans can be performed on future locations this run.",
            color = PrimaryFixedDim
        )
    }

    if (location.travelProfile.riskLevel == TravelRisk.EXTREME) {
        alerts += AlertRow(
            title = "Extreme transit exposure",
            description = "This route projects up to ${location.travelProfile.maxLossPercent}% convoy losses before settlement begins.",
            color = Error
        )
    }

    if (alerts.isEmpty()) {
        alerts += AlertRow(
            title = "No critical alerts",
            description = "All monitored scan systems remain above failure threshold.",
            color = Secondary
        )
    }

    return alerts
}

private fun vaultStatusLine(coreAverage: Int, criticalCount: Int): String = when {
    criticalCount > 0 -> "$criticalCount critical systems need immediate command attention before prolonged searching."
    coreAverage >= 85 -> "Vault stability remains high. Search and deployment operations are fully supported."
    coreAverage >= 60 -> "Vault resilience is slipping, but surface operations remain viable."
    else -> "Vault endurance is weakening. Every additional search cycle carries rising risk."
}

private fun splitName(name: String): List<String> {
    val parts = name.split(" ")
    if (parts.size <= 1) return listOf(name)
    val midpoint = (parts.size + 1) / 2
    return listOf(
        parts.take(midpoint).joinToString(" "),
        parts.drop(midpoint).joinToString(" ")
    ).filter { it.isNotBlank() }
}

private fun getLocationImage(type: LocationType): Int = when (type) {
    LocationType.RUINED_CITY, LocationType.COASTAL_TOWN, LocationType.ABANDONED_SUBWAY -> R.drawable.loc_urban
    LocationType.MILITARY_BASE, LocationType.RESEARCH_FACILITY, LocationType.SCRAP_HEAP -> R.drawable.loc_tech
    else -> R.drawable.loc_urban
}

private fun healthColor(value: Int): Color = when {
    value >= 70 -> Secondary
    value >= 40 -> Primary
    value >= 15 -> PrimaryFixedDim
    else -> Error
}

private fun radiationTone(value: RadiationLevel): Color = when (value) {
    RadiationLevel.NONE -> Secondary
    RadiationLevel.LOW -> SignalBlue
    RadiationLevel.MODERATE -> Primary
    RadiationLevel.HIGH -> PrimaryFixedDim
    RadiationLevel.LETHAL -> Error
}

private fun waterTone(value: WaterAvailability): Color = when (value) {
    WaterAvailability.ABUNDANT -> Secondary
    WaterAvailability.SCARCE -> Primary
    WaterAvailability.NONE -> Error
}

private fun foodTone(value: FoodPotential): Color = when (value) {
    FoodPotential.FERTILE -> Secondary
    FoodPotential.MARGINAL -> Primary
    FoodPotential.BARREN -> Error
}

private fun shelterTone(value: ShelterQuality): Color = when (value) {
    ShelterQuality.EXCELLENT -> Secondary
    ShelterQuality.GOOD -> SignalBlue
    ShelterQuality.POOR -> PrimaryFixedDim
    ShelterQuality.NONE -> Error
}

private fun resourceTone(value: ResourceRichness): Color = when (value) {
    ResourceRichness.RICH -> Secondary
    ResourceRichness.MODERATE -> SignalBlue
    ResourceRichness.POOR -> Primary
}

private fun hostilityTone(value: Hostility): Color = when (value) {
    Hostility.NONE -> Secondary
    Hostility.BANDITS -> Primary
    Hostility.WASTELAND_CULT -> PrimaryFixedDim
    Hostility.WARLORD -> Error
}

private fun travelRiskTone(value: TravelRisk): Color = when (value) {
    TravelRisk.LOW -> Secondary
    TravelRisk.MODERATE -> Primary
    TravelRisk.HIGH -> PrimaryFixedDim
    TravelRisk.EXTREME -> Error
}

private fun anomalyTone(value: SurfaceAnomaly): Color = when (value) {
    SurfaceAnomaly.DISEASE_OUTBREAK_SITE -> Error
    SurfaceAnomaly.WEAPONS_CACHE -> PrimaryFixedDim
    SurfaceAnomaly.ALIEN_SIGNAL -> SignalBlue
    else -> Secondary
}

private fun openingRiskLabel(location: SurfaceLocation): String = when {
    location.radiation == RadiationLevel.LETHAL || location.travelProfile.riskLevel == TravelRisk.EXTREME -> "OPENING RISK: EXTREME"
    location.nativeHostility == Hostility.WARLORD || location.radiation == RadiationLevel.HIGH -> "OPENING RISK: HIGH"
    else -> "OPENING RISK: EVALUATED"
}

private fun severityColor(location: SurfaceLocation): Color = when {
    location.radiation == RadiationLevel.LETHAL || location.travelProfile.riskLevel == TravelRisk.EXTREME -> Error
    location.nativeHostility == Hostility.WARLORD || location.radiation == RadiationLevel.HIGH -> PrimaryFixedDim
    else -> Primary
}

private fun LocationType.displayName(): String {
    return name
        .lowercase()
        .split("_")
        .joinToString(" ") { part -> part.replaceFirstChar { it.titlecase() } }
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
                color = Color.Black.copy(alpha = 0.1f),
                topLeft = Offset(0f, y + horizontalSpacing / 2),
                size = Size(width, horizontalSpacing / 2)
            )
            y += horizontalSpacing
        }

        val verticalSpacing = 3.dp.toPx()
        val columnWidth = verticalSpacing / 3f
        var x = 0f
        while (x < width) {
            drawRect(
                color = Color(255, 0, 0, (255 * 0.03).toInt()),
                topLeft = Offset(x, 0f),
                size = Size(columnWidth, height)
            )
            drawRect(
                color = Color(0, 255, 0, (255 * 0.01).toInt()),
                topLeft = Offset(x + columnWidth, 0f),
                size = Size(columnWidth, height)
            )
            drawRect(
                color = Color(0, 0, 255, (255 * 0.03).toInt()),
                topLeft = Offset(x + 2 * columnWidth, 0f),
                size = Size(columnWidth, height)
            )
            x += verticalSpacing
        }
    }
}
