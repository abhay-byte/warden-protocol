package com.ivarna.wardenprotocol.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.ivarna.wardenprotocol.R
import com.ivarna.wardenprotocol.data.model.NvidiaModelOption
import com.ivarna.wardenprotocol.data.model.RunRecord
import com.ivarna.wardenprotocol.ui.component.WardenBottomNav
import com.ivarna.wardenprotocol.ui.component.WardenTab
import com.ivarna.wardenprotocol.ui.component.WardenTopBar
import com.ivarna.wardenprotocol.ui.viewmodel.HubTab
import com.ivarna.wardenprotocol.ui.viewmodel.UiState

@Composable
fun MainHubScreen(
    currentTab: HubTab,
    highScore: Int,
    runCount: Int,
    leaderboardEntries: List<RunRecord>,
    runHistory: List<RunRecord>,
    musicEnabled: Boolean,
    sfxEnabled: Boolean,
    selectedAiModel: String,
    currentLocaleTag: String,
    availableAiModels: List<NvidiaModelOption>,
    onTabSelected: (HubTab) -> Unit,
    onNewGame: () -> Unit,
    onOpenRun: (RunRecord) -> Unit,
    onToggleMusic: () -> Unit,
    onToggleSfx: () -> Unit,
    onSelectAiModel: (String) -> Unit,
    onSelectLocale: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF121414),
        topBar = {
            WardenTopBar(
                title = when (currentTab) {
                    HubTab.MENU -> stringResource(R.string.top_bar_command_deck)
                    HubTab.LEADERBOARD -> stringResource(R.string.top_bar_global_archive)
                    HubTab.HISTORY -> stringResource(R.string.top_bar_archive_logs)
                    HubTab.SETTINGS -> stringResource(R.string.top_bar_system_config)
                }
            )
        },
        bottomBar = {
            WardenBottomNav(
                activeTab = when (currentTab) {
                    HubTab.MENU -> WardenTab.COMMAND
                    HubTab.LEADERBOARD -> WardenTab.ARCHIVE
                    HubTab.HISTORY -> WardenTab.ARCHIVE
                    HubTab.SETTINGS -> WardenTab.SYSTEM
                },
                showSurface = false,
                onTabClick = { tab ->
                    when (tab) {
                        WardenTab.COMMAND -> onTabSelected(HubTab.MENU)
                        WardenTab.ARCHIVE -> {
                            if (currentTab == HubTab.HISTORY) onTabSelected(HubTab.LEADERBOARD)
                            else onTabSelected(HubTab.HISTORY)
                        }
                        WardenTab.SYSTEM -> onTabSelected(HubTab.SETTINGS)
                        else -> {}
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith 
                    fadeOut(animationSpec = tween(300)) using SizeTransform(clip = false)
                },
                label = "hub_content"
            ) { tab ->
                when (tab) {
                    HubTab.MENU -> {
                        MainMenuContent(
                            highScore = highScore,
                            runCount = runCount,
                            leaderboardPreview = leaderboardEntries,
                            lastRun = runHistory.firstOrNull(),
                            onNewGame = onNewGame,
                            onOpenLeaderboard = { onTabSelected(HubTab.LEADERBOARD) },
                            onOpenHistory = { onTabSelected(HubTab.HISTORY) }
                        )
                    }
                    HubTab.LEADERBOARD -> {
                        LeaderboardContent(
                            entries = leaderboardEntries,
                            onNewGame = onNewGame,
                            onOpenRun = onOpenRun
                        )
                    }
                    HubTab.HISTORY -> {
                        HistoryContent(
                            entries = runHistory,
                            onOpenRun = onOpenRun
                        )
                    }
                    HubTab.SETTINGS -> {
                        SettingsContent(
                            musicEnabled = musicEnabled,
                            sfxEnabled = sfxEnabled,
                            selectedModelId = selectedAiModel,
                            modelOptions = availableAiModels,
                            currentLocaleTag = currentLocaleTag,
                            onToggleMusic = onToggleMusic,
                            onToggleSfx = onToggleSfx,
                            onSelectModel = onSelectAiModel,
                            onSelectLocale = onSelectLocale
                        )
                    }
                }
            }
        }
    }
}
