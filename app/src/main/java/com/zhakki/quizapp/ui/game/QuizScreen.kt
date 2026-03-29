package com.zhakki.quizapp.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhakki.quizapp.viewmodel.QuizViewModel

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onFinishQuiz: () -> Unit,
    onBackToStart: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isFinished) {
        if (uiState.isFinished) {
            onFinishQuiz()
        }
    }

    if (uiState.isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Loading...")
        }
        return
    }

    if (uiState.error != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Error: ${uiState.error}",
                color = MaterialTheme.colorScheme.error
            )

            Button(
                onClick = onBackToStart,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Back")
            }
        }
        return
    }

    val question = uiState.currentQuestion ?: run {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text("No questions available")

            Button(
                onClick = onBackToStart,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Back")
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Quiz has started",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Question ${uiState.currentQuestionIndex + 1} / ${uiState.totalQuestions}",
            style = MaterialTheme.typography.titleSmall
        )

        Text(
            text = question.questionText,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
        )

        uiState.currentAnswers.forEachIndexed { index, answer ->
            OutlinedButton(
                onClick = { viewModel.onAnswerSelected(index) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(answer)
            }
        }

        Button(
            onClick = {
                viewModel.cancelQuiz()
                onBackToStart()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel Game")
        }
    }
}
