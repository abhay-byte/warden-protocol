package com.wardenprotocol.game.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.wardenprotocol.game.data.model.RunRecord
import com.wardenprotocol.game.ui.component.CommandPanel
import com.wardenprotocol.game.ui.component.StatusBadge
import com.wardenprotocol.game.ui.component.VaultCoreSeal
import com.wardenprotocol.game.ui.component.WardenBackdrop
import com.wardenprotocol.game.ui.component.animatedEntranceModifier
import com.wardenprotocol.game.ui.theme.Brass
import com.wardenprotocol.game.ui.theme.BrassDeep
import com.wardenprotocol.game.ui.theme.CyanGlow
import com.wardenprotocol.game.ui.theme.PanelStroke
import com.wardenprotocol.game.ui.theme.SignalCyan
import com.wardenprotocol.game.ui.theme.SurfaceBlack
import com.wardenprotocol.game.ui.theme.SurfaceElevated
import com.wardenprotocol.game.ui.theme.SurfaceMetal
import com.wardenprotocol.game.ui.theme.TextPrimary
import com.wardenprotocol.game.ui.theme.TextSecondary
import com.wardenprotocol.game.ui.theme.VaultGreen
import com.wardenprotocol.game.ui.theme.WarningAmber

@Composable
fun MainMenuScreen(
    highScore: Int,
    runCount: Int,
    leaderboardPreview: List<RunRecord>,
    lastRun: RunRecord?,
    onNewGame: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    onOpenHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    WardenBackdrop(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CommandPanel(
                title = "The Warden Protocol",
                subtitle = "AI-managed vault command authority",
                icon = Icons.Filled.Shield,
                accent = VaultGreen
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(0.95f),
                        contentAlignment = Alignment.Center
                    ) {
                        VaultCoreSeal(
                            accent = CyanGlow,
                            modifier = Modifier.size(148.dp)
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1.05f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "WARDEN CORE",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary
                        )
                        Text(
                            text = "Scan the poisoned world, preserve the bunker, and decide when humanity risks the surface again.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )
                        Text(
                            text = "STATUS: ACTIVE",
                            style = MaterialTheme.typography.labelLarge,
                            color = VaultGreen
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatusBadge(
                        icon = Icons.Filled.EmojiEvents,
                        label = "High Score",
                        value = highScore.toString(),
                        accent = WarningAmber,
                        modifier = Modifier.weight(1f)
                    )
                    StatusBadge(
                        icon = Icons.Filled.History,
                        label = "Runs Logged",
                        value = runCount.toString(),
                        accent = SignalCyan,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MissionCard(
                    title = "Start Mission",
                    subtitle = "Open a fresh command run and choose humanity's next landing site.",
                    icon = Icons.Filled.PlayArrow,
                    accent = CyanGlow,
                    tone = VaultGreen,
                    onClick = onNewGame,
                    modifier = Modifier.weight(1f)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    UtilityCard(
                        title = "Civilization Archive",
                        subtitle = "Review past runs, collapses, and partial victories.",
                        icon = Icons.Filled.History,
                        accent = SignalCyan,
                        onClick = onOpenHistory
                    )
                    UtilityCard(
                        title = "Performance Rankings",
                        subtitle = "Inspect the best surviving settlements on record.",
                        icon = Icons.Filled.EmojiEvents,
                        accent = WarningAmber,
                        onClick = onOpenLeaderboard
                    )
                }
            }

            lastRun?.let { run ->
                CommandPanel(
                    title = "Latest Outcome",
                    subtitle = run.settlementName,
                    icon = Icons.Filled.History,
                    accent = SignalCyan
                ) {
                    Text(
                        text = run.classification.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "${run.score} score  |  ${run.locationName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = run.summary,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
            }

            leaderboardPreview.firstOrNull()?.let { top ->
                CommandPanel(
                    title = "Hall Of Wardens",
                    subtitle = "Current top settlement",
                    icon = Icons.Filled.EmojiEvents,
                    accent = WarningAmber
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TrophyCoin(
                            modifier = Modifier.size(72.dp)
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = top.settlementName,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                            Text(
                                text = "${top.score} score  |  ${top.classification}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            Text(
                                text = top.locationName,
                                style = MaterialTheme.typography.bodyLarge,
                                color = WarningAmber
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MissionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    tone: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = animatedEntranceModifier(modifier, delayMillis = 40),
        colors = CardDefaults.cardColors(containerColor = SurfaceBlack),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.45f)),
        shape = RoundedCornerShape(28.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(SurfaceMetal.copy(alpha = 0.42f), SurfaceBlack, SurfaceElevated)
                    )
                )
                .padding(18.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(accent.copy(alpha = 0.3f), Color.Transparent)
                            ),
                            shape = RoundedCornerShape(999.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    VaultCoreSeal(
                        accent = accent,
                        modifier = Modifier.size(96.dp)
                    )
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = tone,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Brass.copy(alpha = 0.4f), Brass, Brass.copy(alpha = 0.4f))
                            ),
                            shape = RoundedCornerShape(999.dp)
                        )
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun UtilityCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = animatedEntranceModifier(modifier.fillMaxWidth(), delayMillis = 55),
        colors = CardDefaults.cardColors(containerColor = SurfaceBlack),
        border = BorderStroke(1.dp, PanelStroke.copy(alpha = 0.95f)),
        shape = RoundedCornerShape(22.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(SurfaceMetal.copy(alpha = 0.28f), SurfaceBlack)
                    )
                )
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(accent.copy(alpha = 0.26f), Color.Transparent)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent)
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun TrophyCoin(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Brass, Color(0xFF6E4A16))
                ),
                shape = RoundedCornerShape(999.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFF5D08A), BrassDeep)
                    ),
                    shape = RoundedCornerShape(999.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "1",
                style = MaterialTheme.typography.titleLarge,
                color = SurfaceBlack
            )
        }
    }
}
