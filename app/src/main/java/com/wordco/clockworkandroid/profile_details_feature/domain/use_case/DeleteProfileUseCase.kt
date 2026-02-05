package com.wordco.clockworkandroid.profile_details_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.repository.ProfileRepository

class DeleteProfileUseCase(
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(profileId: Long) {
        profileRepository.deleteProfile(profileId)
    }
}