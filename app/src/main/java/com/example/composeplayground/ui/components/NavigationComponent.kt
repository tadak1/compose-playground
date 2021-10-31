package com.example.composeplayground.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController
import com.example.composeplayground.utilities.Navigator

val LocalNavigator = staticCompositionLocalOf<Navigator> {
    error("LocalNavigator not provided")
}

@Composable
fun NavigationComponent(
    navController: NavController,
    content: @Composable() () -> Unit
) {
    val navigator = Navigator(navController)
    CompositionLocalProvider(LocalNavigator provides navigator) {
        content()
    }
}
