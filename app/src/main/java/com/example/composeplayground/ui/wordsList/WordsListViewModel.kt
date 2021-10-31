package com.example.composeplayground.ui.wordsList

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class WordsListViewModel @Inject constructor(
) : ViewModel() {
    private val viewModelState = MutableLiveData(WordsListViewModelState())
    val uiState: LiveData<WordsListUiState> =
        Transformations.map(viewModelState) { viewModelState -> viewModelState.toUiState() }

    init {
        Timber.tag("WordsListViewModel").i("Initialize WordsListViewModel")
        viewModelScope.launch {
            loadWords()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            loadWords()
        }
    }

    private suspend fun loadWords() =
        withContext(Dispatchers.Default) {
            val state = viewModelState.value
            if (state !is WordsListViewModelState) return@withContext
            viewModelState.postValue(
                WordsListViewModelState(
                    isLoading = true,
                    words = state.words,
                    errorMessage = null
                )
            )
            delay(2000L)
            viewModelState.postValue(
                WordsListViewModelState(
                    isLoading = false,
                    words = (1..50).map { Random.nextInt().toString() },
                    errorMessage = null
                )
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
