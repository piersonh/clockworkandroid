package com.wordco.clockworkandroid.core.domain.util

import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.core.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeProfileRepository(
    initialValues: List<Profile>,
) : ProfileRepository {

    private val _profiles = MutableStateFlow(initialValues)

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
        return _profiles.asStateFlow()
    }

    override fun getProfile(id: Long): Flow<Profile> {
        return _profiles.map { profiles ->
            profiles.first { it.id == id }
        }
    }
}