package com.wardenprotocol.game.domain.engine

import com.wardenprotocol.game.data.model.*
import com.wardenprotocol.game.data.repository.EventRepository
import kotlin.random.Random

class GameEngine(private val eventRepository: EventRepository) {

    private data class LocationIntel(
        val shortDescription: String,
        val longDescription: String,
        val travelProfile: TravelProfile
    )

    private data class TravelTemplate(
        val routeName: String,
        val durationText: String,
        val minLossPercent: Int,
        val maxLossPercent: Int,
        val scorePenalty: Int
    )

    private val locationNameGenerators = mapOf(
        LocationType.RUINED_CITY to listOf(
            "Flooded Detroit", "Silent Chicago Ruins", "Ash-Covered Boston", "Broken Los Angeles",
            "Shattered New York", "Hollow Philadelphia", "Scorched Phoenix", "Frozen Minneapolis",
            "Crumbling Seattle", "Dead Miami", "Ghost Atlanta", "Buried Denver",
            "Ashen St. Louis", "Blasted Newark", "Sunken Baltimore", "Husk of Houston",
            "Static Las Vegas", "Melted Cleveland", "Bonewhite Dallas", "Collapsed Sacramento",
            "Dread Memphis", "Shiver Portland"
        ),
        LocationType.FOREST to listOf(
            "Ash-Grey Pinelands", "Recovering Oak Valley", "Mutant Redwood Grove", "Dead Birch Forest",
            "Twisted Maple Woods", "Blackened Cedar Stand", "Poisoned Willow Marsh", "Charred Sequoia Basin",
            "Spore-Choked Timberline", "Hanging Moss Hollow", "Rotfen Grove", "Pale Fungus Thicket"
        ),
        LocationType.MILITARY_BASE to listOf(
            "Fort Zulu", "Outpost Kilo-7", "Fort Alpha Ruins", "Base Tango-9", "Fort Whiskey",
            "Outpost Delta", "Fort November", "Base Echo-3", "Fort Sierra", "Outpost Bravo-6",
            "Missile Silo Raven", "Checkpoint Mordred", "Fort Blackglass", "Outpost Widow",
            "Battery Helix", "Silo Jericho", "Camp Raptor", "Redoubt Cain",
            "Ordnance Yard 14", "Fort Harrow", "Launch Complex M", "Garrison Hollow"
        ),
        LocationType.FARMLAND to listOf(
            "Withered Corn Belt", "Salted Wheat Fields", "Toxic Vineyard", "Barren Pastures",
            "Irradiated Orchards", "Dead Soybean Plains", "Poisoned Rice Paddies", "Scorched Cropland",
            "Maggot Orchard", "Ash Harvest Flats", "Rotted Cattle Range", "Grey Millet Basin"
        ),
        LocationType.UNDERGROUND_RIVER to listOf(
            "Subterranean Flow", "Hidden Aquifer", "Deep Water Vein", "Buried Stream",
            "Cavern Springs", "Underground Lake", "Limestone River", "Crystal Waters Below",
            "Blackwater Shaft", "Echo Flood Galleries", "Stalagmite Channel", "Sunless Delta"
        ),
        LocationType.MOUNTAIN_PASS to listOf(
            "Frozen Summit Trail", "Rockslide Pass", "Avalanche Corridor", "High Ridge Path",
            "Shattered Peak Route", "Windswept Col", "Glacier Pass", "Stone Gate Mountains",
            "Bleak Horn Divide", "Bonewind Ascent", "Needle Ridge", "Frostbite Traverse",
            "Widowmaker Notch", "Ashcliff Traverse", "Hollow Fang Pass", "Razor Scree Ascent",
            "Crowstep Ridge", "The Ice Maw", "Black Flag Switchback", "Deadfall Crest",
            "Stormblind Gap", "Hanging Rock Ladder"
        ),
        LocationType.COASTAL_TOWN to listOf(
            "Drowned Harbor", "Tsunami-Swept Bay", "Radioactive Shoreline", "Flooded Pier Town",
            "Toxic Beach Settlement", "Submerged Port", "Poisoned Fishing Village", "Dead Seaside",
            "Barnacle Mile", "Oil-Black Cove", "Rotted Marina", "Saltgrave Inlet",
            "Corpse Tide Wharf", "Deadlight Breakwater", "Anchor Grave", "Blightwater Quay",
            "The Black Jetty", "Foamrot Village", "Harpoon Wreck", "Tideburn Reach",
            "Siltlung Coast", "Mildew Pier"
        ),
        LocationType.RESEARCH_FACILITY to listOf(
            "Blacksite Omega", "Research Station 7", "Laboratory Complex Alpha", "Science Outpost Theta",
            "Experimental Facility", "Biotech Center Ruins", "Weapons Lab Delta", "Particle Accelerator Site",
            "Cryolab Epsilon", "Containment Annex 4", "Geneforge Campus", "Telemetry Yard Nine"
        ),
        LocationType.RADIOACTIVE_SWAMP to listOf(
            "Glowmire Basin", "Irradiated Fen", "Neon Bog", "Rotwater Marsh", "Cinder Reed Flats",
            "Luminous Peat", "Fever Swale", "Sourwater Hollow", "Mire Delta 6", "Blister Marsh",
            "Yellow Silt Wetland", "Dead Lantern Bog", "Hotmist Fen", "Boilroot Marsh",
            "Greenfire Mire", "Sludge Bloom Wetlands", "Mutter Fen", "The Leech Beds",
            "Glowrot Backwater", "Ashswell Marsh", "Canker Reed Delta", "Slimewake Basin",
            "Bilewater Sump"
        ),
        LocationType.MEGACRATER to listOf(
            "Impact Cradle", "Saint Helix Crater", "Obsidian Bowl", "Ashfall Caldera", "Broken Horizon Pit",
            "Red Mile Crater", "Thunder Glass Basin", "Meteor Hollow", "Char Basin Prime", "The Long Scar",
            "Sulfur Crown", "Shatter Rim", "Gravesmoke Crater", "The Ember Throat",
            "Widow's Caldera", "Ashwheel Pit", "Meltline Crater", "Skullglass Hollow",
            "Black Echo Basin", "The Fallen Eye", "Shrapnel Crown", "Breach Bowl",
            "Smokeveil Impact"
        ),
        LocationType.PLAGUE_ZONE to listOf(
            "Quarantine Block K", "The Fever District", "Carrion Ward", "Mourning Blocks", "Bleachline Sector",
            "Triage Ruins", "Bodyburn Square", "Red Mask Borough", "Sickhouse Row", "Isolation Parish",
            "Pestilent Commons", "Grief Market", "The Coughing Mile", "Septic Avenue",
            "Vomit Gate", "Last Breath Quarter", "Pall Street", "Ashen Infirmary",
            "Needle Market", "Woundbridge", "The Weeping Blocks", "Mortuary Circle",
            "Contagion Court"
        ),
        LocationType.SCRAP_HEAP to listOf(
            "Titan Scrap Fields", "The Iron Mound", "Crushed Freight Sea", "Rust Cathedral", "Wrecker's Spine",
            "Machine Grave", "Tangle Yard", "The Broken Conveyor", "Magnet Hill", "Smelter Bones",
            "Derelict Stack", "Copper Teeth", "Junkspire", "The Razor Yard",
            "Crankshaft Ridge", "Broken Axle Plain", "Shearwall Heap", "Rustwake Valley",
            "The Shredder Fields", "Wiregut Dump", "Bleeding Foundry", "Gearstorm Mound",
            "Hacksaw Terrace"
        ),
        LocationType.ABANDONED_SUBWAY to listOf(
            "Line Zero", "Collapsed Metro Arc", "Tunnel 19", "Ghost Platform", "Station Mercy",
            "Flooded Transfer", "Black Rail Junction", "Sublevel Delta", "Terminal Ash", "Signal Pit",
            "Platform Thirteen", "Rat King Interchange", "Last Stop Hollow", "Blind Switch Nine",
            "Mold Rail Annex", "Red Signal Tunnel", "The Hollow Concourse", "Dripshaft Terminal",
            "Grime Loop", "Third Rail Catacomb", "Station Dread", "The Choking Underpass",
            "Trackbed Ossuary"
        ),
        LocationType.FUNGAL_WASTES to listOf(
            "Mycelium Flats", "Spore Bloom Expanse", "The Mold Barrens", "Fungal Drift", "Puffball Valley",
            "Velvet Rot Fields", "Stalk Forest", "Capgrave Plain", "Lichen Storm Reach", "Softbone Hollow",
            "White Veil Steppe", "Toadstool Shelf", "Dustcap Expanse"
        ),
        LocationType.GLASS_DESERT to listOf(
            "Vitrified Sea", "Mirror Dunes", "Shard Horizon", "Glasswind Expanse", "Sunburn Flats",
            "Cracked Silica Basin", "Knife Sand Reach", "The Bright Waste", "Fused Tide Plain", "Burnglass Coast",
            "Heat Mirage Yard", "Scorched Prism Fields", "The Singing Dunes"
        ),
        LocationType.CULT_TERRITORY to listOf(
            "Pilgrim's Ash", "Shrine Belt", "The Chanting Vale", "Icon Graveyard", "Ash Sermon Camp",
            "Red Banner Reach", "Temple of Static", "Bellfire Steppe", "Sacrament Ditch", "Prophet's Crossing",
            "The Tithe Roads", "Oracle Stockade", "Martyr's Gate", "Bone Reliquary Plains",
            "The Witness Roads", "Ash Halo Station", "Sermon Basin", "The Burning Choir",
            "Ritual Fence", "Censer Hollow", "Static Gospel Camp", "Saintwire Encampment",
            "The Kneeling Mile"
        )
    )

