package com.wordco.clockworkandroid.edit_session_feature.ui.model


sealed interface Modal {
    data object Date : SessionFormModal, CreatePageModal, EditPageModal
    data object Time : SessionFormModal, CreatePageModal, EditPageModal
    data object Estimate : SessionFormModal, CreatePageModal, EditPageModal

    data object Discard : CreatePageModal, EditPageModal
    data object Delete : EditPageModal
}

sealed interface CreatePageModal
fun CreatePageModal.toOptionalSessionFormModal() : SessionFormModal? {
    return this as? SessionFormModal
}

sealed interface EditPageModal
fun EditPageModal.toOptionalSessionFormModal() : SessionFormModal? {
    return this as? SessionFormModal
}
sealed interface SessionFormModal