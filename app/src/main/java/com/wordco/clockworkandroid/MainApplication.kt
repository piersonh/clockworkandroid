package com.wordco.clockworkandroid

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.wordco.clockworkandroid.core.data.local.AppDatabase
import com.wordco.clockworkandroid.core.data.local.TaskDao
import com.wordco.clockworkandroid.core.data.repository.TaskRepository
import com.wordco.clockworkandroid.core.data.repository.impl.TaskRepositoryImpl
import com.wordco.clockworkandroid.core.timer.TimerManager
import com.wordco.clockworkandroid.core.timer.TimerNotificationManager

class MainApplication : Application() {

    // originally were instance fields, not sure if this is better or worse

    companion object;

    lateinit var db: AppDatabase
    lateinit var taskDao: TaskDao
    lateinit var taskRepository: TaskRepository

    lateinit var timer: TimerManager


    override fun onCreate() {
        super.onCreate()
        db = AppDatabase.getDatabase(applicationContext)
//        CoroutineScope(Dispatchers.IO).launch {
//            db.clearAllTables()
//        }
        taskDao = db.taskDao()
        taskRepository = TaskRepositoryImpl(taskDao)

        timer = TimerManager(this)

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                TimerNotificationManager.CHANNEL_ID,
                "Timer Notifications",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "Live notifications for timer status"

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}