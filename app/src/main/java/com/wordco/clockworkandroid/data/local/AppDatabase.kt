package com.wordco.clockworkandroid.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wordco.clockworkandroid.data.model.Marker
import com.wordco.clockworkandroid.data.model.Segment
import com.wordco.clockworkandroid.data.model.TaskProperties

@Database(entities = [TaskProperties::class, Segment::class, Marker::class], version = 2)
@TypeConverters(
    TimestampConverter::class,
    DurationConverter::class,
    TaskStatusConverter::class,
    ColorConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao() : TaskDao
    abstract fun propertiesDao(): TaskPropertiesDao
    abstract fun segmentDao(): SegmentDao
    abstract fun markerDao(): MarkerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "clockwork_db"
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