package com.wardenprotocol.game.data.model

data class GameState(
    val survivors: Int = 1000,
    val yearsSinceWar: Int = 0,
    val surfaceLocationsScanned: Int = 0,
    val surfaceProbes: Int = 3,
    val vaultSystems: VaultSystems = VaultSystems(),
    val databases: Databases = Databases(),
    val phase: GamePhase = GamePhase.SURFACE_SCAN,
    val currentLocation: SurfaceLocation? = null,
    val currentEvent: GameEvent? = null,
    val lastEventOutcome: String? = null
)

enum class GamePhase {
    SURFACE_SCAN,
    RANDOM_EVENT,
    OPEN_VAULT,
    GAME_OVER
}

data class VaultSystems(
    val powerGrid: Int = 100,
    val foodStores: Int = 100,
    val medicalBay: Int = 100,
    val securitySystem: Int = 100,
    val constructionGear: Int = 100,
    val atmosphereScrubbers: Int = 100,
    val radiationScanner: Int = 100,
    val waterScanner: Int = 100,
    val agriculturalScanner: Int = 100,
    val structureScanner: Int = 100,
    val resourceScanner: Int = 100,
    val threatAssessment: Int = 100
) {
    fun applyDelta(system: String, delta: Int): VaultSystems {
        return when (system) {
            "powerGrid" -> copy(powerGrid = (powerGrid + delta).coerceIn(0, 100))
            "foodStores" -> copy(foodStores = (foodStores + delta).coerceIn(0, 100))
            "medicalBay" -> copy(medicalBay = (medicalBay + delta).coerceIn(0, 100))
            "securitySystem" -> copy(securitySystem = (securitySystem + delta).coerceIn(0, 100))
            "constructionGear" -> copy(constructionGear = (constructionGear + delta).coerceIn(0, 100))
            "atmosphereScrubbers" -> copy(atmosphereScrubbers = (atmosphereScrubbers + delta).coerceIn(0, 100))
            "radiationScanner" -> copy(radiationScanner = (radiationScanner + delta).coerceIn(0, 100))
            "waterScanner" -> copy(waterScanner = (waterScanner + delta).coerceIn(0, 100))
            "agriculturalScanner" -> copy(agriculturalScanner = (agriculturalScanner + delta).coerceIn(0, 100))
            "structureScanner" -> copy(structureScanner = (structureScanner + delta).coerceIn(0, 100))
            "resourceScanner" -> copy(resourceScanner = (resourceScanner + delta).coerceIn(0, 100))
            "threatAssessment" -> copy(threatAssessment = (threatAssessment + delta).coerceIn(0, 100))
            else -> this
        }
    }
}

data class Databases(
    val culturalArchive: Int = 100,
    val scientificArchive: Int = 100
) {
    fun applyDelta(database: String, delta: Int): Databases {
        return when (database) {
            "culturalArchive" -> copy(culturalArchive = (culturalArchive + delta).coerceIn(0, 100))
            "scientificArchive" -> copy(scientificArchive = (scientificArchive + delta).coerceIn(0, 100))
            else -> this
        }
    }
}
