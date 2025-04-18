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
import com.wordco.clockworkandroid.model.TaskRegistryViewModel
import kotlinx.coroutines.flow.Flow
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
    abstract fun taskDao() : TaskDao
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
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskProperties(taskProperties: TaskProperties): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSegment(segment: Segment): Long

    @Transaction
    suspend fun insertTask(task: Task) {
        val taskId = insertTaskProperties(task.taskProperties)

        for (segment in task.segments) {
            val segmentToInsert = segment.copy(taskId = taskId)
            insertSegment(segmentToInsert)

        }
    }

    @Transaction
    @Query("SELECT * FROM task_properties")
    fun getAllTasks(): Flow<List<Task>>
}


@Dao
interface TaskPropertiesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskProperties(taskProperties: TaskProperties): Long

    @Transaction
    @Query("SELECT * FROM task_properties WHERE id = :taskId")
    suspend fun getTaskProperties(taskId: Long): Task

    @Transaction
    @Query("SELECT * FROM task_properties")
    fun getAllTasksProperties(): List<Task>

    @Delete
    suspend fun deleteTaskProperties(taskProperties: TaskProperties)
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





