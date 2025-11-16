package com.wordco.clockworkandroid.core.domain.repository

interface AppPreferencesRepository {
    /**
     * Checks if this is the first time the app is being launched.
     * @return true if it's the first launch, false otherwise.
     */
    fun isFirstLaunch(): Boolean

    /**
     * Marks the first launch (e.g., onboarding) as completed.
     */
    fun setFirstLaunchCompleted()
}