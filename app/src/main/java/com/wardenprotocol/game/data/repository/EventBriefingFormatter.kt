package com.ivarna.wardenprotocol.data.repository

import com.ivarna.wardenprotocol.data.model.EventChoice
import com.ivarna.wardenprotocol.data.model.EventOutcome
import com.ivarna.wardenprotocol.data.model.GameEvent

internal fun GameEvent.withExpandedBriefing(): GameEvent {
    return copy(
        description = buildExpandedEventDescription(),
        choiceA = choiceA.withExpandedBriefing(),
        choiceB = choiceB.withExpandedBriefing(),
        choiceC = choiceC?.withExpandedBriefing()
    )
}

private fun GameEvent.buildExpandedEventDescription(): String {
    return description.trim()
}

private fun EventChoice.withExpandedBriefing(): EventChoice = copy(
    description = buildExpandedChoiceDescription()
)

private fun EventChoice.buildExpandedChoiceDescription(): String {
    val effectSummary = renderOutcomeSummary(outcome)
    val riskSummary = when {
        hiddenRisk >= 0.45f -> "Hidden fallout risk is severe."
        hiddenRisk >= 0.25f -> "Hidden fallout is meaningfully possible."
        hiddenRisk > 0f -> "Hidden fallout is possible."
        else -> "No additional hidden complication is currently projected."
    }
    return buildString {
        append(description.trim())
        append(" ")
        append("Visible tradeoff: ")
        append(knownEffect.trim().trimEnd('.'))
        append(". ")
        append("Forecast: ")
        append(effectSummary)
        append(" ")
        append(riskSummary)
    }.replace(Regex("\\s+"), " ").trim()
}

private fun renderOutcomeSummary(outcome: EventOutcome): String {
    val parts = mutableListOf<String>()
    if (outcome.survivorDelta != 0) {
        val delta = outcome.survivorDelta
        parts += if (delta > 0) {
            "survivor count rises by $delta"
        } else {
            "survivor count falls by ${-delta}"
        }
    }
    outcome.systemDeltas
        .toSortedMap(compareBy(::systemDisplayLabel))
        .forEach { (system, delta) ->
            parts += "${systemDisplayLabel(system)} ${signedDelta(delta)}"
        }
    outcome.databaseDeltas
        .toSortedMap(compareBy(::archiveDisplayLabel))
        .forEach { (database, delta) ->
            parts += "${archiveDisplayLabel(database)} ${signedDelta(delta)}"
        }
    if (outcome.probesDelta != 0) {
        val delta = outcome.probesDelta
        parts += if (delta > 0) {
            "surface probes +$delta"
        } else {
            "surface probes ${signedDelta(delta)}"
        }
    }
    return if (parts.isEmpty()) {
        "no direct stat change is forecast, but the narrative consequences remain real."
    } else {
        parts.joinToString(separator = "; ", postfix = ".")
    }
}

private fun signedDelta(delta: Int): String = if (delta > 0) "+$delta" else delta.toString()

private fun systemDisplayLabel(key: String): String = when (key) {
    "powerGrid" -> "power grid"
    "foodStores" -> "food stores"
    "medicalBay" -> "medical bay"
    "securitySystem" -> "security system"
    "constructionGear" -> "construction gear"
    "atmosphereScrubbers" -> "atmosphere scrubbers"
    "radiationScanner" -> "radiation scanners"
    "waterScanner" -> "water scanners"
    "agriculturalScanner" -> "agricultural scanners"
    "structureScanner" -> "structure scanners"
    "resourceScanner" -> "resource scanners"
    "threatAssessment" -> "threat assessment"
    else -> key
}

private fun archiveDisplayLabel(key: String): String = when (key) {
    "culturalArchive" -> "cultural archive"
    "scientificArchive" -> "scientific archive"
    else -> key
}
