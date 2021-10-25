package com.example.composeplayground.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
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
        when (val state = uiState) {
            is WordsListUiState.NoWords -> Box() {
                Text("Empty words")
            }
            is WordsListUiState.HasWords -> {
                WordsListLayout(
                    words = state.words,
                    onTap = {
                        viewModel.loadWords()
                    }
                )
            }
            is WordsListUiState.Initial -> Box() {
                Text("Empty words")
            }
        }
    }
}

@Composable
fun WordsListLayout(words: List<String>, onTap: () -> Unit) {
    LazyColumn(
        Modifier
            .fillMaxWidth(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(
            words.size,
            { index -> "word-${words[index]}" }
        ) { index ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .background(color = Color.Blue)
            ) {
                Text(
                    "word-${words[index]}",
                    modifier = Modifier.fillMaxWidth(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp
                )
            }
        }
    }
}
