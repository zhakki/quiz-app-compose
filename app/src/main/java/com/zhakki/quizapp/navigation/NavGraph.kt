package com.zhakki.quizapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zhakki.quizapp.ui.StartScreen

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

        composable(Routes.QUIZ) {
            // QuizScreen()
        }

        composable(Routes.RESULT) {
            // ResultScreen()
        }
    }
}