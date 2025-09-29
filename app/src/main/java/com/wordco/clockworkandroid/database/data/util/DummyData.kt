package com.wordco.clockworkandroid.database.data.util

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.Marker
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.core.domain.model.Segment
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.model.Task
import com.wordco.clockworkandroid.database.data.local.entities.MarkerEntity
import com.wordco.clockworkandroid.database.data.local.entities.ProfileEntity
import com.wordco.clockworkandroid.database.data.local.entities.SegmentEntity
import com.wordco.clockworkandroid.database.data.local.entities.TaskEntity
import com.wordco.clockworkandroid.database.data.local.entities.mapper.toMarkerEntity
import com.wordco.clockworkandroid.database.data.local.entities.mapper.toProfileEntity
import com.wordco.clockworkandroid.database.data.local.entities.mapper.toSegmentEntity
import com.wordco.clockworkandroid.database.data.local.entities.mapper.toTaskEntity
import java.time.Duration
import java.time.Instant

data class UserDataPackage(
    val sessions: List<TaskEntity>,
    val segments: List<SegmentEntity>,
    val markers: List<MarkerEntity>,
    val profiles: List<ProfileEntity>
) {
    companion object {
        fun factory(
            profiles: List<Profile>,
            orphanSessions: List<Task>
        ) : UserDataPackage {
            var sessionIdInc = 0L
            var segmentIdInc = 0L
            var markerIdInc = 0L
            val profiles = profiles.mapIndexed { i, profile ->
                profile.copy(
                    id = i + 1L,
                    sessions = profile.sessions.map { session ->
                        val sessionId = ++sessionIdInc
                        when (session) {
                            is NewTask -> session.copy(
                                taskId = sessionId,
                                profileId = i + 1L,
                            )
                            is CompletedTask -> session.copy(
                                taskId = sessionId,
                                profileId = i + 1L,
                                segments = session.segments.map { segment ->
                                    segment.copy(
                                        segmentId = (++segmentIdInc),
                                        taskId = sessionId
                                    )
                                },
                                markers = session.markers.map { marker ->
                                    marker.copy(
                                        markerId = (++markerIdInc),
                                        taskId = sessionId
                                    )
                                }
                            )
                            is StartedTask -> session.copy(
                                taskId = sessionId,
                                profileId = i + 1L,
                                segments = session.segments.map { segment ->
                                    segment.copy(
                                        segmentId = (++segmentIdInc),
                                        taskId = sessionId
                                    )
                                },
                                markers = session.markers.map { marker ->
                                    marker.copy(
                                        markerId = (++markerIdInc),
                                        taskId = sessionId
                                    )
                                }
                            )
                        }
                    }
                )
            }
            val sessions = buildList {
                profiles.forEach {
                    addAll(it.sessions)
                }
                orphanSessions.forEach { session ->
                    val sessionId = ++sessionIdInc
                    when (session) {
                        is NewTask -> session.copy(
                            taskId = sessionId,
                            profileId = null,
                        )
                        is CompletedTask -> session.copy(
                            taskId = sessionId,
                            profileId = null,
                            segments = session.segments.map { segment ->
                                segment.copy(
                                    segmentId = (++segmentIdInc),
                                    taskId = sessionId
                                )
                            },
                            markers = session.markers.map { marker ->
                                marker.copy(
                                    markerId = (++markerIdInc),
                                    taskId = sessionId
                                )
                            }
                        )
                        is StartedTask -> session.copy(
                            taskId = sessionId,
                            profileId = null,
                            segments = session.segments.map { segment ->
                                segment.copy(
                                    segmentId = (++segmentIdInc),
                                    taskId = sessionId
                                )
                            },
                            markers = session.markers.map { marker ->
                                marker.copy(
                                    markerId = (++markerIdInc),
                                    taskId = sessionId
                                )
                            }
                        )
                    }.let {
                        add(it)
                    }
                }
            }
            val segments = buildList {
                sessions.forEach {
                    if (it is Task.HasExecutionData) {
                        addAll(it.segments)
                    }
                }
            }
            val markers = buildList {
                sessions.forEach {
                    if (it is Task.HasExecutionData) {
                        addAll(it.markers)
                    }
                }
            }
            Log.i("DummyDataBuilder",sessions.toString())

            return UserDataPackage(
                sessions = sessions.map(Task::toTaskEntity),
                segments = segments.map(Segment::toSegmentEntity),
                markers = markers.map(Marker::toMarkerEntity),
                profiles = profiles.map(Profile::toProfileEntity)
            )
        }
    }
}


