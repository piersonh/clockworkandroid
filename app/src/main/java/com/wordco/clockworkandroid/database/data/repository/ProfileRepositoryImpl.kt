package com.wordco.clockworkandroid.database.data.repository

import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.core.domain.repository.ProfileRepository
import com.wordco.clockworkandroid.database.data.local.ProfileDao
import com.wordco.clockworkandroid.database.data.local.entities.mapper.toProfile
import com.wordco.clockworkandroid.database.data.local.entities.mapper.toProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class ProfileRepositoryImpl(
    private val profileDao: ProfileDao,
) : ProfileRepository {
    override fun getProfiles(): Flow<List<Profile>> {
        return profileDao.getProfiles().map { profileList ->
            profileList.map { it.toProfile() }
        }
    }

    override fun getProfile(id: Long): Flow<Profile> {
        return profileDao.getProfile(id)
            .filterNotNull()
            .map { it.toProfile() }
    }

    override suspend fun insertProfile(profile: Profile) {
        profileDao.insertProfile(profile.toProfileEntity())
    }

    override suspend fun updateProfile(profile: Profile) {
        profileDao.updateProfile(profile.toProfileEntity())
    }

    override suspend fun deleteProfile(id: Long) {
        profileDao.deleteProfile(id)
    }
}