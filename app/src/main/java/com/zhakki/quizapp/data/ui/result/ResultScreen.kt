package com.zhakki.quizapp.data.ui.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhakki.quizapp.viewmodel.QuizViewModel

@Composable
fun ResultScreen(
    viewModel: QuizViewModel,
    onPlayAgain: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Tulemus",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Õigeid vastuseid: ${uiState.correctAnswersCount} / ${uiState.totalQuestions}",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Kategooria: ${uiState.selectedCategory?.name ?: "-"}",
            style = MaterialTheme.typography.titleMedium
        )

        Button(
            onClick = onPlayAgain,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tagasi algusesse")
        }
    }
}