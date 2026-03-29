package com.zhakki.quizapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zhakki.quizapp.data.model.Difficulty
import com.zhakki.quizapp.ui.category.CategoryScreen
import com.zhakki.quizapp.ui.category.CategoryUiState
import com.zhakki.quizapp.ui.game.GameScreen
import com.zhakki.quizapp.ui.game.GameUiState
import com.zhakki.quizapp.ui.history.HistoryScreen
import com.zhakki.quizapp.ui.history.HistoryUiState
import com.zhakki.quizapp.ui.leaderboard.LeaderboardScreen
import com.zhakki.quizapp.ui.leaderboard.LeaderboardUiState
import com.zhakki.quizapp.ui.result.ResultScreen
import com.zhakki.quizapp.ui.result.ResultUiState

@Composable
fun NavGraph(
    startDestination: String = Routes.CATEGORY,
    categoryState: CategoryUiState = CategoryUiState.Loading,
    gameState: GameUiState = GameUiState.Loading,
    resultState: ResultUiState = ResultUiState.Empty,
    historyState: HistoryUiState = HistoryUiState.Empty,
    leaderboardState: LeaderboardUiState = LeaderboardUiState.Empty,
    isQuizFinished: Boolean = false,
    onCategorySelected: (String) -> Unit = {},
    onDifficultySelected: (Difficulty) -> Unit = {},
    onAmountSelected: (Int) -> Unit = {},
    onStartGame: () -> Unit = {},
    onAnswerSelected: (Int) -> Unit = {},
    onCancelGame: () -> Unit = {},
    onPlayAgain: () -> Unit = {},
    onRetryCategory: () -> Unit = {},
    onRetryGame: () -> Unit = {},
    onRetryResult: () -> Unit = {},
    onRetryHistory: () -> Unit = {},
    onRetryLeaderboard: () -> Unit = {},
    historyEnabled: Boolean = true,
    leaderboardEnabled: Boolean = true
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.CATEGORY) {
            CategoryScreen(
                state = categoryState,
                onCategoryClick = onCategorySelected,
                onDifficultySelected = onDifficultySelected,
                onAmountSelected = onAmountSelected,
                onStartClick = {
                    onStartGame()
                    navController.navigate(Routes.GAME)
                },
                onOpenHistory = { navController.navigate(Routes.HISTORY) },
                onOpenLeaderboard = { navController.navigate(Routes.LEADERBOARD) },
                historyEnabled = historyEnabled,
                leaderboardEnabled = leaderboardEnabled,
                onRetry = onRetryCategory
            )
        }

        composable(Routes.GAME) {
            LaunchedEffect(isQuizFinished) {
                if (isQuizFinished) {
                    navController.navigate(Routes.RESULT) {
                        popUpTo(Routes.GAME) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }

            GameScreen(
                state = gameState,
                onAnswerClick = onAnswerSelected,
                onCancel = {
                    onCancelGame()
                    navController.popBackStack(Routes.CATEGORY, false)
                },
                onRetry = onRetryGame
            )
        }

        composable(Routes.RESULT) {
            ResultScreen(
                state = resultState,
                onPlayAgain = {
                    onPlayAgain()
                    navController.popBackStack(Routes.CATEGORY, false)
                },
                onBack = {
                    navController.popBackStack(Routes.CATEGORY, false)
                },
                onRetry = onRetryResult
            )
        }

        composable(Routes.HISTORY) {
            HistoryScreen(
                state = historyState,
                onBack = { navController.popBackStack() },
                onRetry = onRetryHistory
            )
        }

        composable(Routes.LEADERBOARD) {
            LeaderboardScreen(
                state = leaderboardState,
                onBack = { navController.popBackStack() },
                onRetry = onRetryLeaderboard
            )
        }
    }
}