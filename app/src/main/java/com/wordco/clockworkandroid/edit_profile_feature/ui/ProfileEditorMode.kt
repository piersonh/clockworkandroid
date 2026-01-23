package com.wordco.clockworkandroid.edit_profile_feature.ui

sealed interface ProfileEditorMode {
    data object Create : ProfileEditorMode
    data class Edit(val profileId: Long) : ProfileEditorMode
}