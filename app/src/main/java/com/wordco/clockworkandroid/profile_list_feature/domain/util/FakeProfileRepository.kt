package com.wordco.clockworkandroid.profile_list_feature.domain.util

import com.wordco.clockworkandroid.profile_list_feature.domain.model.Profile
import com.wordco.clockworkandroid.profile_list_feature.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

class FakeProfileRepository(
    initialValues: List<Profile>,
) : ProfileRepository {

    private val _profiles = MutableStateFlow(initialValues)
    private val profiles = _profiles.asStateFlow()

    companion object {
        private lateinit var instance: FakeProfileRepository

        fun factory() : FakeProfileRepository {
            if (!::instance.isInitialized) {
                instance = FakeProfileRepository(DummyData.PROFILES)
            }
            return instance
        }
    }

    override fun getProfiles(): Flow<List<Profile>> {
        return profiles
    }
}