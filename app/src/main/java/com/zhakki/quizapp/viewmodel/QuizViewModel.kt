package com.zhakki.quizapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhakki.quizapp.data.local.BestResult
import com.zhakki.quizapp.data.local.GameResultEntity
import com.zhakki.quizapp.data.local.QuestionEntity
import com.zhakki.quizapp.data.local.QuizStateEntity
import com.zhakki.quizapp.data.model.Category
import com.zhakki.quizapp.data.model.Difficulty
import com.zhakki.quizapp.data.repository.QuizRepository
import com.zhakki.quizapp.ui.category.CategoryItemUi
import com.zhakki.quizapp.ui.category.CategoryUiState
import com.zhakki.quizapp.ui.leaderboard.LeaderboardItemUi
import com.zhakki.quizapp.ui.leaderboard.LeaderboardUiState
import com.zhakki.quizapp.ui.result.ResultUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    // Mänguajalugu voona
    val gameHistory: StateFlow<List<GameResultEntity>> = repository.getGameHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Edetabel, tuletatud ajaloo põhjal
    val leaderboard: StateFlow<List<BestResult>> = gameHistory
        .map { history ->
            history.groupBy { it.category }
                .map { (category, results) ->
                    val bestScore = results.maxOfOrNull { it.score } ?: 0
                    BestResult(category, bestScore)
                }
                .sortedByDescending { it.bestScore }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Leaderboardi ekraani olek
    val leaderboardUiState: StateFlow<LeaderboardUiState> = leaderboard
        .map { results ->
            if (results.isEmpty()) {
                LeaderboardUiState.Empty
            } else {
                LeaderboardUiState.Content(
                    items = results.map {
                        LeaderboardItemUi(
                            id = it.category,
                            name = it.category,
                            scoreText = "Parim: ${it.bestScore}"
                        )
                    }
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LeaderboardUiState.Loading
        )

    // Kategooria valiku ekraani olek
    val categoryUiState: StateFlow<CategoryUiState> = _uiState
        .map { state -> state.toCategoryUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CategoryUiState.Loading
        )

    // Tulemuste ekraani olek
    val resultUiState: StateFlow<ResultUiState> = _uiState
        .map { state -> state.toResultUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ResultUiState.Loading
        )

    init {
        viewModelScope.launch {
            repository.categories.collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }

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

                    if (!state.isFinished) {
                        loadQuestion(state.currentQuestionIndex)
                    }
                } else {
                    _uiState.update { currentState ->
                        if (currentState.isFinished) {
                            currentState
                        } else {
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
        }

        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.fetchCategories()
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Kategooriate laadimine ebaõnnestus"
                    )
                }
            }
        }
    }

    fun selectCategory(categoryId: String) {
        val category = _uiState.value.categories.find { it.id.toString() == categoryId }
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
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    currentQuestion = null,
                    currentAnswers = emptyList(),
                    correctAnswersCount = 0,
                    currentQuestionIndex = 0,
                    totalQuestions = 0,
                    isFinished = false
                )
            }

            try {
                repository.clearQuizState()

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
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun cancelQuiz() {
        viewModelScope.launch {
            repository.clearQuizState()
            resetQuizUi()
        }
    }

    fun resetQuizUi() {
        _uiState.update {
            it.copy(
                currentQuestion = null,
                currentAnswers = emptyList(),
                correctAnswersCount = 0,
                currentQuestionIndex = 0,
                totalQuestions = 0,
                isFinished = false,
                error = null
            )
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
            val newCorrectCount =
                if (isCorrect) state.correctAnswersCount + 1 else state.correctAnswersCount
            val nextIndex = state.currentQuestionIndex + 1
            val isFinished = nextIndex >= state.totalQuestions

            if (isFinished) {
                val result = GameResultEntity(
                    date = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date()),
                    category = currentQuestion.category,
                    score = newCorrectCount,
                    totalQuestions = state.totalQuestions
                )

                _uiState.update {
                    it.copy(
                        isFinished = true,
                        correctAnswersCount = newCorrectCount
                    )
                }

                repository.saveGameResult(result)
                repository.clearQuizState()
            } else {
                val newState = QuizStateEntity(
                    currentQuestionIndex = nextIndex,
                    totalQuestions = state.totalQuestions,
                    correctAnswersCount = newCorrectCount,
                    isFinished = false
                )
                repository.updateQuizState(newState)
                loadQuestion(nextIndex)
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

    /**
     * Teisendab QuizUiState -> CategoryUiState.
     * See loogika on siin, et hoida UI-kiht puhas.
     */
    private fun QuizUiState.toCategoryUiState(): CategoryUiState {
        return when {
            isLoading && categories.isEmpty() -> CategoryUiState.Loading
            error != null && categories.isEmpty() && !isLoading ->
                CategoryUiState.Error(message = error.orEmpty())
            !isLoading && categories.isEmpty() && error == null -> CategoryUiState.Empty
            else -> CategoryUiState.Content(
                title = "Choose a category",
                categories = categories.map { CategoryItemUi(id = it.id.toString(), name = it.name) },
                selectedCategoryId = selectedCategory?.id?.toString(),
                selectedDifficulty = selectedDifficulty,
                amount = amount,
                amountOptions = listOf(5, 10, 15),
                inlineError = error?.takeIf { categories.isNotEmpty() },
                canStart = selectedCategory != null && !isLoading,
                isStartInProgress = isLoading
            )
        }
    }

    private fun QuizUiState.toResultUiState(): ResultUiState {
        return when {
            isLoading && totalQuestions == 0 -> ResultUiState.Loading
            error != null && totalQuestions == 0 && !isFinished ->
                ResultUiState.Error(message = error.orEmpty())
            totalQuestions == 0 -> ResultUiState.Empty
            else -> ResultUiState.Content(
                title = "Result",
                summary = "Correct answers: $correctAnswersCount / $totalQuestions",
                details = listOf(
                    "Category" to (selectedCategory?.name ?: "—"),
                    "Difficulty" to selectedDifficulty.name
                )
            )
        }
    }
}
