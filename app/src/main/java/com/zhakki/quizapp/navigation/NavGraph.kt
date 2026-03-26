package com.zhakki.quizapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import com.zhakki.quizapp.ui.theme.QuizAppTheme

@Composable
fun NavGraph(
    darkTheme: Boolean = false,
    startDestination: String = Routes.CATEGORY,
    categoryState: CategoryUiState = CategoryUiState.Loading,
    gameState: GameUiState = GameUiState.Loading,
    resultState: ResultUiState = ResultUiState.Loading,
    historyState: HistoryUiState = HistoryUiState.Loading,
    leaderboardState: LeaderboardUiState = LeaderboardUiState.Loading,
    onCategorySelected: (String) -> Unit = {},
    onStartGame: () -> Unit = {},
    onAnswerSelected: (Int) -> Unit = {},
    onCancelGame: () -> Unit = {},
    onPlayAgain: () -> Unit = {},
    onRetryCategory: () -> Unit = {},
    onRetryGame: () -> Unit = {},
    onRetryResult: () -> Unit = {},
    onRetryHistory: () -> Unit = {},
    onRetryLeaderboard: () -> Unit = {}
) {
    val navController = rememberNavController()

    QuizAppTheme(darkTheme = darkTheme) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable(Routes.CATEGORY) {
                CategoryScreen(
                    state = categoryState,
                    onCategoryClick = onCategorySelected,
                    onDifficultySelected = {},
                    onAmountSelected = {},
                    onStartClick = {
                        onStartGame()
                        navController.navigate(Routes.GAME)
                    },
                    onOpenHistory = { navController.navigate(Routes.HISTORY) },
                    onOpenLeaderboard = { navController.navigate(Routes.LEADERBOARD) },
                    historyEnabled = false,
                    leaderboardEnabled = false,
                    onRetry = onRetryCategory
                )
            }

            composable(Routes.GAME) {
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
                    onBack = { navController.popBackStack(Routes.CATEGORY, false) },
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
}