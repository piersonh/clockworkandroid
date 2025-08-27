package com.wordco.clockworkandroid

import android.app.Application
import com.wordco.clockworkandroid.core.data.local.AppDatabase
import com.wordco.clockworkandroid.core.data.local.TaskDao
import com.wordco.clockworkandroid.core.data.repository.TaskRepository
import com.wordco.clockworkandroid.core.data.repository.impl.TaskRepositoryImpl
import com.wordco.clockworkandroid.core.domain.timer.Timer
import kotlinx.coroutines.Dispatchers

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
//        CoroutineScope(Dispatchers.IO).launch {
//            db.clearAllTables()
//        }
        taskDao = db.taskDao()
        taskRepository = TaskRepositoryImpl(taskDao)

        timer = Timer(Dispatchers.Default, taskRepository)
    }
}