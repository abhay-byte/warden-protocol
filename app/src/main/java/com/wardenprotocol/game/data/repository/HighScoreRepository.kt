package com.ivarna.wardenprotocol.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ivarna.wardenprotocol.BuildConfig
import com.ivarna.wardenprotocol.data.model.ColonyOutcome
import com.ivarna.wardenprotocol.data.model.RunRecord
import com.ivarna.wardenprotocol.data.model.AiTimelineEntry
import com.ivarna.wardenprotocol.data.model.buildArchiveGradeLabel
import com.ivarna.wardenprotocol.data.model.buildArchiveOutcomeLabel
import com.ivarna.wardenprotocol.data.model.OutcomeStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "high_scores")

class HighScoreRepository(private val context: Context) {
    
    private val HIGH_SCORE_KEY = intPreferencesKey("high_score")
    private val RUN_HISTORY_KEY = stringPreferencesKey("run_history")
    private val SELECTED_AI_MODEL_KEY = stringPreferencesKey("selected_ai_model")
    
    val highScore: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[HIGH_SCORE_KEY] ?: 0
    }

    val runHistory: Flow<List<RunRecord>> = context.dataStore.data.map { preferences ->
        decodeRunHistory(preferences[RUN_HISTORY_KEY])
    }

    val leaderboard: Flow<List<RunRecord>> = runHistory.map { entries ->
        entries.sortedByDescending { it.score }.take(10)
    }

    val selectedAiModel: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SELECTED_AI_MODEL_KEY] ?: BuildConfig.NVIDIA_NIM_MODEL
    }
    
    suspend fun saveHighScore(score: Int) {
        context.dataStore.edit { preferences ->
            val currentHighScore = preferences[HIGH_SCORE_KEY] ?: 0
            if (score > currentHighScore) {
                preferences[HIGH_SCORE_KEY] = score
            }
        }
    }

    suspend fun saveRun(outcome: ColonyOutcome) {
        context.dataStore.edit { preferences ->
            val existing = decodeRunHistory(preferences[RUN_HISTORY_KEY]).toMutableList()
            val stats = outcome.detailedStats
            val report = outcome.aiReport
            val record = RunRecord(
                id = System.currentTimeMillis(),
                score = outcome.score,
                classification = outcome.classification,
                settlementName = outcome.settlementName,
                locationName = stats?.locationName ?: "Unknown Surface",
                survivors = stats?.survivors ?: 0,
                yearsSinceWar = stats?.yearsSinceWar ?: 0,
                completedAtMillis = System.currentTimeMillis(),
                summary = outcome.narrative.take(320),
                locationTypeName = stats?.locationTypeName.orEmpty(),
                outcomeLabel = buildArchiveOutcomeLabel(
                    score = outcome.score,
                    classification = outcome.classification,
                    stats = stats
                ),
                gradeLabel = buildArchiveGradeLabel(
                    score = outcome.score,
                    classification = outcome.classification
                ),
                fullNarrative = outcome.narrative,
                forecastVerdict = report?.civilizationVerdict.orEmpty(),
                timelinePayload = encodeTimeline(report?.timeline.orEmpty()),
                failureCausesPayload = encodeStringList(report?.failureCauses.orEmpty()),
                survivalDriversPayload = encodeStringList(report?.survivalDrivers.orEmpty()),
                baseScore = report?.let { outcome.score - it.scoreDelta } ?: outcome.score,
                scoreDelta = report?.scoreDelta ?: 0,
                scoreReason = report?.scoreReason.orEmpty(),
                reportHeadline = report?.headline.orEmpty(),
                outcomeStatsPayload = encodeOutcomeStats(stats)
            )
            existing.add(0, record)
            preferences[RUN_HISTORY_KEY] = encodeRunHistory(existing.take(25))
        }
    }

    suspend fun saveSelectedAiModel(modelId: String) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_AI_MODEL_KEY] = modelId
        }
    }

    private fun decodeRunHistory(raw: String?): List<RunRecord> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList(array.length()) {
                for (index in 0 until array.length()) {
                    val item = array.getJSONObject(index)
                    add(
                        RunRecord(
                            id = item.optLong("id"),
                            score = item.optInt("score"),
                            classification = item.optString("classification"),
                            settlementName = item.optString("settlementName"),
                            locationName = item.optString("locationName"),
                            survivors = item.optInt("survivors"),
                            yearsSinceWar = item.optInt("yearsSinceWar"),
                            completedAtMillis = item.optLong("completedAtMillis"),
                            summary = item.optString("summary"),
                            locationTypeName = item.optString("locationTypeName"),
                            outcomeLabel = item.optString("outcomeLabel"),
                            gradeLabel = item.optString("gradeLabel"),
                            fullNarrative = item.optString("fullNarrative"),
                            forecastVerdict = item.optString("forecastVerdict"),
                            timelinePayload = item.optString("timelinePayload"),
                            failureCausesPayload = item.optString("failureCausesPayload"),
                            survivalDriversPayload = item.optString("survivalDriversPayload"),
                            baseScore = item.optInt("baseScore"),
                            scoreDelta = item.optInt("scoreDelta"),
                            scoreReason = item.optString("scoreReason"),
                            reportHeadline = item.optString("reportHeadline"),
                            outcomeStatsPayload = item.optString("outcomeStatsPayload")
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun encodeRunHistory(entries: List<RunRecord>): String {
        val array = JSONArray()
        entries.forEach { entry ->
            array.put(
                JSONObject().apply {
                    put("id", entry.id)
                    put("score", entry.score)
                    put("classification", entry.classification)
                    put("settlementName", entry.settlementName)
                    put("locationName", entry.locationName)
                    put("survivors", entry.survivors)
                    put("yearsSinceWar", entry.yearsSinceWar)
                    put("completedAtMillis", entry.completedAtMillis)
                    put("summary", entry.summary)
                    put("locationTypeName", entry.locationTypeName)
                    put("outcomeLabel", entry.outcomeLabel)
                    put("gradeLabel", entry.gradeLabel)
                    put("fullNarrative", entry.fullNarrative)
                    put("forecastVerdict", entry.forecastVerdict)
                    put("timelinePayload", entry.timelinePayload)
                    put("failureCausesPayload", entry.failureCausesPayload)
                    put("survivalDriversPayload", entry.survivalDriversPayload)
                    put("baseScore", entry.baseScore)
                    put("scoreDelta", entry.scoreDelta)
                    put("scoreReason", entry.scoreReason)
                    put("reportHeadline", entry.reportHeadline)
                    put("outcomeStatsPayload", entry.outcomeStatsPayload)
                }
            )
        }
        return array.toString()
    }

    private fun encodeOutcomeStats(stats: OutcomeStats?): String {
        if (stats == null) return ""
        return JSONObject().apply {
            put("survivors", stats.survivors)
            put("yearsSinceWar", stats.yearsSinceWar)
            put("deaths", stats.deaths)
            put("locationName", stats.locationName)
            put("locationTypeName", stats.locationTypeName)
            put("travelRoute", stats.travelRoute)
            put("travelTime", stats.travelTime)
            put("travelRisk", stats.travelRisk)
            put("travelDeaths", stats.travelDeaths)
            put("radiation", stats.radiation)
            put("water", stats.water)
            put("food", stats.food)
            put("shelter", stats.shelter)
            put("resources", stats.resources)
            put("threats", stats.threats)
            put("powerGrid", stats.powerGrid)
            put("foodStores", stats.foodStores)
            put("medicalBay", stats.medicalBay)
            put("securitySystem", stats.securitySystem)
            put("constructionGear", stats.constructionGear)
            put("atmosphereScrubbers", stats.atmosphereScrubbers)
            put("culturalArchive", stats.culturalArchive)
            put("scientificArchive", stats.scientificArchive)
        }.toString()
    }

    private fun encodeStringList(values: List<String>): String {
        val array = JSONArray()
        values.forEach(array::put)
        return array.toString()
    }

    private fun encodeTimeline(entries: List<AiTimelineEntry>): String {
        val array = JSONArray()
        entries.forEach { entry ->
            array.put(
                JSONObject().apply {
                    put("marker", entry.marker)
                    put("title", entry.title)
                    put("status", entry.status)
                    put("population_estimate", entry.populationEstimate)
                    put("narrative", entry.narrative)
                }
            )
        }
        return array.toString()
    }
}
