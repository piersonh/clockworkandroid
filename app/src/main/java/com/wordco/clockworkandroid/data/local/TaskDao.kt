package com.wordco.clockworkandroid.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.wordco.clockworkandroid.data.model.Marker
import com.wordco.clockworkandroid.data.model.Segment
import com.wordco.clockworkandroid.data.model.Task
import com.wordco.clockworkandroid.data.model.TaskProperties
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskProperties(taskProperties: TaskProperties): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSegment(segment: Segment): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarker(marker: Marker): Long

    @Transaction
    suspend fun insertTask(task: Task) {
        val taskId = insertTaskProperties(task.taskProperties)

        for (segment in task.segments) {
            val segmentToInsert = segment.copy(taskId = taskId)
            insertSegment(segmentToInsert)

        }

        for (marker in task.markers) {
            val markerToInsert = marker.copy(taskId = taskId)
            insertMarker(markerToInsert)

        }
    }

    @Transaction
    @Query("SELECT * FROM task_properties")
    fun getAllTasks(): Flow<List<Task>>
}