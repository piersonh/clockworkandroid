package com.wordco.clockworkandroid.edit_session_feature.ui

import com.wordco.clockworkandroid.edit_session_feature.ui.model.PickerModal
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import java.time.LocalDate
import java.time.LocalTime

interface EditTaskFormUiState {
    val taskName: String
    val profileName: String?
    val colorSliderPos: Float
    val difficulty: Float
    val dueDate: LocalDate?
    val dueTime: LocalTime?
    val currentModal: PickerModal?
    val estimate: UserEstimate?
}


