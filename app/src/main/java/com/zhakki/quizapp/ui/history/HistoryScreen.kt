package com.zhakki.quizapp.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Error(val message: String) : HistoryUiState
    data object Empty : HistoryUiState
    data class Content(val items: List<HistoryItemUi>) : HistoryUiState
}

data class HistoryItemUi(
    val id: String,
    val title: String,
    val subtitle: String?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    state: HistoryUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") }
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
                HistoryUiState.Loading -> {
                    Text("Loading...", style = MaterialTheme.typography.titleMedium)
                }

                is HistoryUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(onClick = onRetry) { Text("Retry") }
                    OutlinedButton(onClick = onBack) { Text("Back") }
                }

                HistoryUiState.Empty -> {
                    Text("History is empty", style = MaterialTheme.typography.titleMedium)
                    OutlinedButton(onClick = onBack) { Text("Back") }
                }

                is HistoryUiState.Content -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(state.items) { item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                item.subtitle?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Back") }
                }
            }
        }
    }
}
