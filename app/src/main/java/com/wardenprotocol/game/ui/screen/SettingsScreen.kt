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
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.ivarna.wardenprotocol.R
import com.ivarna.wardenprotocol.data.model.NvidiaModelCatalog
import com.ivarna.wardenprotocol.data.model.NvidiaModelOption
import com.wardenprotocol.game.data.locale.LocaleApplier
import com.wardenprotocol.game.data.locale.LocaleOption
import com.wardenprotocol.game.data.locale.LocaleStore
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
    currentLocaleTag: String,
    onToggleMusic: () -> Unit,
    onToggleSfx: () -> Unit,
    onSelectModel: (String) -> Unit,
    onSelectLocale: (String) -> Unit,
    onBack: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    modifier: Modifier = Modifier
) {
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
    ) { padding ->
        SettingsContent(
            musicEnabled = musicEnabled,
            sfxEnabled = sfxEnabled,
            selectedModelId = selectedModelId,
            modelOptions = modelOptions,
            currentLocaleTag = currentLocaleTag,
            onToggleMusic = onToggleMusic,
            onToggleSfx = onToggleSfx,
            onSelectModel = onSelectModel,
            onSelectLocale = onSelectLocale,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
fun SettingsContent(
    musicEnabled: Boolean,
    sfxEnabled: Boolean,
    selectedModelId: String,
    modelOptions: List<NvidiaModelOption>,
    currentLocaleTag: String,
    onToggleMusic: () -> Unit,
    onToggleSfx: () -> Unit,
    onSelectModel: (String) -> Unit,
    onSelectLocale: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val selectedModel = remember(selectedModelId, modelOptions) {
        modelOptions.firstOrNull { it.id == selectedModelId } ?: NvidiaModelCatalog.resolve(selectedModelId)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsHeader()

        SettingsCategory(title = stringResource(R.string.settings_section_audio)) {
            IndustrialToggle(
                label = stringResource(R.string.settings_audio_music_label),
                description = stringResource(R.string.settings_audio_music_desc),
                isActive = musicEnabled,
                onIcon = Icons.Filled.MusicNote,
                offIcon = Icons.Filled.MusicOff,
                onToggle = onToggleMusic
            )

            IndustrialToggle(
                label = stringResource(R.string.settings_audio_sfx_label),
                description = stringResource(R.string.settings_audio_sfx_desc),
                isActive = sfxEnabled,
                onIcon = Icons.Filled.VolumeUp,
                offIcon = Icons.Filled.VolumeOff,
                onToggle = onToggleSfx
            )
        }

        SettingsCategory(title = stringResource(R.string.settings_section_forecast)) {
            ModelSelectorCard(
                selectedModel = selectedModel,
                options = modelOptions,
                onSelectModel = onSelectModel
            )
            DiagnosticRow(stringResource(R.string.settings_active_model), selectedModel.label.uppercase())
            DiagnosticRow(
                stringResource(R.string.settings_default_profile),
                NvidiaModelCatalog.resolve(NvidiaModelCatalog.DEFAULT_MODEL_ID).label.uppercase()
            )
        }

        SettingsCategory(title = stringResource(R.string.settings_section_locale)) {
            LanguageSelectorCard(
                currentTag = currentLocaleTag,
                onSelectLocale = onSelectLocale
            )
        }

        SettingsCategory(title = stringResource(R.string.settings_section_telemetry)) {
            DiagnosticRow(stringResource(R.string.settings_diag_os), stringResource(R.string.settings_diag_os_value))
            DiagnosticRow(stringResource(R.string.settings_diag_kernel), stringResource(R.string.settings_diag_kernel_value))
            DiagnosticRow(stringResource(R.string.settings_diag_hardware), stringResource(R.string.settings_diag_hardware_value))
            DiagnosticRow(stringResource(R.string.settings_diag_encryption), stringResource(R.string.settings_diag_encryption_value))
        }

        SettingsCategory(title = stringResource(R.string.settings_section_external)) {
            StarRepoCard()
        }

        Spacer(modifier = Modifier.height(16.dp))

        TerminalDecorFooter(
            lines = listOf(
                stringResource(R.string.settings_footer_line_1),
                stringResource(R.string.settings_footer_line_2),
                stringResource(R.string.settings_footer_line_3),
                stringResource(R.string.settings_footer_line_4)
            )
        )
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
                    text = stringResource(R.string.settings_forecast_label),
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
                                        if (option.recommended) append("  " + stringResource(R.string.recommended_tag))
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
                stringResource(R.string.settings_header_label),
                style = MaterialTheme.typography.labelSmall,
                color = WarningAmber,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                stringResource(R.string.settings_header_title),
                style = MaterialTheme.typography.headlineLarge,
                color = WarningAmber,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )
        }
    }
}

@Composable
private fun LanguageSelectorCard(
    currentTag: String,
    onSelectLocale: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = LocaleStore.supportedLocales
    val currentOpt = options.firstOrNull { it.tag == currentTag } ?: options.first()
    val context = LocalContext.current

    fun localeDisplayName(context: android.content.Context, opt: LocaleOption): String = when (opt.displayKey) {
        "system" -> context.getString(R.string.locale_system)
        "english" -> context.getString(R.string.locale_english)
        "russian" -> context.getString(R.string.locale_russian)
        else -> opt.displayKey
    }

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
                    .background(VaultGreen.copy(alpha = 0.08f))
                    .border(1.dp, VaultGreen.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Language,
                    contentDescription = null,
                    tint = VaultGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.settings_locale_label),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = localeDisplayName(context, currentOpt),
                    style = MaterialTheme.typography.labelLarge,
                    color = VaultGreen,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(R.string.settings_locale_desc),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                tint = VaultGreen,
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
                        Text(
                            text = localeDisplayName(context, option),
                            color = if (option.tag == currentTag) VaultGreen else Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = {
                        expanded = false
                        if (option.tag != currentTag) {
                            LocaleApplier.applyAndPersist(context, option.tag)
                            onSelectLocale(option.tag)
                            (context as? Activity)?.recreate()
                        }
                    },
                    colors = MenuDefaults.itemColors(textColor = Color.White)
                )
            }
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

@Composable
private fun StarRepoCard() {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1C1C))
            .border(1.dp, Color(0xFF333535).copy(alpha = 0.2f))
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/abhay-byte/warden-protocol"))
                context.startActivity(intent)
            }
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
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = WarningAmber,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.settings_repo_label),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = stringResource(R.string.settings_repo_desc),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 10.sp
            )
        }

        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = WarningAmber,
            modifier = Modifier.size(24.dp)
        )
    }
}
