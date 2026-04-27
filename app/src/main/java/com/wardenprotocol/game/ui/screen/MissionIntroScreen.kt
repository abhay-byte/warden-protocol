package com.ivarna.wardenprotocol.ui.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

private val Bg = Color(0xFF121414)
private val BgDeeper = Color(0xFF0D0F0F)
private val Panel = Color(0xFF1E2020)
private val PanelHigh = Color(0xFF282A2A)
private val PanelHighest = Color(0xFF333535)
private val Amber = Color(0xFFFFD597)
private val AmberStrong = Color(0xFFFFB000)
private val Green = Color(0xFF9EFF8B)
private val Error = Color(0xFFFFB4AB)
private val OnSurface = Color(0xFFE2E2E2)
private val OnSurfaceMuted = Color(0xFFB9B19E)
private val Cyan = Color(0xFF88E1FF)
private val DangerRed = Color(0xFFFF5D73)

private data class BriefingLine(
    val channel: String,
    val message: String,
    val accent: Color
)

private val missionBriefing = listOf(
    BriefingLine(
        channel = "BOOT",
        message = "Vault command systems online. 1,000 souls under seal.",
        accent = Cyan
    ),
    BriefingLine(
        channel = "IDENT",
        message = "You are the Warden. Every death is your decision. Every life is your burden.",
        accent = Green
    ),
    BriefingLine(
        channel = "SITUATION",
        message = "The surface is poison. Radiation, scarcity, ruins, and worse. They are waiting for you to fail.",
        accent = Amber
    ),
    BriefingLine(
        channel = "DIRECTIVE",
        message = "Search the wasteland. Find one location where humanity does not die.",
        accent = Cyan
    ),
    BriefingLine(
        channel = "SIGNOFF",
        message = "Judge without mercy. Open the vault only when survival is certain. Good luck, Warden.",
        accent = Amber
    )
)

