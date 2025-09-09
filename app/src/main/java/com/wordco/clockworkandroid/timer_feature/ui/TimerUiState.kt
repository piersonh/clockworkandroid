package com.wordco.clockworkandroid.timer_feature.ui

sealed interface TimerUiState {
    data object Retrieving : TimerUiState

    sealed interface Retrieved : TimerUiState {
        val taskName: String
        val elapsedSeconds: Int
    }

    sealed interface Shelved : Retrieved {
        val isPreparing: Boolean
    }

    data class New (
        override val taskName: String,
        override val elapsedSeconds: Int,
        override val isPreparing: Boolean,
    ) : Shelved

    data class Suspended (
        override val taskName: String,
        override val elapsedSeconds: Int,
        override val isPreparing: Boolean,
    ) : Shelved


    sealed interface Active : Retrieved

    data class Running (
        override val taskName: String,
        override val elapsedSeconds: Int
    ) : Active

    data class Paused (
        override val taskName: String,
        override val elapsedSeconds: Int
    ) : Active

    data class Finished (
        override val taskName: String,
        override val elapsedSeconds: Int
    ) : Retrieved
}