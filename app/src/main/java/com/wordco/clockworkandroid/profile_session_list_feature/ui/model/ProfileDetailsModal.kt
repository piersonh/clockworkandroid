package com.wordco.clockworkandroid.profile_session_list_feature.ui.model

sealed interface ProfileDetailsModal {
    data object DeleteConfirmation : ProfileDetailsModal
}