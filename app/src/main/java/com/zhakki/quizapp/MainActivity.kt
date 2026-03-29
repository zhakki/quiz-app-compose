package com.zhakki.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zhakki.quizapp.navigation.AppNavGraph
import com.zhakki.quizapp.ui.theme.QuizAppTheme
import com.zhakki.quizapp.viewmodel.QuizViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val app = application as QuizApplication

            val quizViewModel: QuizViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return QuizViewModel(app.repository) as T
                    }
                }
            )

            QuizAppTheme(darkTheme = isSystemInDarkTheme()) {
                AppNavGraph(quizViewModel = quizViewModel)
            }
        }
    }
}