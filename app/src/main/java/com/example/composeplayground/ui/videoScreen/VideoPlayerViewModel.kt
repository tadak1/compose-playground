package com.example.composeplayground.ui.videoScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor() : ViewModel() {
    private val _currentPositionFlow = MutableStateFlow<Long>(value = 0)
    val currentPositionFlow: StateFlow<Long> = _currentPositionFlow

    fun updateLastPosition(position: Long) {
        viewModelScope.launch {
            _currentPositionFlow.emit(position)
        }
    }
}
