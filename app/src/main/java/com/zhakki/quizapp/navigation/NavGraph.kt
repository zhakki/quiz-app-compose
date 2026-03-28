package com.zhakki.quizapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zhakki.quizapp.data.ui.category.StartScreen
import com.zhakki.quizapp.data.ui.game.QuizScreen
import com.zhakki.quizapp.data.ui.history.HistoryScreen
import com.zhakki.quizapp.data.ui.leaderboard.LeaderboardScreen
import com.zhakki.quizapp.data.ui.result.ResultScreen
import com.zhakki.quizapp.viewmodel.QuizViewModel

@Composable
fun AppNavGraph(
    quizViewModel: QuizViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.START
    ) {
        composable(Routes.START) {
            StartScreen(
                viewModel = quizViewModel,
                onStartQuiz = {
                    navController.navigate(Routes.QUIZ)
                },
                onOpenHistory = {
                    navController.navigate(Routes.HISTORY)
                },
                onOpenLeaderboard = {
                    navController.navigate(Routes.LEADERBOARD)
                }
            )
        }

        composable(Routes.QUIZ) {
            QuizScreen(
                viewModel = quizViewModel,
                onFinishQuiz = {
                    navController.navigate(Routes.RESULT) {
                        popUpTo(Routes.START)
                    }
                },
                onBackToStart = {
                    navController.popBackStack(Routes.START, false)
                }
            )
        }

        composable(Routes.RESULT) {
            ResultScreen(
                viewModel = quizViewModel,
                onPlayAgain = {
                    navController.popBackStack(Routes.START, false)
                }
            )
        }

        composable(Routes.HISTORY) {
            HistoryScreen(
                viewModel = quizViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.LEADERBOARD) {
            LeaderboardScreen(
                viewModel = quizViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
