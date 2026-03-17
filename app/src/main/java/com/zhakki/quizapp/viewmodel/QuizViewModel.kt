package com.zhakki.quizapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhakki.quizapp.data.model.Category
import com.zhakki.quizapp.data.repository.QuizRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuizViewModel(private val repository: QuizRepository) : ViewModel() {

    val categories: StateFlow<List<Category>> = repository.categories

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            repository.fetchCategories()
        }
    }

    fun getQuestions(amount: Int) {
        viewModelScope.launch {
            repository.getQuestions(amount)
        }
    }
}
