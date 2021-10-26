package com.example.composeplayground.viewmodels

import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class WordsListViewModel : ViewModel() {
    private val viewModelState = MutableLiveData(WordsListViewModelState())
    val uiState: LiveData<WordsListUiState> =
        Transformations.map(viewModelState) { viewModelState -> viewModelState.toUiState() }

    init {
        viewModelScope.launch {
            loadWords()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            loadWords()
        }
    }

    private suspend fun loadWords() {
        val state = viewModelState.value
        if (state !is WordsListViewModelState) return
        viewModelState.value = WordsListViewModelState(
            isLoading = true,
            words = state.words,
            errorMessage = null
        )
        delay(2000L)
        viewModelState.value = WordsListViewModelState(
            isLoading = false,
            words = (1..50).map { Random.nextInt().toString() },
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
        val words: List<String>,
        override val isLoading: Boolean,
        override val errorMessage: String?,
    ) : WordsListUiState
}

private data class WordsListViewModelState(
    val isLoading: Boolean = true,
    val words: List<String> = listOf(),
    val errorMessage: String? = null,
) {

    fun toUiState(): WordsListUiState =
        if (words.isEmpty())
            WordsListUiState.NoWords(
                isLoading = isLoading,
                errorMessage = errorMessage
            )
        else
            WordsListUiState.HasWords(
                words = words,
                isLoading = isLoading,
                errorMessage = errorMessage
            )
}
