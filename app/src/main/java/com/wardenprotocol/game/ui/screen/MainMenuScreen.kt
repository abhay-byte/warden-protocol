package com.wardenprotocol.game.ui.screen

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wardenprotocol.game.data.model.RunRecord
import com.wardenprotocol.game.ui.component.WardenBackdrop
import com.wardenprotocol.game.ui.component.WardenBottomNav
import com.wardenprotocol.game.ui.component.WardenTab
import com.wardenprotocol.game.ui.component.WardenTopBar
import com.wardenprotocol.game.ui.component.animatedEntranceModifier
import kotlin.math.roundToInt

private val HomeBackground = Color(0xFF121414)
private val HomePanelLow = Color(0xFF1A1C1C)
private val HomePanel = Color(0xFF1E2020)
private val HomePanelHigh = Color(0xFF282A2A)
private val HomeAmber = Color(0xFFFFD597)
private val HomeAmberStrong = Color(0xFFFFB000)
private val HomeGreen = Color(0xFF9EFF8B)
private val HomeError = Color(0xFFFFB4AB)
private val HomeOutline = Color(0xFF9F8E78)
private val HomeOnSurface = Color(0xFFE2E2E2)
private val HomeOnSurfaceMuted = Color(0xFFB9B19E)

@Composable
fun MainMenuScreen(
    highScore: Int,
    runCount: Int,
    leaderboardPreview: List<RunRecord>,
    lastRun: RunRecord?,
    onNewGame: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val successfulRuns = leaderboardPreview.count { it.score > 0 }.coerceAtLeast(if (highScore > 0) 1 else 0)
    val casualtyRate = if (runCount == 0) {
        0f
    } else {
        val previewRuns = buildList {
            lastRun?.let(::add)
            addAll(leaderboardPreview.filterNot { top -> top.id == lastRun?.id })
        }
        if (previewRuns.isEmpty()) 0f
        else {
            val averageSurvivors = previewRuns.map { it.survivors.coerceIn(0, 1000) }.average()
            (((1000.0 - averageSurvivors) / 1000.0) * 100.0).toFloat()
        }
    }

    WardenBackdrop(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(HomeBackground)
        ) {
            HomeScanlineOverlay()

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val isWide = maxWidth >= 900.dp
                if (isWide) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        WardenTopBar()
                        DesktopHomeContent(
                            highScore = highScore,
                            successfulRuns = successfulRuns,
                            casualtyRate = casualtyRate,
                            runCount = runCount,
                            leaderboardPreview = leaderboardPreview,
                            lastRun = lastRun,
                            onNewGame = onNewGame,
                            onOpenLeaderboard = onOpenLeaderboard,
                            onOpenHistory = onOpenHistory
                        )
                    }
                } else {
                    MobileHomeContent(
                        highScore = highScore,
                        successfulRuns = successfulRuns,
                        casualtyRate = casualtyRate,
                        runCount = runCount,
                        leaderboardPreview = leaderboardPreview,
                        lastRun = lastRun,
                        onNewGame = onNewGame,
                        onOpenLeaderboard = onOpenLeaderboard,
                        onOpenHistory = onOpenHistory,
                        onOpenSettings = onOpenSettings
                    )
                }
            }
        }
    }
}

@Composable
private fun DesktopHomeContent(
    highScore: Int,
    successfulRuns: Int,
    casualtyRate: Float,
    runCount: Int,
    leaderboardPreview: List<RunRecord>,
    lastRun: RunRecord?,
    onNewGame: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    onOpenHistory: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(
            modifier = Modifier.weight(0.88f),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ArchiveStatsPanel(
                highScore = highScore,
                successfulRuns = successfulRuns,
                casualtyRate = casualtyRate
            )
            SideCommandButton(
                title = "Run History",
                icon = Icons.Filled.History,
                onClick = onOpenHistory
            )
            SideCommandButton(
                title = "Global Leaderboard",
                icon = Icons.Filled.EmojiEvents,
                onClick = onOpenLeaderboard
            )
        }

        Column(
            modifier = Modifier.weight(1.4f),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            HomeHeroPanel(
                runCount = runCount,
                lastRun = lastRun,
                topRun = leaderboardPreview.firstOrNull()
            )
            StartMissionButton(onClick = onNewGame)
        }

        Column(
            modifier = Modifier.weight(0.9f),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            DiagnosticsPanel()
            BroadcastInterceptPanel()
            TacticalOverlayPanel()
        }
    }
}

