package com.wordco.clockworkandroid.edit_profile_feature.ui.model

sealed interface ProfileEditorModal {
    data object Discard : ProfileEditorModal
}