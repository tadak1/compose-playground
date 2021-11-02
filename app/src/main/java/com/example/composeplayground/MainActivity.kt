package com.example.composeplayground

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composeplayground.ui.components.LocalNavigator
import com.example.composeplayground.ui.components.NavigationComponent
import com.example.composeplayground.ui.theme.ComposePlaygroundTheme
import com.example.composeplayground.ui.wordsList.WordsListScreen
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
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavigationComponent(navController = navController) {
                ComposePlaygroundTheme {
                    NavHost(
                        navController = navController,
                        startDestination = "/"
                    ) {
                        composable("/") {
                            Box(Modifier.fillMaxSize()) {
                                Box(
                                    modifier = Modifier.align(Alignment.Center),
                                    content = @Composable() {
                                        val navigator = LocalNavigator.current
                                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Button(onClick = {
                                                navigator.navigateWords()
                                            }) {
                                                Text("Words")
                                            }
                                            Button(onClick = {
                                                navigator.navigateVideo()
                                            }) {
                                                Text("Video")
                                            }
                                        }
                                    },
                                )
                            }
                        }
                        composable("/words") {
                            WordsListScreen(hiltViewModel())
                        }
                        composable("/video") {
                            val videoPlayerViewModel: VideoPlayerViewModel = hiltViewModel()
                            VideoScreen(videoPlayerViewModel)
                        }
                    }
                }
            }
        }
    }
}


@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
) : ViewModel() {
    private val _currentPositionFlow = MutableStateFlow<Long>(value = 0)
    val currentPositionFlow: StateFlow<Long> = _currentPositionFlow


    fun updateLastPosition(position: Long) {
        viewModelScope.launch {
            _currentPositionFlow.emit(position)
        }
    }
}

@Composable
fun currentPosition(currentPositionFlow: SharedFlow<Long>): State<Long> {
    val lifeCycleOwner = LocalLifecycleOwner.current
    return produceState(initialValue = 0L) {
        lifeCycleOwner.lifecycleScope.launch {
            lifeCycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                currentPositionFlow.collect {
                    Timber.tag("currentPosition").i("current position flow: $it")
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
    val currentPosition by videoPlayerViewModel.currentPositionFlow.collectAsState(initial = 0)
    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)
    val scope = rememberCoroutineScope()

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
    DisposableEffect(lifecycleOwner, scope) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    exoPlayer.playWhenReady = false
                }
                Lifecycle.Event.ON_RESUME -> {
                    exoPlayer.playWhenReady = true
                }
                Lifecycle.Event.ON_DESTROY -> {
                    videoPlayerViewModel.updateLastPosition(exoPlayer.currentPosition)
                    exoPlayer.run {
                        stop()
                        release()
                    }
                }
            }

        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    VideoPlayer(exoPlayer = exoPlayer)
}


@Composable
fun VideoPlayer(exoPlayer: ExoPlayer) {
    val activity = LocalContext.current as Activity
    val statusController = SystemStatusBarController(activity = activity)
    val navigator = LocalNavigator.current
    val configuration = LocalConfiguration.current
    val orientation = remember { mutableStateOf(Configuration.ORIENTATION_PORTRAIT) }

    SideEffect {
        orientation.value = configuration.orientation
    }

    // StatusBarのコントロールを意図的にDelayします。
    // requestOrientationとhideStatusが正しく動作しないため
    LaunchedEffect(orientation.value) {
        if (orientation.value != Configuration.ORIENTATION_LANDSCAPE) {
            statusController.showStatusBar()
            return@LaunchedEffect
        }
        statusController.hideStatusBar()
    }

    Scaffold(
        topBar = {
            if (orientation.value != Configuration.ORIENTATION_LANDSCAPE) {
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
            BackHandler(enabled = true) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                navigator.backToPage()
            }
        }
    }
}