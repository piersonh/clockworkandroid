package com.wordco.clockworkandroid.session_report_feature.ui

sealed interface SessionReportUiEffect {
    data object NavigateBack : SessionReportUiEffect
    data object NavigateToEditSession : SessionReportUiEffect
    data object NavigateToContinue : SessionReportUiEffect
    data class CopyToClipboard(val content: String): SessionReportUiEffect
    data class ShowSnackbar(val message: String) : SessionReportUiEffect
}