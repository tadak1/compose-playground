package com.example.composeplayground.ui.videoScreen

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.widget.ImageButton
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.composeplayground.R
import com.example.composeplayground.ui.components.LocalNavigator
import com.example.composeplayground.ui.handler.LifecycleEventHandler
import com.example.composeplayground.ui.handler.OrientationEventHandler
import com.example.composeplayground.utilities.SystemStatusBarController
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun currentPosition(currentPositionFlow: SharedFlow<Long>): State<Long> {
    val lifeCycleOwner = LocalLifecycleOwner.current
    return produceState(initialValue = 0L) {
        lifeCycleOwner.lifecycleScope.launch {
            lifeCycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                currentPositionFlow.collect {
                    value = it
                }
            }
        }
    }
}

@Composable
fun VideoScreen(
    videoPlayerViewModel: VideoPlayerViewModel,
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        SimpleExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
        }
    }
    val currentPosition by currentPosition(videoPlayerViewModel.currentPositionFlow)

    LaunchedEffect(true) {
        exoPlayer.apply {
            val dataSourceFactory = DefaultDataSourceFactory(
                context,
                Util.getUserAgent(context, context.packageName)
            )
            val url =
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(url)))
            setMediaSource(source)
            seekTo(currentPosition)
            prepare()
            playWhenReady = true
        }
    }

    LifecycleEventHandler(
        onPause = {
            exoPlayer.playWhenReady = false
        },
        onResume = {
            exoPlayer.playWhenReady = true
        },
        onDestroy = {
            videoPlayerViewModel.updateLastPosition(exoPlayer.currentPosition)
            exoPlayer.run {
                stop()
                release()
            }
        }
    )
    VideoPlayer(exoPlayer = exoPlayer)
}

@Composable
fun VideoPlayer(exoPlayer: ExoPlayer) {
    val activity = LocalContext.current as Activity
    val statusController = SystemStatusBarController(activity = activity)
    val navigator = LocalNavigator.current
    val configuration = LocalConfiguration.current

    Scaffold(
        topBar = {
            if (configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
                TopAppBar(
                    title = { Text("AppBar") },
                    navigationIcon = {
                        IconButton(onClick = {
                            navigator.navigateHome()
                        }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Navigate Back page")
                        }
                    },
                )
            }
        }
    ) {
        Box {
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        player = exoPlayer
                        this.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                        findViewById<ImageButton>(R.id.exo_fullscreen).setOnClickListener {
                            activity.requestedOrientation =
                                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        }
                    }
                }
            )
        }
        OrientationEventHandler(
            onOrientationChange = { orientation ->
                if (orientation != Configuration.ORIENTATION_LANDSCAPE) {
                    statusController.showStatusBar()
                } else {
                    statusController.hideStatusBar()
                }
            }
        )
        BackHandler(enabled = true) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            navigator.backToPage()
        }
    }
}
