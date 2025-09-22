package com.wordco.clockworkandroid.edit_session_feature.ui.util

import androidx.compose.animation.core.tween
import androidx.compose.foundation.pager.PagerState

suspend fun PagerState.tweenToPage(page: Int) {
    animateScrollToPage(
        page = page,
        animationSpec = tween(300)
    )
}