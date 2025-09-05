package com.wordco.clockworkandroid.profile_list_feature.domain.repository

import com.wordco.clockworkandroid.profile_list_feature.domain.model.Profile
import com.wordco.clockworkandroid.profile_list_feature.domain.util.DummyData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeProfileRepository : ProfileRepository {
    override fun getProfiles(): Flow<List<Profile>> {
        return flow {
            emit(DummyData.PROFILES)
        }
    }
}