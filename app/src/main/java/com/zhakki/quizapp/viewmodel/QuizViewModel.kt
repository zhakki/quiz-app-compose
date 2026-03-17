package com.zhakki.quizapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhakki.quizapp.data.model.Category
import com.zhakki.quizapp.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuizViewModel(private val repository: QuizRepository) : ViewModel() {

    val categories: StateFlow<List<Category>> = repository.categories

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            repository.fetchCategories()
        }
    }

    fun selectCategory(category: Category) {
        _selectedCategory.value = category
    }

    fun getQuestions(amount: Int) {
        viewModelScope.launch {
            repository.getQuestions(amount, category = _selectedCategory.value?.id)
        }
    }
}