@Composable
private fun MobileHomeContent(
    highScore: Int,
    successfulRuns: Int,
    casualtyRate: Float,
    runCount: Int,
    leaderboardPreview: List<RunRecord>,
    lastRun: RunRecord?,
    onNewGame: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = { WardenTopBar() },
        bottomBar = {
            WardenBottomNav(
                activeTab = WardenTab.COMMAND,
                showSurface = false,
                onTabClick = { tab ->
                    when(tab) {
                        WardenTab.ARCHIVE -> onOpenHistory()
                        WardenTab.SYSTEM -> onOpenSettings()
                        else -> {}
                    }
                }
            )
        }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues = padding)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            HomeHeroPanel(
                runCount = runCount,
                lastRun = lastRun,
                topRun = leaderboardPreview.firstOrNull()
            )
            StartMissionButton(onClick = onNewGame)
            ArchiveStatsPanel(
                highScore = highScore,
                successfulRuns = successfulRuns,
                casualtyRate = casualtyRate
            )
            SideCommandButton(
                title = "Run History",
                icon = Icons.Filled.History,
                onClick = onOpenHistory
            )
            SideCommandButton(
                title = "Global Leaderboard",
                icon = Icons.Filled.EmojiEvents,
                onClick = onOpenLeaderboard
            )
            DiagnosticsPanel()
            BroadcastInterceptPanel()
            TacticalOverlayPanel()
        }
    }
}

@Composable
private fun ArchiveStatsPanel(
    highScore: Int,
    successfulRuns: Int,
    casualtyRate: Float
) {
    IndustrialPanel(
        modifier = animatedEntranceModifier(Modifier.fillMaxWidth(), delayMillis = 20)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "VAULT ARCHIVE STATISTICS",
                style = MaterialTheme.typography.labelMedium,
                color = HomeAmber.copy(alpha = 0.72f)
            )
            LedIndicator(active = true)
        }

        Spacer(modifier = Modifier.height(12.dp))

        StatTile(
            label = "High Score Record",
            value = formatScore(highScore),
            accent = HomeAmberStrong
        )
        Spacer(modifier = Modifier.height(8.dp))
        StatTile(
            label = "Successful Extractions",
            value = successfulRuns.toString(),
            accent = HomeGreen
        )
        Spacer(modifier = Modifier.height(8.dp))
        StatTile(
            label = "Casualty Rate",
            value = "${casualtyRate.coerceAtLeast(0f).roundToInt().coerceAtMost(100)}%",
            accent = HomeError
        )
    }
}

@Composable
private fun StatTile(
    label: String,
    value: String,
    accent: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(HomePanelLow)
            .tacticalGrid(alpha = 0.07f)
            .padding(14.dp)
            .drawBehind {
                drawRect(
                    color = accent,
                    topLeft = Offset.Zero,
                    size = Size(4.dp.toPx(), size.height)
                )
            }
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = HomeOutline,
            modifier = Modifier.padding(start = 8.dp),
            letterSpacing = 1.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = accent,
            modifier = Modifier.padding(start = 8.dp, top = 4.dp),
            letterSpacing = (-0.5).sp
        )
    }
}

