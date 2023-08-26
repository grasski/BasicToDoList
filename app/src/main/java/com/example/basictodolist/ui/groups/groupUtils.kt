package com.example.basictodolist.ui.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.basictodolist.R
import com.example.basictodolist.db.Group
import com.example.basictodolist.db.Task
import com.example.basictodolist.ui.viewmodel.AppViewModel


enum class GroupMenuEvents{
    RESET_ALL_TASKS, FINISH_ALL_TASKS, DELETE_GROUP
}
@Composable
fun GroupMenuAlertDialog(
    group: Group,
    tasks: List<Task>?,
    event: GroupMenuEvents?,
    textsId: Int,
    openState: (Boolean) -> Unit
) {
    val viewModel: AppViewModel = hiltViewModel()
    val context = LocalContext.current
    val texts = context.resources.getStringArray(textsId)

    AlertDialog(
        onDismissRequest = {
            openState(false)
        },
        title = {
            Text(text = texts[0])
        },
        text = {
            Text(text = texts[1])
        },
        confirmButton = {
            TextButton(
                onClick = {
                    event?.let {
                        when(it){
                            GroupMenuEvents.RESET_ALL_TASKS -> { viewModel.finishOrResetAllGroupTasks(tasks, false) }
                            GroupMenuEvents.FINISH_ALL_TASKS -> { viewModel.finishOrResetAllGroupTasks(tasks, true) }
                            GroupMenuEvents.DELETE_GROUP -> { viewModel.deleteGroup(group, tasks) }
                        }
                    }
                    openState(false)
                }
            ) {
                Text(context.resources.getString(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    openState(false)
                }
            ) {
                Text(context.resources.getString(R.string.dismiss))
            }
        }
    )
}


@Composable
fun ShowTasksStatusOfGroup(tasks: List<Task>?) {
    var finishedTasks by remember { mutableIntStateOf(0) }
    var totalTasks by remember { mutableIntStateOf(0) }
    var progress by remember { mutableFloatStateOf(0f) }
    val context = LocalContext.current

    tasks?.let {
        finishedTasks = tasks.count { it.finished }
        totalTasks = tasks.size
        progress =finishedTasks.toFloat() / totalTasks.toFloat()
    }


    Row(
        modifier = Modifier.height(70.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        if (totalTasks == 0){
            Text(
                context.resources.getString(R.string.noTasks),
                textAlign = TextAlign.Center,
                fontSize = 30.sp
            )
        } else{
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(22.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, Color.White, RoundedCornerShape(16.dp))
                    .background(Color.Transparent)
                    .rotate(180f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(progress)
                        .fillMaxWidth()
                        .background(Color.Green, shape = RoundedCornerShape(16.dp))
                        .rotate(180f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = "$finishedTasks/$totalTasks",
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(text = context.resources.getString(R.string.tasks), fontSize = 18.sp)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeDeleteGroupTaskBackground(dismissState: DismissState) {
    val thresholdReached by remember {
        derivedStateOf {
            dismissState.targetValue == DismissValue.DismissedToStart
        }
    }
    val color = when (dismissState.dismissDirection) {
        DismissDirection.EndToStart -> {
            if (thresholdReached) Color(0xFFFF1744) else MaterialTheme.colorScheme.errorContainer
        }
        else -> {Color.Transparent}
    }
    val direction = dismissState.dismissDirection

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.CenterEnd
    ){
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(color)
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            if (direction == DismissDirection.EndToStart){
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "delete",
                )
            }
        }
    }
}


@Composable
fun TasksCompletedShowSwitcher(completedTasks: Int, showStatus: Boolean, showCompleted: (Boolean) -> Unit) {
    val context = LocalContext.current

    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable(
            remember { MutableInteractionSource() },
            null,
            onClick = { showCompleted(!showStatus) }
        )
        .height(50.dp)
        .padding(10.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            text = context.resources.getString(R.string.tasksCompleted, completedTasks),
            color = Color.LightGray.copy(alpha = 0.8f),
        )
        Icon(
            imageVector = if(!showStatus) { Icons.Default.ArrowDropUp } else Icons.Default.ArrowDropDown,
            contentDescription = null,
            tint = Color.LightGray.copy(alpha = 0.8f)
        )
    }
}