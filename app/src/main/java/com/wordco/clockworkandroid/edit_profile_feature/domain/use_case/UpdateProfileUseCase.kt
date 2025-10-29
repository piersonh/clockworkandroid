package com.wordco.clockworkandroid.edit_profile_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.core.domain.repository.ProfileRepository

class UpdateProfileUseCase(
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(profile: Profile) {
        profileRepository.updateProfile(profile)
    }
}