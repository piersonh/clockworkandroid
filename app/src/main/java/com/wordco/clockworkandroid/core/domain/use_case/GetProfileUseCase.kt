package com.wordco.clockworkandroid.core.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.core.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class GetProfileUseCase(
    private val profileRepository: ProfileRepository,
) {
    operator fun invoke(profileId: Long): Flow<Profile> {
        return profileRepository.getProfile(profileId)
    }
}