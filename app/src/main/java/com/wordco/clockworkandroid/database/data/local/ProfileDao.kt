package com.wordco.clockworkandroid.database.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.wordco.clockworkandroid.database.data.local.entities.ProfileEntity
import com.wordco.clockworkandroid.database.data.local.entities.ProfileWithSessionsDataObject
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {

    @Transaction
    @Query("SELECT * FROM ProfileEntity")
    fun getProfiles(): Flow<List<ProfileWithSessionsDataObject>>

    @Transaction
    @Query("SELECT * FROM ProfileEntity WHERE id = :id")
    fun getProfile(id: Long): Flow<ProfileWithSessionsDataObject>

    @Insert
    suspend fun insertProfile(toProfileEntity: ProfileEntity)

    @Update
    suspend fun updateProfile(toProfileEntity: ProfileEntity)

    @Transaction
    @Query("DELETE FROM ProfileEntity WHERE id = :id")
    suspend fun deleteProfile(id: Long)
}