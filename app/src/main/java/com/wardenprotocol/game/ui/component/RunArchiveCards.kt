package com.wardenprotocol.game.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import com.wardenprotocol.game.data.model.RunRecord
import com.wardenprotocol.game.data.model.locationTypeOrNull
import com.wardenprotocol.game.data.model.resolvedGradeLabel
import com.wardenprotocol.game.data.model.resolvedOutcomeLabel
import com.wardenprotocol.game.ui.theme.PanelStroke
import com.wardenprotocol.game.ui.theme.SignalCyan
import com.wardenprotocol.game.ui.theme.TextPrimary
import com.wardenprotocol.game.ui.theme.TextSecondary
import com.wardenprotocol.game.ui.theme.VaultGreen
import com.wardenprotocol.game.ui.theme.WarningAmber
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun RunArchiveCard(
    entry: RunRecord,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF121619))
            .border(1.dp, PanelStroke.copy(alpha = 0.75f), RoundedCornerShape(24.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.25f)
        ) {
            Image(
                painter = painterResource(id = locationArtworkRes(entry.locationTypeOrNull())),
                contentDescription = entry.locationName,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xCC101416),
                                Color(0x66212428),
                                Color(0xEE0B0E10)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .matchParentSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
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
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = entry.resolvedGradeLabel(),
                            style = MaterialTheme.typography.labelLarge,
                            color = VaultGreen,
                            letterSpacing = 2.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "FINAL SCORE",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "%,d".format(entry.score),
                            style = MaterialTheme.typography.headlineMedium,
                            color = WarningAmber,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = entry.locationName.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = SignalCyan,
                    letterSpacing = 1.8.sp
                )
            }
        }

        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ArchiveMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Survivors",
                    value = "%,d".format(entry.survivors),
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Groups,
                            contentDescription = null,
                            tint = VaultGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
                ArchiveMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Duration",
                    value = "${entry.yearsSinceWar} YEARS",
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = null,
                            tint = WarningAmber,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }

            ArchiveMetricCard(
                modifier = Modifier.fillMaxWidth(),
                title = "Outcome",
                value = entry.resolvedOutcomeLabel(),
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = SignalCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )

            Text(
                text = entry.summary,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                lineHeight = 24.sp
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(VaultGreen.copy(alpha = 0.85f))
            )
        }
    }
}

@Composable
fun CompactRunArchiveRow(
    rank: Int? = null,
    entry: RunRecord,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF121619))
            .border(1.dp, PanelStroke.copy(alpha = 0.65f), RoundedCornerShape(18.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (rank != null) {
            Text(
                text = "#$rank",
                style = MaterialTheme.typography.titleLarge,
                color = WarningAmber,
                fontWeight = FontWeight.Bold
            )
        }

        Image(
            painter = painterResource(id = locationArtworkRes(entry.locationTypeOrNull())),
            contentDescription = entry.locationName,
            modifier = Modifier
                .size(92.dp)
                .clip(RoundedCornerShape(14.dp)),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = entry.settlementName,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = entry.locationName,
                style = MaterialTheme.typography.bodyMedium,
                color = SignalCyan,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = entry.resolvedGradeLabel(),
                style = MaterialTheme.typography.labelMedium,
                color = VaultGreen,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${entry.survivors} survivors | ${entry.yearsSinceWar} years | ${entry.resolvedOutcomeLabel()}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "%,d".format(entry.score),
                style = MaterialTheme.typography.titleMedium,
                color = WarningAmber,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = formatArchiveDate(entry.completedAtMillis),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
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
