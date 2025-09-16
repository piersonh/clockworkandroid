package com.wordco.clockworkandroid.edit_session_feature.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wordco.clockworkandroid.core.domain.util.DummyData
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.edit_session_feature.ui.model.ProfilePickerItem
import com.wordco.clockworkandroid.edit_session_feature.ui.model.mapper.toProfilePickerItem

@Composable
fun ProfilePicker(
    profiles: List<ProfilePickerItem>,
    modifier: Modifier = Modifier,
    onProfileClick: (Long?) -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        LazyColumn {
            item {
                ProfilePickerUiItem(
                    name = "No Profile",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.clickable {
                        onProfileClick(null)
                    }
                )
            }

            items(
                items = profiles,
                key = {it.profileId}
            ) {
                ProfilePickerUiItem(
                    name = it.name,
                    color = it.color,
                    modifier = Modifier.clickable {
                        onProfileClick(it.profileId)
                    }
                )
            }
        }
    }
}


@Preview
@Composable
private fun ProfilePickerPreview() {
    ClockworkTheme {
        ProfilePicker(
            profiles = DummyData.PROFILES.map { it.toProfilePickerItem() },
            onProfileClick = { }
        )
    }
}