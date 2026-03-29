package com.wardenprotocol.game.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wardenprotocol.game.data.model.*
import com.wardenprotocol.game.data.repository.AiEndingForecastRepository
import com.wardenprotocol.game.data.repository.AiEndingForecastResult
import com.wardenprotocol.game.data.repository.HighScoreRepository
import com.wardenprotocol.game.domain.engine.GameEngine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.random.Random

sealed class GameAction {
    object StartNewGame : GameAction()
    object ContinueSearching : GameAction()
    object OpenVault : GameAction()
    object DeployProbe : GameAction()
    object ShowLeaderboard : GameAction()
    object ShowRunHistory : GameAction()
    object ShowSettings : GameAction()
    object GoToMainMenu : GameAction()
    data class OpenArchivedOutcome(val entry: RunRecord) : GameAction()
    data class SelectEventChoice(val choice: EventChoice) : GameAction()
    object DismissEventOutcome : GameAction()
    object ToggleMusic : GameAction()
    object ToggleSfx : GameAction()
}

sealed class UiState {
    object MainMenu : UiState()
    object Leaderboard : UiState()
    object RunHistory : UiState()
    object Settings : UiState()
    data class SurfaceScanning(val location: SurfaceLocation, val probeRevealed: Boolean = false) : UiState()
    data class RandomEvent(val event: GameEvent) : UiState()
    data class EventOutcome(val narrative: String) : UiState()
    data class GameOutcome(
        val outcome: ColonyOutcome,
        val isNewHighScore: Boolean,
        val analysisState: EndingForecastState = EndingForecastState.Fallback("Deterministic ending active.")
    ) : UiState()
}

sealed class EndingForecastState {
    data object Loading : EndingForecastState()
    data class Ready(val provider: String) : EndingForecastState()
    data class Fallback(val reason: String) : EndingForecastState()
}

