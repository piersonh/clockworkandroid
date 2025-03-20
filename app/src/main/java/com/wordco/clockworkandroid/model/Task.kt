package com.wordco.clockworkandroid.model

import androidx.compose.ui.graphics.Color
import java.util.Locale

data class Task(
    val name: String,
    val time: Int,
    val days: Int,
    val difficulty: Int,
    val color: Color
)


fun Task.timeAsHHMM () : String {
    val hours = time / 3600
    val minutes = (time % 3600) / 60
    return String.format(Locale.getDefault(), "%02d:%02d", hours , minutes)
}
