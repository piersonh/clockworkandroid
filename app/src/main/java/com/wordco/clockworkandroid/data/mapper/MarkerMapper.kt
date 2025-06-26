package com.wordco.clockworkandroid.data.mapper

import com.wordco.clockworkandroid.data.local.entities.MarkerEntity
import com.wordco.clockworkandroid.domain.model.Marker

fun MarkerEntity.toMarker() : Marker {
    return Marker(
        markerId = markerId,
        taskId = taskId,
        startTime = startTime,
        label = label
    )
}

fun Marker.toMarkerEntity() : MarkerEntity {
    return MarkerEntity(
        markerId = markerId,
        taskId = taskId,
        startTime = startTime,
        label = label
    )
}