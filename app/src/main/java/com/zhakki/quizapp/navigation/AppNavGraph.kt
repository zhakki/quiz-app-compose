package com.zhakki.quizapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.zhakki.quizapp.data.local.BestResult
import com.zhakki.quizapp.data.local.GameResultEntity
import com.zhakki.quizapp.ui.category.CategoryItemUi
import com.zhakki.quizapp.ui.category.CategoryUiState
import com.zhakki.quizapp.ui.game.GameUiState
import com.zhakki.quizapp.ui.history.HistoryItemUi
import com.zhakki.quizapp.ui.history.HistoryUiState
import com.zhakki.quizapp.ui.leaderboard.LeaderboardItemUi
import com.zhakki.quizapp.ui.leaderboard.LeaderboardUiState
import com.zhakki.quizapp.ui.result.ResultUiState
import com.zhakki.quizapp.viewmodel.QuizUiState
import com.zhakki.quizapp.viewmodel.QuizViewModel

@Composable
fun AppNavGraph(
    quizViewModel: QuizViewModel
) {
    val uiState by quizViewModel.uiState.collectAsState()
    val history by quizViewModel.gameHistory.collectAsState()
    val leaderboard by quizViewModel.leaderboard.collectAsState()

    NavGraph(
        categoryState = uiState.toCategoryUiState(),
        gameState = uiState.toGameUiState(),
        resultState = uiState.toResultUiState(),
        historyState = history.toHistoryUiState(),
        leaderboardState = leaderboard.toLeaderboardUiState(),
        isQuizFinished = uiState.isFinished,

        onCategorySelected = { categoryId ->
            uiState.categories
                .firstOrNull { it.id.toString() == categoryId }
                ?.let(quizViewModel::selectCategory)
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

        historyEnabled = true,
        leaderboardEnabled = true
    )
}

private fun QuizUiState.toCategoryUiState(): CategoryUiState {
    if (isLoading && categories.isEmpty()) return CategoryUiState.Loading
    if (error != null && categories.isEmpty()) return CategoryUiState.Error(error)
    if (categories.isEmpty()) return CategoryUiState.Empty

    return CategoryUiState.Content(
        title = "Choose category",
        categories = categories.map {
            CategoryItemUi(
                id = it.id.toString(),
                name = it.name
            )
        },
        selectedCategoryId = selectedCategory?.id?.toString(),
        selectedDifficulty = selectedDifficulty,
        amount = amount,
        amountOptions = listOf(5, 10, 15, 20),
        inlineError = error,
        canStart = selectedCategory != null && !isLoading,
        isStartInProgress = isLoading && selectedCategory != null
    )
}

private fun QuizUiState.toGameUiState(): GameUiState {
    if (isFinished) return GameUiState.Empty
    if (isLoading && currentQuestion == null) return GameUiState.Loading
    if (error != null && currentQuestion == null) return GameUiState.Error(error)

    val question = currentQuestion ?: return GameUiState.Empty

    return GameUiState.Content(
        questionNumber = currentQuestionIndex + 1,
        totalQuestions = totalQuestions.coerceAtLeast(1),
        questionText = question.questionText,
        answers = currentAnswers,
        progress = if (totalQuestions > 0) {
            (currentQuestionIndex + 1).toFloat() / totalQuestions.toFloat()
        } else {
            0f
        },
        correctAnswers = correctAnswersCount
    )
}

private fun QuizUiState.toResultUiState(): ResultUiState {
    if (!isFinished) return ResultUiState.Empty

    val categoryName = selectedCategory?.name ?: currentQuestion?.category ?: "Quiz"

    return ResultUiState.Content(
        title = "Quiz finished",
        summary = "$correctAnswersCount / $totalQuestions",
        details = listOf(
            "Category" to categoryName,
            "Difficulty" to selectedDifficulty.name,
            "Questions" to totalQuestions.toString(),
            "Correct answers" to correctAnswersCount.toString()
        )
    )
}

private fun List<GameResultEntity>.toHistoryUiState(): HistoryUiState {
    if (isEmpty()) return HistoryUiState.Empty

    return HistoryUiState.Content(
        items = mapIndexed { index, item ->
            HistoryItemUi(
                id = "$index-${item.date}",
                title = "${item.category} • ${item.score}/${item.totalQuestions}",
                subtitle = item.date
            )
        }
    )
}

private fun List<BestResult>.toLeaderboardUiState(): LeaderboardUiState {
    if (isEmpty()) return LeaderboardUiState.Empty

    return LeaderboardUiState.Content(
        items = mapIndexed { index, item ->
            LeaderboardItemUi(
                id = "$index-${item.category}",
                name = item.category,
                scoreText = item.bestScore.toString()
            )
        }
    )
}