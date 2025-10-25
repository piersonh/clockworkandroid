package com.wordco.clockworkandroid.profile_session_list_feature.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordco.clockworkandroid.R
import com.wordco.clockworkandroid.core.domain.util.DummyData
import com.wordco.clockworkandroid.core.ui.composables.BackImage
import com.wordco.clockworkandroid.core.ui.composables.NavBar
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.FAKE_TOP_LEVEL_DESTINATIONS
import com.wordco.clockworkandroid.profile_session_list_feature.ui.elements.CompletedSessionUiListItem
import com.wordco.clockworkandroid.profile_session_list_feature.ui.elements.TodoSessionListUiItem
import com.wordco.clockworkandroid.profile_session_list_feature.ui.model.mapper.toTodoSessionListItem
import com.wordco.clockworkandroid.profile_session_list_feature.ui.util.contrastRatioWith
import kotlinx.coroutines.launch

@Composable
fun ProfileSessionListPage(
    viewModel: ProfileSessionListViewModel,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onTodoSessionClick: (Long) -> Unit,
    onCreateNewSessionClick: () -> Unit,
    onCompletedSessionClick: (Long) -> Unit,
    navBar: @Composable () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileSessionListPage(
        uiState = uiState,
        onBackClick = onBackClick,
        onEditClick = onEditClick,
        onDeleteClick = viewModel::onDeleteClick,
        onTodoSessionClick = onTodoSessionClick,
        onCreateNewSessionClick = onCreateNewSessionClick,
        onCompletedSessionClick = onCompletedSessionClick,
        navBar = navBar,
    )

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                ProfileSessionListUiEvent.NavigateBack -> {
                    onBackClick()
                }
            }
        }
    }
}

