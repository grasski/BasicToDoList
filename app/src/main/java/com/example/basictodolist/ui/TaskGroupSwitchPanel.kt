package com.example.basictodolist.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.basictodolist.Keyboard
import com.example.basictodolist.animations.ShrinkInFromVerticalSide
import com.example.basictodolist.db.Group
import com.example.basictodolist.db.Task
import com.example.basictodolist.db.generateRandomColors
import com.example.basictodolist.keyboardAsState
import com.example.basictodolist.ui.groups.GroupsScreen
import com.example.basictodolist.ui.groups.SelectedGroupScreen
import com.example.basictodolist.ui.tasks.TasksScreen
import com.example.basictodolist.ui.viewmodel.AppViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainPanelView() {
    var shouldAnimate by remember { mutableStateOf(false) }
    var showCreateTask by rememberSaveable { mutableStateOf(false) }

    var panelHeight by remember{ mutableStateOf(0.dp) }
    val density = LocalDensity.current

    val scope = rememberCoroutineScope()

    val viewModel: AppViewModel = hiltViewModel()
    val groupWithTasks by viewModel.groupWithTasks.collectAsStateWithLifecycle()
    val tasks = viewModel.allTasks.collectAsStateWithLifecycle().value?.filter { it.groupId == null }
    val textP by rememberSaveable(groupWithTasks) {
        mutableStateOf("Your \nProjects (${groupWithTasks?.size ?: 0})")
    }
    val textT by rememberSaveable(tasks) {
        mutableStateOf("Your \nTasks (${tasks?.size ?: 0})")
    }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    Box{
        Column{
            Column(modifier = Modifier.padding(20.dp)){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned {
                            panelHeight = with(density) {
                                it.size.height.toDp()
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ){
                    VerticalPager(
                        state = pagerState,
                        modifier = Modifier
                            .height(panelHeight)
                            .fillMaxWidth(0.65f),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        when(it){
                            0 -> {
                                Text(
                                    text = textT,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 32.sp,
                                    lineHeight = 40.sp,
                                    maxLines = 3
                                )
                            }
                            1 -> {
                                Text(
                                    text = textP,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 32.sp,
                                    lineHeight = 40.sp,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .height(panelHeight)
                            .width(10.dp)
                            .offset(x = (-0).dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ){
                        repeat(2) { iteration ->
                            val lineWeight = animateFloatAsState(
                                targetValue = if (pagerState.currentPage == iteration) {
                                    1.5f
                                } else {
                                    0.8f
                                }, label = "height", animationSpec = tween(300, easing = EaseInOut)
                            )
                            val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(color)
                                    .weight(lineWeight.value)
                                    .width(6.dp)
                                    .clickable(
                                        remember { MutableInteractionSource() },
                                        null,
                                        onClick = {
                                            if (pagerState.currentPage != iteration) {
                                                scope.launch {
                                                    pagerState.animateScrollToPage(
                                                        iteration,
                                                        animationSpec = tween(
                                                            300,
                                                            easing = EaseInOut
                                                        )
                                                    )
                                                }
                                            }
                                        })
                            )
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd){
                        FilledIconButton(
                            onClick = {
                                showCreateTask = true
                                shouldAnimate = true
                            },
                            modifier = Modifier.size(80.dp)
                        ) {
                            Icon(Icons.Default.AddCircle, null, Modifier.fillMaxSize())
                        }
                    }
                }
            }

            when(pagerState.settledPage){
                0 -> {
                    TasksScreen()
                }
                1 -> {
                    GroupsScreen()
                }
            }
        }

        SelectedGroupScreen()


        val context = LocalContext.current
        val displayMetrics = context.resources.displayMetrics
        val height = displayMetrics.heightPixels
        if(shouldAnimate){
            ShrinkInFromVerticalSide(offsetY = (height), duration = 500, targetState = showCreateTask) {
                CreateNewTaskScreen(close = {showCreateTask = false}, created = {
                    when(it){
                        Created.NOTHING -> {}
                        Created.TASK_ONLY -> { scope.launch {
                            pagerState.scrollToPage(0)
                        } }
                        Created.TASK_GROUP -> { scope.launch {
                            pagerState.scrollToPage(1)
                        } }
                    }
                } )
            }
        }
    }
}


enum class Created{
    NOTHING, TASK_ONLY, TASK_GROUP
}
@Composable
fun CreateNewTaskScreen(
    close: (Boolean) -> Unit,
    created: (Created) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val viewModel: AppViewModel = hiltViewModel()
    val groups by viewModel.groupWithTasks.collectAsStateWithLifecycle()


    var creatingGroup by remember { mutableStateOf(false) }
    var newGroupName by remember { mutableStateOf("") }
    var newGroupColors by remember { mutableStateOf(generateRandomColors(3)) }
    var selectedGroup by remember { mutableStateOf<Group?>(null) }
    val tasksTitle = remember { mutableStateListOf("","","","","") }


    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val isKeyboardOpen by keyboardAsState()
    LaunchedEffect(isKeyboardOpen){
        if (isKeyboardOpen.name != Keyboard.Opened.name){
            focusManager.clearFocus()
            keyboard?.hide()
        }
    }

    fun close(){
        focusManager.clearFocus()
        keyboard?.hide()
        close(true)
    }
    BackHandler(true) {
        created(Created.NOTHING)
        close()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    focusManager.clearFocus()
                    keyboard?.hide()
                }
            }
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            OutlinedButton(onClick = {
                    close()
                    created(Created.NOTHING)
                },
                modifier= Modifier.size(45.dp),
                shape = CircleShape,
                border= BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor =  MaterialTheme.colorScheme.onBackground)
            ) {
                Icon(Icons.Default.Close, contentDescription = null, Modifier.size(30.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("New Task", fontSize = 45.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
        }


        Column(modifier = Modifier.padding(start = 20.dp)) {
            Text("PROJECTS")
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
                horizontalArrangement = Arrangement.Start
            ){
                item {
                    OutlinedButton(onClick = {
                            creatingGroup = !creatingGroup
                            newGroupColors = generateRandomColors(3)

                            selectedGroup = null
                        },
                        modifier= Modifier
                            .defaultMinSize(minWidth = 80.dp, minHeight = 80.dp)
                            .animateContentSize()
                            .padding(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (creatingGroup)
                                    Brush.sweepGradient(newGroupColors)
                                else
                                    Brush.sweepGradient(
                                        listOf(
                                            Color.Transparent,
                                            Color.Transparent
                                        )
                                    )
                            ),
                        shape = CircleShape,
                        border= BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor =  MaterialTheme.colorScheme.onBackground)
                    ) {
                        if(!creatingGroup){
                            Icon(Icons.Default.Add, contentDescription = null, Modifier.size(30.dp))
                        } else{
                            LaunchedEffect(key1 = Unit){
                                focusRequester.requestFocus()
                                keyboard?.show()
                            }

                            OutlinedTextField(
                                value = newGroupName,
                                onValueChange = { newGroupName = it },
                                modifier = Modifier
                                    .defaultMinSize(minWidth = 80.dp, minHeight = 80.dp)
                                    .animateContentSize()
                                    .focusRequester(focusRequester),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions {
                                    focusRequester.freeFocus()
                                    keyboard?.hide()
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    focusedBorderColor = Color.Transparent,
                                ),
                                trailingIcon = {
                                    Icon(Icons.Default.Cancel, null, modifier = Modifier.clickable {
                                        newGroupName = ""
                                        creatingGroup = false
                                    })
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Palette, null, modifier = Modifier.clickable {
                                        newGroupColors = generateRandomColors(3)
                                    })
                                }
                            )
                        }
                    }
                }

                groups?.let { groups ->
                    items(groups.reversed()){ group ->
                        val gradientBrush = Brush.sweepGradient(colors = group.group.colors)

                        OutlinedButton(onClick = {
                                selectedGroup = if (selectedGroup == group.group) null else group.group

                                creatingGroup = false
                            },
                            modifier= Modifier
                                .fillMaxHeight()
                                .padding(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selectedGroup == group.group)
                                        gradientBrush
                                    else
                                        Brush.sweepGradient(
                                            listOf(
                                                Color.Transparent,
                                                Color.Transparent
                                            )
                                        )
                                ),
                            shape = CircleShape,
                            border= BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                            contentPadding = PaddingValues(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground)
                        ) {
                            Text(group.group.name)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))


        val columnState = rememberLazyListState()
        LazyColumn(
            state = columnState,
            modifier = Modifier.padding(20.dp)
        ) {
            item{
                Text("TITLE")
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ){
                    Text("1.")
                    OutlinedTextField(
                        value = tasksTitle[0],
                        onValueChange = { tasksTitle[0] = it },
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    )
                }
            }

            item{
                repeat(4){ id ->
                    if (tasksTitle[id].isNotBlank()){
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ){
                            Text("${id+2}.")
                            OutlinedTextField(
                                value = tasksTitle[id+1],
                                onValueChange = { tasksTitle[id+1] = it },
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            )
                        }
                    } else{
                        repeat(4-id){ i ->
                            tasksTitle[4-i] = ""
                        }
                        return@repeat
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.BottomCenter
        ){
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                onClick = {
                    if (tasksTitle[0].isBlank()){
                        Toast.makeText(context, "Enter title for your new Task.", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    if(creatingGroup && newGroupName.isNotBlank()){
                        scope.launch {
                            val newGroupId = viewModel.createUpdateGroup(
                                Group(name = newGroupName, colors = newGroupColors)
                            )

                            tasksTitle.forEach { text ->
                                viewModel.createUpdateTask(
                                    Task(text, groupId = newGroupId?.toInt())
                                )
                            }
                        }
                        created(Created.TASK_GROUP)
                        close()
                        return@Button
                    }

                    tasksTitle.forEach { text ->
                        viewModel.createUpdateTask(
                            Task(text, groupId = selectedGroup?.id)
                        )
                    }
                    created(Created.TASK_ONLY)
                    close()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("Create", fontSize = 20.sp)
            }
        }
    }
}
