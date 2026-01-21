package com.wordco.clockworkandroid.session_editor_feature.coordinator

import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.core.domain.model.Task
import com.wordco.clockworkandroid.core.ui.util.hue
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import com.wordco.clockworkandroid.edit_session_feature.ui.util.toEstimate
import com.wordco.clockworkandroid.session_editor_feature.domain.model.SessionDraft
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import kotlin.random.Random

class SessionDraftFactory(
    random: Random = Random
) {
    private val defaultNullProfileColor = random.nextFloat()

    fun createNew(profile: Profile?): SessionDraft {
        return SessionDraft(
            sessionId = 0,
            sessionName = getDefaultName(profile),
            profileId = profile?.id,
            colorHue = getDefaultColor(profile),
            difficulty = getDefaultDifficulty(profile),
            dueDateTime = getDefaultDueDateTime(profile),
            estimate = getDefaultEstimate(profile),
        )
    }


    fun createFromExisting(session: Task): SessionDraft {
        return SessionDraft(
            sessionId = session.taskId,
            sessionName = session.name,
            profileId = session.profileId,
            colorHue = session.color.hue() / 360,
            difficulty = session.difficulty,
            dueDateTime = session.dueDate?.atZone(ZoneId.systemDefault())?.toLocalDateTime(),
            estimate = session.userEstimate?.toEstimate(),
        )
    }

    fun getDefaultName(profile: Profile?): String {
        return when (profile) {
            null -> {
                ""
            }
            else -> {
                // TODO replace this with a truth from the database so that the quantity
                //  is retained after sessions are deleted or switched profiles
                "${profile.name} ${profile.sessions.size + 1}"
            }
        }
    }

    fun getDefaultColor(profile: Profile?): Float {
        return when (profile) {
            null -> {
                defaultNullProfileColor
            }
            else -> {
                profile.color.hue() / 360
            }
        }
    }

    fun getDefaultDifficulty(profile: Profile?): Int {
        return when (profile) {
            null -> {
                0
            }
            else -> {
                profile.defaultDifficulty
            }
        }
    }

    fun getDefaultDueDateTime(profile: Profile?): LocalDateTime {
        return when (profile) {
            null -> {
                LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59))
            }
            else -> {
                LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59))
            }
        }
    }

    fun getDefaultEstimate(profile: Profile?): UserEstimate? {
        return when (profile) {
            null -> {
                null
            }
            else -> {
                null
            }
        }
    }
}