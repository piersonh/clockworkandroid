package com.wordco.clockworkandroid.profile_list_feature.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordco.clockworkandroid.R
import com.wordco.clockworkandroid.core.domain.util.DummyData
import com.wordco.clockworkandroid.core.ui.composables.AccentRectangleTextButton
import com.wordco.clockworkandroid.core.ui.composables.NavBar
import com.wordco.clockworkandroid.core.ui.composables.PlusImage
import com.wordco.clockworkandroid.core.ui.composables.SpinningLoader
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.FAKE_TOP_LEVEL_DESTINATIONS
import com.wordco.clockworkandroid.profile_list_feature.ui.elements.ProfileListItemUi
import com.wordco.clockworkandroid.profile_list_feature.ui.model.mapper.toProfileListItem

@Composable
fun ProfileListPage(
    profileListViewModel: ProfileListViewModel,
    navBar: @Composable () -> Unit,
    onProfileClick: (Long) -> Unit,
    onCreateNewProfileClick: () -> Unit,
) {
    val uiState by profileListViewModel.uiState.collectAsStateWithLifecycle()

    ProfileListPage(
        uiState = uiState,
        navBar = navBar,
        onProfileClick = onProfileClick,
        onCreateNewProfileClick = onCreateNewProfileClick,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileListPage (
    uiState: ProfileListUiState,
    navBar: @Composable () -> Unit,
    onProfileClick: (Long) -> Unit,
    onCreateNewProfileClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Task Templates",
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
        bottomBar = navBar,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .background(color = MaterialTheme.colorScheme.primary)
        ) {
            when (uiState) {
                is ProfileListUiState.Retrieved if (uiState.profiles.isEmpty()) -> EmptyProfileList(
                    onCreateProfileClick = onCreateNewProfileClick
                )
                is ProfileListUiState.Retrieved -> ProfileList(
                    uiState = uiState,
                    onProfileClick = onProfileClick
                )

                ProfileListUiState.Retrieving -> SpinningLoader()
            }
        }
    }
}

@Composable
private fun EmptyProfileList(
    onCreateProfileClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box (
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.fanned_cards),
                contentDescription = "Templates",
                contentScale = ContentScale.Fit,
                modifier = Modifier.height(100.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "You Don't Have Any Task Templates!",
                fontFamily = LATO,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Keep similar tasks organized by creating a new template.",
                fontFamily = LATO,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AccentRectangleTextButton(
                onClick = onCreateProfileClick,
            ) {
                Text(
                    text = "Create Template",
                    fontFamily = LATO,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
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
            navBar = { NavBar(
                items = FAKE_TOP_LEVEL_DESTINATIONS,
                currentDestination = Unit::class,
                navigateTo = {}
            ) },
            onProfileClick = { },
            onCreateNewProfileClick = { },
        )
    }
}

@Preview
@Composable
private fun EmptyProfileListPagePreview() {
    ClockworkTheme {
        ProfileListPage(
            uiState = ProfileListUiState.Retrieved(
                profiles = emptyList()
            ),
            navBar = { NavBar(
                items = FAKE_TOP_LEVEL_DESTINATIONS,
                currentDestination = Unit::class,
                navigateTo = {}
            ) },
            onProfileClick = { },
            onCreateNewProfileClick = { },
        )
    }
}