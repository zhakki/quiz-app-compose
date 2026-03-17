package com.zhakki.quizapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zhakki.quizapp.ui.StartScreen
import com.zhakki.quizapp.ui.QuizScreen
import com.zhakki.quizapp.ui.ResultScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.START
    ) {
        composable(Routes.START) {
            StartScreen(
                onStartClick = {
                    navController.navigate(Routes.QUIZ)
                }
            )
        }

        var finalScore = 0

        composable(Routes.QUIZ) {
            QuizScreen(
                onFinishClick = { score ->
                    finalScore = score
                    navController.navigate(Routes.RESULT)
                }
            )
        }

        composable(Routes.RESULT) {
            ResultScreen(
                score = finalScore,
                onRestartClick = {
                    navController.navigate(Routes.START) {
                        popUpTo(Routes.START) { inclusive = true }
                    }
                }
            )
        }
    }
}