@Composable
private fun ProfileSessionListPage(
    uiState: ProfileSessionListUiState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onTodoSessionClick: (Long) -> Unit,
    onCreateNewSessionClick: () -> Unit,
    onCompletedSessionClick: (Long) -> Unit,
    navBar: @Composable () -> Unit,
) {
    when (uiState) {
        is ProfileSessionListUiState.Retrieved -> ProfileSessionListPageRetrieved(
            uiState = uiState,
            onBackClick = onBackClick,
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick,
            onTodoSessionClick = onTodoSessionClick,
            onCreateNewSessionClick = onCreateNewSessionClick,
            onCompletedSessionClick = onCompletedSessionClick,
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
    onDeleteClick: () -> Unit,
    onTodoSessionClick: (Long) -> Unit,
    onCreateNewSessionClick: () -> Unit,
    onCompletedSessionClick: (Long) -> Unit,
    navBar: @Composable () -> Unit,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    var showDeleteDialog by remember {mutableStateOf(false)}

    Scaffold (
        topBar = {
            val contentColor = listOf(
                Color.White,
                Color.Black,
            ).maxBy {
                uiState.profileColor.contrastRatioWith(it)
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
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                actions = {
                    Box {
                        IconButton(onClick = { isMenuExpanded = true }) {
                            Icon(
                                painterResource(R.drawable.three_dots_vertical),
                                contentDescription = "More options",
                                tint = contentColor,
                                modifier = Modifier.padding(vertical = 7.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = isMenuExpanded,
                            onDismissRequest = { isMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Edit",
                                        fontSize = 25.sp,
                                        textAlign = TextAlign.Right,
                                        fontFamily = LATO,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                },
                                onClick = {
                                    isMenuExpanded = false
                                    onEditClick()
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Delete",
                                        fontSize = 25.sp,
                                        textAlign = TextAlign.Right,
                                        fontFamily = LATO,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                },
                                onClick = {
                                    isMenuExpanded = false
                                    showDeleteDialog = true
                                }
                            )
                        }
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
                    TodoList(
                        uiState = uiState,
                        onSessionClick = onTodoSessionClick,
                        onCreateNewSessionClick = onCreateNewSessionClick,
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                    )
                },
                TabbedScreenItem(
                    "Complete"
                ) {
                    CompletedList(
                        uiState = uiState,
                        onSessionClick = onCompletedSessionClick,
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                    )
                },
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

                        BasicText(
                            text = uiState.profileName,
                            autoSize = TextAutoSize.StepBased(
                                minFontSize = 24.sp,
                                maxFontSize = 48.sp
                            ),
                            style = TextStyle(
                                fontFamily = LATO,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 48.sp
                            ),
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary)
                                .fillMaxWidth()
                                .padding(20.dp)
                                .heightIn(max=60.dp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
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

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = {
                        Text(
                            "Delete Template?",
                            fontFamily = LATO,
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    text = {
                        Text(
                            "Are you sure about that?",
                            fontFamily = LATO,
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                onDeleteClick()
                            },
                        ) {
                            Text(
                                "Confirm",
                                fontFamily = LATO,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDeleteDialog = false },
                        ) {
                            Text(
                                "Cancel",
                                fontFamily = LATO,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    },
                    properties = DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                    )
                )
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
                    text = { Text(
                        text = screen.label,
                        fontFamily = LATO,
                    ) },
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


@Composable
private fun TodoList(
    uiState: ProfileSessionListUiState.Retrieved,
    onSessionClick: (Long) -> Unit,
    onCreateNewSessionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.todoSessions.isEmpty()) {
        return EmptyTodoList (
            uiState = uiState,
            onCreateNewSessionClick = onCreateNewSessionClick,
            modifier = modifier,
        )
    }

    Box(
        modifier = modifier
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            item {
                Spacer(Modifier.height(5.dp))
            }

            item {
                TextButton(
                    onClick = onCreateNewSessionClick,
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = uiState.profileColor,
                        contentColor = listOf(
                            Color.White,
                            Color.Black
                        ).maxBy {
                            uiState.profileColor.contrastRatioWith(it)
                        }
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Create New Session",
                        fontFamily = LATO,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                }
            }

            item {
                Spacer(Modifier)
            }

            items(
                items = uiState.todoSessions,
                key = { it.id }
            ) { session ->
                TodoSessionListUiItem(
                    session = session,
                    Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .height(IntrinsicSize.Min)
                        .clickable(onClick = { onSessionClick(session.id) })
                )
            }
        }
    }
}

@Composable
private fun EmptyTodoList(
    uiState: ProfileSessionListUiState.Retrieved,
    onCreateNewSessionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box (
        modifier = modifier
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 25.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Only show the image if the available height is larger than our minimum
                if (this@BoxWithConstraints.maxHeight > 400.dp) {
                    Image(
                        painter = painterResource(id = R.drawable.pencil_writing),
                        contentDescription = "Pencil Writing",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxWidth()
                            .weight(1f, fill = false),
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                    )
                }


                //Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "You Haven't Made Any Tasks for this Template...",
                        fontFamily = LATO,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 34.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }

                //Spacer(modifier = Modifier.height(20.dp))

                TextButton(
                    onClick = onCreateNewSessionClick,
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = uiState.profileColor,
                        contentColor = listOf(
                            Color.White,
                            Color.Black
                        ).maxBy {
                            uiState.profileColor.contrastRatioWith(it)
                        }
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(70.dp)
                        .aspectRatio(4f,true)

                ) {
                    Text(
                        text = "Create New Session",
                        fontFamily = LATO,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                }

                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}


@Composable
private fun CompletedList(
    uiState: ProfileSessionListUiState.Retrieved,
    onSessionClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.completeSessions.isEmpty()) {
        return EmptyCompletedList(
            modifier = modifier
        )
    }

    Box(
        modifier = modifier
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            item {
                Spacer(Modifier.height(5.dp))
            }

            items(
                items = uiState.completeSessions,
                key = { it.id }
            ) { session ->
                CompletedSessionUiListItem(
                    session = session,
                    Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .height(IntrinsicSize.Min)
                        .clickable(onClick = { onSessionClick(session.id) })
                )
            }
        }
    }
}



@Composable
private fun EmptyCompletedList (
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
    ) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 25.dp).then(modifier),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Only show the image if the available height is larger than our minimum
        if (this@BoxWithConstraints.maxHeight > 300.dp) {
            Image(
                painter = painterResource(id = R.drawable.trophy),
                contentDescription = "Trophy",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth()
                    .weight(1f, fill = false)
                    //.heightIn(max=10000.dp),
                        ,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No Completed Sessions yet...",
                fontFamily = LATO,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 32.sp,
                lineHeight = 40.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        Spacer(modifier = Modifier.height(60.dp))
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



@PreviewLightDark
@Composable
private fun ProfileSessionListPageRetrievedPreview() {
    ClockworkTheme {
        ProfileSessionListPageRetrieved(
            uiState = ProfileSessionListUiState.Retrieved(
                profileName = "Preview",
                profileColor = Color.Yellow,
                todoSessions = DummyData.SESSIONS
                    .filter { it.profileId != null }
                    .map { it.toTodoSessionListItem() },
                completeSessions = emptyList(),
            ),
            onBackClick = {},
            onEditClick = {},
            onDeleteClick = {},
            onTodoSessionClick = {},
            onCreateNewSessionClick = {},
            onCompletedSessionClick = {},
        ) {
            NavBar(
                items = FAKE_TOP_LEVEL_DESTINATIONS,
                currentDestination = Unit,
                navigateTo = {}
            )
        }
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

@Preview(heightDp = 900)
@Composable
private fun ProfileSessionListPageEmptyTodoPreview() {
    ClockworkTheme {
        ProfileSessionListPageRetrieved(
            uiState = ProfileSessionListUiState.Retrieved(
                profileName = "PreviewPreviewPreviewPreviewPreviewPreviewPreviewPreviewPreviewPreviewPreviewPreviewPreviewPreviewPreview",
                profileColor = Color.Yellow,
                todoSessions = emptyList(),
                completeSessions = emptyList(),
            ),
            onBackClick = {},
            onEditClick = {},
            onDeleteClick = {},
            onTodoSessionClick = {},
            onCreateNewSessionClick = {},
            onCompletedSessionClick = { },
        ) {
            NavBar(
                items = FAKE_TOP_LEVEL_DESTINATIONS,
                currentDestination = Unit,
                navigateTo = {}
            )
        }
    }
}