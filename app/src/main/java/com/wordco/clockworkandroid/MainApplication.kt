package com.wordco.clockworkandroid

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner

class MainApplication : Application() {

    lateinit var appContainer : AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = ProductionContainer(this)

        ProcessLifecycleOwner.get().lifecycle
            .addObserver(appContainer.restoreTimerObserver)
    }
}