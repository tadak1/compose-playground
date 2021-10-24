package com.example.composeplayground.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.example.composeplayground.viewmodels.WordsListUiState
import com.example.composeplayground.viewmodels.WordsListViewModel

@Preview
@Composable
fun TestWordsListPreview() {
    val words: List<String> = listOf("First", "Second")
    val onTap: () -> Unit = {}
    MaterialTheme() {
        WordsListLayout(words = words, onTap = onTap)
    }
}

@Composable
fun WordsListScreen(viewModel: WordsListViewModel) {
    val uiState by viewModel.uiState.observeAsState(
        WordsListUiState.Initial()
    )
    Scaffold {
        when (uiState) {
            is WordsListUiState.NoWords -> Box() {
                Text("Empty words")
            }
            is WordsListUiState.HasWords -> WordsListLayout(
                words = (uiState as WordsListUiState.HasWords).words,
                onTap = {
                    viewModel.loadWords()
                }
            )
            is WordsListUiState.Initial -> Box() {
            }
        }
    }
}

@Composable
fun WordsListLayout(words: List<String>?, onTap: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        words?.let {
            for (word in it) Text(text = word)
        }
        Button(onClick = onTap) {
            Text(text = "Load Words")
        }
    }
}
