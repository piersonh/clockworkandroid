package com.wordco.clockworkandroid.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wordco.clockworkandroid.data.local.entities.MarkerEntity
import com.wordco.clockworkandroid.data.local.entities.SegmentEntity
import com.wordco.clockworkandroid.data.local.entities.TaskEntity

@Database(
    entities = [
        TaskEntity::class,
        SegmentEntity::class,
        MarkerEntity::class
               ],
    version = 2)
@TypeConverters(
    TimestampConverter::class,
    DurationConverter::class,
    TaskStatusConverter::class,
    ColorConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao() : TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context, AppDatabase::class.java, "clockwork_db"
                )
                    // .fallbackToDestructiveMigration() // Only for development - clears database on schema change
                    // .addMigrations(MIGRATION_1_2) // Add your migration strategies here
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}