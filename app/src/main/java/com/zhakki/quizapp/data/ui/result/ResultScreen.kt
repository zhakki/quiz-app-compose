package com.zhakki.quizapp.data.ui.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.zhakki.quizapp.ui.result.ResultScreen as UiResultScreen
import com.zhakki.quizapp.viewmodel.QuizViewModel

/**
 * Адаптер: [QuizViewModel] → [ResultUiState] → UI [com.zhakki.quizapp.ui.result.ResultScreen].
 */
@Composable
fun ResultScreen(
    viewModel: QuizViewModel,
    onPlayAgain: () -> Unit
) {
    val resultState by viewModel.resultUiState.collectAsState()

    UiResultScreen(
        state = resultState,
        onPlayAgain = onPlayAgain,
        onBack = onPlayAgain,
        onRetry = {}
    )
}
