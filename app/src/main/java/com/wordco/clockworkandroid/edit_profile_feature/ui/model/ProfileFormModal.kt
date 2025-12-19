package com.wordco.clockworkandroid.edit_profile_feature.ui.model

sealed interface ProfileFormModal {
    data object Discard : ProfileFormModal
}