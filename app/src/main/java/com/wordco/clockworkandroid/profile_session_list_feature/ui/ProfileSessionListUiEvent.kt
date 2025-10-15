package com.wordco.clockworkandroid.profile_session_list_feature.ui

sealed interface ProfileSessionListUiEvent {
    data object NavigateBack : ProfileSessionListUiEvent
}