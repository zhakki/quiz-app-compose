package com.zhakki.quizapp.data.ui.result

import android.text.Html
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhakki.quizapp.viewmodel.QuizViewModel

private fun decodeHtml(text: String): String {
    return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString()
}

@Composable
fun ResultScreen(
    viewModel: QuizViewModel,
    onPlayAgain: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

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
            text = "Õigeid vastuseid: ${uiState.correctAnswersCount} / ${uiState.totalQuestions}",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Punktid: ${uiState.correctAnswersCount} / ${uiState.totalQuestions}",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Kategooria: ${
                decodeHtml(uiState.selectedCategory?.name ?: uiState.currentQuestion?.category ?: "-")
            }",
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