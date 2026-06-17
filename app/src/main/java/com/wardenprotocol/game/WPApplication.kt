package com.ivarna.wardenprotocol

import android.app.Application
import com.wardenprotocol.game.data.locale.LocaleApplier
import com.wardenprotocol.game.data.locale.LocaleStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WPApplication : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            val savedTag = LocaleStore.getSavedLocaleTag(this@WPApplication)
            LocaleApplier.applyToDelegate(savedTag)
        }
    }
}
