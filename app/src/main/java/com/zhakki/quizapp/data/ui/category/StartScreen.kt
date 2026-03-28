package com.zhakki.quizapp.data.ui.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.zhakki.quizapp.ui.category.CategoryScreen
import com.zhakki.quizapp.viewmodel.QuizViewModel

/**
 * Точка интеграции с [QuizViewModel]: маппинг состояния в [CategoryUiState] и вызовы ViewModel из коллбэков UI.
 */
@Composable
fun StartScreen(
    viewModel: QuizViewModel,
    onStartQuiz: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenLeaderboard: () -> Unit
) {
    val categoryState by viewModel.categoryUiState.collectAsState()

    CategoryScreen(
        state = categoryState,
        onCategoryClick = { id ->
            viewModel.selectCategory(id)
        },
        onDifficultySelected = { viewModel.selectDifficulty(it) },
        onAmountSelected = { viewModel.updateAmount(it) },
        onStartClick = {
            viewModel.startQuiz()
            onStartQuiz()
        },
        onOpenHistory = onOpenHistory,
        onOpenLeaderboard = {},
        historyEnabled = true,
        leaderboardEnabled = false,
        onRetry = {}
    )
}
