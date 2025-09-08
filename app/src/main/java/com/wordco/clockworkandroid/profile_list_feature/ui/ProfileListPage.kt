package com.wordco.clockworkandroid.profile_list_feature.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordco.clockworkandroid.core.ui.composables.PlusImage
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.domain.util.DummyData
import com.wordco.clockworkandroid.profile_list_feature.ui.elements.ProfileListItemUi
import com.wordco.clockworkandroid.profile_list_feature.ui.model.mapper.toProfileListItem

@Composable
fun ProfileListPage(
    profileListViewModel: ProfileListViewModel,
    navBar: @Composable () -> Unit,
    onProfileClick: (Long) -> Unit,
    onCreateNewProfileClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    val uiState by profileListViewModel.uiState.collectAsStateWithLifecycle()

    ProfileListPage(
        uiState = uiState,
        navBar = navBar,
        onProfileClick = onProfileClick,
        onCreateNewProfileClick = onCreateNewProfileClick,
        onBackClick = onBackClick,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileListPage (
    uiState: ProfileListUiState,
    navBar: @Composable () -> Unit,
    onProfileClick: (Long) -> Unit,
    onCreateNewProfileClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Task Profiles",
                        fontFamily = LATO,
                    )
                },
                actions = {
                    IconButton(
                        onClick = onCreateNewProfileClick
                    ) {
                        PlusImage(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .fillMaxSize()
                        )
                    }
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                ),
            )
        },
        bottomBar = {
            navBar()
            //TaskBottomBar(onCreateNewTaskClick)
        },
        modifier = Modifier.fillMaxSize()
    ) {
            innerPadding ->

        Box(
            modifier = Modifier
                .padding(
                    PaddingValues(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding()
                    )
                )
                .background(color = MaterialTheme.colorScheme.primary)
        )
        {
            when (uiState) {
                is ProfileListUiState.Retrieved -> ProfileList(
                    uiState = uiState,
                    onProfileClick = onProfileClick
                )
                ProfileListUiState.Retrieving -> Text("Loading...")
            }
        }
    }
}

@Composable
private fun ProfileList(
    uiState: ProfileListUiState.Retrieved,
    onProfileClick: (Long) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .padding(5.dp)
            .background(color = MaterialTheme.colorScheme.primary)
            .fillMaxSize()
    ) {
        items(
            uiState.profiles,
            key = { it.id }
        ) {
            ProfileListItemUi(
                profile = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(10.dp))
                    .background(color = MaterialTheme.colorScheme.primaryContainer)
                    .height(100.dp)
                    .clickable(onClick = { onProfileClick(it.id) })
            )
        }
    }
}


@Preview
@Composable
private fun ProfileListPagePreview() {
    ClockworkTheme {
        ProfileListPage(
            uiState = ProfileListUiState.Retrieved(
                profiles = DummyData.PROFILES.map { it.toProfileListItem() }
            ),
            navBar = {},
            onProfileClick = { },
            onCreateNewProfileClick = { },
            onBackClick = { },
        )
    }
}