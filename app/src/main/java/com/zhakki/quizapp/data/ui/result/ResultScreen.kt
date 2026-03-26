package com.zhakki.quizapp.data.ui.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.zhakki.quizapp.ui.result.ResultScreen as UiResultScreen
import com.zhakki.quizapp.ui.result.ResultUiState
import com.zhakki.quizapp.viewmodel.QuizUiState
import com.zhakki.quizapp.viewmodel.QuizViewModel

/**
 * Адаптер: [QuizViewModel] → [ResultUiState] → UI [com.zhakki.quizapp.ui.result.ResultScreen].
 */
@Composable
fun ResultScreen(
    viewModel: QuizViewModel,
    onPlayAgain: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    UiResultScreen(
        state = uiState.toResultUiState(),
        onPlayAgain = onPlayAgain,
        onBack = onPlayAgain,
        onRetry = {}
    )
}

private fun QuizUiState.toResultUiState(): ResultUiState {
    return when {
        isLoading && totalQuestions == 0 -> ResultUiState.Loading
        error != null && totalQuestions == 0 && !isFinished ->
            ResultUiState.Error(message = error.orEmpty())
        totalQuestions == 0 -> ResultUiState.Empty
        else -> ResultUiState.Content(
            title = "Результат",
            summary = "Верных ответов: $correctAnswersCount / $totalQuestions",
            details = listOf(
                "Категория" to (selectedCategory?.name ?: "—")
            )
        )
    }
}
