package com.wordco.clockworkandroid.edit_profile_feature.ui

import com.wordco.clockworkandroid.edit_profile_feature.ui.model.CreatePageModal

data class CreateProfileUiState(
    override val name: String,
    override val colorSliderPos: Float,
    override val difficulty: Float,
    val currentModal: CreatePageModal?,
    val hasFieldChanges: Boolean,
) : ProfileFormUiState