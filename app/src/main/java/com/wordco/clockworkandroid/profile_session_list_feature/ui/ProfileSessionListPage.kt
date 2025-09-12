package com.wordco.clockworkandroid.profile_session_list_feature.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordco.clockworkandroid.core.domain.util.DummyData
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.profile_session_list_feature.ui.model.mapper.toProfileSessionListItem

@Composable
fun ProfileSessionListPage(
    viewModel: ProfileSessionListViewModel,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileSessionListPage(
        uiState = uiState,
        onBackClick = onBackClick,
    )
}

@Composable
private fun ProfileSessionListPage(
    uiState:  ProfileSessionListUiState,
    onBackClick: () -> Unit,
) {
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            when (uiState) {
                is ProfileSessionListUiState.Retrieved -> ProfileSessionListPageRetrieved(
                    uiState = uiState,
                    onBackClick = onBackClick,
                )
                ProfileSessionListUiState.Retrieving -> ProfileSessionListPageRetrieving(
                    onBackClick = onBackClick
                )
            }
        }
    }
}


@Composable
private fun ProfileSessionListPageRetrieved(
    uiState: ProfileSessionListUiState.Retrieved,
    onBackClick: () -> Unit,
) {
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {

        }
    }
}


@Composable
private fun ProfileSessionListPageRetrieving(
    onBackClick: () -> Unit,
) {
    Scaffold { paddingValues ->
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
                sessions = DummyData.SESSIONS.map { it.toProfileSessionListItem() },
            ),
            onBackClick = {},
        )
    }
}

@Preview
@Composable
private fun ProfileSessionListPageRetrievingPreview() {
    ClockworkTheme {
        ProfileSessionListPageRetrieving (
            onBackClick = {},
        )
    }
}