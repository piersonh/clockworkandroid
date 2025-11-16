package com.wordco.clockworkandroid.edit_session_feature.ui

sealed interface SessionFormEffect {
    data class ShowSnackbar(val message: String) : SessionFormEffect
    data object NavigateBack : SessionFormEffect
}