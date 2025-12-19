package com.wordco.clockworkandroid.edit_profile_feature.ui

sealed interface ProfileFormMode {
    data object Create : ProfileFormMode
    data class Edit(val profileId: Long) : ProfileFormMode
}