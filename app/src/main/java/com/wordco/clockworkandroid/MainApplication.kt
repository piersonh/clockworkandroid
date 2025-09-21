package com.wordco.clockworkandroid

import android.app.Application
import com.wordco.clockworkandroid.core.domain.repository.ProfileRepository
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import com.wordco.clockworkandroid.core.ui.timer.Timer

class MainApplication : Application() {

    private lateinit var appContainer : AppContainer
    lateinit var taskRepository: TaskRepository
    lateinit var profileRepository: ProfileRepository

    lateinit var timer: Timer


    override fun onCreate() {
        super.onCreate()
        appContainer = ProductionContainer(this)
        taskRepository = appContainer.sessionRepository
        profileRepository = appContainer.profileRepository
        timer = appContainer.timer
    }
}