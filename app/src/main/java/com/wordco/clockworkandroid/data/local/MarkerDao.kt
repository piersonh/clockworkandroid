package com.wordco.clockworkandroid.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wordco.clockworkandroid.data.model.Marker

@Dao
interface MarkerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarker(marker: Marker): Long

    @Delete
    suspend fun deleteMarker(marker: Marker)

    @Query("SELECT * FROM marker WHERE taskId = :taskId")
    suspend fun getMarkersForTask(taskId: Long): List<Marker>
}