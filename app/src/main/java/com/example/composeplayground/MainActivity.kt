package com.example.composeplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composeplayground.ui.theme.ComposePlaygroundTheme
import com.example.composeplayground.ui.wordsList.WordsListScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposePlaygroundTheme {
                Surface(
                    color = MaterialTheme.colors.background,
                    content = @Composable {
                        val navController = rememberNavController()
                        NavHost(navController = navController, startDestination = "/") {
                            composable("/") {
                                Column() {
                                    Button(onClick = { navController.navigate("/words") }) {
                                    }
                                }
                            }
                            composable("/words") {
                                WordsListScreen()
                            }
                        }
                    }
                )
            }
        }
    }
}