    fun generateSurfaceLocation(): SurfaceLocation {
        val type = LocationType.entries.random()
        val name = locationNameGenerators[type]?.random() ?: "Unknown Location"

        val radiation = RadiationLevel.entries.random()
        val water = WaterAvailability.entries.random()
        val food = FoodPotential.entries.random()
        val shelter = ShelterQuality.entries.random()
        val resources = ResourceRichness.entries.random()
        val nativeHostility = Hostility.entries.random()
        val anomaly = if (Random.nextFloat() < 0.3f) SurfaceAnomaly.entries.random() else null

        val intel = buildLocationIntel(
            name = name,
            type = type,
            radiation = radiation,
            water = water,
            food = food,
            shelter = shelter,
            resources = resources,
            nativeHostility = nativeHostility,
            anomaly = anomaly
        )

        return SurfaceLocation(
            name = name,
            type = type,
            radiation = radiation,
            water = water,
            food = food,
            shelter = shelter,
            resources = resources,
            nativeHostility = nativeHostility,
            anomaly = anomaly,
            shortDescription = intel.shortDescription,
            longDescription = intel.longDescription,
            travelProfile = intel.travelProfile
        )
    }

    @Suppress("UNUSED_PARAMETER")
    fun generateEvent(state: GameState): GameEvent {
        val allEvents = eventRepository.getAllEvents()
        return allEvents.random()
    }

