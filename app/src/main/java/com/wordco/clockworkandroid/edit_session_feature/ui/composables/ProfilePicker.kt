package com.wordco.clockworkandroid.edit_session_feature.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.wordco.clockworkandroid.R
import com.wordco.clockworkandroid.core.domain.util.DummyData
import com.wordco.clockworkandroid.core.ui.composables.AccentRectangleTextButton
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.edit_session_feature.ui.model.ProfilePickerItem
import com.wordco.clockworkandroid.edit_session_feature.ui.model.mapper.toProfilePickerItem

@Composable
fun ProfilePicker(
    profiles: List<ProfilePickerItem>,
    modifier: Modifier = Modifier,
    onProfileClick: (Long?) -> Unit,
    onCreateProfileClick: () -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        if (profiles.isEmpty()) {
            return ProfilePickerNoProfiles (
                onProfileClick = onProfileClick,
                onCreateProfileClick = onCreateProfileClick
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .background(color = MaterialTheme.colorScheme.primary)
                .fillMaxSize()
        ) {
            item {
                Spacer(Modifier)
            }

            item {
                ProfilePickerUiItem(
                    name = "No Template",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.clickable {
                        onProfileClick(null)
                    }
                )
            }

            item {
                HorizontalDivider(
                    thickness = 5.dp,
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(100.dp)
                        )
                        .padding(vertical = 5.dp)
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

@Composable
private fun ProfilePickerNoProfiles(
    onProfileClick: (Long?) -> Unit,
    onCreateProfileClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .background(color = MaterialTheme.colorScheme.primary)
            .fillMaxSize()
    ) {
        Spacer(Modifier)

        ProfilePickerUiItem(
            name = "No Template",
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.clickable {
                onProfileClick(null)
            }
        )

        HorizontalDivider(
            thickness = 5.dp,
            modifier = Modifier
                .clip(
                    RoundedCornerShape(100.dp)
                )
                .padding(vertical = 5.dp)
        )


        Spacer(modifier = Modifier.height(80.dp))

        Box (
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.fanned_cards),
                contentDescription = "Templates",
                contentScale = ContentScale.Fit,
                modifier = Modifier.height(170.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "You Haven't Made Any Templates Yet!",
                fontFamily = LATO,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp,
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
                    fontSize = 32.sp
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
            onProfileClick = {},
            onCreateProfileClick = {},
        )
    }
}

@Preview
@Composable
private fun ProfilePickerNoProfilesPreview() {
    ClockworkTheme {
        ProfilePicker(
            profiles = emptyList(),
            onProfileClick = {},
            onCreateProfileClick = {},
        )
    }
}