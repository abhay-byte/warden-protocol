package com.ivarna.wardenprotocol.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivarna.wardenprotocol.data.model.RunRecord
import com.ivarna.wardenprotocol.data.model.locationTypeOrNull
import com.ivarna.wardenprotocol.data.model.resolvedGradeLabel
import com.ivarna.wardenprotocol.data.model.resolvedOutcomeLabel
import com.ivarna.wardenprotocol.ui.theme.TextPrimary
import com.ivarna.wardenprotocol.ui.theme.TextSecondary
import com.ivarna.wardenprotocol.ui.theme.VaultGreen
import com.ivarna.wardenprotocol.ui.theme.WarningAmber
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun RunArchiveCard(
    entry: RunRecord,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1E2020))
            .clickable(onClick = onClick)
            .drawBehind {
                drawRect(
                    color = VaultGreen.copy(alpha = 0.45f),
                    size = this.size.copy(width = 4.dp.toPx())
                )
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Hero Thumbnail Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFF0D0F0F))
                    .border(1.dp, Color(0xFF333535).copy(alpha = 0.3f))
            ) {
                Image(
                    painter = painterResource(id = locationArtworkRes(entry.locationTypeOrNull())),
                    contentDescription = entry.locationName,
                    modifier = Modifier
                        .matchParentSize()
                        .tacticalGrid(alpha = 0.12f),
                    contentScale = ContentScale.Crop,
                    alpha = 0.65f
                )
                
                // CAM_REF Watermark
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 6.dp, end = 6.dp)
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "CAM_REF: ${(entry.id.hashCode() % 99).toMathAbs()}-X",
                        style = MaterialTheme.typography.labelSmall,
                        color = WarningAmber,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Global Telemetry Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.settlementName.uppercase(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = WarningAmber,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = entry.resolvedGradeLabel(),
                        style = MaterialTheme.typography.labelSmall,
                        color = VaultGreen,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "FINAL SCORE",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "%,d".format(entry.score),
                        style = MaterialTheme.typography.headlineMedium,
                        color = WarningAmber,
                        fontWeight = FontWeight.Black,
                        lineHeight = 28.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3-Column Telemetry Box Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ArchiveGridField(
                    label = "SURVIVORS",
                    value = "%,d".format(entry.survivors),
                    icon = Icons.Filled.Groups,
                    iconTint = VaultGreen,
                    modifier = Modifier.weight(1f)
                )
                ArchiveGridField(
                    label = "DURATION",
                    value = "${entry.yearsSinceWar} YEARS",
                    icon = Icons.Filled.Schedule,
                    iconTint = WarningAmber,
                    modifier = Modifier.weight(1f)
                )
                ArchiveGridField(
                    label = "OUTCOME",
                    value = entry.resolvedOutcomeLabel(),
                    modifier = Modifier.weight(1.2f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Tactical Narrative Summary
            Text(
                text = entry.summary,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 16.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Bottom Stability Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Color(0xFF282A2A))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(if (entry.survivors > 0) 0.85f else 0.15f)
                        .fillMaxHeight()
                        .background(if (entry.survivors > 0) VaultGreen else Color(0xFFFFB4AB))
                )
            }
        }
    }
}

