package com.zhakki.quizapp.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

sealed interface GameUiState {
    data object Loading : GameUiState
    data class Error(val message: String) : GameUiState
    data object Empty : GameUiState
    data class Content(
        val questionNumber: Int,
        val totalQuestions: Int,
        val questionText: String,
        val answers: List<String>,
        val progress: Float,
        val correctAnswers: Int? = null
    ) : GameUiState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    state: GameUiState,
    onAnswerClick: (Int) -> Unit,
    onCancel: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Game") }
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
                GameUiState.Loading -> {
                    Text("Loading...", style = MaterialTheme.typography.titleMedium)
                }

                is GameUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(onClick = onRetry) { Text("Retry") }
                    OutlinedButton(onClick = onCancel) { Text("Back") }
                }

                GameUiState.Empty -> {
                    Text("No questions available", style = MaterialTheme.typography.titleMedium)
                    Button(onClick = onRetry) { Text("Refresh") }
                    OutlinedButton(onClick = onCancel) { Text("Back") }
                }

                is GameUiState.Content -> {
                    LinearProgressIndicator(
                        progress = { state.progress.coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Question ${state.questionNumber} of ${state.totalQuestions}",
                        style = MaterialTheme.typography.titleSmall
                    )

                    state.correctAnswers?.let { count ->
                        Text(
                            text = "Correct answers: $count",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = state.questionText,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(Modifier.height(6.dp))

                    state.answers.forEachIndexed { index, answer ->
                        OutlinedButton(
                            onClick = { onAnswerClick(index) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(answer)
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    Button(
                        onClick = onCancel,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Finish")
                    }
                }
            }
        }
    }
}
