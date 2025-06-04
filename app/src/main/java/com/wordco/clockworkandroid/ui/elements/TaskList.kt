package com.wordco.clockworkandroid.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.ui.TaskViewModel
import com.wordco.clockworkandroid.ui.theme.LATO

// TODO: Make the running task a separate entity
@Composable
fun TaskList (
    taskViewModel: TaskViewModel
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .padding(5.dp)
            .background(color = Color.DarkGray)
            .fillMaxSize()
    ) {
        item {
            Text(
                "STARTED",
                fontFamily = LATO,
                fontSize = 25.sp,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        items(
            taskViewModel.startedTaskList,
            key={task -> task.taskId}
        ) {
            StartedListItem(it)
        }

        item {
            Text(
                "UPCOMING",
                fontFamily = LATO,
                fontSize = 25.sp,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        items(
            taskViewModel.upcomingTaskList,
            key={task -> task.taskId}
        ) {
            UpcomingTaskUIListItem(it)
        }
    }
}


//@Preview
//@Composable
//private fun TaskListPrev() = TaskList(
//)





