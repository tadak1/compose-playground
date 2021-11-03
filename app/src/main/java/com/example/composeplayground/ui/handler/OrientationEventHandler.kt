package com.example.composeplayground.ui.handler

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun OrientationEventHandler(onOrientationChange: (orientation: Int) -> Unit) {
    val configuration = LocalConfiguration.current
    val orientation = remember { mutableStateOf(configuration.orientation) }
    SideEffect {
        orientation.value = configuration.orientation
    }

    // TODO: WorkAround
    // StatusBarのコントロールを意図的にDelayします。
    // requestOrientationとhideStatusが正しく動作しないため
    LaunchedEffect(orientation.value) {
        onOrientationChange(orientation.value)
    }
}
