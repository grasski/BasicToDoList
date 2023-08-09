package com.example.basictodolist

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.lifecycleScope
import com.example.basictodolist.db.Group
import com.example.basictodolist.db.Task
import com.example.basictodolist.repository.AppRepository
import com.example.basictodolist.ui.MainTasksScreen
import com.example.basictodolist.ui.theme.BasicToDoListTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var repository: AppRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
//            repository.upsertTask(Task("Task 1 1", id = 1, finished = true, groupId = 1))
//            repository.upsertGroup(Group("G 1", id = 1))
//            repository.upsertGroup(Group("G 2", id = 2))
//            repository.upsertGroup(Group("G 3", id = 3))
//            repository.upsertGroup(Group("G 4", id = 4))

//
//            repository.upsertTask(Task("Task 1 1", groupId = 1))
//            repository.upsertTask(Task("Task 2 1", groupId = 1))
//            repository.upsertTask(Task("Task 3 1", groupId = 1))
//            repository.upsertTask(Task("Task 4 1", groupId = 1))
//
//            repository.upsertTask(Task("Task 5"))
//            repository.upsertTask(Task("Task 6"))
//
//            repository.upsertTask(Task("Task 7 2", groupId = 2))
//            repository.upsertTask(Task("Task 8 2", groupId = 2))
        }

//        lifecycleScope.launch {
//            repository.deleteTask(Task("Task 7 2", id=7, groupId = 2))
//            repository.deleteTask(Task("Task 8 2", id=8, groupId = 2))
//        }

        setContent {
            BasicToDoListTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainTasksScreen()
                }
            }
        }
    }
}


enum class Keyboard {
    Opened, Closed
}

@Composable
fun keyboardAsState(): State<Keyboard> {
    val keyboardState = remember { mutableStateOf(Keyboard.Closed) }
    val view = LocalView.current
    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            keyboardState.value = if (keypadHeight > screenHeight * 0.15) {
                Keyboard.Opened
            } else {
                Keyboard.Closed
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }

    return keyboardState
}