@Composable
private fun SideCommandButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(HomePanelHigh)
            .clickable(onClick = onClick)
            .tacticalGrid(alpha = 0.12f, horizontalSpacing = 4.dp, verticalSpacing = 6.dp)
            .padding(horizontal = 14.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = HomeAmber,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = HomeAmber
            )
        }
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = HomeAmber,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun HomeHeroPanel(
    runCount: Int,
    lastRun: RunRecord?,
    topRun: RunRecord?
) {
    IndustrialPanel(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            WardenCoreDisplay()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "WARDEN CORE",
                    style = MaterialTheme.typography.displayMedium,
                    color = HomeOnSurface,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = (-1).sp // tracking-tighter
                )
                Text(
                    text = "CENTRAL INTELLIGENCE INTERFACE • BUNKER SUBLEVEL 01",
                    style = MaterialTheme.typography.labelSmall,
                    color = HomeAmber.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp // tracking-widest
                )
            }

            Row(
                modifier = Modifier.widthIn(max = 360.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniTelemetryCard(
                    modifier = Modifier.weight(1f),
                    label = "Core Stability",
                    value = "99.98%",
                    accent = HomeGreen
                )
                MiniTelemetryCard(
                    modifier = Modifier.weight(1f),
                    label = "Network Load",
                    value = buildNetworkLoad(runCount),
                    accent = HomeAmber
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                topRun?.let {
                    Text(
                        text = "TOP SETTLEMENT // ${it.settlementName.uppercase()} // ${it.score}",
                        style = MaterialTheme.typography.bodySmall,
                        color = HomeAmber.copy(alpha = 0.72f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                lastRun?.let {
                    Text(
                        text = "LATEST OUTCOME // ${it.classification.uppercase()} // ${it.locationName.uppercase()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = HomeOnSurfaceMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun WardenCoreDisplay() {
    val pulse by rememberInfiniteTransition(label = "home_pulse").animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Restart
        ),
        label = "hero_pulse"
    )
    val alpha by rememberInfiniteTransition(label = "home_alpha").animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Restart
        ),
        label = "hero_alpha"
    )

    Box(contentAlignment = Alignment.Center) {
        // Background pulsing circle (animate-ping scale-150)
        Box(
            modifier = Modifier
                .size(132.dp)
                .graphicsLayer {
                    scaleX = pulse * 1.5f
                    scaleY = pulse * 1.5f
                    this.alpha = alpha
                }
                .background(HomeAmberStrong.copy(alpha = 0.2f), shape = CircleShape)
        )

        // Main square container
        Box(
            modifier = Modifier
                .size(132.dp)
                .background(Color(0xFF0D0F0F)) // surface-container-lowest
                .border(1.dp, HomeAmber.copy(alpha = 0.2f)), // border-primary/20
            contentAlignment = Alignment.Center
        ) {
            // Main icon (Security)
            Icon(
                imageVector = Icons.Filled.Security,
                contentDescription = null,
                tint = HomeAmber,
                modifier = Modifier
                    .size(72.dp)
                    .graphicsLayer {
                        shadowElevation = 8.dp.toPx()
                        ambientShadowColor = HomeAmberStrong
                        spotShadowColor = HomeAmberStrong
                    }
            )
        }
        
        // Exact LED placement matching HTML (absolute -top-1 -right-1 and -bottom-1 -left-1)
        Box(modifier = Modifier.size(132.dp)) {
            LedIndicator(
                active = true,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .size(12.dp)
            )
            LedIndicator(
                active = true,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-4).dp, y = 4.dp)
                    .size(12.dp)
            )
        }
    }
}

@Composable
private fun MiniTelemetryCard(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(HomePanelLow)
            .tacticalGrid(alpha = 0.08f, horizontalSpacing = 4.dp, verticalSpacing = 6.dp)
            .padding(12.dp)
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.bodySmall,
            color = HomeOutline
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = accent
        )
    }
}

