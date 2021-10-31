package com.example.composeplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composeplayground.ui.theme.ComposePlaygroundTheme
import com.example.composeplayground.ui.wordsList.WordsListScreen
import com.example.composeplayground.utilities.Navigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalNavigator.provides(navigator)
        setContent {
            ComposePlaygroundTheme() {
                CompositionLocalProvider(LocalNavigator provides navigator) {
                    NavigationComponent(navigator) {
                        composable("/") {
                            Column() {
                                Button(onClick = {
                                    navigator.navigateTo(Navigator.NavTarget.WordsList)
                                }) {}
                            }
                        }
                        composable("/words") {
                            WordsListScreen(hiltViewModel())
                        }
                    }
                }
            }
        }
    }
}

@Composable
    }

    NavHost(
        navController = navController,
        startDestination = Navigator.NavTarget.Home.label
    ) {
        navGraphBuilder()
    }
}
