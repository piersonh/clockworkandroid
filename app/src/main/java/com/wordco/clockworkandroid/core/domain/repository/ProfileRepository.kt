package com.wordco.clockworkandroid.core.domain.repository

import com.wordco.clockworkandroid.core.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfiles(): Flow<List<Profile>>

    fun getProfile(id: Long): Flow<Profile>

    suspend fun insertProfile(profile: Profile)

    suspend fun updateProfile(profile: Profile)
}