@Composable
private fun ArchiveGridField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconTint: Color = WarningAmber
) {
    Column(
        modifier = modifier
            .background(Color(0xFF0D0F0F))
            .padding(8.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontSize = 8.sp,
            letterSpacing = 0.5.sp
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun Int.toMathAbs(): Int = if (this < 0) -this else this

@Composable
private fun Modifier.tacticalGrid(alpha: Float): Modifier = this.drawBehind {
    val horizontalSpacing = 3.dp.toPx()
    val verticalSpacing = 4.dp.toPx()
    
    // Horizontal scanlines
    var y = 0f
    while (y < this.size.height) {
        drawRect(
            color = Color.Black.copy(alpha = alpha),
            topLeft = Offset(0f, y),
            size = Size(this.size.width, 1.dp.toPx())
        )
        y += horizontalSpacing
    }

    // Vertical phosphor columns
    var x = 0f
    while (x < this.size.width) {
        drawRect(
            color = Color.Black.copy(alpha = alpha * 0.25f),
            topLeft = Offset(x, 0f),
            size = Size(1.dp.toPx(), this.size.height)
        )
        x += verticalSpacing
    }
}

@Composable
fun CompactRunArchiveRow(
    rank: Int? = null,
    entry: RunRecord,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1E2020))
            .clickable(onClick = onClick)
            .drawBehind {
                drawRect(
                    color = WarningAmber.copy(alpha = 0.45f),
                    size = this.size.copy(width = 4.dp.toPx())
                )
            }
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (rank != null) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(WarningAmber)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF121414),
                    fontWeight = FontWeight.Black
                )
            }
        }

        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFF0D0F0F))
                .border(1.dp, Color(0xFF333535).copy(alpha = 0.3f))
        ) {
            Image(
                painter = painterResource(id = locationArtworkRes(entry.locationTypeOrNull())),
                contentDescription = entry.locationName,
                modifier = Modifier
                    .matchParentSize()
                    .tacticalGrid(alpha = 0.12f),
                contentScale = ContentScale.Crop,
                alpha = 0.6f
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = entry.settlementName.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = WarningAmber,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = entry.locationName.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 9.sp,
                letterSpacing = 1.sp
            )
            Text(
                text = "${entry.survivors} SURVIVORS / ${entry.yearsSinceWar} YEARS",
                style = MaterialTheme.typography.labelSmall,
                color = VaultGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "%,d".format(entry.score),
                style = MaterialTheme.typography.titleLarge,
                color = WarningAmber,
                fontWeight = FontWeight.Black
            )
            Text(
                text = formatArchiveDate(entry.completedAtMillis).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 8.sp
            )
        }
    }
}

@Composable
private fun ArchiveMetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF0E1214))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            icon()
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                letterSpacing = 1.2.sp
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

fun formatArchiveDate(timestamp: Long): String {
    if (timestamp == 0L) return "Unknown"
    return DateTimeFormatter.ofPattern("dd MMM yyyy")
        .format(Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()))
}

@Composable
fun WardenTopBar(
    title: String = "WARDEN_PROTOCOL_V1.0.4",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1C1C))
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(
                Icons.Filled.Terminal,
                contentDescription = null,
                tint = WarningAmber,
                modifier = Modifier.size(24.dp)
            )
            Text(
                title.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = WarningAmber,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp
            )
        }
        Icon(Icons.Filled.Tune, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(24.dp))
    }
}

enum class WardenTab { COMMAND, SURFACE, ARCHIVE, SYSTEM }

@Composable
fun WardenBottomNav(
    activeTab: WardenTab,
    showSurface: Boolean = true,
    onTabClick: (WardenTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(80.dp)
            .background(Color(0xFF121414))
            .drawBehind {
                drawLine(
                    color = WarningAmber.copy(alpha = 0.2f),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 2.dp.toPx()
                )
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        WardenBottomNavItem("COMMAND", Icons.Filled.GridView, activeTab == WardenTab.COMMAND) { onTabClick(WardenTab.COMMAND) }
        if (showSurface) {
            WardenBottomNavItem("SURFACE", Icons.Filled.Visibility, activeTab == WardenTab.SURFACE) { onTabClick(WardenTab.SURFACE) }
        }
        WardenBottomNavItem("ARCHIVE", Icons.Filled.Storage, activeTab == WardenTab.ARCHIVE) { onTabClick(WardenTab.ARCHIVE) }
        WardenBottomNavItem("SYSTEM", Icons.Filled.SettingsApplications, activeTab == WardenTab.SYSTEM) { onTabClick(WardenTab.SYSTEM) }
    }
}

@Composable
private fun WardenBottomNavItem(
    label: String,
    icon: ImageVector,
    isActive: Boolean = false,
    onClick: () -> Unit = {}
) {
    val tone = if (isActive) WarningAmber else Color(0xFFFFD597).copy(alpha = 0.6f)
    val background = if (isActive) WarningAmber.copy(alpha = 0.1f) else Color.Transparent

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(80.dp)
            .background(background)
            .clickable(enabled = !isActive) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isActive) Color(0xFF121414) else tone,
            modifier = Modifier
                .size(24.dp)
                .then(if (isActive) Modifier.background(WarningAmber).padding(2.dp) else Modifier)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive) WarningAmber else tone,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
    }
}

@Composable
fun TerminalDecorFooter(
    lines: List<String> = listOf(
        "> INITIALIZING TERMINAL BUFFER...",
        "> AUTHENTICATION: SUCCESS",
        "> SYNCING_DISTRIBUTED_LOGS...",
        "> END OF DATA FEED"
    )
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0D0F0F))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
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
