package com.wordco.clockworkandroid.profile_session_list_feature.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.profile_session_list_feature.ui.model.TabbedScreenItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TabbedScreen(
    screens: List<TabbedScreenItem>,
    coroutineScope: CoroutineScope,
    accentColor: Color,
) {
    val pagerState = rememberPagerState { screens.size }

    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = MaterialTheme.colorScheme.background,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    color = accentColor,
                    modifier = Modifier.tabIndicatorOffset(
                        tabPositions[pagerState.currentPage]
                    )
                )
            }
        ) {
            screens.forEachIndexed { index, screen ->
                Tab(
                    selected = (pagerState.currentPage == index),
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = screen.label,
                            fontFamily = LATO,
                        )
                    },
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top
        ) { page ->
            screens[page].screen()
        }
    }
}