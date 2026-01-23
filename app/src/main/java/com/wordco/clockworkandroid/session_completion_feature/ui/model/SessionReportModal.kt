package com.wordco.clockworkandroid.session_completion_feature.ui.model

sealed interface SessionReportModal {
    data object DeleteConfirmation : SessionReportModal
}