package com.wordco.clockworkandroid.session_report_feature.ui.model

sealed interface SessionReportModal {
    data object DeleteConfirmation : SessionReportModal
}