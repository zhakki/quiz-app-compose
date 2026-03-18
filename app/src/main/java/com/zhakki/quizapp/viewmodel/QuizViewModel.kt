package com.zhakki.quizapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhakki.quizapp.data.local.QuestionEntity
import com.zhakki.quizapp.data.local.QuizStateEntity
import com.zhakki.quizapp.data.model.Category
import com.zhakki.quizapp.data.model.Difficulty
import com.zhakki.quizapp.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuizUiState(
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val selectedDifficulty: Difficulty = Difficulty.MEDIUM,
    val amount: Int = 10,
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentQuestion: QuestionEntity? = null,
    val currentAnswers: List<String> = emptyList(),
    val correctAnswersCount: Int = 0,
    val currentQuestionIndex: Int = 0,
    val totalQuestions: Int = 0,
    val isFinished: Boolean = false
)

class QuizViewModel(private val repository: QuizRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        // Jälgime kategooriaid repositooriumist
        viewModelScope.launch {
            repository.categories.collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
        
        // Jälgime viktoriini seisundit andmebaasist
        viewModelScope.launch {
            repository.getQuizState().collect { state ->
                if (state != null) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            currentQuestionIndex = state.currentQuestionIndex,
                            totalQuestions = state.totalQuestions,
                            correctAnswersCount = state.correctAnswersCount,
                            isFinished = state.isFinished
                        )
                    }
                    // Laeme ka aktiivse küsimuse andmed
                    if (!state.isFinished) {
                        loadQuestion(state.currentQuestionIndex)
                    }
                } else {
                    // Kui state on null, siis lähtestame UI mänguga seotud väljad
                    _uiState.update { currentState ->
                        currentState.copy(
                            currentQuestion = null,
                            currentAnswers = emptyList(),
                            correctAnswersCount = 0,
                            currentQuestionIndex = 0,
                            totalQuestions = 0,
                            isFinished = false
                        )
                    }
                }
            }
        }

        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            repository.fetchCategories()
        }
    }

    fun selectCategory(category: Category) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun selectDifficulty(difficulty: Difficulty) {
        _uiState.update { it.copy(selectedDifficulty = difficulty) }
    }

    fun updateAmount(newAmount: Int) {
        _uiState.update { it.copy(amount = newAmount) }
    }

    fun startQuiz() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val state = _uiState.value
                val questions = repository.getQuestions(
                    amount = state.amount,
                    category = state.selectedCategory?.id,
                    difficulty = state.selectedDifficulty.apiValue
                )

                if (questions.isNotEmpty()) {
                    val initialState = QuizStateEntity(
                        currentQuestionIndex = 0,
                        totalQuestions = questions.size,
                        correctAnswersCount = 0,
                        isFinished = false
                    )
                    repository.updateQuizState(initialState)
                    loadQuestion(0)
                }
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun cancelQuiz() {
        viewModelScope.launch {
            repository.clearQuizState()
        }
    }

    private suspend fun loadQuestion(index: Int) {
        val question = repository.getQuestionById(index)
        question?.let {
            val answers = shuffleAnswers(it)
            _uiState.update { currentState ->
                currentState.copy(
                    currentQuestion = it,
                    currentAnswers = answers
                )
            }
        }
    }

    fun onAnswerSelected(answerIndex: Int) {
        viewModelScope.launch {
            val state = _uiState.value
            val currentQuestion = state.currentQuestion ?: return@launch
            val selectedAnswer = state.currentAnswers.getOrNull(answerIndex) ?: return@launch
            
            val isCorrect = selectedAnswer == currentQuestion.correctAnswer
            val newCorrectCount = if (isCorrect) state.correctAnswersCount + 1 else state.correctAnswersCount
            val nextIndex = state.currentQuestionIndex + 1
            val isFinished = nextIndex >= state.totalQuestions

            val newState = QuizStateEntity(
                currentQuestionIndex = nextIndex,
                totalQuestions = state.totalQuestions,
                correctAnswersCount = newCorrectCount,
                isFinished = isFinished
            )
            
            repository.updateQuizState(newState)
            
            if (!isFinished) {
                loadQuestion(nextIndex)
            } else {
                _uiState.update { it.copy(isFinished = true, correctAnswersCount = newCorrectCount) }
            }
        }
    }

    private fun shuffleAnswers(question: QuestionEntity): List<String> {
        return listOf(
            question.correctAnswer,
            question.wrongAnswer1,
            question.wrongAnswer2,
            question.wrongAnswer3
        ).filter { it.isNotEmpty() }.shuffled()
    }
}
