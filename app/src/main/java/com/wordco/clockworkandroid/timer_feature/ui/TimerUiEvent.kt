package com.wordco.clockworkandroid.timer_feature.ui

sealed interface TimerUiEvent {
    data class ShowSnackbar(
        val message: String
    ) : TimerUiEvent
}