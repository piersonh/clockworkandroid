package com.wordco.clockworkandroid.profile_session_list_feature.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordco.clockworkandroid.R
import com.wordco.clockworkandroid.core.domain.util.DummyData
import com.wordco.clockworkandroid.core.ui.composables.BackImage
import com.wordco.clockworkandroid.core.ui.composables.NavBar
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.FAKE_TOP_LEVEL_DESTINATIONS
import com.wordco.clockworkandroid.profile_session_list_feature.ui.elements.ProfileSessionListUiItem
import com.wordco.clockworkandroid.profile_session_list_feature.ui.model.ProfileSessionListItem
import com.wordco.clockworkandroid.profile_session_list_feature.ui.model.mapper.toProfileSessionListItem
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

@Composable
fun ProfileSessionListPage(
    viewModel: ProfileSessionListViewModel,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onSessionClick: (Long) -> Unit,
    navBar: @Composable () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileSessionListPage(
        uiState = uiState,
        onBackClick = onBackClick,
        onEditClick = onEditClick,
        onSessionClick = onSessionClick,
        navBar = navBar
    )
}

@Composable
private fun ProfileSessionListPage(
    uiState:  ProfileSessionListUiState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onSessionClick: (Long) -> Unit,
    navBar: @Composable () -> Unit,
) {
    when (uiState) {
        is ProfileSessionListUiState.Retrieved -> ProfileSessionListPageRetrieved(
            uiState = uiState,
            onBackClick = onBackClick,
            onEditClick = onEditClick,
            onSessionClick = onSessionClick,
            navBar = navBar,
        )
        ProfileSessionListUiState.Retrieving -> ProfileSessionListPageRetrieving(
            onBackClick = onBackClick,
            navBar = navBar,
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileSessionListPageRetrieved(
    uiState: ProfileSessionListUiState.Retrieved,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onSessionClick: (Long) -> Unit,
    navBar: @Composable () -> Unit,
) {
    Scaffold (
        topBar = {
            val contentColor = listOf(
                Color.White,
                Color.Black,
            ).maxBy {
                val l1 = max(it.luminance(), uiState.profileColor.luminance())
                val l2 = min(it.luminance(), uiState.profileColor.luminance())

                ((l1 + 0.05f) / (l2 + 0.05f))
            }

            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.aspectRatio(0.7f),
                            colorFilter = ColorFilter.tint(color = contentColor)
                        )
                    }
                },
                colors = topAppBarColors(
                    containerColor = uiState.profileColor,
                    titleContentColor = contentColor
                ),
                title = {
                    Text(
                        uiState.profileName,
                        fontFamily = LATO,
                    )
                },
                actions = {
                    TextButton(
                        onClick = onEditClick
                    ) {
                        Text(
                            text = "Edit",
                            style = TextStyle(fontSize = 25.sp),
                            textAlign = TextAlign.Right,
                            fontFamily = LATO,
                            color = contentColor
                        )
                    }
                }
            )
        },
        bottomBar = navBar,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .background(color = MaterialTheme.colorScheme.primary)
                .fillMaxSize()
        ) {
            val screens = listOf(
                TabbedScreenItem(
                    "To-Do"
                ) {
                    SessionList(
                        sessions = uiState.sessions,
                        onSessionClick = onSessionClick,
                        modifier = Modifier
                            .padding(5.dp)
                    )
                },
                TabbedScreenItem(
                    "Complete"
                ) {
                    Text("Completed Sessions")
                }
            )
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Box (
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.profileName,
                            style = TextStyle(fontSize = 48.sp),
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary)
                                .fillMaxWidth()
                                .padding(20.dp),
                            fontFamily = LATO,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )

                        Box (
                            modifier = Modifier
                                .background(color = uiState.profileColor)
                                .fillMaxWidth()
                                .height(10.dp)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Box(
                    modifier = Modifier
                        .clip(
                            shape = RoundedCornerShape(
                                topStart = 10.dp,
                                topEnd = 10.dp,
                            )
                        )
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    TabbedScreen(
                        screens,
                        tabIndicatorColor = uiState.profileColor
                    )
                }
            }
        }
    }
}

data class TabbedScreenItem (
    val label: String,
    val screen: @Composable () -> Unit
)


@Composable
fun TabbedScreen(
    screens: List<TabbedScreenItem>,
    tabIndicatorColor: Color,
) {
    val pagerState = rememberPagerState { screens.size }
    val coroutineScope = rememberCoroutineScope()

    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = MaterialTheme.colorScheme.background,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    color = tabIndicatorColor,
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
                    text = { Text(text = screen.label) },
                )
            }
        }

        HorizontalPager(
            state = pagerState,
        ) { page ->
            screens[page].screen()
        }
    }
}


@Composable
private fun SessionList(
    sessions: List<ProfileSessionListItem>,
    onSessionClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            item {
                Spacer(Modifier)
            }

            items(
                items = sessions,
                key = { it.id }
            ) { session ->
                ProfileSessionListUiItem(
                    session = session,
                    Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .height(100.dp)
                        .clickable(onClick = { onSessionClick(session.id) })
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileSessionListPageRetrieving(
    onBackClick: () -> Unit,
    navBar: @Composable () -> Unit,
) {
    Scaffold (
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        BackImage()
                    }
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                ),
                title = {},
            )
        },
        bottomBar = navBar,
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            Text("Loading")
        }
    }
}



@Preview
@Composable
private fun ProfileSessionListPageRetrievedPreview() {
    ClockworkTheme {
        ProfileSessionListPageRetrieved (
            uiState = ProfileSessionListUiState.Retrieved(
                profileName = "Preview",
                profileColor = Color.Red,
                sessions = DummyData.SESSIONS
                    .filter { it.profileId != null }
                    .map { it.toProfileSessionListItem() },
            ),
            onBackClick = {},
            onEditClick = {},
            onSessionClick = {},
            navBar = { NavBar(
                items = FAKE_TOP_LEVEL_DESTINATIONS,
                currentDestination = Unit,
                navigateTo = {}
            ) },
        )
    }
}

@PreviewLightDark
@Composable
private fun ProfileSessionListPageRetrievingPreview() {
    ClockworkTheme {
        ProfileSessionListPageRetrieving (
            onBackClick = {},
            navBar = { NavBar(
                items = FAKE_TOP_LEVEL_DESTINATIONS,
                currentDestination = Unit,
                navigateTo = {}
            ) },
        )
    }
}