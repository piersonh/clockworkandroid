package com.wordco.clockworkandroid.session_report_feature.ui.model

/**
 * Holds UI state variables that are managed by the viewmodel as opposed to collected from the database
 *
 * @property isMenuOpen
 * @property currentModal
 */
data class ViewModelManagedUiState(
    val isMenuOpen: Boolean,
    val currentModal: SessionReportModal?
)
