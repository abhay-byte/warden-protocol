package com.wardenprotocol.game.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.wardenprotocol.game.data.model.ColonyOutcome
import com.wardenprotocol.game.data.model.RunRecord
import com.wardenprotocol.game.data.model.buildArchiveGradeLabel
import com.wardenprotocol.game.data.model.buildArchiveOutcomeLabel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "high_scores")

class HighScoreRepository(private val context: Context) {
    
    private val HIGH_SCORE_KEY = intPreferencesKey("high_score")
    private val RUN_HISTORY_KEY = stringPreferencesKey("run_history")
    
    val highScore: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[HIGH_SCORE_KEY] ?: 0
    }

    val runHistory: Flow<List<RunRecord>> = context.dataStore.data.map { preferences ->
        decodeRunHistory(preferences[RUN_HISTORY_KEY])
    }

    val leaderboard: Flow<List<RunRecord>> = runHistory.map { entries ->
        entries.sortedByDescending { it.score }.take(10)
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
                )
            )
            existing.add(0, record)
            preferences[RUN_HISTORY_KEY] = encodeRunHistory(existing.take(25))
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
                            gradeLabel = item.optString("gradeLabel")
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
                }
            )
        }
        return array.toString()
    }
}
