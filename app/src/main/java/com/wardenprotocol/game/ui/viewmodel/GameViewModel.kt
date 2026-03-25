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
        var state = _gameState.value
        val location = state.currentLocation ?: return
        
        // Apply immediate location consequences
        var immediateDeaths = 0
        
        when {
            location.radiation == RadiationLevel.LETHAL -> {
                immediateDeaths += (state.survivors * 0.5).toInt() // 50% die immediately
            }
            location.radiation == RadiationLevel.HIGH -> {
                immediateDeaths += (state.survivors * 0.2).toInt() // 20% die immediately
            }
        }
        
        if (location.water == WaterAvailability.NONE) {
            immediateDeaths += (state.survivors * 0.3).toInt() // 30% die from dehydration
        }
        
        if (location.food == FoodPotential.BARREN && location.water == WaterAvailability.NONE) {
            immediateDeaths += (state.survivors * 0.2).toInt() // Additional 20% for combined effect
        }
        
        when (location.nativeHostility) {
            Hostility.WARLORD -> {
                immediateDeaths += (state.survivors * 0.4).toInt() // 40% killed in attack
            }
            Hostility.WASTELAND_CULT -> {
                immediateDeaths += (state.survivors * 0.2).toInt() // 20% killed
            }
            Hostility.BANDITS -> {
                immediateDeaths += (state.survivors * 0.1).toInt() // 10% killed
            }
            else -> {}
        }
        
        if (location.shelter == ShelterQuality.NONE && location.radiation != RadiationLevel.NONE) {
            immediateDeaths += (state.survivors * 0.15).toInt() // 15% die from exposure
        }
        
        // Apply deaths
        state = state.copy(survivors = (state.survivors - immediateDeaths).coerceAtLeast(0))
        _gameState.value = state
        
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
        
        val probeData = generateProbeData(location)
        val updatedLocation = location.copy(probeData = probeData)
        
        _gameState.value = state.copy(
            surfaceProbes = state.surfaceProbes - 1,
            currentLocation = updatedLocation
        )
        _uiState.value = UiState.SurfaceScanning(updatedLocation, probeRevealed = true)
    }
    
    private fun generateProbeData(location: SurfaceLocation): ProbeData {
        val hiddenResources = when {
            location.resources == ResourceRichness.RICH -> "Underground mineral deposits detected. Valuable salvage potential."
            location.resources == ResourceRichness.MODERATE -> "Scattered resources. Moderate salvage opportunities."
            else -> "Depleted area. Minimal resources available."
        }
        
        val structuralIntegrity = when {
            location.shelter == ShelterQuality.EXCELLENT -> "Structures intact. Immediate habitation possible."
            location.shelter == ShelterQuality.GOOD -> "Structures damaged but repairable. Shelter available."
            location.shelter == ShelterQuality.POOR -> "Structures heavily damaged. Extensive repairs needed."
            else -> "No viable structures. Must build from scratch."
        }
        
        val soilQuality = when {
            location.food == FoodPotential.FERTILE -> "Rich topsoil detected. Agriculture highly viable."
            location.food == FoodPotential.MARGINAL -> "Contaminated soil. Limited agriculture possible with treatment."
            else -> "Dead soil. Hydroponics or imports required for food."
        }
        
        val scoreEstimate = when {
            location.radiation == RadiationLevel.NONE && location.water == WaterAvailability.ABUNDANT && location.food == FoodPotential.FERTILE -> "EXCELLENT LOCATION - High survival probability"
            location.radiation in listOf(RadiationLevel.HIGH, RadiationLevel.LETHAL) -> "DANGEROUS LOCATION - Low survival probability"
            location.water == WaterAvailability.NONE -> "CRITICAL ISSUE - Water scarcity will be fatal"
            else -> "VIABLE LOCATION - Moderate survival probability"
        }
        
        return ProbeData(
            hiddenResources = hiddenResources,
            structuralIntegrity = structuralIntegrity,
            soilQuality = soilQuality,
            recommendation = scoreEstimate
        )
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
