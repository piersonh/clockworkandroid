package com.wordco.clockworkandroid.database.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.wordco.clockworkandroid.database.data.local.entities.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Query("UPDATE ReminderEntity SET status = :status WHERE reminderId = :reminderId")
    suspend fun updateStatus(reminderId: Long, status: Int)

    @Transaction
    @Query("DELETE FROM ReminderEntity WHERE reminderId = :id")
    suspend fun deleteReminder(id: Long)

    @Transaction
    @Query("DELETE FROM ReminderEntity WHERE sessionId = :sessionId")
    suspend fun deleteAllRemindersForSession(sessionId: Long)

    @Transaction
    @Query("DELETE FROM ReminderEntity WHERE sessionId = :sessionId AND status = 0")
    suspend fun deleteAllPendingRemindersForSession(sessionId: Long)

    @Transaction
    @Query("SELECT * FROM ReminderEntity WHERE reminderId = :id")
    fun getReminder(id: Long): Flow<ReminderEntity?>

    @Transaction
    @Query("SELECT * FROM ReminderEntity WHERE sessionId = :sessionId")
    fun getRemindersForSession(sessionId: Long): Flow<List<ReminderEntity>>
}