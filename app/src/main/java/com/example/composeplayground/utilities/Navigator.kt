package com.example.composeplayground.utilities

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Navigator @Inject constructor() {
    private val _navigationFlow =
        MutableSharedFlow<NavTarget>(extraBufferCapacity = 1)
    val navigationSharedFlow = _navigationFlow.asSharedFlow()

    fun navigateTo(navTarget: NavTarget) {
        _navigationFlow.tryEmit(navTarget)
    }

    enum class NavTarget(val label: String) {
        Home("/"),
        WordsList("/words")
    }
}
