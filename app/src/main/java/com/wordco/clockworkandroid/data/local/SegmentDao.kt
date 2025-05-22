package com.wordco.clockworkandroid.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wordco.clockworkandroid.data.model.Segment

@Dao
interface SegmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSegment(segment: Segment): Long

    @Delete
    suspend fun deleteSegment(segment: Segment)

    @Query("SELECT * FROM segment WHERE taskId = :taskId")
    suspend fun getSegmentsForTask(taskId: Long): List<Segment>
}