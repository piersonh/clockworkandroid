package com.wordco.clockworkandroid.timer_feature.ui

sealed interface TimerUiState {
    data object Retrieving : TimerUiState

    sealed interface Retrieved : TimerUiState {
        val taskName: String
        val totalElapsedSeconds: Int
    }

    sealed interface Shelved : Retrieved {
        val isPreparing: Boolean
    }

    data class New (
        override val taskName: String,
        override val totalElapsedSeconds: Int,
        override val isPreparing: Boolean,
    ) : Shelved

    data class Suspended (
        override val taskName: String,
        override val totalElapsedSeconds: Int,
        override val isPreparing: Boolean,
    ) : Shelved

    data class Finished (
        override val taskName: String,
        override val totalElapsedSeconds: Int,
        override val isPreparing: Boolean
    ) : Shelved


    sealed interface Active : Retrieved {
        val currentSegmentElapsedSeconds: Int
    }

    data class Running (
        override val taskName: String,
        override val totalElapsedSeconds: Int,
        override val currentSegmentElapsedSeconds: Int
    ) : Active

    data class Paused (
        override val taskName: String,
        override val totalElapsedSeconds: Int,
        override val currentSegmentElapsedSeconds: Int
    ) : Active
}