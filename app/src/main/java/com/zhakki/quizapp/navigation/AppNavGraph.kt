package com.zhakki.quizapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.zhakki.quizapp.viewmodel.QuizViewModel

@Composable
fun AppNavGraph(
    quizViewModel: QuizViewModel
) {
    val uiState by quizViewModel.uiState.collectAsState()
    val categoryState by quizViewModel.categoryUiState.collectAsState()
    val gameState by quizViewModel.gameUiState.collectAsState()
    val resultState by quizViewModel.resultUiState.collectAsState()
    val historyState by quizViewModel.historyUiState.collectAsState()
    val leaderboardState by quizViewModel.leaderboardUiState.collectAsState()
    val history by quizViewModel.gameHistory.collectAsState()
    val leaderboard by quizViewModel.leaderboard.collectAsState()

    NavGraph(
        categoryState = categoryState,
        gameState = gameState,
        resultState = resultState,
        historyState = historyState,
        leaderboardState = leaderboardState,
        isQuizFinished = uiState.isFinished,

        onCategorySelected = { categoryId ->
            quizViewModel.selectCategory(categoryId)
        },

        onDifficultySelected = quizViewModel::selectDifficulty,
        onAmountSelected = quizViewModel::updateAmount,
        onStartGame = quizViewModel::startQuiz,
        onAnswerSelected = quizViewModel::onAnswerSelected,
        onCancelGame = quizViewModel::cancelQuiz,
        onPlayAgain = quizViewModel::resetQuizUi,

        onRetryCategory = quizViewModel::retryCategory,
        onRetryGame = quizViewModel::startQuiz,
        onRetryResult = quizViewModel::resetQuizUi,
        onRetryHistory = {},
        onRetryLeaderboard = {},

        historyEnabled = history.isNotEmpty(),
        leaderboardEnabled = leaderboard.isNotEmpty()
    )
}
