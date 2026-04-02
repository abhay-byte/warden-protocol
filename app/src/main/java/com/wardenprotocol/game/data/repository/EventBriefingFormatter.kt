package com.ivarna.wardenprotocol.data.repository

import com.ivarna.wardenprotocol.data.model.EventChoice
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

private fun EventChoice.buildExpandedChoiceDescription(): String = description.trim()
