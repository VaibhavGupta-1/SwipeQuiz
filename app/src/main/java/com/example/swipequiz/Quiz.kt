package com.example.swipequiz

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun QuizScreen(
    paddingValues: androidx.compose.foundation.layout.PaddingValues
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)){
        SwipeCard(
            onSwipeLeft = { println("Swiped Left") },
            onSwipeRight = { println("Swiped Right") }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .padding(16.dp)
                    .background(Color.Cyan),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Swipe Me!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.Black
                )
            }
        }
    }
}



@Composable
fun SwipeCard(
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {},
    swipeThreshold: Float = 400f,
    sensitivityFactor: Float = 3f,
    content: @Composable () -> Unit
) {
    var offset by remember { mutableStateOf(0f) }
    var dismissRight by remember { mutableStateOf(false) }
    var dismissLeft by remember { mutableStateOf(false) }
    val density = LocalDensity.current.density

    LaunchedEffect(dismissRight) {
        if (dismissRight) {
            delay(300)
            onSwipeRight()
            offset = 0f
            dismissRight = false
        }
    }

    LaunchedEffect(dismissLeft) {
        if (dismissLeft) {
            delay(300)
            onSwipeLeft()
            offset = 0f
            dismissLeft = false
        }
    }

    val animatedRotation by animateFloatAsState(targetValue = offset / 50)
    val animatedAlpha by animateFloatAsState(targetValue = if (dismissLeft || dismissRight) 0f else 1f)

    Box(
        modifier = Modifier
            .offset { IntOffset(offset.roundToInt(), 0) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        offset = 0f
                    }
                ) { change, dragAmount ->
                    offset += (dragAmount / density) * sensitivityFactor
                    when {
                        offset > swipeThreshold -> dismissRight = true
                        offset < -swipeThreshold -> dismissLeft = true
                    }
                    if (change.positionChange() != Offset.Zero) {
                        change.consume()
                    }
                }
            }
            .graphicsLayer(
                alpha = animatedAlpha,
                rotationZ = animatedRotation
            )
    ) {
        content()
    }
}
