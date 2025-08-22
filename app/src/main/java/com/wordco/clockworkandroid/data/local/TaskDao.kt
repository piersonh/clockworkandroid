package com.wordco.clockworkandroid.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.wordco.clockworkandroid.data.local.entities.MarkerEntity
import com.wordco.clockworkandroid.data.local.entities.SegmentEntity
import com.wordco.clockworkandroid.data.local.entities.TaskEntity
import com.wordco.clockworkandroid.data.local.entities.TaskWithExecutionDataObject
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // TODO: UPSERT??

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(taskEntity: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)


    @Update
    suspend fun updateTask(task: TaskEntity)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSegment(segment: SegmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSegments(segments: List<SegmentEntity>)

    @Update
    suspend fun updateSegment(segment: SegmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarkers(markers: List<MarkerEntity>)

//    @Transaction
//    suspend fun insertTask(task: Task) {
//        val taskId = insertTaskProperties(task.taskProperties)
//
//        for (segment in task.segments) {
//            val segmentToInsert = segment.copy(taskId = taskId)
//            insertSegment(segmentToInsert)
//
//        }
//
//        for (marker in task.markers) {
//            val markerToInsert = marker.copy(taskId = taskId)
//            insertMarker(markerToInsert)
//
//        }
//    }

    @Query("SELECT EXISTS (SELECT 1 FROM TaskEntity WHERE status IN (1,2))")
    suspend fun hasActiveTask() : Boolean

    @Query("SELECT * FROM TaskEntity WHERE status IN (1,2) LIMIT 1")
    fun getActiveTask() : Flow<TaskWithExecutionDataObject?>


    @Transaction
    @Query("SELECT * FROM TaskEntity WHERE taskId == :taskId")
    fun getTaskWithExecutionData(taskId: Long) : Flow<TaskWithExecutionDataObject>


    @Transaction
    @Query("SELECT * FROM TaskEntity")
    fun getTasksWithExecutionData() : Flow<List<TaskWithExecutionDataObject>>
}