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
            val decay = -(Random.nextInt(3, 12) * decayMultiplier).toInt()
            systems = systems.applyDelta(system, decay)
        }
        
        var survivorLoss = 0
        
        // Food shortage deaths
        if (state.vaultSystems.foodStores < 30) {
            survivorLoss += Random.nextInt(10, 30)
        }
        if (state.vaultSystems.foodStores < 15) {
            survivorLoss += Random.nextInt(20, 40)
        }
        
        // Atmosphere failure deaths
        if (state.vaultSystems.atmosphereScrubbers < 20) {
            survivorLoss += Random.nextInt(5, 20)
        }
        if (state.vaultSystems.atmosphereScrubbers < 10) {
            survivorLoss += Random.nextInt(15, 35)
        }
        
        // Medical bay failure deaths
        if (state.vaultSystems.medicalBay < 20) {
            survivorLoss += Random.nextInt(3, 15)
        }
        
        // Power grid critical failure
        if (state.vaultSystems.powerGrid < 10) {
            survivorLoss += Random.nextInt(10, 25)
        }
        
        // Random deaths from time
        survivorLoss += Random.nextInt(1, 5)
        
        return state.copy(
            vaultSystems = systems,
            survivors = (state.survivors - survivorLoss).coerceAtLeast(0),
            yearsSinceWar = state.yearsSinceWar + 1
        )
    }
    
    fun scoreOutcome(state: GameState, location: SurfaceLocation): Int {
        if (state.survivors <= 0) return 0
        
        var score = state.survivors * 5  // Reduced from 10
        
        // Location penalties/bonuses
        score += location.radiation.scoreModifier
        score += location.water.scoreModifier
        score += location.food.scoreModifier
        score += location.shelter.scoreModifier
        score += location.resources.scoreModifier
        score += location.nativeHostility.scoreModifier
        
        // Vault systems (reduced weights)
        score += (state.vaultSystems.constructionGear / 100.0 * 150).toInt()
        score += (state.vaultSystems.powerGrid / 100.0 * 100).toInt()
        score += (state.vaultSystems.foodStores / 100.0 * 100).toInt()
        score += (state.vaultSystems.medicalBay / 100.0 * 80).toInt()
        score += (state.vaultSystems.securitySystem / 100.0 * 80).toInt()
        score += (state.vaultSystems.atmosphereScrubbers / 100.0 * 80).toInt()
        
        // Databases (reduced)
        score += (state.databases.culturalArchive / 100.0 * 100).toInt()
        score += (state.databases.scientificArchive / 100.0 * 100).toInt()
        
        // Heavy penalties
        score -= (state.yearsSinceWar * 100)  // Increased from 50
        score -= ((1000 - state.survivors) * 3)  // Death penalty
        
        // System damage penalties
        val avgSystemHealth = (state.vaultSystems.powerGrid + state.vaultSystems.foodStores + 
                               state.vaultSystems.medicalBay + state.vaultSystems.securitySystem +
                               state.vaultSystems.constructionGear + state.vaultSystems.atmosphereScrubbers) / 6
        if (avgSystemHealth < 50) {
            score -= (50 - avgSystemHealth) * 20
        }
        
        // Database damage penalty
        val avgDatabaseHealth = (state.databases.culturalArchive + state.databases.scientificArchive) / 2
        if (avgDatabaseHealth < 50) {
            score -= (50 - avgDatabaseHealth) * 15
        }
        
        // Anomaly bonus
        location.anomaly?.let { score += it.scoreModifier }
        
        return score.coerceAtLeast(0)
    }
    
    fun generateOutcomeNarrative(state: GameState, location: SurfaceLocation, score: Int): ColonyOutcome {
        val vaultNumber = Random.nextInt(1, 100)
        val cultural = state.databases.culturalArchive
        val scientific = state.databases.scientificArchive
        
        val (classification, tier) = when {
            score >= 8000 -> {
                when {
                    state.survivors > 900 -> "Paradise Reclaimed" to 5
                    state.databases.culturalArchive > 80 && state.databases.scientificArchive > 80 -> "Renaissance of Humanity" to 5
                    location.anomaly != null -> "Blessed Settlement" to 5
                    else -> "Golden Age Civilization" to 5
                }
            }
            score >= 5000 -> {
                when {
                    state.survivors > 700 -> "Prosperous Colony" to 4
                    state.vaultSystems.constructionGear > 70 -> "Industrial Haven" to 4
                    location.water == WaterAvailability.ABUNDANT -> "Oasis of Hope" to 4
                    else -> "Thriving Society" to 4
                }
            }
            score >= 2500 -> {
                when {
                    state.survivors < 400 -> "Fragile Foothold" to 3
                    state.yearsSinceWar > 15 -> "Weary Survivors" to 3
                    location.radiation != RadiationLevel.NONE -> "Irradiated Settlement" to 3
                    else -> "Stable Settlement" to 3
                }
            }
            score >= 1000 -> {
                when {
                    state.survivors < 200 -> "Dying Ember" to 2
                    location.nativeHostility != Hostility.NONE -> "Besieged Outpost" to 2
                    location.water == WaterAvailability.NONE -> "Thirst-Cursed Camp" to 2
                    state.vaultSystems.foodStores < 20 -> "Starving Remnant" to 2
                    else -> "Struggling Outpost" to 2
                }
            }
            else -> {
                when {
                    state.survivors <= 0 -> "Total Extinction" to 1
                    state.survivors < 50 -> "Final Gasps" to 1
                    location.radiation == RadiationLevel.LETHAL -> "Radiation Tomb" to 1
                    location.nativeHostility == Hostility.WARLORD -> "Conquered and Enslaved" to 1
                    else -> "Doomed Colony" to 1
                }
            }
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
            5 -> {
                val specificEnding = when (classification) {
                    "Paradise Reclaimed" -> " With over ${state.survivors} survivors, they had the numbers to build a true civilization. Within a generation, ${location.name} transformed from wasteland to wonderland. Schools, hospitals, farms—all the hallmarks of the old world, reborn and improved."
                    "Renaissance of Humanity" -> " They emerged not just with bodies, but with minds intact. The cultural and scientific archives were nearly complete. Artists painted murals on vault walls. Scientists developed radiation treatments. Philosophers debated the meaning of their survival. Humanity's soul had survived."
                    "Blessed Settlement" -> " The ${location.anomaly?.displayName} changed everything. What seemed like a curse became their greatest blessing. It provided resources, knowledge, or protection that made survival not just possible, but inevitable."
                    else -> " The location was perfect—clean air, abundant water, fertile soil. Humanity had been given a second chance, and they seized it."
                }
                "Against all odds, humanity not only survived but flourished. ${state.survivors} souls emerged from Vault $vaultNumber after ${state.yearsSinceWar} years, carrying the torch of civilization. They founded $settlementName on ${location.name}, a beacon of hope in the wasteland. Their $societyType thrived through $economy, governed by $politics. They lived in $environment.$specificEnding Their descendants would remember this as humanity's second dawn."
            }
            4 -> {
                val specificEnding = when (classification) {
                    "Prosperous Colony" -> " ${state.survivors} survivors was more than enough. They divided into specialized roles—farmers, builders, guards, teachers. Within five years, they had a functioning town with trade routes to other survivor groups."
                    "Industrial Haven" -> " The construction gear at ${state.vaultSystems.constructionGear}% capacity allowed them to build rapidly. Factories rose from ruins. They manufactured tools, weapons, and eventually luxuries. Other settlements came to trade."
                    "Oasis of Hope" -> " Abundant water was their salvation. While others fought over drops, they had rivers. They became a destination for desperate wanderers, growing their population and influence."
                    else -> " Despite the challenges of ${location.name}, they overcame every obstacle through ingenuity and determination."
                }
                "${state.survivors} survivors emerged from Vault $vaultNumber in Year ${state.yearsSinceWar}. They built $settlementName on ${location.name}, establishing a $societyType sustained by $economy and governed through $politics. They lived in $environment.$specificEnding Of the thousand who entered the vault, ${1000 - state.survivors} never saw the sky again, but their sacrifice was not in vain."
            }
            3 -> {
                val specificEnding = when (classification) {
                    "Fragile Foothold" -> " With only ${state.survivors} survivors, every death was a catastrophe. They couldn't afford to lose anyone. Reproduction became a duty. Children were raised communally. They survived, but barely."
                    "Weary Survivors" -> " ${state.yearsSinceWar} years underground had broken something in them. They emerged old, tired, and traumatized. The young had never seen the sun. The old couldn't remember it. They built $settlementName, but their hearts remained in the vault."
                    "Irradiated Settlement" -> " The ${location.radiation.displayName} radiation was inescapable. They wore protective gear constantly. Cancers were common. Mutations appeared in the second generation. They survived, but at what cost?"
                    else -> " The harsh conditions tested them daily."
                }
                "${state.survivors} survivors left Vault $vaultNumber after ${state.yearsSinceWar} years underground. $settlementName rose on ${location.name}, a modest $societyType struggling with $economy under $politics. They lived in $environment.$specificEnding ${1000 - state.survivors} died in the vault, and many more would follow in the harsh world above."
            }
            2 -> {
                val specificEnding = when (classification) {
                    "Dying Ember" -> " ${state.survivors} survivors. That was all that remained of humanity's hope. Too few to maintain genetic diversity. Too few to defend against threats. Too few to rebuild. They knew they were watching humanity's final chapter."
                    "Besieged Outpost" -> " The ${location.nativeHostility.displayName} attacked constantly. Every day was a battle. They slept in shifts, weapons always ready. Half their food went to bribes and protection money. They weren't living—they were dying slowly."
                    "Thirst-Cursed Camp" -> " No water. The defining fact of their existence. They dug wells that came up dry. They collected dew. They rationed urine. Dehydration killed more than radiation ever did. Their tongues were perpetually swollen, their lips cracked and bleeding."
                    "Starving Remnant" -> " Food stores at ${state.vaultSystems.foodStores}%. They were already starving in the vault. The surface offered no relief. They ate rats, insects, anything. Cannibalism was whispered about but never proven. Starvation made them animals."
                    else -> " Every aspect of ${location.name} was hostile to human life. They had chosen poorly."
                }
                "Only ${state.survivors} survivors emerged from Vault $vaultNumber after ${state.yearsSinceWar} desperate years. $settlementName on ${location.name} was less a settlement than a desperate camp. Their $societyType barely functioned, crippled by $economy and fractured $politics. They lived in $environment.$specificEnding ${1000 - state.survivors} died in the vault. Most of the survivors would not see another decade."
            }
            else -> {
                val specificEnding = when (classification) {
                    "Total Extinction" -> " All ${1000} who entered Vault $vaultNumber are dead. The vault door stands open, a monument to failure. Inside, skeletons in their bunks. Outside, bodies that made it only meters before collapsing. Humanity ended not with a bang, but with a whimper."
                    "Final Gasps" -> " ${state.survivors} survivors. They lasted days, maybe weeks. One by one they fell. The last survivor, alone and mad, scratched a final message on the vault door: 'We should have stayed inside.' Then silence."
                    "Radiation Tomb" -> " Lethal radiation killed them all within hours. Their bodies lie where they fell, already decomposing in the toxic air. The vault had been their tomb; ${location.name} became their grave. Future archaeologists would find them and wonder why they left safety for certain death."
                    "Conquered and Enslaved" -> " The warlord's forces were waiting. They had detected the vault years ago. The survivors were captured immediately, their technology stolen, their bodies enslaved. They died in chains, building monuments to their conquerors. A few escaped and spread the warning: never open the vault."
                    else -> " ${location.name} was a death sentence. They should have stayed in the vault."
                }
                "${state.survivors} survivors crawled from Vault $vaultNumber after ${state.yearsSinceWar} years, more dead than alive. What they called $settlementName on ${location.name} was a graveyard in waiting. Their $societyType was a cruel joke, their $economy nonexistent, their $politics mere anarchy. They lived in $environment, but 'lived' was too generous a word.$specificEnding ${1000 - state.survivors} died in the vault. Humanity's light was extinguished."
            }
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