@Composable
private fun StartMissionButton(onClick: () -> Unit) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // 3D Offset values from HTML
    val shadowHeight = if (isPressed) 4.dp else 10.dp
    val translateY = if (isPressed) 4.dp else 0.dp
    val scale = if (isPressed) 0.98f else 1.0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .graphicsLayer {
                translationY = translateY.toPx()
                scaleX = scale
                scaleY = scale
            }
            .background(HomeAmberStrong)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .tacticalGrid(alpha = 0.18f, horizontalSpacing = 3.dp, verticalSpacing = 4.dp)
            .drawBehind {
                // Bottom shadow/3D edge
                drawRect(
                    color = Color(0xFF9C6A00),
                    topLeft = Offset(0f, size.height),
                    size = Size(size.width, shadowHeight.toPx())
                )
            }
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.PowerSettingsNew,
                contentDescription = null,
                tint = HomeBackground,
                modifier = Modifier.size(34.dp)
            )
            Text(
                text = "START NEW MISSION",
                style = MaterialTheme.typography.titleLarge,
                color = HomeBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun DiagnosticsPanel() {
    IndustrialPanel(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "SYSTEM DIAGNOSTICS",
            style = MaterialTheme.typography.labelMedium,
            color = HomeAmber.copy(alpha = 0.72f)
        )
        Spacer(modifier = Modifier.height(14.dp))
        DiagnosticRow(label = "Oxygen Scrubbers", value = "NOMINAL", accent = HomeGreen, fraction = 1f)
        Spacer(modifier = Modifier.height(12.dp))
        DiagnosticRow(label = "Shield Integrity", value = "82%", accent = HomeAmber, fraction = 0.82f)
        Spacer(modifier = Modifier.height(12.dp))
        DiagnosticRow(label = "Radiation Leak", value = "DETECTED", accent = HomeError, fraction = 0.15f)
    }
}

@Composable
private fun DiagnosticRow(
    label: String,
    value: String,
    accent: Color,
    fraction: Float
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = HomeOnSurface.copy(alpha = 0.5f),
            letterSpacing = 1.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = accent,
            fontWeight = FontWeight.Bold
        )
    }
    Spacer(modifier = Modifier.height(6.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(HomePanelLow)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .height(4.dp)
                .background(accent)
        )
    }
}

