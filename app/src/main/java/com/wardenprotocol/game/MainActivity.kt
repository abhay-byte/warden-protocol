package com.wardenprotocol.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
    
    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is UiState.MainMenu -> {
                MainMenuScreen(
                    highScore = highScore,
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
                    onPlayAgain = { viewModel.handleAction(GameAction.StartNewGame) }
                )
            }
            
            is UiState.GameOver -> {
                OutcomeScreen(
                    outcome = com.wardenprotocol.game.data.model.ColonyOutcome(
                        score = 0,
                        classification = "Total Extinction",
                        narrative = "The last survivor died in the vault. The lights went dark. Humanity's final chapter ended not with a bang, but with silence.",
                        settlementName = "None"
                    ),
                    isNewHighScore = false,
                    onPlayAgain = { viewModel.handleAction(GameAction.StartNewGame) }
                )
            }
        }
    }
}
