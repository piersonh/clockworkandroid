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


    @Transaction
    suspend fun updateSegmentAndInsertNew(existing: SegmentEntity, new: SegmentEntity) {
        updateSegment(existing)
        insertSegment(new)
    }


    @Query("""
        SELECT EXISTS (
            WITH LastSegment AS (
                SELECT
                    taskId,
                    type,
                    ROW_NUMBER() OVER(PARTITION BY taskId ORDER BY segmentId DESC) as rn
                FROM
                    SegmentEntity
            )
            SELECT
                T.*
            FROM
                TaskEntity T
            JOIN
                LastSegment LS ON T.taskId = LS.taskID
            WHERE
                LS.rn = 1
                AND LS.type IN (0,1)
        )
    """)
    suspend fun hasActiveTask() : Boolean

    @Transaction
    @Query("""
        WITH LastSegment AS (
            SELECT
                taskId,
                type,
                ROW_NUMBER() OVER(PARTITION BY taskId ORDER BY segmentId DESC) as rn
            FROM
                SegmentEntity
        )
        SELECT
            T.*
        FROM
            TaskEntity T
        JOIN
            LastSegment LS ON T.taskId = LS.taskID
        WHERE
            LS.rn = 1
            AND LS.type IN (0,1)
        LIMIT 1
    """)
    fun getActiveTask() : Flow<TaskWithExecutionDataObject>


    @Transaction
    @Query("SELECT * FROM TaskEntity WHERE taskId == :taskId")
    fun getTaskWithExecutionData(taskId: Long) : Flow<TaskWithExecutionDataObject>


    @Transaction
    @Query("SELECT * FROM TaskEntity")
    fun getTasksWithExecutionData() : Flow<List<TaskWithExecutionDataObject>>
}