@Composable
private fun BroadcastInterceptPanel() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(HomePanelLow)
            .tacticalGrid(alpha = 0.10f)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            tint = HomeAmber,
            modifier = Modifier.size(18.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "BROADCAST INTERCEPT",
                style = MaterialTheme.typography.bodySmall,
                color = HomeAmber
            )
            Text(
                text = "\"...transmission lost at depth 400m. Emergency power only. Awaiting command input from Warden...\"",
                style = MaterialTheme.typography.bodySmall,
                color = HomeOnSurface.copy(alpha = 0.8f),
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@Composable
private fun TacticalOverlayPanel() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(HomePanelLow)
            .tacticalGrid(alpha = 0.10f)
            .clip(RectangleShape)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val amber = HomeAmberStrong.copy(alpha = 0.32f)
            repeat(6) { index ->
                val y = size.height * (0.16f + index * 0.12f)
                drawLine(
                    color = amber.copy(alpha = if (index % 2 == 0) 0.24f else 0.1f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
            }
            repeat(6) { index ->
                val x = size.width * (0.12f + index * 0.14f)
                drawLine(
                    color = amber.copy(alpha = if (index == 3) 0.28f else 0.1f),
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1f
                )
            }

            drawLine(
                color = HomeAmberStrong.copy(alpha = 0.4f),
                start = Offset(size.width * 0.1f, size.height * 0.74f),
                end = Offset(size.width * 0.42f, size.height * 0.44f),
                strokeWidth = 2.2f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = HomeAmberStrong.copy(alpha = 0.4f),
                start = Offset(size.width * 0.42f, size.height * 0.44f),
                end = Offset(size.width * 0.7f, size.height * 0.52f),
                strokeWidth = 2.2f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = HomeAmberStrong.copy(alpha = 0.4f),
                start = Offset(size.width * 0.7f, size.height * 0.52f),
                end = Offset(size.width * 0.86f, size.height * 0.22f),
                strokeWidth = 2.2f,
                cap = StrokeCap.Round
            )

            drawCircle(
                color = HomeAmberStrong.copy(alpha = 0.2f),
                radius = 18.dp.toPx(),
                center = Offset(size.width * 0.42f, size.height * 0.44f),
                style = Stroke(width = 1.4.dp.toPx())
            )
        }

        Text(
            text = "LIVE TACTICAL OVERLAY",
            style = MaterialTheme.typography.bodySmall,
            color = HomeAmber,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .background(HomeBackground.copy(alpha = 0.78f))
                .padding(horizontal = 8.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun MobileCommandBar() {
    val navPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(HomeBackground)
            .padding(bottom = navPadding)
            .height(62.dp)
    ) {
        BottomCommandItem(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.Home,
            label = "COMMAND",
            active = true
        )
        BottomCommandItem(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.Radar,
            label = "SURFACE"
        )
        BottomCommandItem(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.History,
            label = "ARCHIVE"
        )
        BottomCommandItem(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.Settings,
            label = "SYSTEM"
        )
    }
}

@Composable
private fun BottomCommandItem(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    active: Boolean = false
) {
    val background = if (active) HomeAmberStrong else HomeBackground
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(background)
            .tacticalGrid(alpha = if (active) 0.18f else 0.08f, horizontalSpacing = 4.dp, verticalSpacing = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (active) HomeBackground else HomeAmber.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (active) HomeBackground else HomeAmber.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun IndustrialPanel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .background(HomePanel)
            .tacticalGrid(alpha = 0.07f, horizontalSpacing = 4.dp, verticalSpacing = 6.dp)
            .drawBehind {
                // Main subtle gradient
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.03f),
                            Color.Transparent
                        ),
                        start = Offset.Zero,
                        end = Offset(size.width, size.height)
                    )
                )
                // Inset "bevel" effect
                // Top-left light edge
                drawLine(
                    color = Color.White.copy(alpha = 0.05f),
                    start = Offset.Zero,
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.05f),
                    start = Offset.Zero,
                    end = Offset(0f, size.height),
                    strokeWidth = 1.dp.toPx()
                )
                // Bottom-right dark edge
                drawLine(
                    color = Color.Black.copy(alpha = 0.3f),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = Color.Black.copy(alpha = 0.3f),
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
private fun LedIndicator(
    active: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (active) HomeGreen else HomeError
    Box(
        modifier = modifier
            .size(8.dp)
            .background(color)
            .drawBehind {
                if (active) {
                    drawCircle(
                        color = color.copy(alpha = 0.4f),
                        radius = 6.dp.toPx(),
                        center = center
                    )
                    drawCircle(
                        color = color.copy(alpha = 0.15f),
                        radius = 10.dp.toPx(),
                        center = center
                    )
                }
            }
    )
}

@Composable
private fun HomeScanlineOverlay() {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val width = size.width
        val height = size.height
        
        // 1. Horizontal Scanlines (2px)
        val horizontalSpacing = 2.dp.toPx()
        var y = 0f
        while (y < height) {
            drawRect(
                color = Color.Black.copy(alpha = 0.10f),
                topLeft = Offset(0f, y + horizontalSpacing / 2),
                size = Size(width, horizontalSpacing / 2)
            )
            y += horizontalSpacing
        }

        // 2. Vertical RGB Strips (3px)
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

private fun formatScore(score: Int): String = "%,d".format(score)

private fun buildNetworkLoad(runCount: Int): String {
    val load = 4.2f + (runCount.coerceAtMost(18) * 0.07f)
    return "${"%.1f".format(load)} TB/S"
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
