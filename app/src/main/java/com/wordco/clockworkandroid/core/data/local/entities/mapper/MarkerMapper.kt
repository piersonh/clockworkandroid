package com.wordco.clockworkandroid.core.data.local.entities.mapper

import com.wordco.clockworkandroid.core.data.local.entities.MarkerEntity
import com.wordco.clockworkandroid.core.data.util.fromInstant
import com.wordco.clockworkandroid.core.data.util.toInstant
import com.wordco.clockworkandroid.core.domain.model.Marker

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