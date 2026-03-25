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
    val summary: String
)
