package com.wordco.clockworkandroid.ui

import java.time.LocalDate
import java.time.LocalTime

interface EditTaskFormUiState {
    val taskName: String
    val colorSliderPos: Float
    val difficulty: Float
    val dueDate: LocalDate?
    val dueTime: LocalTime?
    val currentModal: PickerModal?
    val estimate: UserEstimate?
}


data class UserEstimate (
    val minutes: Int,
    val hours: Int,
)

enum class PickerModal {
    DATE, TIME
}