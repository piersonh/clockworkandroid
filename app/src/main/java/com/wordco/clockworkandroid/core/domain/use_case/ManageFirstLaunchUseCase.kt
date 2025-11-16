package com.wordco.clockworkandroid.core.domain.use_case

import com.wordco.clockworkandroid.core.domain.repository.AppPreferencesRepository

/**
 * A UseCase dedicated to managing the first-launch business logic.
 *
 * @param appPreferencesRepository The repository for accessing preference data.
 */
class ManageFirstLaunchUseCase(
    private val appPreferencesRepository: AppPreferencesRepository
) {

    fun isFirstLaunch(): Boolean {
        return appPreferencesRepository.isFirstLaunch()
    }

    fun setCompleted() {
        appPreferencesRepository.setFirstLaunchCompleted()
    }
}