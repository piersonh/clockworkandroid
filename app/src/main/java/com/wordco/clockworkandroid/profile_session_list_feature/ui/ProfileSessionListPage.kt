package com.wordco.clockworkandroid.profile_session_list_feature.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileSessionListPage(
    viewModel: ProfileSessionListViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileSessionListPage(
        uiState = uiState
    )
}

@Composable
private fun ProfileSessionListPage(
    uiState:  ProfileSessionListUiState
) {

}