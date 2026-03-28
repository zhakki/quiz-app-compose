package com.zhakki.quizapp.data.ui.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.zhakki.quizapp.ui.category.CategoryItemUi
import com.zhakki.quizapp.ui.category.CategoryScreen
import com.zhakki.quizapp.ui.category.CategoryUiState
import com.zhakki.quizapp.viewmodel.QuizUiState
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
    val uiState by viewModel.uiState.collectAsState()

    CategoryScreen(
        state = uiState.toCategoryUiState(),
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

private fun QuizUiState.toCategoryUiState(): CategoryUiState {
    return when {
        isLoading && categories.isEmpty() -> CategoryUiState.Loading
        error != null && categories.isEmpty() && !isLoading ->
            CategoryUiState.Error(message = error.orEmpty())
        !isLoading && categories.isEmpty() && error == null -> CategoryUiState.Empty
        else -> CategoryUiState.Content(
            title = "Choose a category",
            categories = categories.map { CategoryItemUi(id = it.id.toString(), name = it.name) },
            selectedCategoryId = selectedCategory?.id?.toString(),
            selectedDifficulty = selectedDifficulty,
            amount = amount,
            amountOptions = listOf(5, 10, 15),
            inlineError = error?.takeIf { categories.isNotEmpty() },
            canStart = selectedCategory != null && !isLoading,
            isStartInProgress = isLoading
        )
    }
}
