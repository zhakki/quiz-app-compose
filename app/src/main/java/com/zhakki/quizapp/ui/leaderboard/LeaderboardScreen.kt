package com.zhakki.quizapp.ui.leaderboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhakki.quizapp.viewmodel.QuizViewModel

sealed interface LeaderboardUiState {
    data object Loading : LeaderboardUiState
    data class Error(val message: String) : LeaderboardUiState
    data object Empty : LeaderboardUiState
    data class Content(val items: List<LeaderboardItemUi>) : LeaderboardUiState
}

data class LeaderboardItemUi(
    val id: String,
    val name: String,
    val scoreText: String
)

@Composable
fun LeaderboardScreen(
    viewModel: QuizViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.leaderboardUiState.collectAsState()
    LeaderboardContent(
        state = state,
        onBack = onBack,
        onRetry = { /* implement retry if needed */ }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardContent(
    state: LeaderboardUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edetabel") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .systemBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (state) {
                LeaderboardUiState.Loading -> {
                    Text("Laadimine...", style = MaterialTheme.typography.titleMedium)
                }

                is LeaderboardUiState.Error -> {
                    Text(
                        text = "Viga: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(onClick = onRetry) { Text("Uuesti") }
                    OutlinedButton(onClick = onBack) { Text("Tagasi") }
                }

                LeaderboardUiState.Empty -> {
                    Text("Andmed puuduvad", style = MaterialTheme.typography.titleMedium)
                    OutlinedButton(onClick = onBack) { Text("Tagasi") }
                }

                is LeaderboardUiState.Content -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        itemsIndexed(state.items) { index, item ->
                            Text(
                                text = "${index + 1}. ${item.name} • ${item.scoreText}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Tagasi") }
                }
            }
        }
    }
}
