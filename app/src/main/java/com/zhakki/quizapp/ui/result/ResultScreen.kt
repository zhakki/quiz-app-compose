package com.zhakki.quizapp.ui.result

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

sealed interface ResultUiState {
    data object Loading : ResultUiState
    data class Error(val message: String) : ResultUiState
    data object Empty : ResultUiState
    data class Content(
        val title: String,
        val summary: String,
        val details: List<Pair<String, String>>
    ) : ResultUiState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    state: ResultUiState,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Result") }
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
                ResultUiState.Loading -> {
                    Text("Loading...", style = MaterialTheme.typography.titleMedium)
                }

                is ResultUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(onClick = onRetry) { Text("Retry") }
                    OutlinedButton(onClick = onBack) { Text("Back") }
                }

                ResultUiState.Empty -> {
                    Text("No result data", style = MaterialTheme.typography.titleMedium)
                    OutlinedButton(onClick = onBack) { Text("Back") }
                }

                is ResultUiState.Content -> {
                    Text(
                        text = state.title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = state.summary,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.height(4.dp))

                    state.details.forEach { (label, value) ->
                        Text(
                            text = "$label: $value",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = onPlayAgain,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Play Again")
                    }

                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Back to Categories")
                    }
                }
            }
        }
    }
}
