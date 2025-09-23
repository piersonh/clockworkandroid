package com.wordco.clockworkandroid.database.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wordco.clockworkandroid.database.data.local.entities.MarkerEntity
import com.wordco.clockworkandroid.database.data.local.entities.ProfileEntity
import com.wordco.clockworkandroid.database.data.local.entities.SegmentEntity
import com.wordco.clockworkandroid.database.data.local.entities.TaskEntity
import com.wordco.clockworkandroid.database.data.repository.TaskRepositoryImpl
import com.wordco.clockworkandroid.database.data.util.DummyData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TaskEntity::class,
        SegmentEntity::class,
        MarkerEntity::class,
        ProfileEntity::class,
               ],
    version = 11)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao() : TaskDao

    abstract fun profileDao() : ProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context, AppDatabase::class.java, "clockwork_db"
                )
                    .fallbackToDestructiveMigration(true) // Only for development - clears database on schema change
                    // .addMigrations(MIGRATION_1_2) // Add your migration strategies here
                    .addCallback(object: Callback() {


                        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                            super.onDestructiveMigration(db)

                            val taskDao = getDatabase(context).taskDao()
                            val taskRepo = TaskRepositoryImpl(taskDao)
                            CoroutineScope(Dispatchers.IO).launch {
                                DummyData.TASKS.forEach {
                                    task ->
                                    taskRepo.insertTask(task)
                                }
                            }
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)

                            val resetOnStart = true
                            val taskDao = getDatabase(context).taskDao()
                            val taskRepo = TaskRepositoryImpl(taskDao)
                            if (resetOnStart) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    getDatabase(context).clearAllTables()
                                    DummyData.TASKS.forEach {
                                            task ->
                                        taskRepo.insertTask(task)
                                    }
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}