package com.wordco.clockworkandroid.session_list_feature.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.R
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.theme.ROBOTO
import com.wordco.clockworkandroid.core.ui.util.asHHMM
import com.wordco.clockworkandroid.session_list_feature.ui.model.NewTaskListItem
import com.wordco.clockworkandroid.session_list_feature.ui.util.asTaskDueFormat
import com.wordco.clockworkandroid.session_list_feature.ui.util.toDp

@Composable
fun UpcomingTaskUIListItem(
    task: NewTaskListItem,
    backgroundColor: Color,
    onClick: () -> Unit,
) {
    val density = LocalDensity.current

    SessionListItemUiCard(
        stripeColor = task.color,
        backgroundColor = backgroundColor,
        onClick = onClick
    ) {
        Text(
            task.name,
            fontFamily = LATO,
            fontSize = 23.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.cal),
                contentDescription = "Calendar",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(23.sp.toDp(density)),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
            )
            Text(
                task.dueDate.asTaskDueFormat(),
                fontFamily = LATO,
                fontSize = 23.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                overflow = TextOverflow.Ellipsis,
            )
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = "Completed",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(23.sp.toDp(density)),
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                )
                Text(
                    task.userEstimate?.asHHMM()
                        ?: "––:––",  // These are en dashes
                    fontFamily = ROBOTO,
                    textAlign = TextAlign.Center,
                    fontSize = 23.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.computer),
                    contentDescription = "Completed",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(23.sp.toDp(density)),
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                )

                Text(
                    text = task.appEstimate?.let {
                        String.format(
                            locale = null,
                            format = "%s — %s", // em dash
                            it.low.asHHMM(),
                            it.high.asHHMM()
                        )
                    } ?: "Not Available",

                    fontFamily = LATO,
                    fontSize = 23.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1,
                )
            }
        }
    }
}