package com.zhakki.quizapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhakki.quizapp.data.local.GameResultEntity
import com.zhakki.quizapp.data.repository.QuizRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(private val repository: QuizRepository) : ViewModel() {

    val history: StateFlow<List<GameResultEntity>> = repository.getGameHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
