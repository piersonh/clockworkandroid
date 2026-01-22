package com.wordco.clockworkandroid.session_editor_feature.ui.profile_picker.model.mapper

import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.session_editor_feature.ui.profile_picker.model.ProfilePickerItem

fun Profile.toProfilePickerItem() : ProfilePickerItem {
    return ProfilePickerItem(
        profileId = id,
        name = name,
        color = color,
    )
}