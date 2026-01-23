package com.wordco.clockworkandroid.session_completion_feature.ui

import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.session_completion_feature.ui.model.SessionReportModal
import com.wordco.clockworkandroid.session_completion_feature.ui.model.ViewModelManagedUiState
import java.time.Duration


sealed interface SessionReportUiState {
    data object Retrieving : SessionReportUiState

    data class Error(
        val header: String,
        val message: String,
    ) : SessionReportUiState

    /**
     * Use Retrieved.from() to construct an instance
     *
     * DO NOT USE COPY OR THE DEFAULT CONSTRUCTOR
     */
    data class Retrieved(
        val name: String,
        val estimate: Duration?,
        val workTime: Duration,
        val breakTime: Duration,
        val totalTime: Duration,
        val totalTimeAccuracy: Double?,
        val isMenuOpen: Boolean,
        val currentModal: SessionReportModal?
    ) : SessionReportUiState {
        companion object {
            fun from(
                session: CompletedTask,
                viewModelManagedUiState: ViewModelManagedUiState,
                accuracyCalculator: (total: Duration, estimate: Duration) -> Double
            ) : Retrieved {
                val accuracy = session.userEstimate?.let { estimate ->
                    accuracyCalculator(session.totalTime, estimate)
                }

                return Retrieved(
                    name = session.name,
                    estimate = session.userEstimate,
                    workTime = session.workTime,
                    breakTime = session.breakTime,
                    totalTime = session.totalTime,
                    totalTimeAccuracy = accuracy,
                    isMenuOpen = viewModelManagedUiState.isMenuOpen,
                    currentModal = viewModelManagedUiState.currentModal
                )
            }
        }
    }

    data object Deleting : SessionReportUiState
}