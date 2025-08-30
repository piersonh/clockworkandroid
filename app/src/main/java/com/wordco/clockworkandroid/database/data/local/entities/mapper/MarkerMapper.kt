package com.wordco.clockworkandroid.database.data.local.entities.mapper

import com.wordco.clockworkandroid.core.domain.model.Marker
import com.wordco.clockworkandroid.database.data.local.entities.MarkerEntity
import com.wordco.clockworkandroid.database.data.util.fromInstant
import com.wordco.clockworkandroid.database.data.util.toInstant

fun MarkerEntity.toMarker() : Marker {
    return Marker(
        markerId = markerId,
        taskId = taskId,
        startTime = toInstant(startTime),
        label = label
    )
}

fun Marker.toMarkerEntity() : MarkerEntity {
    return MarkerEntity(
        markerId = markerId,
        taskId = taskId,
        startTime = fromInstant(startTime),
        label = label
    )
}