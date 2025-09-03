package com.wordco.clockworkandroid

import android.app.Application
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import com.wordco.clockworkandroid.core.ui.timer.Timer
import com.wordco.clockworkandroid.database.data.local.AppDatabase
import com.wordco.clockworkandroid.database.data.local.TaskDao
import com.wordco.clockworkandroid.database.data.repository.TaskRepositoryImpl
import com.wordco.clockworkandroid.timer_feature.ui.timer.TimerManager

class MainApplication : Application() {

    // originally were instance fields, not sure if this is better or worse

    companion object;

    lateinit var db: AppDatabase
    lateinit var taskDao: TaskDao
    lateinit var taskRepository: TaskRepository

    lateinit var timer: Timer


    override fun onCreate() {
        super.onCreate()
        db = AppDatabase.getDatabase(applicationContext)
        taskDao = db.taskDao()
        taskRepository = TaskRepositoryImpl(taskDao)

        timer = TimerManager(this)
    }
}