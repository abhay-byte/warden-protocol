package com.wardenprotocol.game.data.repository

import com.wardenprotocol.game.data.model.EventChoice
import com.wardenprotocol.game.data.model.EventOutcome
import com.wardenprotocol.game.data.model.GameEvent

internal fun GameEvent.withExpandedBriefing(): GameEvent {
    val choices = listOfNotNull(choiceA, choiceB, choiceC)
    return copy(
        description = buildExpandedEventDescription(choices),
        choiceA = choiceA.withExpandedBriefing(),
        choiceB = choiceB.withExpandedBriefing(),
        choiceC = choiceC?.withExpandedBriefing()
    )
}

private fun GameEvent.buildExpandedEventDescription(choices: List<EventChoice>): String {
    val hiddenRiskCount = choices.count { it.hiddenRisk > 0f }
    val categorySummary = when {
        id.startsWith("vault_") -> "This is an internal vault incident. The decision is mostly about what the bunker can afford to lose: people, system integrity, or moral ground."
        id.startsWith("surface_") -> "This is a surface-facing incident. The choice is usually a trade between contact, salvage, environmental danger, and immediate casualties."
        id.startsWith("cosmic_") -> "This is an anomalous event. Practical gains often come tied to archive damage, psychological strain, or consequences the vault cannot model cleanly."
        id.startsWith("apex_human_") -> "This is a top-tier human threat. Even a good answer is damage control, not a clean victory."
        id.startsWith("apex_ai_") -> "This is a top-tier machine threat. The safest protocol usually still costs infrastructure, secrecy, or lives."
        id.startsWith("apex_alien_") -> "This is a top-tier nonhuman threat. Command should assume incomplete understanding and plan for brutal long-term fallout."
        else -> "This incident presents a direct operational tradeoff inside a collapsing survival system."
    }
    val protocolSummary = buildString {
        append("Command has ")
        append(choices.size)
        append(if (choices.size == 1) " response protocol" else " response protocols")
        append(" available.")
        append(" ")
        append(
            when {
                hiddenRiskCount <= 0 -> "Current modeling shows no explicit hidden fallout beyond the listed tradeoffs."
                hiddenRiskCount == 1 -> "One protocol carries uncertain fallout beyond the visible tradeoff."
                else -> "$hiddenRiskCount protocols carry uncertain fallout beyond the visible tradeoffs."
            }
        )
    }
    return buildString {
        append(description.trim())
        append("\n\n")
        append(categorySummary)
        append(" ")
        append(protocolSummary)
    }
}

private fun EventChoice.withExpandedBriefing(): EventChoice = copy(
    description = buildExpandedChoiceDescription()
)

private fun EventChoice.buildExpandedChoiceDescription(): String {
    val effectSummary = renderOutcomeSummary(outcome)
    val riskSummary = when {
        hiddenRisk >= 0.45f -> "Uncertainty is severe. The visible cost is probably not the full cost."
        hiddenRisk >= 0.25f -> "Uncertainty is meaningful. Secondary fallout is plausible even if the immediate exchange looks acceptable."
        hiddenRisk > 0f -> "Uncertainty is low but real. There is still room for the situation to degrade after commitment."
        else -> "No additional hidden complication is currently projected."
    }
    return buildString {
        append(description.trim())
        append(" ")
        append("Command expectation: ")
        append(knownEffect.trim().trimEnd('.'))
        append(". ")
        append("Projected operational impact: ")
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
