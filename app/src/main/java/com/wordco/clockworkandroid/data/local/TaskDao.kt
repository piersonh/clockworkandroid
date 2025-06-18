package com.wordco.clockworkandroid.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.wordco.clockworkandroid.data.local.entities.MarkerEntity
import com.wordco.clockworkandroid.data.local.entities.SegmentEntity
import com.wordco.clockworkandroid.data.local.entities.TaskEntity
import com.wordco.clockworkandroid.data.local.entities.TaskWithExecutionDataObject
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(taskEntity: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSegments(segments: List<SegmentEntity>)

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

    @Transaction
    @Query("SELECT * FROM TaskEntity")
    fun getTasksWithExecutionData() : Flow<List<TaskWithExecutionDataObject>>
}