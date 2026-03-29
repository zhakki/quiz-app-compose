package com.zhakki.quizapp.ui.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.zhakki.quizapp.viewmodel.QuizViewModel

/**
 * StartScreen ühendab [QuizViewModel] andmed [CategoryScreen] vaatega.
 */
@Composable
fun StartScreen(
    viewModel: QuizViewModel,
    onStartQuiz: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenLeaderboard: () -> Unit
) {
    val categoryState by viewModel.categoryUiState.collectAsState()
    val gameHistory by viewModel.gameHistory.collectAsState()
    val leaderboard by viewModel.leaderboard.collectAsState()

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
        onOpenLeaderboard = onOpenLeaderboard,
        historyEnabled = gameHistory.isNotEmpty(),
        leaderboardEnabled = leaderboard.isNotEmpty(),
        onRetry = {}
    )
}
