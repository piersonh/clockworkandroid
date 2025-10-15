package com.wordco.clockworkandroid.edit_session_feature.ui.composables

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/**
 * A bottom bar that slides horizontally in perfect sync with a HorizontalPager's scroll.
 *
 * @param pagerState The state of the HorizontalPager that controls this bar's visibility.
 * @param visibleOnPage The index of the page where the bottom bar should be fully visible.
 * @param content The content to display inside the BottomAppBar.
 */
@Composable
fun PagerAwareSlideAwayBottomBar(
    pagerState: PagerState,
    visibleOnPage: Int,
    content: @Composable (RowScope.() -> Unit)
) {
    // calculates how far (0 to 1) the bar should be translated
    //  off-screen based on the pager's current scroll offset.
    val translationFraction by remember {
        derivedStateOf {
            val offset = when (pagerState.currentPage) {
                visibleOnPage -> {
                    // Swiping AWAY from the visible page (from page 1 to 0)
                    //  offset fraction goes from 0 to -1 but it needs to be 0 to 1
                    -pagerState.currentPageOffsetFraction
                }
                visibleOnPage - 1 -> {
                    // Swiping TOWARDS the visible page (from page 0 to 1)
                    //  offset fraction goes from 0 to 1 but it needs to be 1 to 0
                    1f - pagerState.currentPageOffsetFraction
                }
                else -> {
                    // do not show the bar (it's off screen)
                    1f
                }
            }
            // Ensure the value stays within the 0 to 1 range, handling overscrolling
            offset.coerceIn(0f, 1f)
        }
    }

    // don't compose the BottomAppBar if it's fully off-screen and not moving
    if (translationFraction == 1f && pagerState.currentPageOffsetFraction == 0f) {
        return
    }

    BottomAppBar(
        // better than modifier.offset
        modifier = Modifier.graphicsLayer {
            translationX = translationFraction * size.width
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        content = content
    )
}