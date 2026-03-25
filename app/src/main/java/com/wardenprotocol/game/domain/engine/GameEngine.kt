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
        
        val (classification, tier) = when {
            score >= 10000 -> "Golden Age Civilization" to 5
            score >= 6000 -> "Thriving Society" to 4
            score >= 3000 -> "Stable Settlement" to 3
            score >= 1000 -> "Struggling Outpost" to 2
            else -> "Doomed Colony" to 1
        }
        
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
        
        val outcomeDetail = when (tier) {
            5 -> "Against all odds, humanity not only survived but flourished. ${state.survivors} souls emerged from Vault $vaultNumber after ${state.yearsSinceWar} years, carrying the torch of civilization. They founded $settlementName on ${location.name}, a beacon of hope in the wasteland. Their $societyType thrived through $economy, governed by $politics. They lived in $environment, and their descendants would remember this as humanity's second dawn."
            4 -> "${state.survivors} survivors emerged from Vault $vaultNumber in Year ${state.yearsSinceWar}. They built $settlementName on ${location.name}, establishing a $societyType sustained by $economy and governed through $politics. They lived in $environment. While challenges remained, they had secured humanity's future. Of the thousand who entered the vault, ${1000 - state.survivors} never saw the sky again, but their sacrifice was not in vain."
            3 -> "${state.survivors} survivors left Vault $vaultNumber after ${state.yearsSinceWar} years underground. $settlementName rose on ${location.name}, a modest $societyType struggling with $economy under $politics. They lived in $environment. Survival was uncertain, but they endured. ${1000 - state.survivors} died in the vault, and many more would follow in the harsh world above."
            2 -> "Only ${state.survivors} survivors emerged from Vault $vaultNumber after ${state.yearsSinceWar} desperate years. $settlementName on ${location.name} was less a settlement than a desperate camp. Their $societyType barely functioned, crippled by $economy and fractured $politics. They lived in $environment, each day a battle for survival. ${1000 - state.survivors} died in the vault. Most of the survivors would not see another decade."
            else -> "${state.survivors} survivors crawled from Vault $vaultNumber after ${state.yearsSinceWar} years, more dead than alive. What they called $settlementName on ${location.name} was a graveyard in waiting. Their $societyType was a cruel joke, their $economy nonexistent, their $politics mere anarchy. They lived in $environment, but 'lived' was too generous a word. ${1000 - state.survivors} died in the vault. The rest would follow within months. Humanity's light was extinguished."
        }
        
        return ColonyOutcome(
            score = score,
            classification = classification,
            narrative = outcomeDetail,
            settlementName = settlementName,
            detailedStats = OutcomeStats(
                survivors = state.survivors,
                yearsSinceWar = state.yearsSinceWar,
                deaths = 1000 - state.survivors,
                locationName = location.name,
                radiation = location.radiation.displayName,
                water = location.water.displayName,
                food = location.food.displayName,
                shelter = location.shelter.displayName,
                resources = location.resources.displayName,
                threats = location.nativeHostility.displayName,
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
    
    private fun generateSettlementName(location: SurfaceLocation): String {
        val prefixes = listOf("New", "Fort", "Haven", "Hope", "Last", "First", "Eden", "Sanctuary")
        val suffixes = listOf("Dawn", "Light", "Home", "Rest", "Peace", "Tomorrow", "Spring", "Harbor")
        return "${prefixes.random()} ${suffixes.random()}"
    }
}
