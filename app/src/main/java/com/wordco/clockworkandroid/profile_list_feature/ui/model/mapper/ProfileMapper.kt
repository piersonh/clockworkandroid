package com.wordco.clockworkandroid.profile_list_feature.ui.model.mapper

import com.wordco.clockworkandroid.profile_list_feature.domain.model.Profile
import com.wordco.clockworkandroid.profile_list_feature.ui.model.ProfileListItem

fun Profile.toProfileListItem() : ProfileListItem {
    return ProfileListItem(
        id = id,
        name = name,
        color = color,
        numSessions = sessions.size,
    )
}