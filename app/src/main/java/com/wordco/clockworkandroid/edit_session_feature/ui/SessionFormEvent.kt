package com.wordco.clockworkandroid.edit_session_feature.ui

import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import java.time.LocalTime

sealed interface SessionFormEvent {
    data class TaskNameChanged(val name: String) : SessionFormEvent
    data class ProfileChanged(val id: Long?) : SessionFormEvent
    data class ColorSliderChanged(val position: Float) : SessionFormEvent
    data class DifficultyChanged(val difficulty: Float) : SessionFormEvent
    data class DueDateChanged(val date: Long?) : SessionFormEvent
    data class DueTimeChanged(val time: LocalTime) : SessionFormEvent
    data class EstimateChanged(val estimate: UserEstimate?) : SessionFormEvent

    data class ReminderDateChanged(val date: Long?) : SessionFormEvent
    data class ReminderTimeChanged(val time: LocalTime) : SessionFormEvent
    data object SaveClicked : SessionFormEvent
}