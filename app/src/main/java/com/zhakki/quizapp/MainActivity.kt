package com.zhakki.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import com.zhakki.quizapp.data.repository.QuizRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val localDataSource = (application as QuizApplication).localDataSource
            val repository = QuizRepository(localDataSource)
            
            LaunchedEffect(Unit) {
                repository.getQuestions(10)
            }
            Text("Quiz App")
        }
    }
}
