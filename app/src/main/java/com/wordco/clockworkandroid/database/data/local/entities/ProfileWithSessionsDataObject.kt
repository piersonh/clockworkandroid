package com.wordco.clockworkandroid.database.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class ProfileWithSessionsDataObject (
    @Embedded val profileEntity: ProfileEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "profileId",
        entity = TaskEntity::class,
    )
    val sessions: List<TaskWithExecutionDataObject>,
)