package com.wordco.clockworkandroid

import android.app.Application
import com.wordco.clockworkandroid.core.domain.repository.ProfileRepository
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import com.wordco.clockworkandroid.core.ui.timer.Timer
import com.wordco.clockworkandroid.database.data.local.AppDatabase
import com.wordco.clockworkandroid.database.data.local.ProfileDao
import com.wordco.clockworkandroid.database.data.local.TaskDao
import com.wordco.clockworkandroid.database.data.repository.ProfileRepositoryImpl
import com.wordco.clockworkandroid.database.data.repository.TaskRepositoryImpl
import com.wordco.clockworkandroid.timer_feature.ui.timer.TimerManager

class MainApplication : Application() {

    lateinit var db: AppDatabase
    lateinit var taskDao: TaskDao
    lateinit var profileDao: ProfileDao
    lateinit var taskRepository: TaskRepository
    lateinit var profileRepository: ProfileRepository

    lateinit var timer: Timer


    override fun onCreate() {
        super.onCreate()
        db = AppDatabase.getDatabase(applicationContext)

        taskDao = db.taskDao()
        taskRepository = TaskRepositoryImpl(taskDao)

        profileDao = db.profileDao()
        profileRepository = ProfileRepositoryImpl(profileDao)

        timer = TimerManager(this)
    }
}