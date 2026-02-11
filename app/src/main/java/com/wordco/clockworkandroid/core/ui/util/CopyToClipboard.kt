package com.wordco.clockworkandroid.core.ui.util

import android.content.ClipData
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.toClipEntry

suspend fun Clipboard.newEntry(
    label: CharSequence,
    text: CharSequence,
) {
    val clipData = ClipData.newPlainText(
        label,
        text
    )
    setClipEntry(clipData.toClipEntry())
}