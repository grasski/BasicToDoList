package com.example.basictodolist.ui.tasks

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.basictodolist.ui.groups.FocusedGroupTaskView
import com.example.basictodolist.ui.groups.TasksCompletedShowSwitcher
import com.example.basictodolist.ui.viewmodel.AppViewModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TasksScreen() {
    val viewModel: AppViewModel = hiltViewModel()
    val tasks = viewModel.allTasks.collectAsStateWithLifecycle().value?.filter { it.groupId == null }

    var showCompleted by remember { mutableStateOf(true) }
    tasks?.let { ts ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
        ){
            val lazyState = rememberLazyListState()

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                state = lazyState,
                modifier = Modifier.clip(RoundedCornerShape(40.dp))
            ){
                if (ts.none { !it.finished } && ts.isNotEmpty()){
                    item{
                        TasksCompletedShowSwitcher(
                            ts.filter { it.finished }.size,
                            showCompleted
                        ) { showCompleted = it }
                    }
                }

                itemsIndexed(ts.filter { !it.finished }.reversed() +
                        ts.filter { it.finished }.reversed(),
                    key = { _, item -> item.id }
                ){index, task ->
                    Column(modifier = Modifier.animateItemPlacement(tween(durationMillis = 600))){
                        if (!task.finished || showCompleted){
                            FocusedGroupTaskView(task = task, showKeyb = {  })
                            Spacer(
                                modifier = Modifier
                                    .height(2.dp)
                                    .fillMaxWidth()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color.Transparent,
                                                MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                    alpha = 0.5f
                                                ),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                        }

                        if (
                            index == ts.filter { !it.finished }.size-1 &&
                            !ts.none { it.finished }
                        ){
                            TasksCompletedShowSwitcher(
                                ts.filter { it.finished }.size,
                                showCompleted
                            ) { showCompleted = it }
                        }
                    }
                }
            }
        }
    }
}
