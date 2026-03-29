package com.wardenprotocol.game.data.model

data class RunRecord(
    val id: Long,
    val score: Int,
    val classification: String,
    val settlementName: String,
    val locationName: String,
    val survivors: Int,
    val yearsSinceWar: Int,
    val completedAtMillis: Long,
    val summary: String,
    val locationTypeName: String = "",
    val outcomeLabel: String = "",
    val gradeLabel: String = "",
    val fullNarrative: String = "",
    val forecastVerdict: String = "",
    val timelinePayload: String = "",
    val failureCausesPayload: String = "",
    val survivalDriversPayload: String = "",
    val baseScore: Int = 0,
    val scoreDelta: Int = 0
)

fun RunRecord.locationTypeOrNull(): LocationType? =
    locationTypeName
        .takeIf { it.isNotBlank() }
        ?.let { raw -> runCatching { enumValueOf<LocationType>(raw) }.getOrNull() }
        ?: inferLocationTypeFromLocationName(locationName)

fun RunRecord.resolvedOutcomeLabel(): String =
    outcomeLabel.ifBlank { classification.uppercase() }

fun RunRecord.resolvedGradeLabel(): String =
    gradeLabel.ifBlank { buildArchiveGradeLabel(score = score, classification = classification) }

fun buildArchiveGradeLabel(score: Int, classification: String): String {
    val grade = when {
        score >= 7000 -> "S"
        score >= 5000 -> "A"
        score >= 3200 -> "B"
        score >= 1800 -> "C"
        score >= 800 -> "D"
        else -> "E"
    }
    return "GRADE-$grade ${classification.uppercase()}"
}

fun buildArchiveOutcomeLabel(
    score: Int,
    classification: String,
    stats: OutcomeStats?
): String {
    if (stats == null) return classification.uppercase()
    return when {
        stats.survivors <= 0 || classification == "Total Extinction" -> "TOTAL EXTINCTION"
        stats.radiation == RadiationLevel.LETHAL.displayName -> "RADIATION COLLAPSE"
        stats.water == WaterAvailability.NONE.displayName && stats.food == FoodPotential.BARREN.displayName -> "RESOURCE DEPLETION"
        stats.water == WaterAvailability.NONE.displayName -> "WATER FAILURE"
        stats.food == FoodPotential.BARREN.displayName -> "FOOD COLLAPSE"
        stats.threats == Hostility.WARLORD.displayName -> "HOSTILE OVERRUN"
        stats.travelDeaths >= 250 -> "TRANSIT CATASTROPHE"
        score >= 6000 -> "CIVILIZATION REBORN"
        score >= 3500 -> "STABLE COLONY"
        score >= 1800 -> "FRAGILE SETTLEMENT"
        else -> classification.uppercase()
    }
}

private fun inferLocationTypeFromLocationName(locationName: String): LocationType? {
    val normalized = locationName.lowercase()
    return when {
        normalized.containsAny("detroit", "chicago", "boston", "los angeles", "new york", "philadelphia", "phoenix", "minneapolis", "seattle", "miami", "atlanta", "denver", "newark", "baltimore", "houston", "vegas", "cleveland", "dallas", "sacramento", "memphis", "portland") ->
            LocationType.RUINED_CITY
        normalized.containsAny("forest", "woods", "grove", "timberline", "willow", "redwood", "birch", "cedar", "sequoia", "pinelands", "fungus thicket") ->
            LocationType.FOREST
        normalized.containsAny("fort ", "outpost", "base ", "silo", "checkpoint", "battery", "garrison", "ordnance", "launch complex", "camp raptor", "redoubt") ->
            LocationType.MILITARY_BASE
        normalized.containsAny("fields", "belt", "vineyard", "pastures", "orchards", "paddies", "cropland", "harvest", "range", "basin") ->
            LocationType.FARMLAND
        normalized.containsAny("aquifer", "river", "stream", "springs", "lake", "water vein", "channel", "delta") ->
            LocationType.UNDERGROUND_RIVER
        normalized.containsAny("pass", "summit", "ridge", "col", "mountains", "divide", "notch", "switchback", "crest", "traverse", "gap") ->
            LocationType.MOUNTAIN_PASS
        normalized.containsAny("harbor", "bay", "shoreline", "pier", "port", "village", "seaside", "cove", "breakwater", "wharf", "quay", "jetty", "marina", "coast") ->
            LocationType.COASTAL_TOWN
        normalized.containsAny("blacksite", "research", "laboratory", "science", "facility", "biotech", "weapons lab", "accelerator", "cryolab", "containment", "geneforge", "telemetry") ->
            LocationType.RESEARCH_FACILITY
        normalized.containsAny("bog", "marsh", "fen", "mire", "wetland", "swale", "backwater", "reed", "sump") ->
            LocationType.RADIOACTIVE_SWAMP
        normalized.containsAny("crater", "caldera", "impact", "rim", "pit", "bowl", "hollow", "crown") ->
            LocationType.MEGACRATER
        normalized.containsAny("quarantine", "fever", "ward", "triage", "mortuary", "contagion", "parish", "infirmary", "sickhouse", "plague", "coughing") ->
            LocationType.PLAGUE_ZONE
        normalized.containsAny("scrap", "iron", "rust", "machine grave", "yard", "junk", "wiregut", "foundry", "gearstorm", "shredder") ->
            LocationType.SCRAP_HEAP
        normalized.containsAny("metro", "tunnel", "platform", "station", "rail", "concourse", "underpass", "catacomb", "transfer", "junction", "terminal") ->
            LocationType.ABANDONED_SUBWAY
        normalized.containsAny("mycelium", "spore", "mold", "fungal", "puffball", "lichen", "toadstool", "dustcap", "capgrave", "softbone") ->
            LocationType.FUNGAL_WASTES
        normalized.containsAny("glass", "mirror", "shard", "silica", "prism", "dunes", "burnglass", "bright waste", "heat mirage") ->
            LocationType.GLASS_DESERT
        normalized.containsAny("pilgrim", "shrine", "chanting", "sermon", "temple", "prophet", "martyr", "reliquary", "oracle", "static gospel", "tithe", "witness") ->
            LocationType.CULT_TERRITORY
        else -> null
    }
}

private fun String.containsAny(vararg needles: String): Boolean =
    needles.any { needle -> contains(needle) }
