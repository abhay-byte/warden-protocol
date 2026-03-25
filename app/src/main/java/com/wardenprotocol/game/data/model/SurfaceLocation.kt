package com.wardenprotocol.game.data.model

data class SurfaceLocation(
    val name: String,
    val type: LocationType,
    val radiation: RadiationLevel,
    val water: WaterAvailability,
    val food: FoodPotential,
    val shelter: ShelterQuality,
    val resources: ResourceRichness,
    val nativeHostility: Hostility,
    val anomaly: SurfaceAnomaly?,
    val probeData: ProbeData? = null
)

data class ProbeData(
    val hiddenResources: String,
    val structuralIntegrity: String,
    val soilQuality: String,
    val recommendation: String
)

enum class LocationType {
    RUINED_CITY, FOREST, MILITARY_BASE, FARMLAND,
    UNDERGROUND_RIVER, MOUNTAIN_PASS, COASTAL_TOWN, RESEARCH_FACILITY
}

enum class RadiationLevel(val displayName: String, val scoreModifier: Int) {
    NONE("None", 500),
    LOW("Low", 200),
    MODERATE("Moderate", 0),
    HIGH("High", -300),
    LETHAL("Lethal", -800)
}

enum class WaterAvailability(val displayName: String, val scoreModifier: Int) {
    ABUNDANT("Abundant", 400),
    SCARCE("Scarce", 100),
    NONE("None", -400)
}

enum class FoodPotential(val displayName: String, val scoreModifier: Int) {
    FERTILE("Fertile", 300),
    MARGINAL("Marginal", 100),
    BARREN("Barren", -300)
}

enum class ShelterQuality(val displayName: String, val scoreModifier: Int) {
    EXCELLENT("Excellent", 200),
    GOOD("Good", 100),
    POOR("Poor", 0),
    NONE("None", -200)
}

enum class ResourceRichness(val displayName: String, val scoreModifier: Int) {
    RICH("Rich", 250),
    MODERATE("Moderate", 100),
    POOR("Poor", 0)
}

enum class Hostility(val displayName: String, val scoreModifier: Int) {
    NONE("None", 0),
    BANDITS("Bandits", -150),
    WARLORD("Warlord", -400),
    WASTELAND_CULT("Wasteland Cult", -300)
}

enum class SurfaceAnomaly(val displayName: String, val scoreModifier: Int) {
    SEED_VAULT("Seed Vault", 300),
    FUNCTIONING_REACTOR("Functioning Reactor", 400),
    ALIEN_SIGNAL("Alien Signal", 200),
    SURVIVOR_CAMP("Survivor Camp", 250),
    ANCIENT_LIBRARY("Ancient Library", 300),
    CLEAN_WATER_SPRING("Clean Water Spring", 350),
    WEAPONS_CACHE("Weapons Cache", 150),
    DISEASE_OUTBREAK_SITE("Disease Outbreak Site", -200)
}
