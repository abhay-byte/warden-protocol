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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val BackgroundColor = Color(0xFF121414)
private val Primary = Color(0xFFFFD597)
private val Secondary = Color(0xFF9EFF8B)
private val SurfaceContainerMedium = Color(0xFF1E2020)
private val SurfaceContainerHigh = Color(0xFF282A2A)
private val SurfaceContainerHighest = Color(0xFF333535)
private val SurfaceContainerLowest = Color(0xFF0D0F0F)
private val TextPrimaryColor = Color(0xFFE2E2E2)
private val TextSecondaryColor = Color(0xFFD7C4AC)
private val SignalBlue = Color(0xFF88E1FF)

private data class BriefingLine(
    val channel: String,
    val message: String,
    val accent: Color
)

private val missionBriefing = listOf(
    BriefingLine(
        channel = "BOOT",
        message = "Vault command systems online.",
        accent = SignalBlue
    ),
    BriefingLine(
        channel = "IDENT",
        message = "You are the Warden, the intelligence responsible for every life sealed below.",
        accent = Secondary
    ),
    BriefingLine(
        channel = "SITUATION",
        message = "The surface is broken by radiation, scarcity, ruined shelter, and hostile survivors.",
        accent = Primary
    ),
    BriefingLine(
        channel = "DIRECTIVE",
        message = "Search the wasteland and find one location where humanity can rebuild civilization.",
        accent = SignalBlue
    ),
    BriefingLine(
        channel = "SIGNOFF",
        message = "Judge without mercy. Open the vault only when survival is real. Good luck, Warden.",
        accent = Primary
    )
)

@Composable
fun MissionIntroScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mission_intro")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 620, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
    )
    val sweepProgress by infiniteTransition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5200, easing = LinearEasing),
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
                delay(if (index == missionBriefing.lastIndex) 18L else 14L)
            }
            delay(if (index == missionBriefing.lastIndex) 260L else 340L)
        }
        briefingComplete = true
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundColor,
        topBar = { MissionIntroTopBar() },
        bottomBar = {
            MissionIntroBottomBar(
                briefingComplete = briefingComplete,
                onContinue = onContinue
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
            MissionIntroOverlay(sweepProgress = sweepProgress)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                MissionStatusBreadcrumb(briefingComplete = briefingComplete)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .border(1.dp, SurfaceContainerHighest)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    SurfaceContainerHigh,
                                    SurfaceContainerMedium,
                                    SurfaceContainerLowest
                                )
                            )
                        )
                        .tacticalGrid(alpha = 0.08f, horizontalSpacing = 3.dp, verticalSpacing = 4.dp)
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Text(
                            text = "MISSION PREFACE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "WARDEN INITIATION PROTOCOL",
                            style = MaterialTheme.typography.displaySmall,
                            color = TextPrimaryColor,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "A live terminal brief precedes the first scan. You can continue immediately or stay and read the full transmission.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondaryColor
                        )

                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(SurfaceContainerHighest)
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.SpaceEvenly
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

                        Text(
                            text = if (briefingComplete) {
                                "BRIEFING COMPLETE // CONTINUE WHEN READY"
                            } else {
                                "BRIEFING STREAM ACTIVE // CONTINUE AVAILABLE"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = if (briefingComplete) Secondary else SignalBlue,
                            letterSpacing = 1.6.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MissionIntroTopBar() {
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
                    text = "WARDEN_PROTOCOL_V1.0.4",
                    style = MaterialTheme.typography.titleMedium,
                    color = Primary,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "MISSION BRIEF / YEAR 0",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimaryColor.copy(alpha = 0.55f),
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
private fun MissionStatusBreadcrumb(briefingComplete: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRect(Primary, size = size.copy(width = 4.dp.toPx()))
            }
            .padding(start = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "MISSION_BRIEF: ACTIVE",
                style = MaterialTheme.typography.labelSmall,
                color = Primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                text = "DIRECTIVE: FIND A SURFACE SITE WHERE CIVILIZATION CAN ENDURE",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryColor,
                letterSpacing = 1.6.sp
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(if (briefingComplete) Secondary else SignalBlue)
            )
            Text(
                text = if (briefingComplete) "BRIEF_READY" else "CONTINUE_AVAILABLE",
                style = MaterialTheme.typography.labelSmall,
                color = if (briefingComplete) Secondary else SignalBlue,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MissionIntroBottomBar(
    briefingComplete: Boolean,
    onContinue: () -> Unit
) {
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundColor)
            .tacticalGrid(alpha = 0.12f, horizontalSpacing = 3.dp, verticalSpacing = 4.dp)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .padding(bottom = bottomInset),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = if (briefingComplete) {
                "The full transmission is complete. Proceed when ready."
            } else {
                "Continue is live now. The terminal feed will keep unfolding if you stay."
            },
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryColor,
            letterSpacing = 0.9.sp
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Primary.copy(alpha = 0.48f))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            SurfaceContainerHigh,
                            SurfaceContainerMedium,
                            SurfaceContainerLowest
                        )
                    )
                )
                .tacticalGrid(alpha = 0.18f, horizontalSpacing = 3.dp, verticalSpacing = 4.dp)
                .clickable(onClick = onContinue)
        ) {
            Column {
                Text(
                    text = "CRT COMMAND LINK",
                    style = MaterialTheme.typography.labelSmall,
                    color = SignalBlue,
                    letterSpacing = 1.6.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "BEGIN SURFACE SCAN",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimaryColor,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = if (briefingComplete) {
                                "Mission brief acknowledged. Open the first settlement target."
                            } else {
                                "Skip ahead now or let the briefing finish before entering the first scan."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryColor
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Primary.copy(alpha = 0.55f),
                                    Primary,
                                    Primary.copy(alpha = 0.55f)
                                )
                            )
                        )
                )
            }
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
            style = MaterialTheme.typography.labelMedium,
            color = accent,
            letterSpacing = 2.2.sp
        )
        Row(verticalAlignment = Alignment.Top) {
            Text(
                text = "> ",
                style = MaterialTheme.typography.titleMedium,
                color = accent,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimaryColor
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
private fun MissionIntroOverlay(sweepProgress: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .tacticalGrid(alpha = 0.05f, horizontalSpacing = 3.dp, verticalSpacing = 5.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sweepY = size.height * sweepProgress
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        SignalBlue.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    startY = sweepY - 140f,
                    endY = sweepY + 140f
                ),
                topLeft = Offset.Zero,
                size = size
            )
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
