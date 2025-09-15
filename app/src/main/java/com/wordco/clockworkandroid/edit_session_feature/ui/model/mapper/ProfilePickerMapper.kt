package com.wordco.clockworkandroid.edit_session_feature.ui.model.mapper

import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.edit_session_feature.ui.model.ProfilePickerItem

fun Profile.toProfilePickerItem() : ProfilePickerItem {
    return ProfilePickerItem(
        profileId = id,
        name = name,
        color = color,
    )
}