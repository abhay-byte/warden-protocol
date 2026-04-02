package com.ivarna.wardenprotocol

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ivarna.wardenprotocol.audio.MusicScene
import com.ivarna.wardenprotocol.audio.UiSound
import com.ivarna.wardenprotocol.audio.WardenAudioController
import com.ivarna.wardenprotocol.data.repository.EventRepository
import com.ivarna.wardenprotocol.data.repository.AiEndingForecastRepository
import com.ivarna.wardenprotocol.data.repository.HighScoreRepository
import com.ivarna.wardenprotocol.domain.engine.GameEngine
import com.ivarna.wardenprotocol.ui.component.ActionButton
import com.ivarna.wardenprotocol.ui.screen.*
import com.ivarna.wardenprotocol.ui.theme.BackgroundBlack
import com.ivarna.wardenprotocol.ui.theme.PanelStroke
import com.ivarna.wardenprotocol.ui.theme.SurfaceBlack
import com.ivarna.wardenprotocol.ui.theme.TextPrimary
import com.ivarna.wardenprotocol.ui.theme.TextSecondary
import com.ivarna.wardenprotocol.ui.theme.VaultGreen
import com.ivarna.wardenprotocol.ui.theme.WardenProtocolTheme
import com.ivarna.wardenprotocol.ui.theme.WarningAmber
import com.ivarna.wardenprotocol.ui.viewmodel.GameViewModel
import com.ivarna.wardenprotocol.ui.viewmodel.GameAction
import com.ivarna.wardenprotocol.ui.viewmodel.UiState

class MainActivity : ComponentActivity() {
    
    private lateinit var viewModel: GameViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        val eventRepository = EventRepository()
        val gameEngine = GameEngine(eventRepository)
        val highScoreRepository = HighScoreRepository(applicationContext)
        val aiEndingForecastRepository = AiEndingForecastRepository()
        viewModel = GameViewModel(gameEngine, highScoreRepository, aiEndingForecastRepository)
        
