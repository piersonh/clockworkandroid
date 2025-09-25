package com.wordco.clockworkandroid.session_completion_feature.ui

import com.wordco.clockworkandroid.session_completion_feature.domain.use_case.CalculateEstimateAccuracyUseCase
import java.time.Duration


sealed interface TaskCompletionUiState {
    data object Retrieving : TaskCompletionUiState

    data class Retrieved (
        val name: String,
        val estimate: Duration?,
        val workTime: Duration,
        val breakTime: Duration,
        val totalTime: Duration,
        val totalTimeAccuracy: Double?
    ) : TaskCompletionUiState {
//        val totalTimeAccuracy = estimate?.let {
//            calculateEstimateAccuracyUseCase(
//                totalTime,
//                estimate,
//            )
//        }
        //val workTimeAccuracy: Int,
    }
}