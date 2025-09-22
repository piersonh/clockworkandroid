package com.wordco.clockworkandroid.edit_profile_feature.ui

data class CreateProfileUiState(
    override val name: String,
    override val colorSliderPos: Float,
    override val difficulty: Float
) : EditProfileFormUiState