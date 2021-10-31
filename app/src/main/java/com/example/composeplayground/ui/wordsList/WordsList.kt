package com.example.composeplayground.ui.wordsList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeplayground.R
import com.example.composeplayground.ui.components.LocalNavigator
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Preview
@Composable
fun PreviewWordsListPreview() {
    val words: List<String> = listOf("First", "Second")
    val onTap: () -> Unit = {}
    MaterialTheme() {
        WordsListLayout(words = words, onTap = onTap, isLoading = true)
    }
}

@Composable
fun WordsListScreen(
    wordsListViewModel: WordsListViewModel
) {
    val uiState by wordsListViewModel.uiState.observeAsState(
        WordsListUiState.Initial()
    )

    val navigator = LocalNavigator.current
    Scaffold(
        Modifier.background(color = colorResource(R.color.md_white_1000)),
        topBar = {
            TopAppBar(
                title = { Text("AppBar") },
                navigationIcon = {
                    IconButton(onClick = {
                        navigator.navigateHome()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Navigate Back page")
                    }
                },
            )
        }
    ) {
        val state = uiState
        LoadingContent(
            empty = when (state) {
                is WordsListUiState.Initial -> true
                is WordsListUiState.NoWords -> true
                is WordsListUiState.HasWords -> false
            },
            onRefresh = {
                wordsListViewModel.refresh()
            },
            emptyContent = {
                FullScreenLoading()
            },
            loading = state.isLoading,
            content = {
                when (state) {

                    is WordsListUiState.NoWords -> Box() {
                        Text("Empty words")
                    }
                    is WordsListUiState.HasWords -> {
                        WordsListLayout(
                            isLoading = state.isLoading,
                            words = state.words,
                            onTap = {
                                wordsListViewModel.refresh()
                            }
                        )
                    }
                    is WordsListUiState.Initial -> Box() {
                        Text("Empty words")
                    }
                }
            }
        )
    }
}

@Composable
fun WordsListLayout(isLoading: Boolean, words: List<String>, onTap: () -> Unit) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isLoading),
        onRefresh = {
            onTap()
        }
    ) {
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
                        .fillParentMaxWidth(1F)
                        .padding(horizontal = 8.dp, vertical = 10.dp)
                        .background(
                            color = colorResource(R.color.md_red_200),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            onTap()
                        }
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
}

@Composable
private fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    loading: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    if (empty) {
        emptyContent()
    } else {
        SwipeRefresh(
            state = rememberSwipeRefreshState(loading),
            onRefresh = onRefresh,
            content = content,
        )
    }
}

@Composable
private fun FullScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}
