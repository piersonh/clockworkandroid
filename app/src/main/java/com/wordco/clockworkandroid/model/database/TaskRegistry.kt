package com.wordco.clockworkandroid.model.database

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverters
import com.wordco.clockworkandroid.model.Segment
import com.wordco.clockworkandroid.model.Task
import com.wordco.clockworkandroid.model.TaskProperties
import java.time.Instant

val TASKS = listOf(
    Task("Assignment", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.Green),
    Task("Project Plan", Instant.parse("2025-04-17T18:29:04Z"), 2, Color.Blue),
    Task("Homework 99", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.White),
    Task("Homework 99.5", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.Cyan),
    Task("Homework -1", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.Black),
    Task("Homework 100", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.Red),
    Task("Evil Homework 101", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.Magenta),
    Task("Super Homework 102", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.Yellow),
)


@Database(entities = [TaskProperties::class, Segment::class], version = 1)
@TypeConverters(
    TimestampConverter::class,
    DurationConverter::class,
    TaskStatusConverter::class,
    ColorConverter::class
)
abstract class TaskRegistry : RoomDatabase() {
    abstract fun propertiesDao(): TaskPropertiesDao
    abstract fun segmentDao(): SegmentDao

    companion object {
        @Volatile
        private var INSTANCE: TaskRegistry? = null

        fun getDatabase(context: Context): TaskRegistry {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, TaskRegistry::class.java, "task_registry"
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


@Dao
interface TaskPropertiesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(taskProperties: TaskProperties): Long

    @Transaction
    @Query("SELECT * FROM task_properties WHERE id = :taskId")
    suspend fun getTask(taskId: Long): Task

    @Transaction
    @Query("SELECT * FROM task_properties")
    fun getAllTasks(): List<Task>

    @Delete
    suspend fun delete(taskProperties: TaskProperties)
}

@Dao
interface SegmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSegment(segment: Segment): Long

    @Delete
    suspend fun deleteSegment(segment: Segment)

    @Query("SELECT * FROM segment WHERE taskId = :taskId")
    suspend fun getSegmentsForTask(taskId: Long): List<Segment>
}





