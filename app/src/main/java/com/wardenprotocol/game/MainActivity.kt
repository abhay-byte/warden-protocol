package com.wardenprotocol.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.wardenprotocol.game.data.repository.EventRepository
import com.wardenprotocol.game.data.repository.HighScoreRepository
import com.wardenprotocol.game.domain.engine.GameEngine
import com.wardenprotocol.game.ui.screen.*
import com.wardenprotocol.game.ui.theme.WardenProtocolTheme
import com.wardenprotocol.game.ui.viewmodel.GameViewModel
import com.wardenprotocol.game.ui.viewmodel.GameAction
import com.wardenprotocol.game.ui.viewmodel.UiState

class MainActivity : ComponentActivity() {
    
    private lateinit var viewModel: GameViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        val eventRepository = EventRepository()
        val gameEngine = GameEngine(eventRepository)
        val highScoreRepository = HighScoreRepository(applicationContext)
        viewModel = GameViewModel(gameEngine, highScoreRepository)
        
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
                        onNewGame = { viewModel.handleAction(GameAction.StartNewGame) },
                        onOpenLeaderboard = { viewModel.handleAction(GameAction.ShowLeaderboard) },
                        onOpenHistory = { viewModel.handleAction(GameAction.ShowRunHistory) }
                    )
                }

                is UiState.Leaderboard -> {
                    LeaderboardScreen(
                        entries = leaderboard,
                        onBack = { viewModel.handleAction(GameAction.GoToMainMenu) },
                        onNewGame = { viewModel.handleAction(GameAction.StartNewGame) }
                    )
                }

                is UiState.RunHistory -> {
                    HistoryScreen(
                        entries = runHistory,
                        onBack = { viewModel.handleAction(GameAction.GoToMainMenu) },
                        onNewGame = { viewModel.handleAction(GameAction.StartNewGame) }
                    )
                }

                is UiState.SurfaceScanning -> {
                    GameScreen(
                        gameState = gameState,
                        location = state.location,
                        probeRevealed = state.probeRevealed,
                        onOpenVault = { viewModel.handleAction(GameAction.OpenVault) },
                        onContinueSearching = { viewModel.handleAction(GameAction.ContinueSearching) },
                        onDeployProbe = { viewModel.handleAction(GameAction.DeployProbe) }
                    )
                }

                is UiState.RandomEvent -> {
                    EventScreen(
                        event = state.event,
                        onChoiceSelected = { choice ->
                            viewModel.handleAction(GameAction.SelectEventChoice(choice))
                        }
                    )
                }

                is UiState.EventOutcome -> {
                    EventOutcomeScreen(
                        narrative = state.narrative,
                        onDismiss = { viewModel.handleAction(GameAction.DismissEventOutcome) }
                    )
                }

                is UiState.GameOutcome -> {
                    OutcomeScreen(
                        outcome = state.outcome,
                        isNewHighScore = state.isNewHighScore,
                        onPlayAgain = { viewModel.handleAction(GameAction.StartNewGame) },
                        onShowLeaderboard = { viewModel.handleAction(GameAction.ShowLeaderboard) },
                        onShowHistory = { viewModel.handleAction(GameAction.ShowRunHistory) }
                    )
                }
            }
        }
    }
}

private fun screenKey(state: UiState): String = when (state) {
    UiState.MainMenu -> "menu"
    UiState.Leaderboard -> "leaderboard"
    UiState.RunHistory -> "history"
    is UiState.SurfaceScanning -> "surface"
    is UiState.RandomEvent -> "event"
    is UiState.EventOutcome -> "event_outcome"
    is UiState.GameOutcome -> "outcome"
}

private fun screenRank(state: UiState): Int = when (state) {
    UiState.MainMenu -> 0
    UiState.Leaderboard -> 1
    UiState.RunHistory -> 1
    is UiState.SurfaceScanning -> 2
    is UiState.RandomEvent -> 3
    is UiState.EventOutcome -> 4
    is UiState.GameOutcome -> 5
}
