package com.wordco.clockworkandroid.core.data.preferences

import android.content.SharedPreferences
import com.wordco.clockworkandroid.core.domain.repository.AppPreferencesRepository

class SharedPreferencesAppPreferencesRepository(
    private val prefs: SharedPreferences
) : AppPreferencesRepository {

    private companion object {
        const val PREFS_NAME = "ClockworkPrefs"
        const val PREF_KEY_FIRST_LAUNCH = "isFirstLaunch"
    }

    override fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(PREF_KEY_FIRST_LAUNCH, true)
    }

    override fun setFirstLaunchCompleted() {
        prefs.edit().putBoolean(PREF_KEY_FIRST_LAUNCH, false).apply()
    }
}