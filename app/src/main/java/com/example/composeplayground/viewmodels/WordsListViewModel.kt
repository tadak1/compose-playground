package com.example.composeplayground.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class WordsListViewModel : ViewModel() {
    private val viewModelState = MutableLiveData<WordsListViewModelState>()
    val uiState: LiveData<WordsListUiState> =
        Transformations.map(viewModelState) { viewModelState -> viewModelState.toUiState() }

    init {
        loadWords()
    }

    fun loadWords() {
        val state = viewModelState.value
        if (state !is WordsListViewModelState) return
        WordsListViewModelState(
            isLoading = true,
            words = listOf(
                "first", "second", "third"
            ),
            errorMessage = null
        )
    }
}

sealed interface WordsListUiState {
    val isLoading: Boolean
    val errorMessage: String?

    data class Initial(
        override val isLoading: Boolean = false,
        override val errorMessage: String? = null,
    ) : WordsListUiState

    data class NoWords(
        override val isLoading: Boolean,
        override val errorMessage: String?,
    ) : WordsListUiState

    data class HasWords(
        val words: List<String>?,
        override val isLoading: Boolean,
        override val errorMessage: String?,
    ) : WordsListUiState
}

private data class WordsListViewModelState(
    val isLoading: Boolean,
    val words: List<String>?,
    val errorMessage: String?,
) {

    fun toUiState(): WordsListUiState =
        words?.let {
            WordsListUiState.NoWords(
                isLoading = isLoading,
                errorMessage = errorMessage
            )
        } ?: run {
            WordsListUiState.HasWords(
                words = words,
                isLoading = isLoading,
                errorMessage = errorMessage
            )
        }
}
