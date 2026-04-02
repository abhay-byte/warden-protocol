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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivarna.wardenprotocol.ui.component.ActionButton
import com.ivarna.wardenprotocol.ui.component.WardenBackdrop
import com.ivarna.wardenprotocol.ui.theme.BackgroundBlack
import com.ivarna.wardenprotocol.ui.theme.CyanGlow
import com.ivarna.wardenprotocol.ui.theme.PanelStroke
import com.ivarna.wardenprotocol.ui.theme.SignalCyan
import com.ivarna.wardenprotocol.ui.theme.SurfaceBlack
import com.ivarna.wardenprotocol.ui.theme.SurfaceElevated
import com.ivarna.wardenprotocol.ui.theme.TextPrimary
import com.ivarna.wardenprotocol.ui.theme.TextSecondary
import com.ivarna.wardenprotocol.ui.theme.VaultGreen
import com.ivarna.wardenprotocol.ui.theme.WarningAmber
import kotlinx.coroutines.delay

private data class BriefingLine(
    val channel: String,
    val message: String,
    val accent: Color
)

private val missionBriefing = listOf(
    BriefingLine(
        channel = "BOOT",
        message = "Vault command systems online. Strategic archive integrity stable.",
        accent = SignalCyan
    ),
    BriefingLine(
        channel = "IDENT",
        message = "You are the Warden, the governing intelligence responsible for every human life sealed below.",
        accent = VaultGreen
    ),
    BriefingLine(
        channel = "SITUATION",
        message = "The old world above has collapsed into irradiated wasteland, fractured enclaves, and scavenged ruins.",
        accent = WarningAmber
    ),
    BriefingLine(
        channel = "DIRECTIVE",
        message = "Your task is to scan the surface, reject death traps, and locate ground where survivors can rebuild civilization.",
        accent = SignalCyan
    ),
    BriefingLine(
        channel = "THREATS",
        message = "Water scarcity, radiation, failed shelter, hostile factions, and travel losses will decide whether the vault becomes a nation or a grave.",
        accent = WarningAmber
    ),
    BriefingLine(
        channel = "MANDATE",
        message = "Preserve the bunker. Judge each site without mercy. Open the vault only when humanity has a real chance to endure.",
        accent = VaultGreen
    ),
    BriefingLine(
        channel = "SIGNOFF",
        message = "Good luck, Warden.",
        accent = WarningAmber
    )
)

@Composable
fun MissionIntroScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val terminalScroll = rememberScrollState()
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

    LaunchedEffect(activeLineIndex, activeCharacterCount, briefingComplete) {
        terminalScroll.animateScrollTo(terminalScroll.maxValue)
    }

    val bottomInset = WindowInsets.navigationBars.asPaddingValues()

    WardenBackdrop(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BackgroundBlack,
                            Color(0xFF08121A),
                            Color(0xFF071016),
                            BackgroundBlack
                        )
                    )
                )
        ) {
            TerminalScanOverlay(sweepProgress = sweepProgress)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
                    .padding(bottom = bottomInset.calculateBottomPadding()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MissionIntroHeader()

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .border(1.dp, PanelStroke.copy(alpha = 0.95f))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    SurfaceElevated.copy(alpha = 0.96f),
                                    SurfaceBlack.copy(alpha = 0.98f),
                                    Color(0xFF091018).copy(alpha = 0.99f)
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MissionTag(
                                icon = Icons.Filled.Shield,
                                label = "VAULT 01",
                                value = "STRATEGIC BRIEFING",
                                accent = VaultGreen
                            )
                            MissionTag(
                                icon = Icons.Filled.Radar,
                                label = "OBJECTIVE",
                                value = "FIND CIVILIZATION SITE",
                                accent = WarningAmber
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "MISSION PREFACE",
                                style = MaterialTheme.typography.labelMedium,
                                color = SignalCyan,
                                letterSpacing = 2.8.sp
                            )
                            Text(
                                text = "WARDEN INITIATION PROTOCOL",
                                style = MaterialTheme.typography.displaySmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "The vault survives. The species might not. Read the brief, then begin the surface search.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }

                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(PanelStroke.copy(alpha = 0.8f))
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(terminalScroll),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                "SYSTEM READY // CONTINUE TO FIRST SURFACE SCAN"
                            } else {
                                "DECODING VAULT DIRECTIVE..."
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = if (briefingComplete) VaultGreen else SignalCyan.copy(alpha = 0.84f),
                            letterSpacing = 1.6.sp
                        )
                    }
                }

                ActionButton(
                    title = "Begin Surface Scan",
                    subtitle = if (briefingComplete) {
                        "Accept command authority and review the first settlement target."
                    } else {
                        "Stand by while the mission briefing finishes loading."
                    },
                    icon = Icons.Filled.PlayArrow,
                    accent = WarningAmber,
                    enabled = briefingComplete,
                    onClick = onContinue
                )
            }
        }
    }
}

@Composable
private fun MissionIntroHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, PanelStroke.copy(alpha = 0.7f))
            .background(Color(0xFF0A1119).copy(alpha = 0.94f))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = SurfaceElevated,
                tonalElevation = 0.dp
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF101923)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Terminal,
                        contentDescription = null,
                        tint = SignalCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column {
                Text(
                    text = "WARDEN_PROTOCOL",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "PRE-RUN COMMAND BRIEFING",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    letterSpacing = 1.6.sp
                )
            }
        }

        Text(
            text = "YEAR 0 // VAULT SEALED",
            style = MaterialTheme.typography.labelSmall,
            color = WarningAmber,
            letterSpacing = 1.8.sp
        )
    }
}

@Composable
private fun MissionTag(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    accent: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(accent.copy(alpha = 0.12f), CircleShape)
                .border(1.dp, accent.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(18.dp)
            )
        }
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                letterSpacing = 1.2.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary
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
            letterSpacing = 2.2.sp
        )
        Row(verticalAlignment = Alignment.Top) {
            Text(
                text = "> ",
                style = MaterialTheme.typography.bodyLarge,
                color = accent,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
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
private fun TerminalScanOverlay(sweepProgress: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val spacing = 6.dp.toPx()
        var currentY = 0f
        while (currentY < size.height) {
            drawLine(
                color = Color.White.copy(alpha = 0.032f),
                start = Offset(0f, currentY),
                end = Offset(size.width, currentY),
                strokeWidth = 1f
            )
            currentY += spacing
        }

        val sweepY = size.height * sweepProgress
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    CyanGlow.copy(alpha = 0.12f),
                    Color.Transparent
                ),
                startY = sweepY - 140f,
                endY = sweepY + 140f
            ),
            topLeft = Offset.Zero,
            size = size
        )

        drawRect(color = Color(0xFF020304).copy(alpha = 0.08f))
    }
}
