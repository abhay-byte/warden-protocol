package com.wardenprotocol.game.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "high_scores")

class HighScoreRepository(private val context: Context) {
    
    private val HIGH_SCORE_KEY = intPreferencesKey("high_score")
    
    val highScore: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[HIGH_SCORE_KEY] ?: 0
    }
    
    suspend fun saveHighScore(score: Int) {
        context.dataStore.edit { preferences ->
            val currentHighScore = preferences[HIGH_SCORE_KEY] ?: 0
            if (score > currentHighScore) {
                preferences[HIGH_SCORE_KEY] = score
            }
        }
    }
}
