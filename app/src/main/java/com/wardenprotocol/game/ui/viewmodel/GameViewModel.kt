package com.wardenprotocol.game.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wardenprotocol.game.data.model.*
import com.wardenprotocol.game.data.repository.HighScoreRepository
import com.wardenprotocol.game.domain.engine.GameEngine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class GameAction {
    object StartNewGame : GameAction()
    object ContinueSearching : GameAction()
    object OpenVault : GameAction()
    object DeployProbe : GameAction()
    data class SelectEventChoice(val choice: EventChoice) : GameAction()
    object DismissEventOutcome : GameAction()
}

sealed class UiState {
    object MainMenu : UiState()
    data class SurfaceScanning(val location: SurfaceLocation, val probeRevealed: Boolean = false) : UiState()
    data class RandomEvent(val event: GameEvent) : UiState()
    data class EventOutcome(val narrative: String) : UiState()
    data class GameOutcome(val outcome: ColonyOutcome, val isNewHighScore: Boolean) : UiState()
    object GameOver : UiState()
}

class GameViewModel(
    private val gameEngine: GameEngine,
    private val highScoreRepository: HighScoreRepository
) : ViewModel() {
    
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()
    
    private val _uiState = MutableStateFlow<UiState>(UiState.MainMenu)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    val highScore: StateFlow<Int> = highScoreRepository.highScore
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    
    fun handleAction(action: GameAction) {
        when (action) {
            is GameAction.StartNewGame -> startNewGame()
            is GameAction.ContinueSearching -> continueSearching()
            is GameAction.OpenVault -> openVault()
            is GameAction.DeployProbe -> deployProbe()
            is GameAction.SelectEventChoice -> selectEventChoice(action.choice)
            is GameAction.DismissEventOutcome -> dismissEventOutcome()
        }
    }
    
    private fun startNewGame() {
        _gameState.value = GameState()
        val location = gameEngine.generateSurfaceLocation()
        _gameState.value = _gameState.value.copy(currentLocation = location)
        _uiState.value = UiState.SurfaceScanning(location)
    }
    
    private fun continueSearching() {
        var state = _gameState.value
        state = gameEngine.applyPassiveDecay(state)
        state = state.copy(surfaceLocationsScanned = state.surfaceLocationsScanned + 1)
        
        if (state.survivors <= 0) {
            _gameState.value = state.copy(phase = GamePhase.GAME_OVER)
            _uiState.value = UiState.GameOver
            return
        }
        
        val event = gameEngine.generateEvent(state)
        _gameState.value = state.copy(currentEvent = event, phase = GamePhase.RANDOM_EVENT)
        _uiState.value = UiState.RandomEvent(event)
    }
    
    private fun openVault() {
        val state = _gameState.value
        val location = state.currentLocation ?: return
        
        val score = gameEngine.scoreOutcome(state, location)
        val outcome = gameEngine.generateOutcomeNarrative(state, location, score)
        
        viewModelScope.launch {
            highScoreRepository.saveHighScore(score)
        }
        
        val isNewHighScore = score > highScore.value
        _gameState.value = state.copy(phase = GamePhase.OPEN_VAULT)
        _uiState.value = UiState.GameOutcome(outcome, isNewHighScore)
    }
    
    private fun deployProbe() {
        val state = _gameState.value
        if (state.surfaceProbes <= 0) return
        
        val location = state.currentLocation ?: return
        _gameState.value = state.copy(surfaceProbes = state.surfaceProbes - 1)
        _uiState.value = UiState.SurfaceScanning(location, probeRevealed = true)
    }
    
    private fun selectEventChoice(choice: EventChoice) {
        val (newState, narrative) = gameEngine.applyEventChoice(_gameState.value, choice)
        _gameState.value = newState.copy(lastEventOutcome = narrative)
        _uiState.value = UiState.EventOutcome(narrative)
    }
    
    private fun dismissEventOutcome() {
        val location = gameEngine.generateSurfaceLocation()
        _gameState.value = _gameState.value.copy(
            currentLocation = location,
            phase = GamePhase.SURFACE_SCAN
        )
        _uiState.value = UiState.SurfaceScanning(location)
    }
}
