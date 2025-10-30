package com.wordco.clockworkandroid.core.data.fake

import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.core.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicLong

class FakeProfileRepository(
    initialValues: List<Profile> = emptyList()
) : ProfileRepository {

    private val _profiles = MutableStateFlow<Map<Long, Profile>>(emptyMap())

    private val nextProfileId = AtomicLong(1L)

    init {
        _profiles.value = initialValues.associateBy { it.id }
        val maxId = initialValues.maxOfOrNull { it.id } ?: 0L
        nextProfileId.set(maxId + 1)
    }


    override fun getProfiles(): Flow<List<Profile>> {
        return _profiles.map { it.values.toList() }
    }

    override fun getProfile(id: Long): Flow<Profile> {
        return _profiles.mapNotNull { map ->
            map[id]
        }
    }

    override suspend fun insertProfile(profile: Profile) {
        if (profile.id != 0L) {
            error("new database entries must have an id of 0")
        }

        val newId = nextProfileId.getAndIncrement()
        val newProfile = profile.copy(id = newId)

        _profiles.update { currentMap ->
            currentMap + (newId to newProfile)
        }
    }

    override suspend fun updateProfile(profile: Profile) {
        _profiles.update { currentMap ->
            if (currentMap.containsKey(profile.id)) {
                currentMap + (profile.id to profile)
            } else {
                currentMap
            }
        }
    }

    override suspend fun deleteProfile(id: Long) {
        _profiles.update { currentMap ->
            currentMap - id
        }
    }
}