package com.zhakki.quizapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhakki.quizapp.data.model.Category
import com.zhakki.quizapp.data.model.Difficulty
import com.zhakki.quizapp.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuizViewModel(private val repository: QuizRepository) : ViewModel() {

    val categories: StateFlow<List<Category>> = repository.categories

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    private val _selectedDifficulty = MutableStateFlow<Difficulty?>(Difficulty.MEDIUM)
    val selectedDifficulty: StateFlow<Difficulty?> = _selectedDifficulty.asStateFlow()

    private val _amount = MutableStateFlow(10)
    val amount: StateFlow<Int> = _amount.asStateFlow()

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

    fun selectDifficulty(difficulty: Difficulty?) {
        _selectedDifficulty.value = difficulty
    }

    fun updateAmount(newAmount: Int) {
        _amount.value = newAmount
    }

    fun getQuestions() {
        viewModelScope.launch {
            repository.getQuestions(
                amount = _amount.value,
                category = _selectedCategory.value?.id,
                difficulty = _selectedDifficulty.value?.apiValue
            )
        }
    }
}
