package com.wordco.clockworkandroid.timer_feature.ui

sealed interface TimerUiState {
    data object Retrieving : TimerUiState

    sealed interface Retrieved : TimerUiState {
        val taskName: String
        val elapsedSeconds: Int
    }

    data class New (
        override val taskName: String,
        override val elapsedSeconds: Int
    ) : Shelved

    sealed interface Active : Retrieved

    sealed interface Shelved : Retrieved

    data class Running (
        override val taskName: String,
        override val elapsedSeconds: Int
    ) : Active

    data class Paused (
        override val taskName: String,
        override val elapsedSeconds: Int
    ) : Active

    data class Suspended (
        override val taskName: String,
        override val elapsedSeconds: Int
    ) : Shelved
}