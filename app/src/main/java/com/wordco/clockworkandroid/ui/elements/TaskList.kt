package com.wordco.clockworkandroid.ui.elements

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.wordco.clockworkandroid.model.Status
import com.wordco.clockworkandroid.model.Task
import com.wordco.clockworkandroid.model.returnDueDate
import com.wordco.clockworkandroid.model.timeAsHHMM
import com.wordco.clockworkandroid.ui.LATO

@Composable
fun TaskList(tasks: List<Task>, navController: NavController, curTask: MutableState<Task>) = Column(
    verticalArrangement = Arrangement.spacedBy(5.dp),
    modifier = Modifier
        .padding(5.dp)
        .background(color = Color.DarkGray)
        .fillMaxSize()
        .verticalScroll(rememberScrollState())

) {
    Text("STARTED",
        fontFamily = LATO,
        fontSize = 25.sp,
        color = Color.White,
        modifier = Modifier.padding(horizontal = 20.dp)
    )
    for (item in tasks) {
        if (item.status != Status.SCHEDULED) {
            StartedListItem(item, navController, curTask)
        }
    }
    Text("UPCOMING",
        fontFamily = LATO,
        fontSize = 25.sp,
        color = Color.White,
        modifier = Modifier.padding(horizontal = 20.dp)
    )
    for (item in tasks) {
        if (item.status == Status.SCHEDULED) {
            UpcomingListItem(item)
        }
    }
}

/*
@Preview
@Composable
private fun TaskListPrev() = TaskList(
    listOf(
        Task("Assignment", 2660, 60,  33, 3, Color.Green, Status.RUNNING),
        Task("Project Plan", 30000, 60, 20, 2, Color.Blue, Status.SUSPENDED),
        Task("Homework 99", 100, 60, System.currentTimeMillis() - 100000000, 3, Color.White, Status.SCHEDULED),
        Task("Homework 99.5", 100, 60, System.currentTimeMillis(), 3, Color.Cyan, Status.SCHEDULED),
        Task("Homework -1", 100, 60, 0, 3, Color.Black, Status.SCHEDULED),
        Task("Homework 100", 100, 60, System.currentTimeMillis() + 22000000, 3, Color.Red, Status.SCHEDULED),
        Task("Evil Homework 101", 100, 60, System.currentTimeMillis() + 25000000, 3, Color.Magenta, Status.SCHEDULED),
        Task("Super Homework 102", 100, 60, System.currentTimeMillis() + 111000000, 3, Color.Yellow, Status.SCHEDULED),
    )
)*/


@Composable
fun StartedListItem(task: Task, navController: NavController, curTask: MutableState<Task>) = Row(
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
        .fillMaxWidth()
        .clip(shape = RoundedCornerShape(10.dp))
        .background(color = Color(42, 42, 42))
        .height(100.dp)
        .clickable(onClick = {
            curTask.value = task
            navController.navigate("Timer")
        })
) {
    Box(
        modifier = Modifier
            .background(color = task.color)
            .fillMaxHeight()
            .width(10.dp)
    )
    Column(verticalArrangement = Arrangement.spacedBy(2.dp),
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
                task.timeAsHHMM(task.workTime),
                fontFamily = LATO,
                fontSize = 23.sp,
                color = Color.White,
                modifier = Modifier.width(65.dp)
            )
            MugImage()
            Text(
                task.timeAsHHMM(task.breakTime),
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
    Column(verticalArrangement = Arrangement.spacedBy(2.dp),
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
                task.returnDueDate(),
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
                task.timeAsHHMM(task.workTime),
                fontFamily = LATO,
                fontSize = 23.sp,
                color = Color.White,
                modifier = Modifier.width(65.dp)
            )
            ComputerImage()
            Text(
                task.timeAsHHMM(task.breakTime),
                fontFamily = LATO,
                fontSize = 23.sp,
                color = Color.White,
                modifier = Modifier.width(65.dp)
            )
        }
    }
}
