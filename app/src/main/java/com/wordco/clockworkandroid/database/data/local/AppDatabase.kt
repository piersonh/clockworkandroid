package com.wordco.clockworkandroid.database.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wordco.clockworkandroid.database.data.local.entities.MarkerEntity
import com.wordco.clockworkandroid.database.data.local.entities.ProfileEntity
import com.wordco.clockworkandroid.database.data.local.entities.ReminderEntity
import com.wordco.clockworkandroid.database.data.local.entities.SegmentEntity
import com.wordco.clockworkandroid.database.data.local.entities.TaskEntity
import com.wordco.clockworkandroid.database.data.util.DummyData
import com.wordco.clockworkandroid.database.data.util.UserDataPackage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TaskEntity::class,
        SegmentEntity::class,
        MarkerEntity::class,
        ProfileEntity::class,
        ReminderEntity::class,
               ],
    version = 14)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao() : TaskDao

    abstract fun profileDao() : ProfileDao

    abstract fun reminderDao() : ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context, AppDatabase::class.java, "clockwork_db"
                )
                    .fallbackToDestructiveMigration(true) // Only for development - clears database on schema change
                    .addMigrations(
                        MIGRATION_12_13,
                        MIGRATION_13_14
                    ) // Add your migration strategies here
                    .addCallback(object: Callback() {


//                        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
//                            super.onDestructiveMigration(db)
//
//                            val taskDao = getDatabase(context).taskDao()
//                            val taskRepo = TaskRepositoryImpl(taskDao)
//                            CoroutineScope(Dispatchers.IO).launch {
//                                DummyData.TASKS.forEach {
//                                    task ->
//                                    taskRepo.insertTask(task)
//                                }
//                            }
//                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)

                            val loadPackage =
                                null
                                //DummyData.package0_empty
                                //DummyData.package1
                                //DummyData.package3_csStudentHistory
                                //DummyData.package4_laundry
                            if (loadPackage != null) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    getDatabase(context).clearAllTables()

                                    insertPackage(
                                        loadPackage,
                                        taskDao = getDatabase(context).taskDao(),
                                        profileDao = getDatabase((context)).profileDao()
                                    )
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                return instance
            }
        }

        private suspend fun insertPackage(
            dataPackage: UserDataPackage,
            taskDao: TaskDao,
            profileDao: ProfileDao,
        ) {
            dataPackage.run {
                profiles.forEach {
                    profileDao.insertProfile(it)
                }
                taskDao.insertTasks(sessions)
                taskDao.insertSegments(segments)
                taskDao.insertMarkers(markers)
            }
        }
    }
}