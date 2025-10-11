package com.wordco.clockworkandroid

import android.app.Application
import com.wordco.clockworkandroid.core.domain.repository.ProfileRepository
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import com.wordco.clockworkandroid.core.domain.repository.TimerRepository

class MainApplication : Application() {

    lateinit var appContainer : AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = ProductionContainer(this)
    }
}