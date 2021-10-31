package com.example.composeplayground.utilities

import androidx.navigation.NavController
import timber.log.Timber

class Navigator constructor(
    private val navController: NavController
) {
    init {
        Timber.tag("Navigator").i("Initialize Navigator")
    }

    fun navigateHome() {
        navController.navigate("/")
    }

    fun navigateWords() {
        navController.navigate("/words")
    }

    fun navigateVideo() {
        navController.navigate("/video")
    }

    fun backToPage() {
        navController.popBackStack()
    }
}
