package com.example.basictodolist.ui.groups


import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.basictodolist.Keyboard
import com.example.basictodolist.R
import com.example.basictodolist.animations.AnimatedTextScrolling
import com.example.basictodolist.animations.FadeInFromHorizontallySide
import com.example.basictodolist.animations.FadeInFromVerticallySide
import com.example.basictodolist.animations.ScaleIn
import com.example.basictodolist.db.GroupWithTasks
import com.example.basictodolist.db.Task
import com.example.basictodolist.keyboardAsState
import com.example.basictodolist.ui.viewmodel.AppViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun GroupsScreen() {
    val viewModel: AppViewModel = hiltViewModel()
    val groupWithTasks by viewModel.groupWithTasks.collectAsStateWithLifecycle()

    val lazyState = rememberLazyListState()
    groupWithTasks?.let { g ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)){
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                state = lazyState,
                modifier = Modifier.clip(RoundedCornerShape(40.dp))
            ){
                items(g.reversed()){ grpt ->
                    GroupView(
                        groupWithTasks = grpt,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SelectedGroupScreen() {
    val viewModel: AppViewModel = hiltViewModel()
    val groupWithTasks by viewModel.groupWithTasks.collectAsStateWithLifecycle()
    val selectedGroupId by viewModel.selectedGroupId.collectAsStateWithLifecycle()

    if (selectedGroupId != null) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
        ){
            ScaleIn(
                400,
                content = {
                    val selectedGroup = groupWithTasks?.find {
                        it.group.id == selectedGroupId
                    }
                    selectedGroup?.let {
                        GroupView(
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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupView(
    groupWithTasks: GroupWithTasks,
    modifier: Modifier,
    focused: Boolean = false
) {
    val viewModel: AppViewModel = hiltViewModel()
    val context = LocalContext.current
    val gradientBrush = Brush.sweepGradient(colors = groupWithTasks.group.colors)

    // No clue why, but it updates UI after Menu action with tasks
    LaunchedEffect(viewModel.allTasks.collectAsStateWithLifecycle().value){}

    var showKeyboard by remember { mutableStateOf(false) }

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
                        .fillMaxWidth(0.70f)
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

                        val focusManager = LocalFocusManager.current
                        val keyboard = LocalSoftwareKeyboardController.current

                        val isKeyboardOpen by keyboardAsState()
                        LaunchedEffect(key1 = isKeyboardOpen){
                            if (isKeyboardOpen.name == Keyboard.Closed.name){
                                focusManager.clearFocus()
                                keyboard?.hide()
                                groupNameUpdating = false
                            }
                        }

                        var groupName by remember { mutableStateOf(groupWithTasks.group.name) }
                        BasicTextField(
                            value = groupName,
                            onValueChange = {
                                groupName = it
                                viewModel.viewModelScope.launch {

                                    viewModel.createUpdateGroup(groupWithTasks.group.copy(name = it))
                                }
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
                            maxLines = 3,
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
                        val scroll = rememberScrollState(0)
                        AnimatedTextScrolling(scroll)
                        Text(
                            text = groupWithTasks.group.name,
                            fontSize = 40.sp,
                            lineHeight = 40.sp,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            minLines = 1,
                            modifier = Modifier
                                .height(120.dp)
                                .verticalScroll(scroll, enabled = false)
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
                        var showMenu by remember { mutableStateOf(false) }

                        var showAlertDialog by remember { mutableStateOf(false) }
                        var textsId by remember { mutableIntStateOf(0) }
                        var event by remember { mutableStateOf<GroupMenuEvents?>(null) }

                        OutlinedIconButton(
                            onClick = {
                                if (!focused) {
                                    viewModel.setSelectedGroup(groupWithTasks.group.id)
                                } else{
                                    showMenu = !showMenu
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

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                modifier = Modifier
                            ) {
                                DropdownMenuItem(
                                    leadingIcon = { Icon(Icons.Default.RestartAlt, null) },
                                    text = { Text(context.resources.getString(R.string.resetTasks)) },
                                    onClick = {
                                        showAlertDialog = true
                                        textsId = R.array.GroupAlertsReset
                                        event = GroupMenuEvents.RESET_ALL_TASKS
                                    },
                                    enabled = groupWithTasks.tasks.isNotEmpty()
                                )
                                DropdownMenuItem(
                                    leadingIcon = { Icon(Icons.Default.DoneAll, null) },
                                    text = { Text(context.resources.getString(R.string.finishTasks)) },
                                    onClick = {
                                        showAlertDialog = true
                                        textsId = R.array.GroupAlertsFinishAll
                                        event = GroupMenuEvents.FINISH_ALL_TASKS
                                    },
                                    enabled = groupWithTasks.tasks.isNotEmpty()
                                )
                                DropdownMenuItem(
                                    leadingIcon = { Icon(Icons.Default.Delete, null) },
                                    text = { Text(context.resources.getString(R.string.deleteGroup)) },
                                    onClick = {
                                        if (groupWithTasks.tasks.isEmpty()){
                                            viewModel.deleteGroup(groupWithTasks.group, null)
                                        } else{
                                            showAlertDialog = true
                                            textsId = R.array.GroupAlertsDelete
                                            event = GroupMenuEvents.DELETE_GROUP
                                        }
                                    }
                                )
                            }

                            if (showAlertDialog){
                                GroupMenuAlertDialog(
                                    group = groupWithTasks.group,
                                    tasks = groupWithTasks.tasks,
                                    event = event,
                                    textsId = textsId,
                                    openState = {
                                        showAlertDialog = it

                                        event = null
                                        textsId = 0
                                        showMenu = false
                                    }
                                )
                            }
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
                var showCompleted by remember { mutableStateOf(true) }
                val columnState = rememberLazyListState()
                LaunchedEffect(key1 = groupWithTasks.tasks.none { !it.finished }){
                    columnState.scrollToItem(0)
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (showKeyboard && !groupNameUpdating) 86.dp else 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    state = columnState
                ){
                    if (groupWithTasks.tasks.none { !it.finished } && groupWithTasks.tasks.isNotEmpty()){
                        item{
                            TasksCompletedShowSwitcher(
                                groupWithTasks.tasks.filter { it.finished }.size,
                                showCompleted
                            ) { showCompleted = it }
                        }
                    }

                    itemsIndexed(groupWithTasks.tasks.filter { !it.finished }.reversed() +
                            groupWithTasks.tasks.filter { it.finished }.reversed(),
                        key = { _, item -> item.id }
                    ){index, task ->
                        Column(modifier = Modifier.animateItemPlacement(tween(durationMillis = 600))){
                            if (!task.finished || showCompleted){
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

                            if (
                                index == groupWithTasks.tasks.filter { !it.finished }.size-1 &&
                                        !groupWithTasks.tasks.none { it.finished }
                            ){
                                TasksCompletedShowSwitcher(
                                    groupWithTasks.tasks.filter { it.finished }.size,
                                    showCompleted
                                ) { showCompleted = it }
                            }
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
            newTaskText = TextFieldValue(
                text = "",
                selection = TextRange(0)
            )
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusedGroupTaskView(task: Task, showKeyb: (Boolean) -> Unit) {
    val viewModel: AppViewModel = hiltViewModel()
    val density = LocalDensity.current

    val interactionSource = remember { MutableInteractionSource() }
    val isKeyboardOpen by keyboardAsState()

    var show by remember { mutableStateOf(true) }
    val dismissState = rememberDismissState(
        confirmValueChange = {
            if (it == DismissValue.DismissedToStart) {
                show = false
                true
            } else false
        }, positionalThreshold = {
            with(density) {
                { 130.dp.toPx() }
            }.invoke()
        }
    )
    LaunchedEffect(show) {
        if (!show) {
            viewModel.deleteTask(task)
        }
    }

    SwipeToDismiss(
        state = dismissState,
        modifier = Modifier,
        background = {
            SwipeDeleteGroupTaskBackground(dismissState)
        },
        directions = setOf(DismissDirection.EndToStart),
        dismissContent = {
            Box(modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(0.dp, 20.dp, 20.dp, 0.dp))
                .background(MaterialTheme.colorScheme.background)
                .height(80.dp)
                .padding(10.dp)
            ){
                Row(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
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
    )
}