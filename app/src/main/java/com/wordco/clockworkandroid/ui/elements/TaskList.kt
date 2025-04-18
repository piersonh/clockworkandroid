package com.wordco.clockworkandroid.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.model.Status
import com.wordco.clockworkandroid.model.Task
import com.wordco.clockworkandroid.model.TaskRegistryViewModel
import com.wordco.clockworkandroid.ui.LATO


// TODO: make lazy columns work
@Composable
fun TaskList(viewModel: TaskRegistryViewModel) = Column(
    verticalArrangement = Arrangement.spacedBy(5.dp),
    modifier = Modifier
        .padding(5.dp)
        .background(color = Color.DarkGray)
        .fillMaxSize()
        .verticalScroll(rememberScrollState())

) {
    val tasks by viewModel.allEntries.collectAsState()
    Text(
        "STARTED",
        fontFamily = LATO,
        fontSize = 25.sp,
        color = Color.White,
        modifier = Modifier.padding(horizontal = 20.dp)
    )
    for (item in tasks) {
        if (item.status != Status.NOT_STARTED) {
            StartedListItem(item)
        }
    }
    Text(
        "UPCOMING",
        fontFamily = LATO,
        fontSize = 25.sp,
        color = Color.White,
        modifier = Modifier.padding(horizontal = 20.dp)
    )
    for (item in tasks) {
        if (item.status == Status.NOT_STARTED) {
            UpcomingListItem(item)
        }
    }
}


//@Preview
//@Composable
//private fun TaskListPrev() = TaskList(
//)


@Composable
fun StartedListItem(task: Task) = Row(
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
        .fillMaxWidth()
        .clip(shape = RoundedCornerShape(10.dp))
        .background(color = Color(42, 42, 42))
        .height(100.dp)
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
            color = Color.White,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
        if (task.status == Status.SUSPENDED) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.height(30.dp),

                ) {
                MoonImage()
                Text(
                    "Suspended",
                    fontFamily = LATO,
                    fontSize = 20.sp,
                    color = Color.White,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.height(30.dp),

                ) {
                StarImage()
                Text(
                    "Running",
                    fontFamily = LATO,
                    fontSize = 20.sp,
                    color = Color.White,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        )
        {
            ClockImage()
            Text(
                Task.timeAsHHMM(task.workTime),
                fontFamily = LATO,
                fontSize = 23.sp,
                color = Color.White,
                modifier = Modifier.width(65.dp)
            )
            MugImage()
            Text(
                Task.timeAsHHMM(task.breakTime),
                fontFamily = LATO,
                fontSize = 23.sp,
                color = Color.White,
                modifier = Modifier.width(65.dp)
            )
        }
    }
}

@Composable
fun UpcomingListItem(task: Task) = Row(
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
        .fillMaxWidth()
        .clip(shape = RoundedCornerShape(10.dp))
        .background(color = Color(42, 42, 42))
        .height(100.dp)
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
            color = Color.White,
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
                task.formatDue(),
                fontFamily = LATO,
                fontSize = 20.sp,
                color = Color.White,
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
                Task.timeAsHHMM(task.workTime),
                fontFamily = LATO,
                fontSize = 23.sp,
                color = Color.White,
                modifier = Modifier.width(65.dp)
            )
            ComputerImage()
            Text(
                Task.timeAsHHMM(task.breakTime),
                fontFamily = LATO,
                fontSize = 23.sp,
                color = Color.White,
                modifier = Modifier.width(65.dp)
            )
        }
    }
}
