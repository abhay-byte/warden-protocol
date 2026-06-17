package com.wardenprotocol.game.data.locale

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object LocaleApplier {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    fun applyAndPersist(context: Context, tag: String) {
        scope.launch {
            LocaleStore.saveLocaleTag(context, tag)
            applyToDelegate(tag)
        }
    }

    fun applyToDelegate(tag: String) {
        val list = if (tag.isBlank()) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(tag)
        }
        AppCompatDelegate.setApplicationLocales(list)
    }
}
