package com.wordco.clockworkandroid.session_editor_feature.coordinator

sealed interface SessionEditorMode {
    data class Create(val profileId: Long?) : SessionEditorMode
    data class Edit(val sessionId: Long) : SessionEditorMode
}