        setContent {
            WardenProtocolTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun GameApp(viewModel: GameViewModel) {
    val gameState by viewModel.gameState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val highScore by viewModel.highScore.collectAsState()
    val leaderboard by viewModel.leaderboard.collectAsState()
    val runHistory by viewModel.runHistory.collectAsState()
    val musicEnabled by viewModel.musicEnabled.collectAsState()
    val sfxEnabled by viewModel.sfxEnabled.collectAsState()
    val selectedAiModel by viewModel.selectedAiModel.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    val audioController = remember(context.applicationContext) {
        WardenAudioController(context.applicationContext)
    }
    var showQuitDialog by remember { mutableStateOf(false) }

    DisposableEffect(audioController) {
        onDispose { audioController.release() }
    }

    DisposableEffect(lifecycleOwner, audioController) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> audioController.onResume()
                Lifecycle.Event.ON_PAUSE -> audioController.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(musicEnabled) {
        audioController.setMusicEnabled(musicEnabled)
    }

    LaunchedEffect(sfxEnabled) {
        audioController.setSfxEnabled(sfxEnabled)
    }

    LaunchedEffect(uiState) {
        audioController.setScene(musicSceneFor(uiState))
    }

    BackHandler {
        when {
            showQuitDialog -> showQuitDialog = false
            uiState != UiState.MainMenu -> {
                audioController.play(UiSound.NAV)
                viewModel.handleAction(GameAction.GoToMainMenu)
            }
            else -> showQuitDialog = true
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        AnimatedContent(
            targetState = uiState,
            contentKey = { screenKey(it) },
            transitionSpec = {
                val forward = screenRank(targetState) >= screenRank(initialState)
                val slideIn = slideInHorizontally(
                    animationSpec = tween(420),
                    initialOffsetX = { fullWidth -> if (forward) fullWidth / 5 else -fullWidth / 5 }
                ) + fadeIn(animationSpec = tween(320))
                val slideOut = slideOutHorizontally(
                    animationSpec = tween(280),
                    targetOffsetX = { fullWidth -> if (forward) -fullWidth / 8 else fullWidth / 8 }
                ) + fadeOut(animationSpec = tween(220))
                slideIn togetherWith slideOut using SizeTransform(clip = false)
            },
            label = "screen_transition"
        ) { state ->
            when (state) {
                is UiState.MainMenu -> {
                    MainMenuScreen(
                        highScore = highScore,
                        runCount = runHistory.size,
                        leaderboardPreview = leaderboard,
                        lastRun = runHistory.firstOrNull(),
                        onNewGame = {
                            audioController.play(UiSound.PRIMARY)
                            viewModel.handleAction(GameAction.StartNewGame)
                        },
                        onOpenLeaderboard = {
                            audioController.play(UiSound.SECONDARY)
                            viewModel.handleAction(GameAction.ShowLeaderboard)
                        },
                        onOpenHistory = {
                            audioController.play(UiSound.SECONDARY)
                            viewModel.handleAction(GameAction.ShowRunHistory)
                        },
                        onOpenSettings = {
                            audioController.play(UiSound.NAV)
                            viewModel.handleAction(GameAction.ShowSettings)
                        }
                    )
                }

                is UiState.Settings -> {
                    SettingsScreen(
                        musicEnabled = musicEnabled,
                        sfxEnabled = sfxEnabled,
                        selectedModelId = selectedAiModel,
                        modelOptions = viewModel.availableAiModels,
                        onToggleMusic = {
                            val enabling = !musicEnabled
                            audioController.setMusicEnabled(enabling)
                            viewModel.handleAction(GameAction.ToggleMusic)
                            if (sfxEnabled) {
                                audioController.play(UiSound.TOGGLE, force = true)
                            }
                        },
                        onToggleSfx = {
                            val enabling = !sfxEnabled
                            audioController.setSfxEnabled(enabling)
                            viewModel.handleAction(GameAction.ToggleSfx)
                            if (enabling) {
                                audioController.play(UiSound.TOGGLE, force = true)
                            }
                        },
                        onSelectModel = { modelId ->
                            audioController.play(UiSound.SECONDARY, force = true)
                            viewModel.handleAction(GameAction.SelectAiModel(modelId))
                        },
                        onBack = {
                            audioController.play(UiSound.NAV)
                            viewModel.handleAction(GameAction.GoToMainMenu)
                        },
                        onOpenHistory = {
                            audioController.play(UiSound.SECONDARY)
                            viewModel.handleAction(GameAction.ShowRunHistory)
                        },
                        onOpenLeaderboard = {
                            audioController.play(UiSound.SECONDARY)
                            viewModel.handleAction(GameAction.ShowLeaderboard)
                        }
                    )
                }

                is UiState.Leaderboard -> {
                    LeaderboardScreen(
                        entries = leaderboard,
                        onBack = {
                            audioController.play(UiSound.NAV)
                            viewModel.handleAction(GameAction.GoToMainMenu)
                        },
                        onNewGame = {
                            audioController.play(UiSound.PRIMARY)
                            viewModel.handleAction(GameAction.StartNewGame)
                        },
                        onOpenSettings = {
                            audioController.play(UiSound.NAV)
                            viewModel.handleAction(GameAction.ShowSettings)
                        },
                        onOpenHistory = {
                            audioController.play(UiSound.SECONDARY)
                            viewModel.handleAction(GameAction.ShowRunHistory)
                        },
                        onOpenRun = { entry ->
                            audioController.play(UiSound.SECONDARY)
                            viewModel.handleAction(GameAction.OpenArchivedOutcome(entry))
                        }
                    )
                }

                is UiState.RunHistory -> {
                    HistoryScreen(
                        entries = runHistory,
                        onBack = {
                            audioController.play(UiSound.NAV)
                            viewModel.handleAction(GameAction.GoToMainMenu)
                        },
                        onOpenSettings = {
                            audioController.play(UiSound.NAV)
                            viewModel.handleAction(GameAction.ShowSettings)
                        },
                        onOpenLeaderboard = {
                            audioController.play(UiSound.SECONDARY)
                            viewModel.handleAction(GameAction.ShowLeaderboard)
                        },
                        onOpenRun = { entry ->
                            audioController.play(UiSound.SECONDARY)
                            viewModel.handleAction(GameAction.OpenArchivedOutcome(entry))
                        }
                    )
                }

                is UiState.SurfaceScanning -> {
                    GameScreen(
                        gameState = gameState,
                        location = state.location,
                        probeRevealed = state.probeRevealed,
                        onOpenVault = {
                            audioController.play(UiSound.DANGER)
                            viewModel.handleAction(GameAction.OpenVault)
                        },
                        onContinueSearching = {
                            audioController.play(UiSound.PRIMARY)
                            viewModel.handleAction(GameAction.ContinueSearching)
                        },
                        onDeployProbe = {
                            audioController.play(UiSound.SECONDARY)
                            viewModel.handleAction(GameAction.DeployProbe)
                        }
                    )
                }

                is UiState.RandomEvent -> {
                    EventScreen(
                        event = state.event,
                        onChoiceSelected = { choice ->
                            audioController.play(UiSound.PRIMARY)
                            viewModel.handleAction(GameAction.SelectEventChoice(choice))
                        }
                    )
                }

                is UiState.EventOutcome -> {
                    EventOutcomeScreen(
                        narrative = state.narrative,
                        onDismiss = {
                            audioController.play(UiSound.NAV)
                            viewModel.handleAction(GameAction.DismissEventOutcome)
                        }
                    )
                }

                is UiState.GameOutcome -> {
                    if (state.analysisState is com.ivarna.wardenprotocol.ui.viewmodel.EndingForecastState.Loading) {
                        EndingProcessingScreen(outcome = state.outcome)
                    } else {
                        OutcomeScreen(
                            outcome = state.outcome,
                            isNewHighScore = state.isNewHighScore,
                            onPlayAgain = {
                                audioController.play(UiSound.PRIMARY)
                                viewModel.handleAction(GameAction.StartNewGame)
                            },
                            onShowLeaderboard = {
                                audioController.play(UiSound.SECONDARY)
                                viewModel.handleAction(GameAction.ShowLeaderboard)
                            },
                            onShowHistory = {
                                audioController.play(UiSound.SECONDARY)
                                viewModel.handleAction(GameAction.ShowRunHistory)
                            },
                            onGoToMainMenu = {
                                audioController.play(UiSound.NAV)
                                viewModel.handleAction(GameAction.GoToMainMenu)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showQuitDialog) {
        QuitGameDialog(
            onDismiss = {
                audioController.play(UiSound.NAV)
                showQuitDialog = false
            },
            onQuit = {
                audioController.play(UiSound.DANGER, force = true)
                context.findActivity()?.finish()
            }
        )
    }
}

private fun musicSceneFor(state: UiState): MusicScene = when (state) {
    UiState.MainMenu -> MusicScene.HUB
    UiState.Settings -> MusicScene.HUB
    UiState.Leaderboard -> MusicScene.HUB
    UiState.RunHistory -> MusicScene.HUB
    is UiState.SurfaceScanning -> MusicScene.SURFACE
    is UiState.RandomEvent -> MusicScene.EVENT
    is UiState.EventOutcome -> MusicScene.EVENT
    is UiState.GameOutcome -> MusicScene.OUTCOME
}

private fun screenKey(state: UiState): String = when (state) {
    UiState.MainMenu -> "menu"
    UiState.Leaderboard -> "leaderboard"
    UiState.RunHistory -> "history"
    UiState.Settings -> "settings"
    is UiState.SurfaceScanning -> "surface"
    is UiState.RandomEvent -> "event"
    is UiState.EventOutcome -> "event_outcome"
    is UiState.GameOutcome -> "outcome"
}

private fun screenRank(state: UiState): Int = when (state) {
    UiState.MainMenu -> 0
    UiState.Leaderboard -> 1
    UiState.RunHistory -> 1
    UiState.Settings -> 1
    is UiState.SurfaceScanning -> 2
    is UiState.RandomEvent -> 3
    is UiState.EventOutcome -> 4
    is UiState.GameOutcome -> 5
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
private fun QuitGameDialog(
    onDismiss: () -> Unit,
    onQuit: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundBlack.copy(alpha = 0.82f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceBlack.copy(alpha = 0.98f))
                    .border(1.dp, PanelStroke)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1A1C1C))
                        .padding(start = 24.dp, top = 12.dp, bottom = 12.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 4.dp, height = 48.dp)
                            .background(WarningAmber)
                    )
                    Column {
                        Text(
                            "EXIT PROTOCOL",
                            style = MaterialTheme.typography.labelSmall,
                            color = WarningAmber,
                            fontSize = 10.sp,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            "COMMAND_ABORT",
                            style = MaterialTheme.typography.headlineLarge,
                            color = WarningAmber,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1).sp
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1A1C1C))
                            .border(1.dp, Color(0xFF333535).copy(alpha = 0.2f))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(WarningAmber.copy(alpha = 0.10f))
                                .border(1.dp, WarningAmber.copy(alpha = 0.55f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = null,
                                tint = WarningAmber
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "LEAVE THE BUNKER",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                text = "The command feed will go dark if you terminate the current session.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    QuitDialogRow(label = "EXIT_MODE", value = "FULL SHUTDOWN")
                    QuitDialogRow(label = "ACTIVE_SESSION", value = "COMMAND DECK ONLINE")
                    QuitDialogRow(label = "FAILSAFE", value = "RETURN TO HUB AVAILABLE")

                    Text(
                        text = "Stay keeps the current bunker session alive. Quit closes the app immediately.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )

                    ActionButton(
                        title = "Stay In The Vault",
                        subtitle = "Return to the home deck and keep the command feed online.",
                        icon = Icons.Filled.Home,
                        accent = VaultGreen,
                        onClick = onDismiss
                    )

                    ActionButton(
                        title = "Execute Shutdown",
                        subtitle = "Close the application and terminate the bunker session.",
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        accent = WarningAmber,
                        onClick = onQuit
                    )
                }
            }
        }
    }
}

@Composable
private fun QuitDialogRow(label: String, value: String) {
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
            fontSize = 11.sp
        )
    }
}
