package com.ivarna.wardenprotocol.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SettingsApplications
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.PaddingValues
import com.ivarna.wardenprotocol.data.model.RunRecord
import com.ivarna.wardenprotocol.ui.component.*
import com.ivarna.wardenprotocol.ui.theme.TextSecondary
import com.ivarna.wardenprotocol.ui.theme.VaultGreen
import com.ivarna.wardenprotocol.ui.theme.WarningAmber

@Composable
fun LeaderboardScreen(
    entries: List<RunRecord>,
    onBack: () -> Unit,
    onNewGame: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenRun: (RunRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF121414),
        topBar = { WardenTopBar(title = "GLOBAL_ARCHIVE_v1.0.4") },
        bottomBar = {
            WardenBottomNav(
                activeTab = WardenTab.ARCHIVE,
                showSurface = false,
                onTabClick = { tab ->
                    when(tab) {
                        WardenTab.COMMAND -> onBack()
                        WardenTab.SYSTEM -> onOpenSettings()
                        else -> {}
                    }
                }
            )
        }
    ) { padding: PaddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues = padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                // Header Telemetry Block
                LeaderboardHeaderTelemetry(totalRuns = entries.size, topScore = entries.firstOrNull()?.score ?: 0)

                Spacer(modifier = Modifier.height(32.dp))

                // Leaderboard List
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (entries.isEmpty()) {
                        Text(
                            text = "NO RANKED RECORDS DETECTED IN GLOBAL BUFFER.",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarningAmber.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    } else {
                        entries.forEachIndexed { index, entry ->
                            CompactRunArchiveRow(
                                rank = index + 1,
                                entry = entry,
                                onClick = { onOpenRun(entry) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Terminal Decor Footer
                TerminalDecorFooter()
            }

            // Floating Start New Mission FAB
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 24.dp)
            ) {
                StartMissionFAB(onClick = onNewGame)
            }
        }
    }
}

@Composable
private fun LeaderboardHeaderTelemetry(totalRuns: Int, topScore: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1C1C))
            .drawBehind {
                drawRect(
                    color = WarningAmber,
                    size = this.size.copy(width = 4.dp.toPx())
                )
            }
            .padding(start = 24.dp, top = 12.dp, bottom = 12.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text(
                "GLOBAL PERFORMANCE INDEX",
                style = MaterialTheme.typography.labelSmall,
                color = WarningAmber,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                "HALL_OF_WARDENS",
                style = MaterialTheme.typography.headlineLarge,
                color = WarningAmber,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Box(
                modifier = Modifier
                    .background(WarningAmber.copy(alpha = 0.1f))
                    .border(1.dp, WarningAmber.copy(alpha = 0.3f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    "TOP_RATING: %,d".format(topScore),
                    style = MaterialTheme.typography.labelSmall,
                    color = WarningAmber,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "RUNS RANKED: $totalRuns",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun StartMissionFAB(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .background(WarningAmber)
            .drawBehind {
                drawRect(
                    color = Color(0xFF6A4700),
                    topLeft = Offset(0f, this.size.height - 4.dp.toPx()),
                    size = this.size.copy(height = 4.dp.toPx())
                )
            }
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = null,
            tint = Color(0xFF121414),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "START NEW MISSION",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF121414),
            fontWeight = FontWeight.Black,
            letterSpacing = (-0.5).sp
        )
    }
}
