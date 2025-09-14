package com.wordco.clockworkandroid.database.data.local.entities.mapper

import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.database.data.local.entities.ProfileWithSessionsDataObject
import com.wordco.clockworkandroid.database.data.util.toColor

fun ProfileWithSessionsDataObject.toProfile() : Profile {
    return Profile(
        id = profileEntity.id,
        name = profileEntity.name,
        color = toColor(profileEntity.color),
        defaultDifficulty = profileEntity.difficulty,
        sessions = sessions.map { it.toTask() }
    )
}