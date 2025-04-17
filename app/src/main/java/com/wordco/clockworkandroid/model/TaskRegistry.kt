package com.wordco.clockworkandroid.model

import androidx.compose.ui.graphics.Color
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

val TASKS = listOf(
    Task("Assignment", 2660, 60,  33, 3, Color.Green, Status.RUNNING),
    Task("Project Plan", 30000, 60, 20, 2, Color.Blue, Status.SUSPENDED),
    Task("Homework 99", 100, 60, System.currentTimeMillis() - 100000000, 3, Color.White, Status.SCHEDULED),
    Task("Homework 99.5", 100, 60, System.currentTimeMillis(), 3, Color.Cyan, Status.SCHEDULED),
    Task("Homework -1", 100, 60, 0, 3, Color.Black, Status.SCHEDULED),
    Task("Homework 100", 100, 60, System.currentTimeMillis() + 22000000, 3, Color.Red, Status.SCHEDULED),
    Task("Evil Homework 101", 100, 60, System.currentTimeMillis() + 25000000, 3, Color.Magenta, Status.SCHEDULED),
    Task("Super Homework 102", 100, 60, System.currentTimeMillis() + 111000000, 3, Color.Yellow, Status.SCHEDULED),
)

@Dao
interface TaskPropertiesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
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
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSegment(segment: Segment): Long

    @Delete
    suspend fun deleteSegment(segment: Segment)

    @Query("SELECT * FROM segment WHERE taskId = :taskId")
    suspend fun getSegmentsForTask(taskId: Long): List<Segment>
}