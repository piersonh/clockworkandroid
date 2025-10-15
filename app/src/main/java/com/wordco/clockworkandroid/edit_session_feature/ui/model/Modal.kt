package com.wordco.clockworkandroid.edit_session_feature.ui.model


sealed interface Modal {
    data object Date : Modal
    data object Time : Modal
    data object Estimate : Modal

    data object Discard : Modal
}