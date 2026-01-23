package com.wordco.clockworkandroid.profile_editor_feature.ui

sealed interface ProfileEditorMode {
    data object Create : ProfileEditorMode
    data class Edit(val profileId: Long) : ProfileEditorMode
}