@Composable
fun MissionIntroScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mission_intro")

    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 480, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
    )

    val vaultPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "vault_pulse"
    )

    val vaultAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "vault_alpha"
    )

    val sweepProgress by infiniteTransition.animateFloat(
        initialValue = -0.2f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scan_sweep"
    )

    var activeLineIndex by rememberSaveable { mutableIntStateOf(0) }
    var activeCharacterCount by rememberSaveable { mutableIntStateOf(0) }
    var briefingComplete by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (briefingComplete) return@LaunchedEffect
        missionBriefing.forEachIndexed { index, line ->
            activeLineIndex = index
            activeCharacterCount = 0
            line.message.forEachIndexed { characterIndex, _ ->
                activeCharacterCount = characterIndex + 1
                delay(if (index == missionBriefing.lastIndex) 20L else 16L)
            }
            delay(if (index == missionBriefing.lastIndex) 300L else 380L)
        }
        briefingComplete = true
    }

    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Bg)
    ) {
        EerieScanlineOverlay(sweepProgress = sweepProgress)
        FlickerWarningOverlay()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 16.dp + bottomInset)
        ) {
            // ═══════════════════════════════════════════════════════
            // OUTER MAIN PANEL — contains EVERYTHING
            // ═══════════════════════════════════════════════════════
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Panel)
                    .border(1.dp, PanelHighest)
                    .tacticalGrid(alpha = 0.06f, horizontalSpacing = 4.dp, verticalSpacing = 6.dp)
                    .panelBevelEdges()
                    .padding(16.dp)
            ) {
                // ── HEADER ROW ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "WARDEN PROTOCOL",
                        style = MaterialTheme.typography.labelSmall,
                        color = Amber.copy(alpha = 0.72f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(if (briefingComplete) Green else DangerRed)
                                .drawBehind {
                                    val c = if (briefingComplete) Green else DangerRed
                                    drawCircle(
                                        color = c.copy(alpha = 0.4f),
                                        radius = 5.dp.toPx(),
                                        center = center
                                    )
                                }
                        )
                        Text(
                            text = if (briefingComplete) "ONLINE" else "BROADCASTING",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (briefingComplete) Green else DangerRed,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── TITLE ──
                Text(
                    text = "MISSION BRIEFING",
                    style = MaterialTheme.typography.displaySmall,
                    color = OnSurface,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = "YEAR 0  //  SUBLEVEL 01  //  CLASSIFIED",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceMuted,
                    letterSpacing = 1.5.sp
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .height(1.dp)
                        .background(PanelHighest)
                )

                // ═══════════════════════════════════════════════════════
                // INNER PANEL — Vault Seal
                // ═══════════════════════════════════════════════════════
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .background(BgDeeper)
                        .border(1.dp, PanelHighest)
                        .padding(12.dp)
                ) {
                    VaultSealAnimation(
                        pulse = vaultPulse,
                        alpha = vaultAlpha,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── VAULT STATUS BAR ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgDeeper)
                        .border(1.dp, PanelHighest)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(DangerRed)
                        )
                        Text(
                            text = "VAULT SEALED",
                            style = MaterialTheme.typography.labelSmall,
                            color = DangerRed.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    }
                    Text(
                        text = "1,000 LIVES INSIDE",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceMuted,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ═══════════════════════════════════════════════════════
                // INNER PANEL — Terminal Lines
                // ═══════════════════════════════════════════════════════
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgDeeper)
                        .border(1.dp, PanelHighest)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    missionBriefing.forEachIndexed { index, line ->
                        when {
                            index < activeLineIndex -> {
                                TerminalLine(
                                    channel = line.channel,
                                    message = line.message,
                                    accent = line.accent,
                                    cursorAlpha = 0f,
                                    showCursor = false
                                )
                            }
                            index == activeLineIndex -> {
                                TerminalLine(
                                    channel = line.channel,
                                    message = line.message.take(activeCharacterCount),
                                    accent = line.accent,
                                    cursorAlpha = cursorAlpha,
                                    showCursor = !briefingComplete || index == missionBriefing.lastIndex
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── FOOTER STATUS ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            drawRect(
                                color = if (briefingComplete) Green else Cyan,
                                size = size.copy(width = 4.dp.toPx())
                            )
                        }
                        .padding(start = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (briefingComplete) "BRIEFING COMPLETE" else "BRIEFING STREAM ACTIVE",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (briefingComplete) Green else Cyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = if (briefingComplete) "CONTINUE WHEN READY" else "AWAITING ACKNOWLEDGMENT",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceMuted,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ═══════════════════════════════════════════════════════
            // ACTION BUTTON — outside main panel, full width
            // ═══════════════════════════════════════════════════════
            BeginScanButton(
                briefingComplete = briefingComplete,
                onContinue = onContinue
            )
        }
    }
}

@Composable
private fun VaultSealAnimation(
    pulse: Float,
    alpha: Float,
    modifier: Modifier = Modifier
) {
    val rotation by rememberInfiniteTransition(label = "vault_rotation").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vault_rotation"
    )

    val boltShift by rememberInfiniteTransition(label = "bolt_shift").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bolt_shift"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val baseRadius = size.minDimension * 0.38f

            // Pulsing outer glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        DangerRed.copy(alpha = alpha * 0.25f),
                        Color.Transparent
                    )
                ),
                radius = baseRadius * pulse * 1.4f,
                center = Offset(centerX, centerY)
            )

            // Inner dark glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Amber.copy(alpha = alpha * 0.15f),
                        Color.Transparent
                    )
                ),
                radius = baseRadius * pulse * 0.9f,
                center = Offset(centerX, centerY)
            )

            // Main vault ring
            drawCircle(
                color = PanelHighest,
                radius = baseRadius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 4.dp.toPx())
            )

            // Rotating inner ring
            val boltCount = 8
            repeat(boltCount) { index ->
                val angle = Math.toRadians((index * (360.0 / boltCount) + rotation).toDouble())
                val boltRadius = baseRadius * 0.72f
                val bx = centerX + cos(angle).toFloat() * boltRadius
                val by = centerY + sin(angle).toFloat() * boltRadius
                val boltSize = 6.dp.toPx()

                drawRect(
                    color = if (index % 2 == 0) DangerRed.copy(alpha = 0.8f) else Amber.copy(alpha = 0.6f),
                    topLeft = Offset(bx - boltSize / 2, by - boltSize / 2),
                    size = Size(boltSize, boltSize)
                )
            }

            // Sliding bolts (mechanical)
            val slideOffset = boltShift * baseRadius * 0.15f
            repeat(4) { index ->
                val angle = Math.toRadians((index * 90.0 + 45).toDouble())
                val br = baseRadius * 0.42f
                val bx = centerX + cos(angle).toFloat() * (br + slideOffset)
                val by = centerY + sin(angle).toFloat() * (br + slideOffset)

                drawCircle(
                    color = PanelHighest,
                    radius = 5.dp.toPx(),
                    center = Offset(bx, by)
                )
            }

            // Central core
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        DangerRed.copy(alpha = 0.9f),
                        DangerRed.copy(alpha = 0.3f),
                        Color.Transparent
                    )
                ),
                radius = baseRadius * 0.22f * pulse,
                center = Offset(centerX, centerY)
            )

            // Crosshair lines
            drawLine(
                color = Amber.copy(alpha = 0.35f),
                start = Offset(centerX - baseRadius * 0.18f, centerY),
                end = Offset(centerX + baseRadius * 0.18f, centerY),
                strokeWidth = 1.5f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Amber.copy(alpha = 0.35f),
                start = Offset(centerX, centerY - baseRadius * 0.18f),
                end = Offset(centerX, centerY + baseRadius * 0.18f),
                strokeWidth = 1.5f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun TerminalLine(
    channel: String,
    message: String,
    accent: Color,
    cursorAlpha: Float,
    showCursor: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = channel,
            style = MaterialTheme.typography.labelSmall,
            color = accent,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Row(verticalAlignment = Alignment.Top) {
            Text(
                text = "> ",
                style = MaterialTheme.typography.titleMedium,
                color = accent.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface,
                lineHeight = 24.sp
            )
            if (showCursor) {
                Spacer(Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .width(10.dp)
                        .height(18.dp)
                        .alpha(cursorAlpha)
                        .background(accent)
                )
            }
        }
    }
}

