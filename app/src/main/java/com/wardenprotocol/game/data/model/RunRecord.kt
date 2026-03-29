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
    val gradeLabel: String = ""
)

fun RunRecord.locationTypeOrNull(): LocationType? =
    locationTypeName
        .takeIf { it.isNotBlank() }
        ?.let { raw -> runCatching { enumValueOf<LocationType>(raw) }.getOrNull() }

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
