package com.wordco.clockworkandroid.session_list_feature.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.core.ui.composables.CalImage
import com.wordco.clockworkandroid.core.ui.composables.ComputerImage
import com.wordco.clockworkandroid.core.ui.composables.UserImage
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.session_list_feature.ui.model.NewTaskListItem
import com.wordco.clockworkandroid.session_list_feature.ui.util.asHHMM
import com.wordco.clockworkandroid.session_list_feature.ui.util.asTaskDueFormat

@Composable
fun UpcomingTaskUIListItem(
    task: NewTaskListItem,
    modifier: Modifier = Modifier
) = Row(
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
) {
    Box(
        modifier = Modifier
            .background(color = task.color)
            .fillMaxHeight()
            .width(10.dp)
    )
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.padding(2.dp)
    )
    {
        Text(
            task.name,
            fontFamily = LATO,
            fontSize = 23.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.height(30.dp),

            ) {
            CalImage()
            Text(
                task.dueDate.asTaskDueFormat(),
                fontFamily = LATO,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        )
        {
            UserImage()
            Text(
                task.userEstimate.asHHMM(),
                fontFamily = LATO,
                fontSize = 23.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.width(65.dp)
            )
            ComputerImage()
            Text(
                task.appEstimate.asHHMM(),
                fontFamily = LATO,
                fontSize = 23.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.width(65.dp)
            )
        }
    }
}