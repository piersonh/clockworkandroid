package com.wordco.clockworkandroid.profile_editor_feature.ui.model

sealed interface ProfileEditorModal {
    data object Discard : ProfileEditorModal
}