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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
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

import com.ivarna.wardenprotocol.ui.screen.*

import com.ivarna.wardenprotocol.ui.theme.WardenProtocolTheme
import com.ivarna.wardenprotocol.ui.viewmodel.GameViewModel
import com.ivarna.wardenprotocol.ui.viewmodel.GameAction
import com.ivarna.wardenprotocol.ui.viewmodel.UiState
import com.ivarna.wardenprotocol.ui.viewmodel.HubTab
import com.ivarna.wardenprotocol.ui.viewmodel.EndingForecastState

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
            uiState !is UiState.MainHub || (uiState as UiState.MainHub).tab != HubTab.MENU -> {
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
                is UiState.MainHub -> {
                    MainHubScreen(
                        currentTab = state.tab,
                        highScore = highScore,
                        runCount = runHistory.size,
                        leaderboardEntries = leaderboard,
                        runHistory = runHistory,
                        musicEnabled = musicEnabled,
                        sfxEnabled = sfxEnabled,
                        selectedAiModel = selectedAiModel,
                        availableAiModels = viewModel.availableAiModels,
                        onTabSelected = { tab ->
                            when (tab) {
                                HubTab.MENU -> viewModel.handleAction(GameAction.GoToMainMenu)
                                HubTab.LEADERBOARD -> viewModel.handleAction(GameAction.ShowLeaderboard)
                                HubTab.HISTORY -> viewModel.handleAction(GameAction.ShowRunHistory)
                                HubTab.SETTINGS -> viewModel.handleAction(GameAction.ShowSettings)
                            }
                        },
                        onNewGame = {
                            audioController.play(UiSound.PRIMARY)
                            viewModel.handleAction(GameAction.StartNewGame)
                        },
                        onOpenRun = { entry ->
                            audioController.play(UiSound.SECONDARY)
                            viewModel.handleAction(GameAction.OpenArchivedOutcome(entry))
                        },
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
                        onSelectAiModel = { modelId ->
                            audioController.play(UiSound.SECONDARY, force = true)
                            viewModel.handleAction(GameAction.SelectAiModel(modelId))
                        },
                        onBack = {
                            audioController.play(UiSound.NAV)
                            viewModel.handleAction(GameAction.GoToMainMenu)
                        }
                    )
                }

                is UiState.PreRunBriefing -> {
                    MissionIntroScreen(
                        onContinue = {
                            audioController.play(UiSound.PRIMARY)
                            viewModel.handleAction(GameAction.ContinueFromBriefing)
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
                    if (state.analysisState is EndingForecastState.Loading) {
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
    is UiState.MainHub -> MusicScene.HUB
    UiState.PreRunBriefing -> MusicScene.SURFACE
    is UiState.SurfaceScanning -> MusicScene.SURFACE
    is UiState.RandomEvent -> MusicScene.EVENT
    is UiState.EventOutcome -> MusicScene.EVENT
    is UiState.GameOutcome -> MusicScene.OUTCOME
}

private fun screenKey(state: UiState): String = when (state) {
    is UiState.MainHub -> "hub"
    UiState.PreRunBriefing -> "briefing"
    is UiState.SurfaceScanning -> "surface"
    is UiState.RandomEvent -> "event"
    is UiState.EventOutcome -> "event_outcome"
    is UiState.GameOutcome -> "outcome"
}

private fun screenRank(state: UiState): Int = when (state) {
    is UiState.MainHub -> 0
    UiState.PreRunBriefing -> 2
    is UiState.SurfaceScanning -> 3
    is UiState.RandomEvent -> 4
    is UiState.EventOutcome -> 5
    is UiState.GameOutcome -> 6
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
    val Bg = Color(0xFF121414)
    val Panel = Color(0xFF1E2020)
    val PanelHigh = Color(0xFF282A2A)
    val Error = Color(0xFFFFB4AB)
    val OnSurface = Color(0xFFE2E2E2)
    val OnSurfaceMuted = Color(0xFFB9B19E)
    val Cyan = Color(0xFF71E6FF)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Bg.copy(alpha = 0.92f))
                .tacticalGrid(alpha = 0.08f)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Panel)
                    .tacticalGrid(alpha = 0.07f, horizontalSpacing = 4.dp, verticalSpacing = 6.dp)
                    .drawBehind {
                        drawLine(
                            color = Color.White.copy(alpha = 0.05f),
                            start = Offset.Zero,
                            end = Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = Color.White.copy(alpha = 0.05f),
                            start = Offset.Zero,
                            end = Offset(0f, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = Color.Black.copy(alpha = 0.3f),
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = Color.Black.copy(alpha = 0.3f),
                            start = Offset(size.width, 0f),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(PanelHigh)
                            .border(1.dp, Error.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null,
                            tint = Error
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "LEAVE THE BUNKER?",
                            style = MaterialTheme.typography.titleLarge,
                            color = OnSurface,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "The command feed will go dark if you exit now.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceMuted
                        )
                    }
                }

                // Warning broadcast panel
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0D0F0F))
                        .border(1.dp, Error.copy(alpha = 0.15f))
                        .tacticalGrid(alpha = 0.08f)
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Error)
                            .drawBehind {
                                drawCircle(
                                    color = Error.copy(alpha = 0.4f),
                                    radius = 6.dp.toPx(),
                                    center = center
                                )
                            }
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "COMMAND PROMPT",
                            style = MaterialTheme.typography.labelSmall,
                            color = Error,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "Do you want to quit?",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceMuted
                        )
                    }
                }

                Text(
                    text = "Press Stay to remain on the home deck, or Quit to shut down the current session.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceMuted
                )

                // Stay button (amber primary)
                QuitDialogPrimaryButton(
                    title = "STAY IN THE VAULT",
                    subtitle = "Return to the home page and keep command online.",
                    icon = Icons.Filled.Home,
                    onClick = onDismiss
                )

                // Quit button (dark panel with cyan accent)
                QuitDialogSecondaryButton(
                    title = "QUIT GAME",
                    subtitle = "Close the app and leave the bunker interface.",
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    accent = Cyan,
                    onClick = onQuit
                )
            }
        }
    }
}

