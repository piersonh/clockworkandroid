package com.wordco.clockworkandroid.database.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.wordco.clockworkandroid.database.data.local.entities.MarkerEntity
import com.wordco.clockworkandroid.database.data.local.entities.SegmentEntity
import com.wordco.clockworkandroid.database.data.local.entities.TaskEntity
import com.wordco.clockworkandroid.database.data.local.entities.TaskWithExecutionDataObject
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTask(taskEntity: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTasks(tasks: List<TaskEntity>)


    @Update
    suspend fun updateTask(task: TaskEntity)

    @Transaction
    @Query("DELETE FROM TaskEntity WHERE taskId = :id")
    suspend fun deleteTask(id: Long)


    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSegment(segment: SegmentEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSegments(segments: List<SegmentEntity>)

    @Update
    suspend fun updateSegment(segment: SegmentEntity)

    @Transaction
    suspend fun updateSegmentAndInsertNew(existing: SegmentEntity, new: SegmentEntity) {
        updateSegment(existing)
        insertSegment(new)
    }

    @Transaction
    @Query("""
        SELECT T.taskId
        FROM TaskEntity T
        JOIN SegmentEntity S ON T.taskId = S.taskId
        WHERE T.status = 1
          AND S.type IN (0, 1)
          AND S.startTime = (
              SELECT MAX(startTime)
              FROM SegmentEntity
              WHERE taskId = T.taskId
          )
        LIMIT 1
    """)
    suspend fun getActiveTaskId() : Long?


    @Transaction
    @Query("SELECT * FROM TaskEntity WHERE taskId == :taskId")
    fun getTaskWithExecutionData(taskId: Long) : Flow<TaskWithExecutionDataObject?>


    @Transaction
    @Query("SELECT * FROM TaskEntity")
    fun getTasksWithExecutionData() : Flow<List<TaskWithExecutionDataObject>>

    @Transaction
    @Query("SELECT * FROM TaskEntity WHERE status IN (0,1)")
    fun getTodoTasksWithExecutionData() : Flow<List<TaskWithExecutionDataObject>>

    @Transaction
    @Query("SELECT * FROM TaskEntity WHERE status = 2")
    fun getCompletedTasksWithExecutionData() : Flow<List<TaskWithExecutionDataObject>>

    @Transaction
    @Query("SELECT * FROM TaskEntity WHERE profileId = :profileId")
    fun getSessionsForProfile(profileId: Long) : Flow<List<TaskWithExecutionDataObject>>


    @Transaction
    @Query("SELECT * FROM TaskEntity WHERE profileId = :profileId AND status = 2")
    fun getCompletedSessionsForProfile(profileId: Long) : Flow<List<TaskWithExecutionDataObject>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMarker(toMarkerEntity: MarkerEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMarkers(markers: List<MarkerEntity>)
}