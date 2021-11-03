package com.example.composeplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composeplayground.ui.components.LocalNavigator
import com.example.composeplayground.ui.components.NavigationComponent
import com.example.composeplayground.ui.theme.ComposePlaygroundTheme
import com.example.composeplayground.ui.videoScreen.VideoPlayerViewModel
import com.example.composeplayground.ui.videoScreen.VideoScreen
import com.example.composeplayground.ui.wordsList.WordsListScreen
import dagger.hilt.android.AndroidEntryPoint

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
                            val videoPlayerViewModel = hiltViewModel<VideoPlayerViewModel>()
                            VideoScreen(videoPlayerViewModel)
                        }
                    }
                }
            }
        }
    }
}