@Composable
private fun QuitDialogPrimaryButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val shadowHeight = if (isPressed) 4.dp else 10.dp
    val translateY = if (isPressed) 4.dp else 0.dp
    val scale = if (isPressed) 0.98f else 1.0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .graphicsLayer {
                translationY = translateY.toPx()
                scaleX = scale
                scaleY = scale
            }
            .background(Color(0xFFFFB000))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .tacticalGrid(alpha = 0.18f, horizontalSpacing = 3.dp, verticalSpacing = 4.dp)
            .drawBehind {
                drawRect(
                    color = Color(0xFF9C6A00),
                    topLeft = Offset(0f, size.height),
                    size = Size(size.width, shadowHeight.toPx())
                )
            }
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF121414),
                modifier = Modifier.size(28.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF121414),
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF121414).copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun QuitDialogSecondaryButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit
) {
    val PanelHigh = Color(0xFF282A2A)
    val OnSurface = Color(0xFFE2E2E2)
    val OnSurfaceMuted = Color(0xFFB9B19E)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PanelHigh)
            .border(1.dp, accent.copy(alpha = 0.25f))
            .clickable(onClick = onClick)
            .tacticalGrid(alpha = 0.12f, horizontalSpacing = 4.dp, verticalSpacing = 6.dp)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceMuted
                )
            }
        }
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(20.dp)
        )
    }
}

private fun Modifier.tacticalGrid(
    alpha: Float = 0.15f,
    horizontalSpacing: androidx.compose.ui.unit.Dp = 3.dp,
    verticalSpacing: androidx.compose.ui.unit.Dp = 4.dp
): Modifier = this.drawBehind {
    val horizontalPx = horizontalSpacing.toPx()
    val verticalPx = verticalSpacing.toPx()
    var y = 0f
    while (y < size.height) {
        drawRect(
            color = Color.Black.copy(alpha = alpha),
            topLeft = Offset(0f, y),
            size = Size(size.width, 1.dp.toPx())
        )
        y += horizontalPx
    }
    var x = 0f
    while (x < size.width) {
        drawRect(
            color = Color.Black.copy(alpha = alpha * 0.25f),
            topLeft = Offset(x, 0f),
            size = Size(1.dp.toPx(), size.height)
        )
        x += verticalPx
    }
}
