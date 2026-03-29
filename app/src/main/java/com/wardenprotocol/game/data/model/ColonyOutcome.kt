package com.wardenprotocol.game.data.model

data class ColonyOutcome(
    val score: Int,
    val classification: String,
    val narrative: String,
    val settlementName: String,
    val detailedStats: OutcomeStats? = null,
    val aiReport: AiEndingReport? = null
)

data class AiEndingReport(
    val scoreDelta: Int,
    val adjustedScore: Int,
    val scoreReason: String,
    val headline: String,
    val civilizationVerdict: String,
    val failureCauses: List<String>,
    val survivalDrivers: List<String>,
    val timeline: List<AiTimelineEntry>,
    val detailedNarrative: String
)

data class AiTimelineEntry(
    val marker: String,
    val title: String,
    val status: String,
    val populationEstimate: String,
    val narrative: String
)

data class OutcomeStats(
    val survivors: Int,
    val yearsSinceWar: Int,
    val deaths: Int,
    val locationName: String,
    val locationTypeName: String,
    val travelRoute: String,
    val travelTime: String,
    val travelRisk: String,
    val travelDeaths: Int,
    val radiation: String,
    val water: String,
    val food: String,
    val shelter: String,
    val resources: String,
    val threats: String,
    val powerGrid: Int,
    val foodStores: Int,
    val medicalBay: Int,
    val securitySystem: Int,
    val constructionGear: Int,
    val atmosphereScrubbers: Int,
    val culturalArchive: Int,
    val scientificArchive: Int
)
