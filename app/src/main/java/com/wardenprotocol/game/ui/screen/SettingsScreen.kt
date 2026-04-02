package com.ivarna.wardenprotocol.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivarna.wardenprotocol.data.model.NvidiaModelCatalog
import com.ivarna.wardenprotocol.data.model.NvidiaModelOption
import com.ivarna.wardenprotocol.ui.component.TerminalDecorFooter
import com.ivarna.wardenprotocol.ui.component.WardenBottomNav
import com.ivarna.wardenprotocol.ui.component.WardenTab
import com.ivarna.wardenprotocol.ui.component.WardenTopBar
import com.ivarna.wardenprotocol.ui.theme.TextSecondary
import com.ivarna.wardenprotocol.ui.theme.VaultGreen
import com.ivarna.wardenprotocol.ui.theme.WarningAmber

@Composable
fun SettingsScreen(
    musicEnabled: Boolean,
    sfxEnabled: Boolean,
    selectedModelId: String,
    modelOptions: List<NvidiaModelOption>,
    onToggleMusic: () -> Unit,
    onToggleSfx: () -> Unit,
    onSelectModel: (String) -> Unit,
    onBack: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedModel = remember(selectedModelId, modelOptions) {
        modelOptions.firstOrNull { it.id == selectedModelId } ?: NvidiaModelCatalog.resolve(selectedModelId)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF121414),
        topBar = { WardenTopBar(title = "SYSTEM_CONFIG_V1.0.4") },
        bottomBar = {
            WardenBottomNav(
                activeTab = WardenTab.SYSTEM,
                showSurface = false,
                onTabClick = { tab ->
                    when(tab) {
                        WardenTab.COMMAND -> onBack()
                        WardenTab.ARCHIVE -> onOpenHistory()
                        else -> {}
                    }
                }
            )
        }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            SettingsHeader()

            // Audio Configuration
            SettingsCategory(title = "AUDIO_MATRICES") {
                IndustrialToggle(
                    label = "MUSIC_FEED",
                    description = "Atmospheric background frequency modulation.",
                    isActive = musicEnabled,
                    onIcon = Icons.Filled.MusicNote,
                    offIcon = Icons.Filled.MusicOff,
                    onToggle = onToggleMusic
                )
                
                IndustrialToggle(
                    label = "SFX_REVERB",
                    description = "Tactical audio feedback and terminal alerts.",
                    isActive = sfxEnabled,
                    onIcon = Icons.Filled.VolumeUp,
                    offIcon = Icons.Filled.VolumeOff,
                    onToggle = onToggleSfx
                )
            }

            SettingsCategory(title = "FORECAST_MATRIX") {
                ModelSelectorCard(
                    selectedModel = selectedModel,
                    options = modelOptions,
                    onSelectModel = onSelectModel
                )
                DiagnosticRow("ACTIVE_MODEL", selectedModel.label.uppercase())
                DiagnosticRow(
                    "DEFAULT_PROFILE",
                    NvidiaModelCatalog.resolve(NvidiaModelCatalog.DEFAULT_MODEL_ID).label.uppercase()
                )
            }

            // System Diagnostics
            SettingsCategory(title = "CORE_TELEMETRY") {
                DiagnosticRow("OS_VERSION", "WARDEN_OS 1.0.4")
                DiagnosticRow("KERNEL_STATUS", "OPTIMAL")
                DiagnosticRow("HARDWARE_ID", "WP-8812-BUNKER")
                DiagnosticRow("ENCRYPTION", "AES-256-WARDEN")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer
            TerminalDecorFooter(
                lines = listOf(
                    "> ACCESSING SYSTEM CONFIGURATION...",
                    "> LOADING AUDIO DRIVERS...",
                    "> HARDWARE_CHECK: COMPLIANT",
                    "> SETTINGS_BUFFER_READY"
                )
            )
        }
    }
}

@Composable
private fun ModelSelectorCard(
    selectedModel: NvidiaModelOption,
    options: List<NvidiaModelOption>,
    onSelectModel: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1C1C))
                .border(1.dp, Color(0xFF333535).copy(alpha = 0.2f))
                .clickable { expanded = true }
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(WarningAmber.copy(alpha = 0.08f))
                    .border(1.dp, WarningAmber.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Memory,
                    contentDescription = null,
                    tint = WarningAmber,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "FORECAST MODEL",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = selectedModel.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = WarningAmber,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = selectedModel.detail,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                tint = WarningAmber,
                modifier = Modifier.size(28.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .background(Color(0xFF1A1C1C))
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = buildString {
                                    append(option.label)
                                    if (option.recommended) append("  [RECOMMENDED]")
                                },
                                color = if (option.id == selectedModel.id) WarningAmber else Color.White,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = option.detail,
                                color = TextSecondary,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    onClick = {
                        expanded = false
                        onSelectModel(option.id)
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = Color.White
                    )
                )
            }
        }
    }
}

@Composable
private fun SettingsHeader() {
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
                "SYSTEM PARAMETERS",
                style = MaterialTheme.typography.labelSmall,
                color = WarningAmber,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                "CONFIG_DECK",
                style = MaterialTheme.typography.headlineLarge,
                color = WarningAmber,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )
        }
    }
}

@Composable
private fun SettingsCategory(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = WarningAmber.copy(alpha = 0.5f),
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        content()
    }
}

@Composable
private fun IndustrialToggle(
    label: String,
    description: String,
    isActive: Boolean,
    onIcon: ImageVector,
    offIcon: ImageVector,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1C1C))
            .border(1.dp, Color(0xFF333535).copy(alpha = 0.2f))
            .clickable { onToggle() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(if (isActive) VaultGreen.copy(alpha = 0.1f) else Color(0xFF252727))
                .border(1.dp, if (isActive) VaultGreen else Color(0xFF444646)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isActive) onIcon else offIcon,
                contentDescription = null,
                tint = if (isActive) VaultGreen else Color(0xFF888A8A),
                modifier = Modifier.size(24.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = if (isActive) Color.White else Color(0xFF888A8A),
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 10.sp
            )
        }

        Surface(
            modifier = Modifier
                .width(52.dp)
                .height(28.dp),
            color = if (isActive) VaultGreen.copy(alpha = 0.2f) else Color(0xFF252727),
            border = BorderStroke(1.dp, if (isActive) VaultGreen else Color(0xFF444646))
        ) {
            Box(contentAlignment = if (isActive) Alignment.CenterEnd else Alignment.CenterStart) {
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxHeight()
                        .width(20.dp)
                        .background(if (isActive) VaultGreen else Color(0xFF666868))
                )
            }
        }
    }
}

@Composable
private fun DiagnosticRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontSize = 11.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = WarningAmber,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}
