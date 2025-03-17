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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.wordco.clockworkandroid.model.Task
import com.wordco.clockworkandroid.model.timeAsHHMM
import com.wordco.clockworkandroid.ui.LATO

@Composable
fun TaskList(tasks: List<Task>) = Column(
    verticalArrangement = Arrangement.spacedBy(5.dp),
    modifier = Modifier
        .padding(5.dp)
        .background(color = Color.DarkGray)
        .fillMaxSize()
        .verticalScroll(rememberScrollState())

) {
    for (item in tasks) {
        ListItem(item)
    }
}


@Preview
@Composable
private fun TaskListPrev() = TaskList(
    listOf(
        Task("Assignment", 2660, 33, 3, Color.Green),
        Task("Project Plan", 30000, 20, 2, Color.Blue),
        Task("Homework 100", 100, 5, 3, Color.Red)
    )
)


@Composable
fun ListItem(task: Task) = Row(
    horizontalArrangement = Arrangement.spacedBy(5.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
        .fillMaxWidth(1f)
        .clip(shape = RoundedCornerShape(10.dp))
        .background(color = Color(42, 42, 42))
        .height(40.dp)
) {
    Box(
        modifier = Modifier
            .background(color = task.color)
            .fillMaxHeight()
            .width(10.dp)
    )
    Text(
        task.name,
        fontFamily = LATO,
        fontSize = 23.sp,
        color = Color.White,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.width(130.dp)
    )
    Box(
        modifier = Modifier
            .background(color = Color.Black)
            .fillMaxHeight()
            .width(3.dp)
            .zIndex(4f)
    )
    ClockImage()
    Text(
        task.timeAsHHMM(),
        fontFamily = LATO,
        fontSize = 23.sp,
        color = Color.White,
        modifier = Modifier.width(65.dp)
    )
    CalImage()
    Text(
        task.days.toString(),
        fontFamily = LATO,
        fontSize = 23.sp,
        color = Color.White,
        modifier = Modifier.width(30.dp)
    )
    StarImage()
    Text(
        task.difficulty.toString(),
        fontFamily = LATO,
        fontSize = 23.sp,
        color = Color.White,
        modifier = Modifier.width(30.dp)
    )
}
