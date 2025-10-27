package com.wordco.clockworkandroid.database.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.wordco.clockworkandroid.database.data.local.entities.ReminderEntity

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertReminder(reminder: ReminderEntity)

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Transaction
    @Query("DELETE FROM ReminderEntity WHERE reminderId = :id")
    suspend fun deleteReminder(id: Long)
}