package com.wordco.clockworkandroid.core.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.core.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class GetAllProfilesUseCase(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(): Flow<List<Profile>> {
        return profileRepository.getProfiles()
    }
}