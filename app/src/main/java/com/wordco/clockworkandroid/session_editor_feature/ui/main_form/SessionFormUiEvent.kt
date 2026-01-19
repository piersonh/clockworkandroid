package com.wordco.clockworkandroid.session_editor_feature.ui.main_form

import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import java.time.LocalTime

sealed interface SessionFormUiEvent {
    sealed interface LoadingEvent: SessionFormUiEvent
    sealed interface ErrorEvent: SessionFormUiEvent
    sealed interface FormEvent: SessionFormUiEvent

    data object BackClicked: LoadingEvent, ErrorEvent, FormEvent
    data object DiscardConfirmed: FormEvent
    data object ModalDismissed: FormEvent
    data object CopyErrorClicked: ErrorEvent
    data object SaveClicked: FormEvent
    data object ProfileFieldClicked: FormEvent
    data object DueDateFieldClicked: FormEvent
    data object DueTimeFieldClicked: FormEvent
    data object EstimateFieldClicked: FormEvent
    data object ReminderDateFieldClicked: FormEvent
    data object ReminderTimeFieldClicked: FormEvent

    data class TaskNameChanged(val newName: String): FormEvent
    data class ColorSliderChanged(val newPos: Float): FormEvent
    data class DifficultySliderChanged(val newPos: Float): FormEvent
    data class DueDateChanged(val newDate: Long?): FormEvent
    data class DueTimeChanged(val newTime: LocalTime): FormEvent
    data class EstimateChanged(val estimate: UserEstimate?): FormEvent

    data class ReminderDateChanged(val newDate: Long?): FormEvent
    data class ReminderTimeChanged(val newTime: LocalTime): FormEvent
}