package com.wordco.clockworkandroid.edit_session_feature.ui

sealed interface SessionFormMode {
    data class Create(val profileId: Long?) : SessionFormMode
    data class Edit(val sessionId: Long) : SessionFormMode
}