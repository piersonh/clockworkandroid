package com.wordco.clockworkandroid.profile_list_feature.domain.repository

import com.wordco.clockworkandroid.profile_list_feature.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfiles(): Flow<List<Profile>>
}