class GameViewModel(
    private val gameEngine: GameEngine,
    private val highScoreRepository: HighScoreRepository,
    private val aiEndingForecastRepository: AiEndingForecastRepository
) : ViewModel() {
    
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()
    
    private val _uiState = MutableStateFlow<UiState>(UiState.MainMenu)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    val highScore: StateFlow<Int> = highScoreRepository.highScore
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val leaderboard: StateFlow<List<RunRecord>> = highScoreRepository.leaderboard
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val runHistory: StateFlow<List<RunRecord>> = highScoreRepository.runHistory
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    
    fun handleAction(action: GameAction) {
        when (action) {
            is GameAction.StartNewGame -> startNewGame()
            is GameAction.ContinueSearching -> continueSearching()
            is GameAction.OpenVault -> openVault()
            is GameAction.DeployProbe -> deployProbe()
            is GameAction.ShowLeaderboard -> _uiState.value = UiState.Leaderboard
            is GameAction.ShowRunHistory -> _uiState.value = UiState.RunHistory
            is GameAction.ShowSettings -> _uiState.value = UiState.Settings
            is GameAction.GoToMainMenu -> _uiState.value = UiState.MainMenu
            is GameAction.OpenArchivedOutcome -> openArchivedOutcome(action.entry)
            is GameAction.SelectEventChoice -> selectEventChoice(action.choice)
            is GameAction.DismissEventOutcome -> dismissEventOutcome()
            is GameAction.ToggleMusic -> _musicEnabled.value = !_musicEnabled.value
            is GameAction.ToggleSfx -> _sfxEnabled.value = !_sfxEnabled.value
        }
    }

    private val _musicEnabled = MutableStateFlow(true)
    val musicEnabled: StateFlow<Boolean> = _musicEnabled.asStateFlow()

    private val _sfxEnabled = MutableStateFlow(true)
    val sfxEnabled: StateFlow<Boolean> = _sfxEnabled.asStateFlow()
    
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
            val outcome = createGameOverOutcome(state)
            finishGame(state.copy(phase = GamePhase.GAME_OVER), outcome)
            return
        }
        
        val event = gameEngine.generateEvent(state)
        _gameState.value = state.copy(currentEvent = event, phase = GamePhase.RANDOM_EVENT)
        _uiState.value = UiState.RandomEvent(event)
    }
    
    private fun openVault() {
        var state = _gameState.value
        val location = state.currentLocation ?: return

        val travelDeaths = randomCasualties(
            state.survivors,
            location.travelProfile.minLossPercent / 100f,
            location.travelProfile.maxLossPercent / 100f
        )
        var immediateDeaths = travelDeaths

        when {
            location.radiation == RadiationLevel.LETHAL -> {
                immediateDeaths += randomCasualties(state.survivors, 0.35f, 0.55f)
            }
            location.radiation == RadiationLevel.HIGH -> {
                immediateDeaths += randomCasualties(state.survivors, 0.12f, 0.28f)
            }
        }
        
        if (location.water == WaterAvailability.NONE) {
            immediateDeaths += randomCasualties(state.survivors, 0.18f, 0.34f)
        }
        
        if (location.food == FoodPotential.BARREN && location.water == WaterAvailability.NONE) {
            immediateDeaths += randomCasualties(state.survivors, 0.08f, 0.18f)
        }
        
        when (location.nativeHostility) {
            Hostility.WARLORD -> {
                immediateDeaths += randomCasualties(state.survivors, 0.22f, 0.42f)
            }
            Hostility.WASTELAND_CULT -> {
                immediateDeaths += randomCasualties(state.survivors, 0.10f, 0.24f)
            }
            Hostility.BANDITS -> {
                immediateDeaths += randomCasualties(state.survivors, 0.04f, 0.14f)
            }
            else -> {}
        }
        
        if (location.shelter == ShelterQuality.NONE && location.radiation != RadiationLevel.NONE) {
            immediateDeaths += randomCasualties(state.survivors, 0.06f, 0.16f)
        }

        state = state.copy(survivors = (state.survivors - immediateDeaths).coerceAtLeast(0))
        _gameState.value = state

        val outcome = gameEngine.generateOutcomeNarrative(
            state = state,
            location = location,
            score = gameEngine.scoreOutcome(state, location),
            travelDeaths = travelDeaths
        )
        finishGame(state.copy(phase = GamePhase.OPEN_VAULT), outcome)
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
        if (newState.survivors <= 0) {
            val gameOver = createGameOverOutcome(newState).copy(
                narrative = "$narrative The encounter finished what was left of the vault. No recovery followed."
            )
            finishGame(newState.copy(phase = GamePhase.GAME_OVER), gameOver)
            return
        }
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

    private fun openArchivedOutcome(entry: RunRecord) {
        _uiState.value = UiState.GameOutcome(
            outcome = entry.toColonyOutcome(),
            isNewHighScore = false,
            analysisState = EndingForecastState.Fallback("Archived outcome loaded.")
        )
    }

    private fun finishGame(state: GameState, outcome: ColonyOutcome) {
        _gameState.value = state
        _uiState.value = UiState.GameOutcome(
            outcome = outcome,
            isNewHighScore = outcome.score > highScore.value,
            analysisState = EndingForecastState.Loading
        )

        viewModelScope.launch {
            val forecastResult = withTimeoutOrNull(60_000) {
                aiEndingForecastRepository.enhanceOutcome(outcome)
            } ?: AiEndingForecastResult.Fallback(
                reason = "Forecast engine timed out. Using deterministic ending log."
            )

            val finalOutcome = when (forecastResult) {
                is AiEndingForecastResult.Success -> forecastResult.outcome
                is AiEndingForecastResult.Fallback -> outcome
            }
            val finalIsNewHighScore = finalOutcome.score > highScore.value

            highScoreRepository.saveHighScore(finalOutcome.score)
            highScoreRepository.saveRun(finalOutcome)

            val finalAnalysisState = when (forecastResult) {
                is AiEndingForecastResult.Success -> EndingForecastState.Ready("NVIDIA NIM")
                is AiEndingForecastResult.Fallback -> EndingForecastState.Fallback(forecastResult.reason)
            }

            _uiState.value = UiState.GameOutcome(
                outcome = finalOutcome,
                isNewHighScore = finalIsNewHighScore,
                analysisState = finalAnalysisState
            )
        }
    }

    private fun createGameOverOutcome(state: GameState): ColonyOutcome {
        val locationName = state.currentLocation?.name ?: "Unknown Surface"
        return ColonyOutcome(
            score = 0,
            classification = "Total Extinction",
            narrative = "The last survivors died before the vault could open. The bunker endured, but the species did not. The command deck became a tomb lit only by failing systems.",
            settlementName = "Vault Memorial",
            detailedStats = OutcomeStats(
                survivors = 0,
                yearsSinceWar = state.yearsSinceWar,
                deaths = 1000,
                locationName = locationName,
                locationTypeName = state.currentLocation?.type?.name.orEmpty(),
                travelRoute = state.currentLocation?.travelProfile?.routeName ?: "Unavailable",
                travelTime = state.currentLocation?.travelProfile?.durationText ?: "Unavailable",
                travelRisk = state.currentLocation?.travelProfile?.riskLevel?.displayName ?: "Unknown",
                travelDeaths = 0,
                radiation = state.currentLocation?.radiation?.displayName ?: "Unknown",
                water = state.currentLocation?.water?.displayName ?: "Unknown",
                food = state.currentLocation?.food?.displayName ?: "Unknown",
                shelter = state.currentLocation?.shelter?.displayName ?: "Unknown",
                resources = state.currentLocation?.resources?.displayName ?: "Unknown",
                threats = state.currentLocation?.nativeHostility?.displayName ?: "Unknown",
                powerGrid = state.vaultSystems.powerGrid,
                foodStores = state.vaultSystems.foodStores,
                medicalBay = state.vaultSystems.medicalBay,
                securitySystem = state.vaultSystems.securitySystem,
                constructionGear = state.vaultSystems.constructionGear,
                atmosphereScrubbers = state.vaultSystems.atmosphereScrubbers,
                culturalArchive = state.databases.culturalArchive,
                scientificArchive = state.databases.scientificArchive
            )
        )
    }

    private fun randomCasualties(survivors: Int, minFraction: Float, maxFraction: Float): Int {
        val minDeaths = (survivors * minFraction).toInt()
        val maxDeaths = (survivors * maxFraction).toInt().coerceAtLeast(minDeaths)
        return if (maxDeaths <= minDeaths) minDeaths else Random.nextInt(minDeaths, maxDeaths + 1)
    }
}
