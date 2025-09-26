package com.wordco.clockworkandroid.edit_session_feature.ui.model


sealed interface Modal {
    data object Date : SessionFormModal, CreatePageModal
    data object Time : SessionFormModal, CreatePageModal
    data object Estimate : SessionFormModal, CreatePageModal

    data object Discard : CreatePageModal
    data object Delete : EditPageModal
}

sealed interface CreatePageModal : EditPageModal

sealed interface EditPageModal {
    fun toOptionalSessionFormModal() : SessionFormModal? {
        return this as? SessionFormModal
    }
}
sealed interface SessionFormModal