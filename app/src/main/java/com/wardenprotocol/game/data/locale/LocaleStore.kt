package com.wardenprotocol.game.data.locale

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.localeDataStore by preferencesDataStore(name = "locale_prefs")

object LocaleStore {
    private val KEY_LOCALE_TAG = stringPreferencesKey("locale_tag")

    val SYSTEM_DEFAULT_TAG = ""

    val supportedLocales: List<LocaleOption> = listOf(
        LocaleOption(tag = SYSTEM_DEFAULT_TAG, displayKey = "system"),
        LocaleOption(tag = "en", displayKey = "english"),
        LocaleOption(tag = "ru", displayKey = "russian")
    )

    suspend fun getSavedLocaleTag(context: Context): String {
        val prefs = context.localeDataStore.data.first()
        return prefs[KEY_LOCALE_TAG] ?: SYSTEM_DEFAULT_TAG
    }

    fun observeLocaleTag(context: Context): Flow<String> =
        context.localeDataStore.data.map { prefs -> prefs[KEY_LOCALE_TAG] ?: SYSTEM_DEFAULT_TAG }

    suspend fun saveLocaleTag(context: Context, tag: String) {
        context.localeDataStore.edit { prefs ->
            if (tag == SYSTEM_DEFAULT_TAG) {
                prefs.remove(KEY_LOCALE_TAG)
            } else {
                prefs[KEY_LOCALE_TAG] = tag
            }
        }
    }

    fun applyLocale(tag: String) {
        val list = if (tag.isBlank()) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(tag)
        }
        AppCompatDelegate.setApplicationLocales(list)
    }
}

data class LocaleOption(
    val tag: String,
    val displayKey: String
)
