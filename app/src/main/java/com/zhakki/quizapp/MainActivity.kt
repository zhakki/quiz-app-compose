package com.zhakki.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zhakki.quizapp.data.repository.QuizRepository
import com.zhakki.quizapp.viewmodel.QuizViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val localDataSource = (application as QuizApplication).localDataSource
            val repository = remember { QuizRepository(localDataSource) }
            
            val viewModel: QuizViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return QuizViewModel(repository) as T
                    }
                }
            )

            val categories by viewModel.categories.collectAsState()

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
