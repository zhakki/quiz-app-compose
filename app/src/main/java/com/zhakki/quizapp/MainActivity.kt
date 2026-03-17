package com.zhakki.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.zhakki.quizapp.data.repository.QuizRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val localDataSource = (application as QuizApplication).localDataSource
            val repository = remember { QuizRepository(localDataSource) }
            val categories by repository.categories.collectAsState()
            
            LaunchedEffect(Unit) {
                repository.fetchCategories()
                repository.getQuestions(10)
            }

            Column {
                Text("Quiz App - Categories:")
                LazyColumn {
                    items(categories) { category ->
                        Text(text = category.name)
                    }
                }
            }
        }
    }
}
