package com.wordco.clockworkandroid.profile_session_list_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.repository.ProfileRepository

class DeleteProfileUseCase(
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(profileId: Long) {
        profileRepository.deleteProfile(profileId)
    }
}