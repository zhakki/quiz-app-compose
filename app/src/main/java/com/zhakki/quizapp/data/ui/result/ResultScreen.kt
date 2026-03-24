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
import androidx.compose.foundation.layout.safeDrawingPadding

@Composable
fun ResultScreen(
    viewModel: QuizViewModel,
    onPlayAgain: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val correct = uiState.correctAnswersCount
    val total = uiState.totalQuestions

    val resultMessage = when {
        total == 0 -> "Tulemus puudub"
        correct == total -> "Kõik vastused olid õiged!"
        correct == 0 -> "Seekord ei tulnud ühtegi õiget vastust."
        else -> "Õigeid vastuseid: $correct / $total"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Tulemus",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = resultMessage,
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Punktid: $correct / $total",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Kategooria: ${uiState.selectedCategory?.name ?: "-"}",
            style = MaterialTheme.typography.titleMedium
        )

        Button(
            onClick = {
                viewModel.resetQuizUi()
                onPlayAgain()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tagasi algusesse")
        }
    }
}
