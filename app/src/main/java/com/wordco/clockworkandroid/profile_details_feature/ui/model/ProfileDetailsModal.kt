package com.wordco.clockworkandroid.profile_details_feature.ui.model

sealed interface ProfileDetailsModal {
    data object DeleteConfirmation : ProfileDetailsModal
}