package com.ivarna.wardenprotocol.data.model

data class GameEvent(
    val id: String,
    val title: String,
    val description: String,
    val choiceA: EventChoice,
    val choiceB: EventChoice,
    val choiceC: EventChoice? = null
)

data class EventChoice(
    val label: String,
    val description: String,
    val knownEffect: String,
    val hiddenRisk: Float = 0f,
    val outcome: EventOutcome
)

data class EventOutcome(
    val survivorDelta: Int = 0,
    val systemDeltas: Map<String, Int> = emptyMap(),
    val databaseDeltas: Map<String, Int> = emptyMap(),
    val probesDelta: Int = 0,
    val narrativeText: String
)
