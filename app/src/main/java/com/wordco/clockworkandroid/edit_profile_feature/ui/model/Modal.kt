package com.wordco.clockworkandroid.edit_profile_feature.ui.model

sealed interface Modal {
    data object Discard : CreatePageModal, EditPageModal
    data object Delete : EditPageModal
}

sealed interface CreatePageModal
sealed interface EditPageModal