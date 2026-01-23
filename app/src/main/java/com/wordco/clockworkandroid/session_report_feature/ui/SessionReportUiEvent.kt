package com.wordco.clockworkandroid.session_report_feature.ui

sealed interface SessionReportUiEvent {
    sealed interface LoadingEvent: SessionReportUiEvent
    sealed interface ErrorEvent : SessionReportUiEvent
    sealed interface ReportEvent : SessionReportUiEvent
    sealed interface DeletingEvent : SessionReportUiEvent

    data object BackClicked : LoadingEvent, ErrorEvent, ReportEvent
    data object DeleteClicked : ReportEvent
    data object EditClicked : ReportEvent
    data object ContinueClicked : ReportEvent
    data object MenuOpened : ReportEvent
    data object MenuClosed : ReportEvent
    data object DeleteConfirmed : ReportEvent
    data object ModalDismissed : ReportEvent
    data object CopyErrorClicked : ErrorEvent
}