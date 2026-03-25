package com.wardenprotocol.game.domain.engine

import com.wardenprotocol.game.data.model.*
import com.wardenprotocol.game.data.repository.EventRepository
import kotlin.random.Random

class GameEngine(private val eventRepository: EventRepository) {
    
    private val locationNameGenerators = mapOf(
        LocationType.RUINED_CITY to listOf(
            "Flooded Detroit", "Silent Chicago Ruins", "Ash-Covered Boston", "Broken Los Angeles",
            "Shattered New York", "Hollow Philadelphia", "Scorched Phoenix", "Frozen Minneapolis",
            "Crumbling Seattle", "Dead Miami", "Ghost Atlanta", "Buried Denver"
        ),
        LocationType.FOREST to listOf(
            "Ash-Grey Pinelands", "Recovering Oak Valley", "Mutant Redwood Grove", "Dead Birch Forest",
            "Twisted Maple Woods", "Blackened Cedar Stand", "Poisoned Willow Marsh", "Charred Sequoia Basin"
        ),
        LocationType.MILITARY_BASE to listOf(
            "Fort Zulu", "Outpost Kilo-7", "Fort Alpha Ruins", "Base Tango-9", "Fort Whiskey",
            "Outpost Delta", "Fort November", "Base Echo-3", "Fort Sierra", "Outpost Bravo-6"
        ),
        LocationType.FARMLAND to listOf(
            "Withered Corn Belt", "Salted Wheat Fields", "Toxic Vineyard", "Barren Pastures",
            "Irradiated Orchards", "Dead Soybean Plains", "Poisoned Rice Paddies", "Scorched Cropland"
        ),
        LocationType.UNDERGROUND_RIVER to listOf(
            "Subterranean Flow", "Hidden Aquifer", "Deep Water Vein", "Buried Stream",
            "Cavern Springs", "Underground Lake", "Limestone River", "Crystal Waters Below"
        ),
        LocationType.MOUNTAIN_PASS to listOf(
            "Frozen Summit Trail", "Rockslide Pass", "Avalanche Corridor", "High Ridge Path",
            "Shattered Peak Route", "Windswept Col", "Glacier Pass", "Stone Gate Mountains"
        ),
        LocationType.COASTAL_TOWN to listOf(
            "Drowned Harbor", "Tsunami-Swept Bay", "Radioactive Shoreline", "Flooded Pier Town",
            "Toxic Beach Settlement", "Submerged Port", "Poisoned Fishing Village", "Dead Seaside"
        ),
        LocationType.RESEARCH_FACILITY to listOf(
            "Blacksite Omega", "Research Station 7", "Laboratory Complex Alpha", "Science Outpost Theta",
            "Experimental Facility", "Biotech Center Ruins", "Weapons Lab Delta", "Particle Accelerator Site"
        )
    )
    
    fun generateSurfaceLocation(): SurfaceLocation {
        val type = LocationType.entries.random()
        val name = locationNameGenerators[type]?.random() ?: "Unknown Location"
        
        return SurfaceLocation(
            name = name,
            type = type,
            radiation = RadiationLevel.entries.random(),
            water = WaterAvailability.entries.random(),
            food = FoodPotential.entries.random(),
            shelter = ShelterQuality.entries.random(),
            resources = ResourceRichness.entries.random(),
            nativeHostility = Hostility.entries.random(),
            anomaly = if (Random.nextFloat() < 0.3f) SurfaceAnomaly.entries.random() else null
        )
    }
    
    fun generateEvent(state: GameState): GameEvent {
        val allEvents = eventRepository.getAllEvents()
        return allEvents.random()
    }
    
    fun applyEventChoice(state: GameState, choice: EventChoice): Pair<GameState, String> {
        var newState = state.copy(survivors = state.survivors + choice.outcome.survivorDelta)
        
        var systems = newState.vaultSystems
        choice.outcome.systemDeltas.forEach { (system, delta) ->
            systems = systems.applyDelta(system, delta)
        }
        newState = newState.copy(vaultSystems = systems)
        
        var databases = newState.databases
        choice.outcome.databaseDeltas.forEach { (database, delta) ->
            databases = databases.applyDelta(database, delta)
        }
        newState = newState.copy(databases = databases)
        
        newState = newState.copy(surfaceProbes = (newState.surfaceProbes + choice.outcome.probesDelta).coerceAtLeast(0))
        
        val narrativeText = if (choice.hiddenRisk > 0 && Random.nextFloat() < choice.hiddenRisk) {
            choice.outcome.narrativeText + " The situation was worse than anticipated."
        } else {
            choice.outcome.narrativeText
        }
        
        return Pair(newState, narrativeText)
    }
    