object DummyData {

    val package0_empty by lazy {
        UserDataPackage.factory(
            profiles = emptyList(),
            orphanSessions = emptyList()
        )
    }

    val package1_sillyTasks by lazy {
        UserDataPackage.factory(
            profiles = emptyList(),
            orphanSessions = listOf(
                StartedTask(
                    0,
                    "Assignment",
                    Instant.parse("2025-04-17T18:29:04Z"),
                    3,
                    Color.Companion.Green,
                    null,
                    listOf(
                        Segment(
                            segmentId = 0,
                            taskId = 0,
                            startTime = Instant.parse("2025-04-17T18:31:04Z"),
                            duration = Duration.ofSeconds(12345),
                            type = Segment.Type.WORK
                        ),
                        Segment(
                            segmentId = 0,
                            taskId = 0,
                            startTime = Instant.parse("2025-04-17T18:31:04Z").plusSeconds(12345),
                            duration = null,
                            type = Segment.Type.SUSPEND
                        )
                    ),
                    emptyList(),
                    null
                ),
                NewTask(
                    0,
                    "Project Plan",
                    Instant.parse("2025-04-17T18:29:04Z"),
                    2,
                    Color.Companion.Blue,
                    null,
                    null
                ),
                NewTask(
                    0,
                    "Homework 99",
                    Instant.parse("2025-04-17T18:29:04Z"),
                    3,
                    Color.Companion.White,
                    null,
                    null
                ),
                NewTask(
                    0,
                    "Homework 99.5",
                    Instant.parse("2025-04-17T18:29:04Z"),
                    3,
                    Color.Companion.Cyan,
                    null,
                    null,
                ),
                NewTask(
                    0,
                    "Homework -1",
                    Instant.parse("2025-04-17T18:29:04Z"),
                    3,
                    Color.Companion.Black,
                    null,
                    null,
                ),
                NewTask(
                    0,
                    "Homework 100",
                    Instant.parse("2025-04-17T18:29:04Z"),
                    3,
                    Color.Companion.Red,
                    null,
                    null,
                ),
                NewTask(
                    0,
                    "Evil Homework 101",
                    Instant.parse("2025-04-17T18:29:04Z"),
                    3,
                    Color.Companion.Magenta,
                    null,
                    null,
                ),
                NewTask(
                    8,
                    "Super Homework 102",
                    Instant.parse("2025-04-17T18:29:04Z"),
                    3,
                    Color.Companion.Yellow,
                    null,
                    null,
                ),
                CompletedTask(
                    taskId = 0,
                    name = "Finalized Report",
                    dueDate = Instant.parse("2025-04-18T10:00:00Z"),
                    difficulty = 4,
                    color = Color.Companion.DarkGray,
                    userEstimate = Duration.ofHours(5),
                    segments = listOf(
                        Segment(
                            segmentId = 0,
                            taskId = 0,
                            startTime = Instant.parse("2025-04-18T09:00:00Z"),
                            duration = Duration.ofHours(2),
                            type = Segment.Type.WORK
                        )
                    ),
                    markers = listOf(
                        Marker(
                            markerId = 0,
                            taskId = 0,
                            startTime = Instant.parse("2025-04-18T09:30:00Z"),
                            label = "Research Phase"
                        )
                    ),
                    profileId = null,
                )
            )
        )
    }

    val package2_reloadRunning by lazy {
        UserDataPackage.factory(
            profiles = emptyList(),
            orphanSessions = listOf(
                StartedTask(
                    taskId = 0,
                    name = "Running",
                    dueDate = null,
                    difficulty = 1,
                    color = Color.Green,
                    userEstimate = null,
                    segments = listOf(
                        Segment(
                            segmentId = 0,
                            taskId = 0,
                            startTime = Instant.now().minusSeconds(5 * 60 * 60 + 5 * 60),
                            duration = null,
                            type = Segment.Type.BREAK
                        )
                    ),
                    markers = emptyList(),
                    null
                )
            )
        )
    }
}