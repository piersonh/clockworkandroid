package com.wordco.clockworkandroid.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.wordco.clockworkandroid.data.model.Task
import com.wordco.clockworkandroid.data.model.TaskProperties

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