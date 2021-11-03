package com.example.composeplayground.ui.handler

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

typealias VoidCallback = () -> Unit

class LifecycleEventsCallback constructor(
    val onCreate: VoidCallback?,
    val onStart: VoidCallback?,
    val onResume: VoidCallback?,
    val onPause: VoidCallback?,
    val onStop: VoidCallback?,
    val onDestroy: VoidCallback?,
    val onAny: VoidCallback?,
)

@Composable
fun LifecycleEventHandler(
    onCreate: VoidCallback? = null,
    onStart: VoidCallback? = null,
    onResume: VoidCallback? = null,
    onPause: VoidCallback? = null,
    onStop: VoidCallback? = null,
    onDestroy: VoidCallback? = null,
    onAny: VoidCallback? = null,
) {
    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)
    val scope = rememberCoroutineScope()
    val callback by remember {
        mutableStateOf(
            LifecycleEventsCallback(
                onCreate = onCreate,
                onStart = onStart,
                onResume = onResume,
                onPause = onPause,
                onStop = onStop,
                onDestroy = onDestroy,
                onAny = onAny
            )
        )
    }

    DisposableEffect(callback, lifecycleOwner, scope) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> callback.onCreate?.apply { this() }
                Lifecycle.Event.ON_START -> callback.onStart?.apply { this() }
                Lifecycle.Event.ON_RESUME -> callback.onResume?.apply { this() }
                Lifecycle.Event.ON_PAUSE -> callback.onPause?.apply { this() }
                Lifecycle.Event.ON_STOP -> callback.onStop?.apply { this() }
                Lifecycle.Event.ON_DESTROY -> callback.onDestroy?.apply { this() }
                Lifecycle.Event.ON_ANY -> callback.onAny?.apply { this() }
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}
