package com.ivarna.wardenprotocol.ui.screen

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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivarna.wardenprotocol.R
import com.ivarna.wardenprotocol.ui.*
import com.ivarna.wardenprotocol.data.model.FoodPotential
import com.ivarna.wardenprotocol.data.model.GameState
import com.ivarna.wardenprotocol.data.model.Hostility
import com.ivarna.wardenprotocol.data.model.LocationType
import com.ivarna.wardenprotocol.data.model.RadiationLevel
import com.ivarna.wardenprotocol.data.model.ResourceRichness
import com.ivarna.wardenprotocol.data.model.ShelterQuality
import com.ivarna.wardenprotocol.data.model.SurfaceAnomaly
import com.ivarna.wardenprotocol.data.model.SurfaceLocation
import com.ivarna.wardenprotocol.data.model.TravelRisk
import com.ivarna.wardenprotocol.data.model.WaterAvailability
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
            Column {
                Text(
                    stringResource(R.string.top_bar_default),
                    style = MaterialTheme.typography.titleMedium,
                    color = Primary,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    "${locationTypeDisplayName(location.type)} / YEAR ${gameState.yearsSinceWar}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary.copy(alpha = 0.55f),
                    fontWeight = FontWeight.Bold
                )
            }
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
private fun VaultStatusPanel(gameState: GameState) {
    val coreAverage = listOf(
                gameState.vaultSystems.powerGrid,
                gameState.vaultSystems.foodStores,
                gameState.vaultSystems.medicalBay,
                gameState.vaultSystems.securitySystem,
                gameState.vaultSystems.constructionGear,
                gameState.vaultSystems.atmosphereScrubbers
            ).average().toFloat()

    val radiationMsv = (0.01f + (100 - gameState.vaultSystems.radiationScanner) * 0.04f)
    val foodDays = (gameState.vaultSystems.foodStores * 4.2f).toInt()

    TacticalPanel(
        modifier = Modifier.padding(horizontal = 16.dp),
        accent = Primary
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Status Indicator
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(Secondary)
                        .graphicsLayer { shadowElevation = 8.dp.toPx(); ambientShadowColor = Secondary; spotShadowColor = Secondary }
                )
                Text(
                    stringResource(R.string.surface_vault_01_status),
                    color = Primary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatusItem(stringResource(R.string.surface_metric_health), "${String.format("%.1f", coreAverage)}%", Secondary)
                StatusItem(stringResource(R.string.surface_metric_radiation), "${String.format("%.2f", radiationMsv)} mSv", Error.copy(alpha = 0.8f))
                StatusItem(stringResource(R.string.surface_metric_food_reserves), "$foodDays DAYS", Primary)
            }

            // Progress Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(SurfaceContainerHighest)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(coreAverage / 100f)
                            .fillMaxSize()
                            .background(Primary)
                    )
                }
            }
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
                stringResource(R.string.surface_bunker_telemetry),
                color = TextPrimary.copy(0.7f),
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 2.sp
            )

            TelemetryRow(stringResource(R.string.surface_metric_survivors), gameState.survivors.toString())
            TelemetryRow(stringResource(R.string.surface_metric_years_sealed), gameState.yearsSinceWar.toString().padStart(3, '0'))
            TelemetryRow(stringResource(R.string.surface_metric_sites_scanned), gameState.surfaceLocationsScanned.toString().padStart(2, '0'))
            TelemetryRow(stringResource(R.string.surface_metric_active_probes), gameState.surfaceProbes.toString().padStart(2, '0'))

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
                stringResource(R.string.surface_env_telemetry),
                color = TextPrimary.copy(0.7f),
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 2.sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                EnvBox(
                    stringResource(R.string.surface_env_radiation),
                    scannerReading(
                        gameState.vaultSystems.radiationScanner,
                        radiationDisplayName(location.radiation),
                        radiationTone(location.radiation)
                    ),
                    Modifier.weight(1f)
                )
                EnvBox(
                    stringResource(R.string.surface_env_water),
                    scannerReading(
                        gameState.vaultSystems.waterScanner,
                        waterDisplayName(location.water),
                        waterTone(location.water)
                    ),
                    Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                EnvBox(
                    stringResource(R.string.surface_env_food),
                    scannerReading(
                        gameState.vaultSystems.agriculturalScanner,
                        foodDisplayName(location.food),
                        foodTone(location.food)
                    ),
                    Modifier.weight(1f)
                )
                EnvBox(
                    stringResource(R.string.surface_env_shelter),
                    scannerReading(
                        gameState.vaultSystems.structureScanner,
                        shelterDisplayName(location.shelter),
                        shelterTone(location.shelter)
                    ),
                    Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                EnvBox(
                    stringResource(R.string.surface_env_resources),
                    scannerReading(
                        gameState.vaultSystems.resourceScanner,
                        resourceDisplayName(location.resources),
                        resourceTone(location.resources)
                    ),
                    Modifier.weight(1f)
                )
                EnvBox(
                    stringResource(R.string.surface_env_threat),
                    scannerReading(
                        gameState.vaultSystems.threatAssessment,
                        hostilityDisplayName(location.nativeHostility),
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
                stringResource(R.string.surface_scanner_array),
                color = TextPrimary.copy(0.7f),
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 2.sp
            )

            SignalBar(stringResource(R.string.surface_scanner_radiation), gameState.vaultSystems.radiationScanner)
            SignalBar(stringResource(R.string.surface_scanner_water), gameState.vaultSystems.waterScanner)
            SignalBar(stringResource(R.string.surface_scanner_agriculture), gameState.vaultSystems.agriculturalScanner)
            SignalBar(stringResource(R.string.surface_scanner_structure), gameState.vaultSystems.structureScanner)
            SignalBar(stringResource(R.string.surface_scanner_resource), gameState.vaultSystems.resourceScanner)
            SignalBar(stringResource(R.string.surface_scanner_threat), gameState.vaultSystems.threatAssessment)
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

        Column(
            modifier = Modifier
                .padding(24.dp)
                .tacticalGrid(alpha = 0.12f),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        stringResource(R.string.surface_target_locked),
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
                    Text(locationTypeDisplayName(location.type).uppercase(), color = Primary.copy(0.72f), style = MaterialTheme.typography.labelSmall)
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
                        text = if (expanded) stringResource(R.string.surface_collapse_report) else stringResource(R.string.surface_expand_report),
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
            text = stringResource(R.string.surface_deep_field_telemetry),
            color = Secondary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ProbeBox(
                stringResource(R.string.surface_probe_anomaly),
                location.anomaly?.let { anomalyDisplayName(it) } ?: stringResource(R.string.surface_probe_anomaly_none),
                location.anomaly?.let(::anomalyTone) ?: TextPrimary,
                Modifier.weight(1f)
            )
            ProbeBox(
                stringResource(R.string.surface_probe_recommendation),
                location.probeData?.recommendation ?: stringResource(R.string.surface_probe_recommendation_none),
                Secondary,
                Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ProbeBox(
                stringResource(R.string.surface_probe_resources),
                location.probeData?.hiddenResources ?: stringResource(R.string.surface_probe_resources_none),
                Primary,
                Modifier.weight(1f)
            )
            ProbeBox(
                stringResource(R.string.surface_probe_structure),
                location.probeData?.structuralIntegrity ?: stringResource(R.string.surface_probe_structure_none),
                SignalBlue,
                Modifier.weight(1f)
            )
        }

        ProbeBox(
            stringResource(R.string.surface_probe_soil),
            location.probeData?.soilQuality ?: stringResource(R.string.surface_probe_soil_none),
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
            text = stringResource(R.string.surface_probe_link_ready),
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
                stringResource(R.string.surface_transit_section),
                color = TextPrimary.copy(0.7f),
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 2.sp
            )

            Column {
                Text(stringResource(R.string.surface_active_route), color = TextPrimary.copy(0.6f), style = MaterialTheme.typography.labelSmall)
                Text(location.travelProfile.routeName, color = Primary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(location.travelProfile.durationText, color = TextMuted, style = MaterialTheme.typography.bodySmall)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TransitMetric(
                    stringResource(R.string.surface_metric_risk),
                    travelRiskDisplayName(location.travelProfile.riskLevel),
                    travelRiskTone(location.travelProfile.riskLevel),
                    Modifier.weight(1f)
                )
                TransitMetric(
                    stringResource(R.string.surface_metric_attrition),
                    "${location.travelProfile.minLossPercent}-${location.travelProfile.maxLossPercent}%",
                    travelRiskTone(location.travelProfile.riskLevel),
                    Modifier.weight(1f)
                )
            }

            TransitMetric(
                stringResource(R.string.surface_metric_score_penalty),
                stringResource(R.string.surface_score_penalty_value, location.travelProfile.scorePenalty),
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
                stringResource(R.string.surface_system_alerts),
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
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val totalTrayHeight = 112.dp + bottomPadding

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(totalTrayHeight)
            .background(BackgroundColor)
            .tacticalGrid(alpha = 0.12f)
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp, bottom = 12.dp + bottomPadding),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CrtCommandButton(
            title = stringResource(R.string.surface_action_search),
            icon = Icons.Filled.GridView,
            tone = Primary,
            onClick = onSearch,
            modifier = Modifier.width(90.dp)
        )

        CrtCommandButton(
            title = stringResource(R.string.surface_action_probe),
            icon = Icons.Filled.Radar,
            tone = Secondary,
            enabled = probesAvailable && !probeRevealed,
            onClick = onProbe,
            modifier = Modifier.width(90.dp)
        )

        CrtVaultButton(
            title = stringResource(R.string.surface_action_open_vault),
            subtitle = stringResource(R.string.surface_action_open_vault_subtitle),
            icon = Icons.Filled.PowerSettingsNew,
            location = location,
            onClick = onOpenVault,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CrtCommandButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tone: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val contentTone = if (enabled) tone else TextMuted.copy(alpha = 0.4f)
    val backgroundTone = if (enabled) SurfaceContainerHigh else SurfaceContainerLow

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundTone)
            .border(1.dp, OutlineVariant.copy(alpha = 0.35f))
            .clickable(enabled = enabled) { onClick() }
            .tacticalGrid(alpha = 0.18f, horizontalSpacing = 3.dp, verticalSpacing = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = contentTone,
                modifier = Modifier.size(28.dp)
            )
            Text(
                title,
                color = contentTone,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun CrtVaultButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    location: SurfaceLocation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vaultGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF800000), // Brighter red top
            Color(0xFF500000), // Darker red middle
            Color(0xFF800000)  // Brighter red bottom
        )
    )
    
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(vaultGradient)
            .border(1.dp, Color(0xFFC00000).copy(alpha = 0.3f))
            .clickable { onClick() }
            .tacticalGrid(alpha = 0.20f, horizontalSpacing = 2.dp, verticalSpacing = 3.dp)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                title.uppercase(),
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )
            Text(
                subtitle.uppercase(),
                color = Color.White.copy(alpha = 0.55f),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        Icon(
            icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(36.dp)
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
            .tacticalGrid(alpha = 0.08f, horizontalSpacing = 4.dp, verticalSpacing = 6.dp)
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

@Composable
private fun scannerReading(
    systemValue: Int,
    displayValue: String,
    tone: Color
): ScannerReading {
    return if (systemValue <= 15) {
        ScannerReading(
            value = stringResource(R.string.value_unknown),
            supportingLine = stringResource(R.string.surface_scanner_offline, systemValue),
            color = TextMuted
        )
    } else {
        ScannerReading(
            value = displayValue,
            supportingLine = stringResource(R.string.surface_scanner_integrity, systemValue),
            color = tone
        )
    }
}

@Composable
private fun buildOverviewLine(location: SurfaceLocation): String {
    val hazard = when {
        location.radiation == RadiationLevel.LETHAL -> stringResource(R.string.surface_overview_hazard_lethal)
        location.water == WaterAvailability.NONE -> stringResource(R.string.surface_overview_hazard_no_water)
        location.nativeHostility == Hostility.WARLORD -> stringResource(R.string.surface_overview_hazard_warlord)
        else -> stringResource(R.string.surface_overview_hazard_mixed)
    }
    return stringResource(R.string.surface_overview_line, location.name, travelRiskDisplayName(location.travelProfile.riskLevel).lowercase(), location.travelProfile.minLossPercent, location.travelProfile.maxLossPercent, hazard)
}

@Composable
private fun buildAlerts(gameState: GameState, location: SurfaceLocation): List<AlertRow> {
    val alerts = mutableListOf<AlertRow>()
    val systems = gameState.vaultSystems

    fun addIfLow(nameLabel: String, value: Int, failureDesc: String) {
        if (value <= 15) {
            alerts += AlertRow(
                title = "$nameLabel failure threshold reached",
                description = failureDesc,
                color = Error
            )
        } else if (value <= 35) {
            alerts += AlertRow(
                title = "$nameLabel unstable",
                description = "$nameLabel is down to $value% integrity. Expect worsening read fidelity.",
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
            title = stringResource(R.string.surface_alert_probe_exhausted),
            description = stringResource(R.string.surface_alert_probe_exhausted_desc),
            color = PrimaryFixedDim
        )
    }

    if (location.travelProfile.riskLevel == TravelRisk.EXTREME) {
        alerts += AlertRow(
            title = stringResource(R.string.surface_alert_extreme_transit),
            description = stringResource(R.string.surface_alert_extreme_transit_desc, location.travelProfile.maxLossPercent),
            color = Error
        )
    }

    if (alerts.isEmpty()) {
        alerts += AlertRow(
            title = stringResource(R.string.surface_alert_no_critical),
            description = stringResource(R.string.surface_alert_no_critical_desc),
            color = Secondary
        )
    }

    return alerts
}

@Composable
private fun vaultStatusLine(coreAverage: Int, criticalCount: Int): String = when {
    criticalCount > 0 -> stringResource(R.string.surface_vault_status_critical, criticalCount)
    coreAverage >= 85 -> stringResource(R.string.surface_vault_status_high)
    coreAverage >= 60 -> stringResource(R.string.surface_vault_status_mid)
    else -> stringResource(R.string.surface_vault_status_low)
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
    LocationType.RUINED_CITY -> R.drawable.loc_ruined_city
    LocationType.FOREST -> R.drawable.loc_forest
    LocationType.MILITARY_BASE -> R.drawable.loc_military_base
    LocationType.FARMLAND -> R.drawable.loc_farmland
    LocationType.UNDERGROUND_RIVER -> R.drawable.loc_underground_river
    LocationType.MOUNTAIN_PASS -> R.drawable.loc_mountain_pass
    LocationType.COASTAL_TOWN -> R.drawable.loc_coastal_town
    LocationType.RESEARCH_FACILITY -> R.drawable.loc_research_facility
    LocationType.RADIOACTIVE_SWAMP -> R.drawable.loc_radioactive_swamp
    LocationType.MEGACRATER -> R.drawable.loc_megacrater
    LocationType.PLAGUE_ZONE -> R.drawable.loc_plague_zone
    LocationType.SCRAP_HEAP -> R.drawable.loc_scrap_heap
    LocationType.ABANDONED_SUBWAY -> R.drawable.loc_abandoned_subway
    LocationType.FUNGAL_WASTES -> R.drawable.loc_fungal_wastes
    LocationType.GLASS_DESERT -> R.drawable.loc_glass_desert
    LocationType.CULT_TERRITORY -> R.drawable.loc_cult_territory
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

@Composable
private fun openingRiskLabel(location: SurfaceLocation): String = when {
    location.radiation == RadiationLevel.LETHAL || location.travelProfile.riskLevel == TravelRisk.EXTREME -> stringResource(R.string.surface_opening_risk_extreme)
    location.nativeHostility == Hostility.WARLORD || location.radiation == RadiationLevel.HIGH -> stringResource(R.string.surface_opening_risk_high)
    else -> stringResource(R.string.surface_opening_risk_evaluated)
}

private fun severityColor(location: SurfaceLocation): Color = when {
    location.radiation == RadiationLevel.LETHAL || location.travelProfile.riskLevel == TravelRisk.EXTREME -> Error
    location.nativeHostility == Hostility.WARLORD || location.radiation == RadiationLevel.HIGH -> PrimaryFixedDim
    else -> Primary
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
