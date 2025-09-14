package com.wordco.clockworkandroid.database.data.local.entities.mapper

import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.database.data.local.entities.ProfileEntity
import com.wordco.clockworkandroid.database.data.util.fromColor

fun Profile.toProfileEntity() : ProfileEntity {
    return ProfileEntity(
        id = id,
        name = name,
        difficulty = defaultDifficulty,
        color = fromColor(color),
    )
}