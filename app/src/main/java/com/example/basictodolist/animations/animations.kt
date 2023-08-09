package com.example.basictodolist.animations


import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun ScaleIn(duration: Int, targetState: Boolean=true, content: @Composable () -> Unit){
    val visibleState = remember { MutableTransitionState(!targetState) }
    visibleState.targetState = targetState

    AnimatedVisibility(
        visibleState = visibleState,
        modifier = Modifier,
        enter = scaleIn(animationSpec = tween(durationMillis = duration)) + expandVertically(expandFrom = Alignment.CenterVertically),
        exit = scaleOut(animationSpec = tween(durationMillis = duration)) + shrinkVertically(shrinkTowards = Alignment.CenterVertically),
    ){
        content()
    }
}

@Composable
fun FadeInFromVerticallySide(offsetY: Int, duration: Int, targetState: Boolean=true, delayMs: Int=0, content: @Composable () -> Unit){
    val visibleState = remember { MutableTransitionState(!targetState) }
    visibleState.targetState = targetState

    var playAnimation by rememberSaveable { mutableStateOf(true) }

    if (playAnimation) {
        playAnimation = false
        AnimatedVisibility(
            visibleState = visibleState,
            enter = slideInVertically(
                animationSpec = tween(durationMillis = duration, delayMillis = delayMs),
                initialOffsetY = { offsetY }
            ) + fadeIn(initialAlpha = 0.01f, animationSpec = tween(durationMillis = duration * 2)),
            //            exit = slideOutVertically() + shrinkVertically() + fadeOut(),
            exit = slideOutVertically(
                targetOffsetY = { offsetY },
                animationSpec = tween(durationMillis = duration)
            ) + fadeOut(),
        ) {
            content()
        }
    } else{
        content()
    }
}

@Composable
fun FadeInFromHorizontallySide(offsetX: Int, duration: Int, targetState: Boolean=true, content: @Composable () -> Unit){
    val visibleState = remember { MutableTransitionState(!targetState) }
    visibleState.targetState = targetState

    var playAnimation by rememberSaveable { mutableStateOf(true) }

    if (playAnimation){
        playAnimation = false
        AnimatedVisibility(
            visibleState = visibleState,
            enter = slideInHorizontally(
                animationSpec = tween(durationMillis = duration),
                initialOffsetX = { offsetX }
            ) + fadeIn(initialAlpha = 0.1f),
            exit = slideOutHorizontally(
                animationSpec = tween(durationMillis = (duration * 1.5).toInt()),
                targetOffsetX = { offsetX }
            ),
        ){
            content()
        }
    } else{
        content()
    }
}

@Composable
fun ShrinkInFromHorizontallySide(offsetX: Int, duration: Int, targetState: Boolean=true, content: @Composable () -> Unit){
    val visibleState = remember { MutableTransitionState(!targetState) }
    visibleState.targetState = targetState

    AnimatedVisibility(
        visibleState = visibleState,
        enter = expandHorizontally { offsetX },
        exit = shrinkHorizontally(
            animationSpec = tween(durationMillis = duration),
            shrinkTowards = Alignment.End,
        ) { 0 }
    ) {
        content()
    }
}

@Composable
fun ScreensCrossFade(
    targetState: Boolean,
    duration: Int,
    trueScreen: @Composable () -> Unit,
    falseScreen: @Composable () -> Unit
) {
    Crossfade(
        targetState = targetState,
        animationSpec = TweenSpec(durationMillis = duration)
    ) { isChecked ->
        if (isChecked) {
            trueScreen()
        } else {
            falseScreen()
        }
    }
}
