package com.wordco.clockworkandroid.edit_session_feature.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
internal fun SlideAwayBottomBar(
    isBottomBarVisible: Boolean,
    content: @Composable (RowScope.() -> Unit)
) {
    // Mimic Horizontal Pager transition
    AnimatedVisibility(
        visible = isBottomBarVisible,
        enter = slideInHorizontally(
            initialOffsetX = { it }, animationSpec = tween(280)
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { it }, animationSpec = tween(280)
        )
    ) {
        BottomAppBar(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            content = content
        )
    }
}