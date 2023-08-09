package com.example.basictodolist.ui


import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.basictodolist.Keyboard
import com.example.basictodolist.animations.FadeInFromHorizontallySide
import com.example.basictodolist.animations.FadeInFromVerticallySide
import com.example.basictodolist.animations.ScaleIn
import com.example.basictodolist.db.Group
import com.example.basictodolist.db.GroupWithTasks
import com.example.basictodolist.db.Task
import com.example.basictodolist.keyboardAsState
import com.example.basictodolist.ui.viewmodel.AppViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun MainTasksScreen() {
    LocalContext.current
    val viewModel: AppViewModel = hiltViewModel()

    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val groupWithTasks by viewModel.groupWithTasks.collectAsStateWithLifecycle()

    val lazyState = rememberLazyListState()

    val selectedGroupId by viewModel.selectedGroupId.collectAsStateWithLifecycle()

    Crossfade(targetState = selectedGroupId) {selectedId ->
        if (selectedId == null){
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ){
                Text("Hello, Jiří")
                Text("Groups (${groupWithTasks?.size ?: 0})", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Tasks (${allTasks?.size ?: 0})", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("VYBRANY: $selectedGroupId")

                Button(onClick = {
                    viewModel.createUpdateGroup(Group("GROUP Of HOLIDAYS"))
                }){
                    Text("New group")
                }


                groupWithTasks?.let { g ->
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        state = lazyState,
                        modifier = Modifier.clip(RoundedCornerShape(40.dp))
                    ){
                        items(g){ grpt ->
                            GlobalGroupView(
                                groupWithTasks = grpt,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        } else{
            Box(modifier = Modifier
                .fillMaxSize()
            ){
                ScaleIn(
                    400,
                    content = {
                        val selectedGroup = groupWithTasks?.find {
                            it.group.id == selectedId
                        }
                        selectedGroup?.let {
                            GlobalGroupView(
                                groupWithTasks = it,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.4f)
                                    .animateContentSize(animationSpec = tween(durationMillis = 3000)),
                                true
                            )
                        }
                    }
                )
            }

            BackHandler(true) {
                viewModel.setSelectedGroup(null)
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GlobalGroupView(
    groupWithTasks: GroupWithTasks,
    modifier: Modifier,
    focused: Boolean = false
) {
    val viewModel: AppViewModel = hiltViewModel()
    val gradientBrush = Brush.sweepGradient(colors = groupWithTasks.group.colors)

    var showKeyboard by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    var groupNameUpdating by remember { mutableStateOf(false) }


    var focusedVal by rememberSaveable { mutableStateOf(false) }
    var animPlayed by rememberSaveable { mutableStateOf(false) }
    val verticalBias by animateFloatAsState(
        if (!focusedVal) 1f else -1f,
        animationSpec = tween(400, delayMillis = 200)
    )
    val iconSize by animateDpAsState(
        if (!focusedVal) 80.dp else 40.dp,
        animationSpec = tween(400, delayMillis = 200)
    )
    LaunchedEffect(key1 = Unit) {
        if (focused && !animPlayed) {
            focusedVal = !focusedVal
            animPlayed = true
        }
    }

    Box(
        modifier = modifier
    ) {
        Card(
            shape = if(!focused) RoundedCornerShape(40.dp) else RoundedCornerShape(0.dp, 0.dp, 40.dp, 40.dp)
        ) {
            Row(
                modifier = Modifier
                    .background(gradientBrush)
                    .fillMaxSize()
                    .height(IntrinsicSize.Min)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.7f)
                        .padding(vertical = 30.dp, horizontal = 20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    if (focused) {
                        FadeInFromHorizontallySide(-520, 400, focused) {
                            OutlinedIconButton(
                                onClick = {
                                    if (viewModel.selectedGroupId.value == null) {
                                        viewModel.setSelectedGroup(groupWithTasks.group.id)
                                    } else {
                                        viewModel.setSelectedGroup(null)
                                    }
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .offset((-10).dp, (-20).dp)
                            ) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(0.6f)
                                )
                            }
                        }
                    }

                    if (focused){
                        val focusManager = LocalFocusManager.current
                        val keyboard = LocalSoftwareKeyboardController.current

                        var groupName by remember { mutableStateOf(groupWithTasks.group.name) }

                        BasicTextField(
                            value = groupName,
                            onValueChange = {
                                groupName = it
                                viewModel.createUpdateGroup(groupWithTasks.group.copy(name = it))
                            },
                            interactionSource = remember { MutableInteractionSource() }
                                .also { interactionSource ->
                                    LaunchedEffect(interactionSource) {
                                        interactionSource.interactions.collect {
                                            if (it is PressInteraction.Release) {
                                                groupNameUpdating = true
                                            }
                                        }
                                    }
                                },
                            minLines = 1,
                            maxLines = 2,
                            textStyle = TextStyle(
                                lineHeight = 40.sp,
                                fontSize = 40.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions {
                                focusManager.clearFocus()
                                keyboard?.hide()
                                groupNameUpdating = false
                            }
                        )
                    } else{
                        Text(
                            text = groupWithTasks.group.name,
                            fontSize = 40.sp,
                            lineHeight = 40.sp,
                            fontWeight = FontWeight.ExtraBold,
                            minLines = 1,
                            maxLines = 2,
                        )
                    }


                    Spacer(modifier = Modifier.height(30.dp))

                    ShowTasksStatusOfGroup(groupWithTasks.tasks)
                }


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 10.dp, horizontal = 10.dp)
                ) {
                    Box(
                        Modifier
                            .fillMaxSize()
                    ) {
                        OutlinedIconButton(
                            onClick = {
                                if (viewModel.selectedGroupId.value == null) {
                                    viewModel.setSelectedGroup(groupWithTasks.group.id)
                                }
                            },
                            modifier = Modifier
                                .align(BiasAlignment(1f, verticalBias))
                                .size(iconSize)
                        ) {
                            Icon(
                                Icons.Default.MoreHoriz,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(0.6f)
                            )
                        }

                        if (focused){
                            OutlinedIconButton(
                                onClick = {
                                    viewModel.changeColor(groupWithTasks.group.id)
                                },
                                modifier = Modifier
                                    .align(BiasAlignment(1f, 0f))
                                    .size(iconSize.minus(10.dp))
                            ) {
                                Icon(
                                    Icons.Default.Palette,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if (focused) {
        Column(modifier = Modifier
            .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.45f),
                contentAlignment = Alignment.BottomCenter
            ) {
                FadeInFromVerticallySide(120, 400, focused, delayMs = 200) {
                    FilledIconButton(
                        onClick = {
                            showKeyboard = true
                        },
                        modifier = Modifier
                            .size(80.dp)
                            .offset((0).dp, (0).dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(0.4f),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                Modifier
                    .fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (showKeyboard && !groupNameUpdating) 86.dp else 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ){
                    listOf(
                        groupWithTasks.tasks.filter { !it.finished }.reversed(),
                        groupWithTasks.tasks.filter { it.finished }.reversed()
                    ).forEach{
                        items(it) { task ->
                            FocusedGroupTaskView(task = task, showKeyb = { showKeyboard = it })
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
                    }
                }

                if (!groupNameUpdating){
                    CreateNewGroupTask(groupId = groupWithTasks.group.id, showKeyboard = showKeyboard, showKeyb = { showKeyboard = it })
                }
            }
        }
    }
}


@Composable
fun ShowTasksStatusOfGroup(tasks: List<Task>?) {
    var finishedTasks by remember { mutableIntStateOf(0) }
    var totalTasks by remember { mutableIntStateOf(0) }
    var progress by remember { mutableFloatStateOf(0f) }

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
                "No tasks found.",
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

                Text(text = "tasks", fontSize = 18.sp)
            }
        }
    }
}


@Composable
fun TaskView(task: Task) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .border(2.dp, MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,

    ) {
        OutlinedButton(
            onClick = { /*TODO*/ },
            modifier= Modifier.size(40.dp),  //avoid the oval shape
            shape = CircleShape,
            border= BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            colors = ButtonDefaults.outlinedButtonColors(contentColor =  MaterialTheme.colorScheme.onPrimary)
        ) {
            Icon(Icons.Default.Add, contentDescription = "content description", modifier = Modifier.fillMaxSize())
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = task.taskText,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun CreateNewGroupTask(groupId: Int, showKeyboard: Boolean = false, showKeyb: (Boolean) -> Unit) {
    val viewModel: AppViewModel = hiltViewModel()

    val workingTaskId = viewModel.workingTaskId.collectAsStateWithLifecycle().value
    val editingTask = viewModel.allTasks.collectAsStateWithLifecycle().value?.find { it.id == workingTaskId }

    var newTaskText by remember {
        mutableStateOf(
            TextFieldValue(
                text = editingTask?.taskText ?: "",
                selection = TextRange(editingTask?.taskText?.length ?: 0)
            )
        )
    }

    LaunchedEffect(editingTask){
        newTaskText = TextFieldValue(
            text = editingTask?.taskText ?: "",
            selection = TextRange(editingTask?.taskText?.length ?: 0)
        )
    }


    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val isKeyboardOpen by keyboardAsState()
    LaunchedEffect(isKeyboardOpen){
        showKeyb(isKeyboardOpen.name == Keyboard.Opened.name)   // Listening for HW Back button
        if (isKeyboardOpen.name != Keyboard.Opened.name){
            viewModel.setWorkingTaskId(null)
        }
    }


    if (showKeyboard){
        LaunchedEffect(focusRequester) {
            focusRequester.requestFocus()
            delay(100) // Make sure you have delay here
            keyboard?.show()
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    viewModel.createUpdateTask(
                        editingTask ?: Task(
                            newTaskText.text,
                            groupId = groupId
                        )
                    )
                    showKeyb(false)
                }
            },
            contentAlignment = Alignment.BottomCenter
        ) {
            TextField(
                value = newTaskText,
                onValueChange = {
                    newTaskText = TextFieldValue(
                        text = it.text,
                        selection = TextRange(it.text.length)
                    )

                    editingTask?.taskText =  it.text
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .heightIn(max = 80.dp)
                    .focusRequester(focusRequester),
                keyboardActions = KeyboardActions(
                    onAny = {
                        viewModel.createUpdateTask(editingTask ?: Task(newTaskText.text, groupId = groupId))
                        showKeyb(false)
                    }
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                maxLines = 3
            )
        }
    }
}


@Composable
fun FocusedGroupTaskView(task: Task, showKeyb: (Boolean) -> Unit) {
    val viewModel: AppViewModel = hiltViewModel()

    val interactionSource = remember { MutableInteractionSource() }

    val isKeyboardOpen by keyboardAsState()


    Box(modifier = Modifier
        .fillMaxWidth()
        .height(80.dp)
        .padding(horizontal = 10.dp, vertical = 10.dp)
    ){
        Row(
            Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(modifier = Modifier
                .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ){
                IconButton(
                    onClick = {
                        viewModel.updateTaskStatus(task.copy(finished = !task.finished))
                    },
                    modifier = Modifier
                        .size(30.dp)
                ) {
                    Icon(
                        if (task.finished) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            val scroll = rememberScrollState(0)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource,
                        null,
                        onClick = {
                            viewModel.viewModelScope.launch {
                                if (isKeyboardOpen.name == Keyboard.Opened.name && viewModel.workingTaskId.value != task.id) {
                                    viewModel.setWorkingTaskId(null)
                                    showKeyb(false)
                                } else {
                                    viewModel.setWorkingTaskId(task.id)
                                    showKeyb(true)
                                }

                            }
                        }
                    ),
                contentAlignment = Alignment.CenterStart
            ){
                Text(
                    text = task.taskText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.verticalScroll(scroll)
                )
            }
        }
    }
}