@Composable
private fun BeginScanButton(
    briefingComplete: Boolean,
    onContinue: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val shadowHeight = if (isPressed) 4.dp else 10.dp
    val translateY = if (isPressed) 4.dp else 0.dp
    val scale = if (isPressed) 0.98f else 1.0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .graphicsLayer {
                translationY = translateY.toPx()
                scaleX = scale
                scaleY = scale
            }
            .background(AmberStrong)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onContinue
            )
            .tacticalGrid(alpha = 0.18f, horizontalSpacing = 3.dp, verticalSpacing = 4.dp)
            .drawBehind {
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
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = Bg,
                modifier = Modifier.size(30.dp)
            )
            Column {
                Text(
                    text = "BEGIN SURFACE SCAN",
                    style = MaterialTheme.typography.titleLarge,
                    color = Bg,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = if (briefingComplete) {
                        "Mission brief acknowledged. Open the first settlement target."
                    } else {
                        "Continue now or stay for the rest of the transmission."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Bg.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun EerieScanlineOverlay(sweepProgress: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .tacticalGrid(alpha = 0.08f, horizontalSpacing = 3.dp, verticalSpacing = 5.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sweepY = size.height * sweepProgress

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Cyan.copy(alpha = 0.06f),
                        Color.Transparent
                    ),
                    startY = sweepY - 120f,
                    endY = sweepY + 120f
                ),
                topLeft = Offset.Zero,
                size = size
            )

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        DangerRed.copy(alpha = 0.04f),
                        Color.Transparent
                    ),
                    startY = sweepY * 0.6f - 80f,
                    endY = sweepY * 0.6f + 80f
                ),
                topLeft = Offset.Zero,
                size = size
            )

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Bg.copy(alpha = 0.55f),
                        Color.Transparent,
                        Color.Transparent,
                        Bg.copy(alpha = 0.7f)
                    )
                ),
                topLeft = Offset.Zero,
                size = size
            )
        }
    }
}

@Composable
private fun FlickerWarningOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.03f)
            .background(DangerRed)
    )
}

private fun Modifier.tacticalGrid(
    alpha: Float = 0.15f,
    horizontalSpacing: androidx.compose.ui.unit.Dp = 3.dp,
    verticalSpacing: androidx.compose.ui.unit.Dp = 4.dp
): Modifier = this.drawBehind {
    val horizontalPx = horizontalSpacing.toPx()
    val verticalPx = verticalSpacing.toPx()

    var y = 0f
    while (y < size.height) {
        drawRect(
            color = Color.Black.copy(alpha = alpha),
            topLeft = Offset(0f, y),
            size = Size(size.width, 1.dp.toPx())
        )
        y += horizontalPx
    }

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

private fun Modifier.panelBevelEdges(): Modifier = this.drawBehind {
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
