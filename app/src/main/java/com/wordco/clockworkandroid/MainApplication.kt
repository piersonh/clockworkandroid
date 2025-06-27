package com.wordco.clockworkandroid

import android.app.Application
import com.wordco.clockworkandroid.data.local.AppDatabase
import com.wordco.clockworkandroid.data.local.TaskDao
import com.wordco.clockworkandroid.data.repository.TaskRepositoryImpl
import com.wordco.clockworkandroid.domain.model.Timer
import com.wordco.clockworkandroid.domain.repository.TaskRepository

class MainApplication : Application() {

    // originally were instance fields, not sure if this is better or worse

    companion object;

    lateinit var db: AppDatabase
    lateinit var taskDao: TaskDao
    lateinit var taskRepository: TaskRepository

    var timer  = Timer()


    override fun onCreate() {
        super.onCreate()
        db = AppDatabase.getDatabase(applicationContext)
//        CoroutineScope(Dispatchers.IO).launch {
//            db.clearAllTables()
//        }
        taskDao = db.taskDao()
        taskRepository = TaskRepositoryImpl(taskDao)
    }
}