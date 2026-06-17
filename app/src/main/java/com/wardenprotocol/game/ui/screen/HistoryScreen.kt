package com.ivarna.wardenprotocol.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.GridView
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.PaddingValues
import com.ivarna.wardenprotocol.R
import com.ivarna.wardenprotocol.data.model.RunRecord
import com.ivarna.wardenprotocol.ui.component.*
import com.ivarna.wardenprotocol.ui.theme.TextSecondary
import com.ivarna.wardenprotocol.ui.theme.VaultGreen
import com.ivarna.wardenprotocol.ui.theme.WarningAmber

@Composable
fun HistoryScreen(
    entries: List<RunRecord>,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    onOpenRun: (RunRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF121414),
        topBar = { WardenTopBar(title = stringResource(R.string.history_top_bar)) },
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
    ) { padding ->
        HistoryContent(
            entries = entries,
            onOpenRun = onOpenRun,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
fun HistoryContent(
    entries: List<RunRecord>,
    onOpenRun: (RunRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            // Header Telemetry Block
            ArchiveHeaderTelemetry(totalRuns = entries.size)

            Spacer(modifier = Modifier.height(32.dp))

            // History List
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (entries.isEmpty()) {
                    Text(
                        text = stringResource(R.string.history_empty),
                        style = MaterialTheme.typography.bodySmall,
                        color = WarningAmber.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                } else {
                    entries.forEach { entry ->
                        RunArchiveCard(
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
    }
}

@Composable
private fun ArchiveHeaderTelemetry(totalRuns: Int) {
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
                stringResource(R.string.history_index_label),
                style = MaterialTheme.typography.labelSmall,
                color = WarningAmber,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                stringResource(R.string.history_index_title),
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
                    stringResource(R.string.history_auth),
                    style = MaterialTheme.typography.labelSmall,
                    color = WarningAmber,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(R.string.history_records_format, totalRuns),
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
private fun TerminalDecorFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0D0F0F))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val lines = listOf(
            stringResource(R.string.history_footer_line_1),
            stringResource(R.string.history_footer_line_2),
            stringResource(R.string.history_footer_line_3),
            stringResource(R.string.history_footer_line_4)
        )
        lines.forEach { line ->
            Text(
                text = line,
                style = MaterialTheme.typography.labelSmall,
                color = WarningAmber.copy(alpha = 0.4f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