    fun applyEventChoice(state: GameState, choice: EventChoice): Pair<GameState, String> {
        var newState = state.copy(survivors = (state.survivors + choice.outcome.survivorDelta).coerceAtLeast(0))

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

        if (state.vaultSystems.foodStores < 30) survivorLoss += Random.nextInt(10, 30)
        if (state.vaultSystems.foodStores < 15) survivorLoss += Random.nextInt(20, 40)
        if (state.vaultSystems.atmosphereScrubbers < 20) survivorLoss += Random.nextInt(5, 20)
        if (state.vaultSystems.atmosphereScrubbers < 10) survivorLoss += Random.nextInt(15, 35)
        if (state.vaultSystems.medicalBay < 20) survivorLoss += Random.nextInt(3, 15)
        if (state.vaultSystems.powerGrid < 10) survivorLoss += Random.nextInt(10, 25)

        survivorLoss += Random.nextInt(1, 5)

        return state.copy(
            vaultSystems = systems,
            survivors = (state.survivors - survivorLoss).coerceAtLeast(0),
            yearsSinceWar = state.yearsSinceWar + 1
        )
    }

    fun scoreOutcome(state: GameState, location: SurfaceLocation): Int {
        if (state.survivors <= 0) return 0

        var score = state.survivors * 5
        score += location.radiation.scoreModifier
        score += location.water.scoreModifier
        score += location.food.scoreModifier
        score += location.shelter.scoreModifier
        score += location.resources.scoreModifier
        score += location.nativeHostility.scoreModifier

        score += (state.vaultSystems.constructionGear / 100.0 * 150).toInt()
        score += (state.vaultSystems.powerGrid / 100.0 * 100).toInt()
        score += (state.vaultSystems.foodStores / 100.0 * 100).toInt()
        score += (state.vaultSystems.medicalBay / 100.0 * 80).toInt()
        score += (state.vaultSystems.securitySystem / 100.0 * 80).toInt()
        score += (state.vaultSystems.atmosphereScrubbers / 100.0 * 80).toInt()

        score += (state.databases.culturalArchive / 100.0 * 100).toInt()
        score += (state.databases.scientificArchive / 100.0 * 100).toInt()

        score -= (state.yearsSinceWar * 100)
        score -= ((1000 - state.survivors) * 3)

        val avgSystemHealth = (
            state.vaultSystems.powerGrid +
                state.vaultSystems.foodStores +
                state.vaultSystems.medicalBay +
                state.vaultSystems.securitySystem +
                state.vaultSystems.constructionGear +
                state.vaultSystems.atmosphereScrubbers
            ) / 6
        if (avgSystemHealth < 50) {
            score -= (50 - avgSystemHealth) * 20
        }

        val avgDatabaseHealth = (state.databases.culturalArchive + state.databases.scientificArchive) / 2
        if (avgDatabaseHealth < 50) {
            score -= (50 - avgDatabaseHealth) * 15
        }

        location.anomaly?.let { score += it.scoreModifier }
        score -= location.travelProfile.scorePenalty

        return score.coerceAtLeast(0)
    }

