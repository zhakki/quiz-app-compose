package com.zhakki.quizapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhakki.quizapp.data.local.GameResultEntity
import com.zhakki.quizapp.data.repository.QuizRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: QuizRepository) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    // Unikaalsed kategooriad filtri menüü jaoks
    val availableCategories: StateFlow<List<String>> = repository.getGameHistory()
        .map { results -> results.map { it.category }.distinct().sorted() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Filtreeritud ajalugu
    val history: StateFlow<List<GameResultEntity>> = combine(
        repository.getGameHistory(),
        _selectedCategory
    ) { results, selected ->
        if (selected == null) results else results.filter { it.category == selected }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setCategoryFilter(category: String?) {
        _selectedCategory.value = category
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearGameHistory()
        }
    }
}
