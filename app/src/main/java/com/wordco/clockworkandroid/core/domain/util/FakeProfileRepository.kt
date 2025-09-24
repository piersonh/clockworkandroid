package com.wordco.clockworkandroid.core.domain.util

import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.core.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeProfileRepository(
    initialValues: List<Profile>,
) : ProfileRepository {

    private val _profiles = MutableStateFlow(initialValues)

    companion object {
        private lateinit var instance: FakeProfileRepository

        fun factory(
            initialValues: List<Profile> = emptyList()
        ) : FakeProfileRepository {
            if (!::instance.isInitialized) {
                instance = FakeProfileRepository(initialValues)
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

    override suspend fun insertProfile(profile: Profile) {
        if (profile.id != 0L) {
            error("new database entries must have an id of 0")
        }

        _profiles.update { profiles ->
            val newId = profiles.maxOfOrNull {
                it.id
            }?.plus(1) ?: 1

            profiles.plus(profile.copy(id = newId))
        }
    }

    override suspend fun updateProfile(profile: Profile) {
        _profiles.update { list ->
            list.map {
                if (it.id == profile.id) {
                    profile
                } else {
                    it
                }
            }
        }
    }
}