    fun applyPassiveDecay(state: GameState): GameState {
        val decayMultiplier = if (state.vaultSystems.powerGrid < 20) 2.0 else 1.0
        
        var systems = state.vaultSystems
        val systemsList = listOf(
            "powerGrid", "foodStores", "medicalBay", "securitySystem", "constructionGear",
            "atmosphereScrubbers", "radiationScanner", "waterScanner", "agriculturalScanner",
            "structureScanner", "resourceScanner", "threatAssessment"
        )
        
        systemsList.forEach { system ->
            val decay = -(Random.nextInt(2, 9) * decayMultiplier).toInt()
            systems = systems.applyDelta(system, decay)
        }
        
        var survivorLoss = 0
        if (state.vaultSystems.foodStores < 30) {
            survivorLoss += Random.nextInt(5, 21)
        }
        if (state.vaultSystems.atmosphereScrubbers < 20) {
            survivorLoss += Random.nextInt(3, 11)
        }
        
        return state.copy(
            vaultSystems = systems,
            survivors = (state.survivors - survivorLoss).coerceAtLeast(0),
            yearsSinceWar = state.yearsSinceWar + 1
        )
    }
    
    fun scoreOutcome(state: GameState, location: SurfaceLocation): Int {
        var score = state.survivors * 10
        score += location.radiation.scoreModifier
        score += location.water.scoreModifier
        score += location.food.scoreModifier
        score += location.shelter.scoreModifier
        score += location.resources.scoreModifier
        score += (state.vaultSystems.constructionGear / 100.0 * 300).toInt()
        score += (state.databases.culturalArchive / 100.0 * 200).toInt()
        score += (state.databases.scientificArchive / 100.0 * 200).toInt()
        score += location.nativeHostility.scoreModifier
        location.anomaly?.let { score += it.scoreModifier }
        return score.coerceAtLeast(0)
    }
    
    fun generateOutcomeNarrative(state: GameState, location: SurfaceLocation, score: Int): ColonyOutcome {
        val vaultNumber = Random.nextInt(1, 100)
        val cultural = state.databases.culturalArchive
        val scientific = state.databases.scientificArchive
        
        val societyType = when {
            cultural >= 70 && scientific >= 70 -> "enlightened democratic republic"
            cultural >= 70 && scientific < 40 -> "artistic but fragile theocracy"
            cultural < 40 && scientific >= 70 -> "efficient but cold technocracy"
            cultural < 40 && scientific < 40 -> "superstitious tribal society"
            else -> "pragmatic survivor collective"
        }
        
        val economy = when {
            location.resources == ResourceRichness.RICH && state.vaultSystems.constructionGear >= 60 -> "industrial prosperity"
            location.resources == ResourceRichness.RICH && state.vaultSystems.constructionGear < 40 -> "resource-dependent feudalism"
            location.resources == ResourceRichness.POOR && state.vaultSystems.constructionGear >= 60 -> "innovative recycling economy"
            else -> "subsistence survival"
        }
        
        val politics = when {
            state.vaultSystems.securitySystem >= 70 -> "stable meritocracy"
            state.vaultSystems.securitySystem < 30 && state.survivors > 500 -> "chaotic democracy"
            state.vaultSystems.securitySystem < 30 -> "warlord territory"
            else -> "council-led governance"
        }
        
        val environment = when {
            location.radiation == RadiationLevel.NONE && location.food == FoodPotential.FERTILE && location.water == WaterAvailability.ABUNDANT ->
                "harmony with the renewed land"
            location.radiation in listOf(RadiationLevel.HIGH, RadiationLevel.LETHAL) ->
                "underground civilization that never sees sunlight"
            else -> "constant struggle against the poisoned earth"
        }
        
        val settlementName = generateSettlementName(location)
        
        val classification = when {
            score >= 8000 -> "Thriving Civilization"
            score >= 5000 -> "Stable Colony"
            score >= 3000 -> "Struggling Settlement"
            score >= 1000 -> "Desperate Outpost"
            else -> "Doomed Remnant"
        }
        
        val narrative = "${state.survivors} survivors emerged from Vault $vaultNumber in Year ${state.yearsSinceWar} after the war. " +
                "They built $settlementName on ${location.name}. " +
                "Their society became a $societyType, sustained by $economy and governed through $politics. " +
                "They lived in $environment. " +
                "Of the thousand who entered the vault, ${1000 - state.survivors} never saw the sky again. " +
                "Their sacrifice will not be forgotten."
        
        return ColonyOutcome(
            score = score,
            classification = classification,
            narrative = narrative,
            settlementName = settlementName
        )
    }
    
    private fun generateSettlementName(location: SurfaceLocation): String {
        val prefixes = listOf("New", "Fort", "Haven", "Hope", "Last", "First", "Eden", "Sanctuary")
        val suffixes = listOf("Dawn", "Light", "Home", "Rest", "Peace", "Tomorrow", "Spring", "Harbor")
        return "${prefixes.random()} ${suffixes.random()}"
    }
}