    fun generateOutcomeNarrative(
        state: GameState,
        location: SurfaceLocation,
        score: Int,
        travelDeaths: Int = 0
    ): ColonyOutcome {
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
        val transitLine = generateTransitLine(location, travelDeaths)
        val futureEpilogue = generateFutureEpilogue(
            state = state,
            location = location,
            score = score,
            tier = tier,
            classification = classification,
            settlementName = settlementName,
            vaultNumber = vaultNumber
        )

        val outcomeDetail = when (tier) {
            5 -> {
                val specificEnding = when (classification) {
                    "Paradise Reclaimed" -> " With over ${state.survivors} survivors, they had the numbers to build a true civilization. Within a generation, ${location.name} transformed from wasteland to wonderland. Schools, hospitals, farms, and markets returned in stronger form."
                    "Renaissance of Humanity" -> " They emerged not just with bodies, but with minds intact. The cultural and scientific archives were nearly complete. Artists painted murals on vault walls. Scientists developed radiation treatments. Philosophers debated the meaning of their survival. Humanity's soul had survived."
                    "Blessed Settlement" -> " The ${location.anomaly?.displayName} changed everything. What seemed like a curse became their greatest blessing. It provided resources, knowledge, or protection that made survival not just possible, but inevitable."
                    else -> " The location was unusually favorable, and humanity used that second chance with discipline and ambition."
                }
                "Against all odds, humanity not only survived but flourished. ${state.survivors} souls emerged from Vault $vaultNumber after ${state.yearsSinceWar} years, carrying the torch of civilization. They founded $settlementName on ${location.name}, a beacon of hope in the wasteland. Their $societyType thrived through $economy, governed by $politics. They lived in $environment.$transitLine$specificEnding Their descendants would remember this as humanity's second dawn. $futureEpilogue"
            }

            4 -> {
                val specificEnding = when (classification) {
                    "Prosperous Colony" -> " ${state.survivors} survivors was more than enough. They divided into specialized roles: farmers, builders, guards, and teachers. Within five years, they had a functioning town with trade routes to other survivor groups."
                    "Industrial Haven" -> " The construction gear at ${state.vaultSystems.constructionGear}% capacity allowed them to build rapidly. Factories rose from ruins. They manufactured tools, weapons, and eventually luxuries. Other settlements came to trade."
                    "Oasis of Hope" -> " Abundant water was their salvation. While others fought over drops, they had rivers. They became a destination for desperate wanderers, growing their population and influence."
                    else -> " Despite the challenges of ${location.name}, they overcame every obstacle through ingenuity and determination."
                }
                "${state.survivors} survivors emerged from Vault $vaultNumber in Year ${state.yearsSinceWar}. They built $settlementName on ${location.name}, establishing a $societyType sustained by $economy and governed through $politics. They lived in $environment.$transitLine$specificEnding Of the thousand who entered the vault, ${1000 - state.survivors} never saw the sky again, but their sacrifice was not in vain. $futureEpilogue"
            }

            3 -> {
                val specificEnding = when (classification) {
                    "Fragile Foothold" -> " With only ${state.survivors} survivors, every death was a catastrophe. They could not afford to lose anyone. Reproduction became a duty. Children were raised communally. They survived, but barely."
                    "Weary Survivors" -> " ${state.yearsSinceWar} years underground had broken something in them. They emerged old, tired, and traumatized. The young had never seen the sun. The old barely remembered it. They built $settlementName, but their hearts remained in the vault."
                    "Irradiated Settlement" -> " The ${location.radiation.displayName} radiation was inescapable. They wore protective gear constantly. Cancers were common. Mutations appeared in the second generation. They survived, but at great cost."
                    else -> " The harsh conditions tested them daily."
                }
                "${state.survivors} survivors left Vault $vaultNumber after ${state.yearsSinceWar} years underground. $settlementName rose on ${location.name}, a modest $societyType struggling with $economy under $politics. They lived in $environment.$transitLine$specificEnding ${1000 - state.survivors} died in the vault, and many more would follow in the harsh world above. $futureEpilogue"
            }

            2 -> {
                val specificEnding = when (classification) {
                    "Dying Ember" -> " ${state.survivors} survivors. That was all that remained of humanity's hope. Too few to maintain genetic diversity. Too few to defend against threats. Too few to rebuild. They knew they were watching humanity's final chapter."
                    "Besieged Outpost" -> " The ${location.nativeHostility.displayName} attacked constantly. Every day was a battle. They slept in shifts, weapons always ready. Half their food went to bribes and protection money. They were dying slowly."
                    "Thirst-Cursed Camp" -> " No water defined their existence. Wells came up dry. Dew became a resource. Dehydration killed more than radiation ever did. Their lips cracked, their tempers failed, and the camp turned desperate."
                    "Starving Remnant" -> " Food stores at ${state.vaultSystems.foodStores}% meant they were already hungry in the vault. The surface offered no relief. They ate pests, bark, and anything scavenged before hunger made every choice cruel."
                    else -> " Every aspect of ${location.name} was hostile to human life. They had chosen poorly."
                }
                "Only ${state.survivors} survivors emerged from Vault $vaultNumber after ${state.yearsSinceWar} desperate years. $settlementName on ${location.name} was less a settlement than a desperate camp. Their $societyType barely functioned, crippled by $economy and fractured $politics. They lived in $environment.$transitLine$specificEnding ${1000 - state.survivors} died in the vault. Most of the survivors would not see another decade. $futureEpilogue"
            }

            else -> {
                val specificEnding = when (classification) {
                    "Total Extinction" -> " All 1000 who entered Vault $vaultNumber are dead. The vault door stands open, a monument to failure. Inside are silent bunks. Outside lie the bodies of those who made it only meters into the world."
                    "Final Gasps" -> " ${state.survivors} survivors remained. They lasted days, maybe weeks. The last survivor, alone and mad, scratched a final warning onto the vault door before the silence took them too."
                    "Radiation Tomb" -> " Lethal radiation killed them within hours. The vault had been their tomb; ${location.name} became their grave."
                    "Conquered and Enslaved" -> " The warlord's forces were waiting. The survivors were captured immediately, their technology stolen, their bodies enslaved. They died in chains, and only rumor remained."
                    else -> " ${location.name} was a death sentence. They should have stayed in the vault."
                }
                "${state.survivors} survivors crawled from Vault $vaultNumber after ${state.yearsSinceWar} years, more dead than alive. What they called $settlementName on ${location.name} was a graveyard in waiting. Their $societyType was a cruel joke, their $economy nonexistent, their $politics mere anarchy. They lived in $environment, but 'lived' was too generous a word.$transitLine$specificEnding ${1000 - state.survivors} died in the vault. Humanity's light was extinguished. $futureEpilogue"
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
                travelRoute = location.travelProfile.routeName,
                travelTime = location.travelProfile.durationText,
                travelRisk = location.travelProfile.riskLevel.displayName,
                travelDeaths = travelDeaths,
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

    private fun generateFutureEpilogue(
        state: GameState,
        location: SurfaceLocation,
        score: Int,
        tier: Int,
        classification: String,
        settlementName: String,
        vaultNumber: Int
    ): String {
        val foundationYears = when (tier) {
            5 -> (2 + (100 - state.vaultSystems.constructionGear) / 18).coerceAtLeast(2)
            4 -> (4 + (100 - state.vaultSystems.constructionGear) / 12).coerceAtLeast(4)
            3 -> (8 + (100 - state.vaultSystems.constructionGear) / 10).coerceAtLeast(6)
            2 -> (6 + (100 - state.vaultSystems.constructionGear) / 14).coerceAtLeast(5)
            else -> 1
        }
        val foundationYear = state.yearsSinceWar + foundationYears
        val futureYear = state.yearsSinceWar + foundationYears + when (tier) {
            5 -> 45
            4 -> 30
            3 -> 18
            2 -> 8
            else -> 1
        }

        return when (tier) {
            5 -> {
                val maturityYears = state.yearsSinceWar + 60 + (state.survivors / 25)
                val milestone = when {
                    location.resources == ResourceRichness.RICH ->
                        "metalworks, power relays, and reclaimed transit routes tied neighboring settlements to $settlementName"
                    state.databases.scientificArchive >= 80 ->
                        "their engineers restored medicine, irrigation, and machine industry far faster than anyone expected"
                    else ->
                        "their schools, archives, and farms spread stable life across the surrounding region"
                }
                "Within $foundationYears years, they had raised permanent walls, clinics, and fields around $settlementName. By Year $futureYear after the war, the first generation born under open sky was reaching adulthood and no longer thought of the vault as home. By Year $maturityYears, $milestone. Historians would later mark the opening of Vault $vaultNumber as the beginning of a civilization that endured for centuries."
            }

            4 -> {
                val civilizationYear = state.yearsSinceWar + 25 + (score / 350)
                val milestone = when (classification) {
                    "Industrial Haven" -> "workshops became foundries, and foundries became a true industrial district"
                    "Oasis of Hope" -> "the settlement grew into a regional refuge, drawing migrants and forging alliances around its water supply"
                    else -> "their camp hardened into a town, then into a recognized city-state with laws, markets, and a shared identity"
                }
                "They needed $foundationYears hard years to get past hunger, exposure, and panic, but by Year $foundationYear after the war the settlement was no longer temporary. By Year $civilizationYear, $milestone. The people of the vault did not simply survive; they created a durable society that outlived the bunker generation."
            }

            3 -> {
                val survivalYears = 18 + (state.survivors / 35) + (state.databases.scientificArchive / 12)
                val endYear = state.yearsSinceWar + survivalYears
                val branch = when {
                    classification == "Irradiated Settlement" ->
                        "By Year $endYear after the war, they were still alive, but only through strict rationing, radiation discipline, and endless repair work. Their descendants inherited a functioning settlement, yet also inherited tumors, deformities, and a life expectancy far below that of the old world."
                    classification == "Fragile Foothold" ->
                        "They held on for roughly $survivalYears years. Births replaced some of the dead, but never enough to feel safe. By Year $endYear after the war, $settlementName remained inhabited, though it was more fortress than civilization and one bad winter from collapse."
                    else ->
                        "For about $survivalYears years, the colony wavered between recovery and ruin. By Year $endYear after the war, they had not died out, but neither had they truly rebuilt the world. They became one more stubborn survivor culture in the ashes, remembered more for endurance than triumph."
                }
                "It took them until Year $foundationYear after the war to build anything that deserved the name settlement. $branch"
            }

            2 -> {
                val survivalYears = when (classification) {
                    "Besieged Outpost" -> 3 + (state.survivors / 90)
                    "Thirst-Cursed Camp" -> 1 + (state.survivors / 140)
                    "Starving Remnant" -> 2 + (state.survivors / 110)
                    else -> 4 + (state.survivors / 80)
                }
                val endYear = state.yearsSinceWar + survivalYears
                val ending = when (classification) {
                    "Besieged Outpost" ->
                        "They managed to resist for about $survivalYears years, but every season cost lives they could not replace. By Year $endYear after the war, the settlement had either been overrun or abandoned by the few survivors still able to flee."
                    "Thirst-Cursed Camp" ->
                        "Water broke them first. Within roughly $survivalYears years, disease, dehydration, and infighting had gutted the camp. By Year $endYear after the war, only ruins and shallow graves marked where they had tried to live."
                    "Starving Remnant" ->
                        "They stretched scavenged calories further than seemed possible, but hunger ruled every choice. They lasted around $survivalYears years before the settlement fragmented into wandering families and the name $settlementName disappeared from memory."
                    else ->
                        "They clung to life for roughly $survivalYears years, never secure enough to raise a second stable generation. By Year $endYear after the war, the colony had dwindled into scattered survivors, and the dream of rebuilding died with it."
                }
                "They never truly stabilized after leaving the vault. $ending"
            }

            else -> {
                val timing = when (classification) {
                    "Final Gasps" -> "within weeks"
                    "Radiation Tomb" -> "within hours"
                    "Conquered and Enslaved" -> "within months"
                    "Total Extinction" -> "before the first day ended"
                    else -> "within the first year"
                }
                val finalYearText = if (state.yearsSinceWar == 0) {
                    "the same year the vault opened"
                } else {
                    "Year ${state.yearsSinceWar + 1} after the war"
                }
                "Their final fate was sealed $timing. By $finalYearText, no free community remained from Vault $vaultNumber. At most, later travelers found a few relics, a half-finished wall, or stories told by strangers about people who had emerged too late into a dead world."
            }
        }
    }

    private fun buildLocationIntel(
        name: String,
        type: LocationType,
        radiation: RadiationLevel,
        water: WaterAvailability,
        food: FoodPotential,
        shelter: ShelterQuality,
        resources: ResourceRichness,
        nativeHostility: Hostility,
        anomaly: SurfaceAnomaly?
    ): LocationIntel {
        val terrainLine = when (type) {
            LocationType.RUINED_CITY -> "A dead urban basin of collapsed towers, blocked avenues, and exposed utility corridors."
            LocationType.FOREST -> "A regrown wilderness where the old world is disappearing under roots, spores, and standing water."
            LocationType.MILITARY_BASE -> "A hardened military footprint with blast walls, depot yards, and half-buried command structures."
            LocationType.FARMLAND -> "A broad agricultural zone of broken irrigation, skeletal silos, and long open sightlines."
            LocationType.UNDERGROUND_RIVER -> "A subterranean water network threaded through caverns, shafts, and unstable access tunnels."
            LocationType.MOUNTAIN_PASS -> "A steep highland route of exposed ridges, rockfall choke points, and narrow defensible ground."
            LocationType.COASTAL_TOWN -> "A drowned shoreline settlement where salt, flood damage, and broken harbor works define the landscape."
            LocationType.RESEARCH_FACILITY -> "A sealed research compound ringed with service tunnels, labs, and damaged containment systems."
            LocationType.RADIOACTIVE_SWAMP -> "A glowing wetland of chemical muck, irradiated reeds, and sinkholes that swallow equipment whole."
            LocationType.MEGACRATER -> "A colossal impact scar of shattered earth, sulfur vents, unstable rims, and strange mineral exposure."
            LocationType.PLAGUE_ZONE -> "A disease-haunted urban quarantine belt littered with burned checkpoints, triage ruins, and biohazard pits."
            LocationType.SCRAP_HEAP -> "A metallic wasteland of crushed vehicles, twisted cranes, collapsing heaps, and razor-edged salvage corridors."
            LocationType.ABANDONED_SUBWAY -> "A buried transit labyrinth of dark platforms, flooded tunnels, collapsed passages, and blind choke points."
            LocationType.FUNGAL_WASTES -> "A spore-heavy biome where mutated fungal growth blankets the ground, structures, and even the air itself."
            LocationType.GLASS_DESERT -> "A vitrified wasteland of fused sand, knife-sharp dunes, brutal heat shimmer, and almost no natural cover."
            LocationType.CULT_TERRITORY -> "A fanatical dominion marked by shrine roads, warning totems, sacrificial compounds, and armed watchfires."
        }

        val radiationLine = when (radiation) {
            RadiationLevel.NONE -> "Readings suggest the air is unusually clean for the post-war surface."
            RadiationLevel.LOW -> "Background radiation remains present but appears manageable with disciplined precautions."
            RadiationLevel.MODERATE -> "Radiation sits above safe pre-war limits and would demand continuous monitoring."
            RadiationLevel.HIGH -> "Hot zones are widespread enough that every work crew would need shielding and route control."
            RadiationLevel.LETHAL -> "The area is saturated with lethal contamination that would kill the unprotected quickly."
        }

        val waterLine = when (water) {
            WaterAvailability.ABUNDANT -> "Water signatures are strong, offering reliable collection, storage, and purification potential."
            WaterAvailability.SCARCE -> "Water exists, but not in the volumes needed for comfort or rapid growth."
            WaterAvailability.NONE -> "No dependable water source is visible near the primary settlement zone."
        }

        val foodLine = when (food) {
            FoodPotential.FERTILE -> "Soil and biomass patterns suggest crops could take hold once the first season is secured."
            FoodPotential.MARGINAL -> "Food production is possible, but only with ration discipline, treatment, and careful site selection."
            FoodPotential.BARREN -> "The land offers little natural support for agriculture, forcing dependence on stores or engineered systems."
        }

        val shelterLine = when (shelter) {
            ShelterQuality.EXCELLENT -> "Several intact structures could be sealed and occupied almost immediately."
            ShelterQuality.GOOD -> "Some surviving structures could be repaired into workable housing and storage."
            ShelterQuality.POOR -> "Existing cover is damaged and patchwork, buying time rather than real safety."
            ShelterQuality.NONE -> "Settlers would need to build shelter from scratch as soon as they arrived."
        }

        val resourceLine = when (resources) {
            ResourceRichness.RICH -> "The site shows enough salvage, ore, or industrial remnants to support early expansion."
            ResourceRichness.MODERATE -> "Useful material exists here, though growth would still demand careful extraction and reuse."
            ResourceRichness.POOR -> "Salvage density is low, so every tool and structural part would matter."
        }

        val threatLine = when (nativeHostility) {
            Hostility.NONE -> "No organized hostile presence is visible from current telemetry."
            Hostility.BANDITS -> "Small raider activity is likely, especially against slow convoys or exposed workers."
            Hostility.WASTELAND_CULT -> "Fanatical groups appear active nearby and may treat the vault emergence as a target or omen."
            Hostility.WARLORD -> "The area appears contested by a heavily armed force capable of siege, taxation, or outright conquest."
        }

        val anomalyLine = anomaly?.let {
            when (it) {
                SurfaceAnomaly.SEED_VAULT -> "A sealed seed repository may still exist nearby, offering a rare agricultural breakthrough."
                SurfaceAnomaly.FUNCTIONING_REACTOR -> "Residual power signatures hint at a functioning reactor or partially active grid."
                SurfaceAnomaly.ALIEN_SIGNAL -> "A repeating non-human signal pattern continues to pulse from beyond the primary survey zone."
                SurfaceAnomaly.SURVIVOR_CAMP -> "Thermal traces suggest another survivor enclave may already be operating nearby."
                SurfaceAnomaly.ANCIENT_LIBRARY -> "A structurally unusual archive block may contain recoverable pre-war records."
                SurfaceAnomaly.CLEAN_WATER_SPRING -> "One sensor cluster suggests a rare clean spring untouched by heavy contamination."
                SurfaceAnomaly.WEAPONS_CACHE -> "Buried metallic signatures are consistent with a weapons cache or munitions locker."
                SurfaceAnomaly.DISEASE_OUTBREAK_SITE -> "Biohazard residues point to an old outbreak zone that still warrants quarantine."
            }
        } ?: "No extraordinary anomaly dominates the scan, leaving raw survival factors to decide the site."

        val shortDescription = "$terrainLine $radiationLine"
        val longDescription = listOf(
            "$name appears on the command map as a viable candidate for surface resettlement.",
            terrainLine,
            radiationLine,
            waterLine,
            foodLine,
            shelterLine,
            resourceLine,
            threatLine,
            anomalyLine
        ).joinToString(" ")

        return LocationIntel(
            shortDescription = shortDescription,
            longDescription = longDescription,
            travelProfile = buildTravelProfile(type, radiation, water, shelter, nativeHostility, anomaly)
        )
    }

    private fun buildTravelProfile(
        type: LocationType,
        radiation: RadiationLevel,
        water: WaterAvailability,
        shelter: ShelterQuality,
        nativeHostility: Hostility,
        anomaly: SurfaceAnomaly?
    ): TravelProfile {
        val template = when (type) {
            LocationType.RUINED_CITY -> TravelTemplate("Collapsed interstate approach", "4 to 6 days through rubble lanes", 4, 9, 90)
            LocationType.FOREST -> TravelTemplate("Forest breach trail", "5 to 8 days through brush and marsh", 3, 8, 80)
            LocationType.MILITARY_BASE -> TravelTemplate("Fortified service road", "3 to 5 days along exposed service corridors", 4, 8, 95)
            LocationType.FARMLAND -> TravelTemplate("Open plain convoy route", "3 to 4 days across open ground", 2, 6, 60)
            LocationType.UNDERGROUND_RIVER -> TravelTemplate("Cavern descent route", "2 to 4 days through shafts and flooded tunnels", 3, 7, 70)
            LocationType.MOUNTAIN_PASS -> TravelTemplate("High ridge ascent", "6 to 9 days over exposed switchbacks", 6, 12, 130)
            LocationType.COASTAL_TOWN -> TravelTemplate("Flooded shoreline corridor", "5 to 7 days around washouts and salt flats", 5, 11, 120)
            LocationType.RESEARCH_FACILITY -> TravelTemplate("Service tunnel convoy route", "4 to 6 days through damaged access roads", 4, 8, 85)
            LocationType.RADIOACTIVE_SWAMP -> TravelTemplate("Toxic mire crossing", "5 to 8 days through mud, reeds, and sinkwater", 6, 13, 135)
            LocationType.MEGACRATER -> TravelTemplate("Crater rim descent", "4 to 7 days over ash shelves and broken slopes", 5, 12, 125)
            LocationType.PLAGUE_ZONE -> TravelTemplate("Quarantine breach corridor", "3 to 5 days through sealed wards and infected streets", 6, 12, 130)
            LocationType.SCRAP_HEAP -> TravelTemplate("Salvage maze route", "4 to 6 days through stacked wreckage", 4, 10, 100)
            LocationType.ABANDONED_SUBWAY -> TravelTemplate("Subterranean rail ingress", "3 to 5 days through tunnels and service shafts", 5, 10, 105)
            LocationType.FUNGAL_WASTES -> TravelTemplate("Spore belt traverse", "4 to 7 days through mold bloom territory", 5, 11, 115)
            LocationType.GLASS_DESERT -> TravelTemplate("Burnglass crossing", "6 to 8 days across exposed vitrified flats", 7, 14, 145)
            LocationType.CULT_TERRITORY -> TravelTemplate("Pilgrim road incursion", "4 to 6 days past shrines and checkpoints", 6, 13, 140)
        }

        var minLoss = template.minLossPercent
        var maxLoss = template.maxLossPercent
        var scorePenalty = template.scorePenalty
        val riskDrivers = mutableListOf<String>()

        when (radiation) {
            RadiationLevel.NONE -> Unit
            RadiationLevel.LOW -> {
                minLoss += 1
                maxLoss += 2
                scorePenalty += 20
                riskDrivers += "light fallout pockets"
            }
            RadiationLevel.MODERATE -> {
                minLoss += 2
                maxLoss += 4
                scorePenalty += 45
                riskDrivers += "persistent radiation exposure"
            }
            RadiationLevel.HIGH -> {
                minLoss += 4
                maxLoss += 7
                scorePenalty += 90
                riskDrivers += "hot zones that force detours and shielding"
            }
            RadiationLevel.LETHAL -> {
                minLoss += 6
                maxLoss += 11
                scorePenalty += 150
                riskDrivers += "lethal contamination along the route"
            }
        }

        when (water) {
            WaterAvailability.ABUNDANT -> Unit
            WaterAvailability.SCARCE -> {
                minLoss += 1
                maxLoss += 3
                scorePenalty += 20
                riskDrivers += "thin water reserves for the convoy"
            }
            WaterAvailability.NONE -> {
                minLoss += 3
                maxLoss += 6
                scorePenalty += 55
                riskDrivers += "a dry approach with no safe refill point"
            }
        }

        when (shelter) {
            ShelterQuality.EXCELLENT -> {
                minLoss -= 1
                maxLoss -= 1
                scorePenalty -= 10
            }
            ShelterQuality.GOOD -> Unit
            ShelterQuality.POOR -> {
                minLoss += 1
                maxLoss += 2
                scorePenalty += 20
                riskDrivers += "little defensible cover on arrival"
            }
            ShelterQuality.NONE -> {
                minLoss += 2
                maxLoss += 4
                scorePenalty += 45
                riskDrivers += "no immediate shelter for the first night"
            }
        }

        when (nativeHostility) {
            Hostility.NONE -> Unit
            Hostility.BANDITS -> {
                minLoss += 2
                maxLoss += 4
                scorePenalty += 45
                riskDrivers += "raider harassment"
            }
            Hostility.WASTELAND_CULT -> {
                minLoss += 3
                maxLoss += 6
                scorePenalty += 65
                riskDrivers += "fanatical ambush threats"
            }
            Hostility.WARLORD -> {
                minLoss += 5
                maxLoss += 9
                scorePenalty += 110
                riskDrivers += "warlord patrols and checkpoint violence"
            }
        }

        when (anomaly) {
            SurfaceAnomaly.CLEAN_WATER_SPRING -> {
                minLoss -= 1
                maxLoss -= 2
                scorePenalty -= 20
            }
            SurfaceAnomaly.SURVIVOR_CAMP -> {
                minLoss -= 1
                maxLoss -= 1
                scorePenalty -= 15
            }
            SurfaceAnomaly.FUNCTIONING_REACTOR -> {
                scorePenalty += 15
                riskDrivers += "unstable power infrastructure"
            }
            SurfaceAnomaly.DISEASE_OUTBREAK_SITE -> {
                minLoss += 2
                maxLoss += 3
                scorePenalty += 35
                riskDrivers += "infection control delays"
            }
            else -> Unit
        }

        minLoss = minLoss.coerceAtLeast(1)
        maxLoss = maxLoss.coerceAtLeast(minLoss + 2)
        scorePenalty = scorePenalty.coerceAtLeast(25)

        val riskLevel = when {
            maxLoss >= 26 -> TravelRisk.EXTREME
            maxLoss >= 18 -> TravelRisk.HIGH
            maxLoss >= 11 -> TravelRisk.MODERATE
            else -> TravelRisk.LOW
        }

        val riskSummary = if (riskDrivers.isEmpty()) {
            "The route is relatively manageable, but exposure and convoy strain will still cost lives before the settlement secures itself."
        } else {
            "Expect losses from ${riskDrivers.joinToString(", ")} before the first defensive line is established."
        }

        return TravelProfile(
            routeName = template.routeName,
            durationText = template.durationText,
            riskLevel = riskLevel,
            riskSummary = riskSummary,
            minLossPercent = minLoss,
            maxLossPercent = maxLoss,
            scorePenalty = scorePenalty
        )
    }

    private fun generateTransitLine(location: SurfaceLocation, travelDeaths: Int): String {
        if (travelDeaths <= 0) return " "

        val lead = when (location.travelProfile.riskLevel) {
            TravelRisk.LOW -> " The march out of the vault was orderly but still costly,"
            TravelRisk.MODERATE -> " The transit to the site bled the convoy through exposure and breakdowns,"
            TravelRisk.HIGH -> " The approach to the site turned into a lethal migration under constant pressure,"
            TravelRisk.EXTREME -> " Reaching the site was almost a battle in itself,"
        }
        return "$lead and ${location.travelProfile.durationText.lowercase()} cost $travelDeaths lives before the colony could even begin."
    }

    private fun generateSettlementName(location: SurfaceLocation): String {
        val prefixes = when (location.type) {
            LocationType.RUINED_CITY -> listOf("New", "Iron", "Cinder", "Metro")
            LocationType.FOREST -> listOf("Green", "Timber", "Root", "Haven")
            LocationType.MILITARY_BASE -> listOf("Fort", "Bastion", "Command", "Watch")
            LocationType.FARMLAND -> listOf("Harvest", "Field", "Hearth", "Grain")
            LocationType.UNDERGROUND_RIVER -> listOf("Deep", "Spring", "Current", "Cavern")
            LocationType.MOUNTAIN_PASS -> listOf("High", "Stone", "Summit", "Ridge")
            LocationType.COASTAL_TOWN -> listOf("Tide", "Harbor", "Breakwater", "Beacon")
            LocationType.RESEARCH_FACILITY -> listOf("Atlas", "Signal", "Archive", "Nova")
            LocationType.RADIOACTIVE_SWAMP -> listOf("Glow", "Mire", "Reed", "Fen")
            LocationType.MEGACRATER -> listOf("Rim", "Ash", "Impact", "Crown")
            LocationType.PLAGUE_ZONE -> listOf("Mercy", "Bleach", "Ward", "Pall")
            LocationType.SCRAP_HEAP -> listOf("Rust", "Forge", "Scrap", "Iron")
            LocationType.ABANDONED_SUBWAY -> listOf("Line", "Tunnel", "Signal", "Platform")
            LocationType.FUNGAL_WASTES -> listOf("Spore", "Cap", "Myco", "Veil")
            LocationType.GLASS_DESERT -> listOf("Glass", "Prism", "Burn", "Shard")
            LocationType.CULT_TERRITORY -> listOf("Pilgrim", "Shrine", "Oracle", "Martyr")
        }
        val suffixes = listOf("Dawn", "Light", "Home", "Rest", "Peace", "Tomorrow", "Spring", "Harbor")
        return "${prefixes.random()} ${suffixes.random()}"
    }
}
