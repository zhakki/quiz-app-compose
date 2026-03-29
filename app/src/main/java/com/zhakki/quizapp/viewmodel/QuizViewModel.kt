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
import com.zhakki.quizapp.ui.game.GameUiState
import com.zhakki.quizapp.ui.history.HistoryItemUi
import com.zhakki.quizapp.ui.history.HistoryUiState
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

    private val _selectedHistoryCategory = MutableStateFlow<String?>(null)
    val selectedHistoryCategory: StateFlow<String?> = _selectedHistoryCategory.asStateFlow()

    val gameHistory: StateFlow<List<GameResultEntity>> = repository.getGameHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val historyUiState: StateFlow<HistoryUiState> = combine(
        gameHistory,
        _selectedHistoryCategory
    ) { history, selectedCategory ->
        if (history.isEmpty()) {
            HistoryUiState.Empty
        } else {
            val filtered = if (selectedCategory == null) history else history.filter { it.category == selectedCategory }
            HistoryUiState.Content(
                items = filtered.map {
                    HistoryItemUi(
                        id = it.id.toString(),
                        title = it.category,
                        subtitle = "${it.date}\nScore: ${it.score} / ${it.totalQuestions}"
                    )
                }
            )
        }
    }
    .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = HistoryUiState.Loading)

    val leaderboard: StateFlow<List<BestResult>> = gameHistory
        .map { history ->
            history.groupBy { it.category }
                .map { (category, results) ->
                    val bestScore = results.maxOfOrNull { it.score } ?: 0
                    BestResult(category, bestScore)
                }
                .sortedByDescending { it.bestScore }
        }
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList())

    val leaderboardUiState: StateFlow<LeaderboardUiState> = leaderboard
        .map { results ->
            if (results.isEmpty()) {
                LeaderboardUiState.Empty
            } else {
                LeaderboardUiState.Content(
                    items = results.mapIndexed { index, it ->
                        val medal = when (index) {
                            0 -> "🥇 "
                            1 -> "🥈 "
                            2 -> "🥉 "
                            else -> ""
                        }
                        LeaderboardItemUi(
                            id = it.category,
                            name = "$medal${it.category}", // Eemaldatud järjekorranumber, et vältida topelt numbreid UI-s
                            scoreText = it.bestScore.toString()
                        )
                    }
                )
            }
        }
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = LeaderboardUiState.Loading)

    val categoryUiState: StateFlow<CategoryUiState> = _uiState
        .map { state -> state.toCategoryUiState() }
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = CategoryUiState.Loading)

    val gameUiState: StateFlow<GameUiState> = _uiState
        .map { state -> state.toGameUiState() }
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = GameUiState.Loading)

    val resultUiState: StateFlow<ResultUiState> = _uiState
        .map { state -> state.toResultUiState() }
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = ResultUiState.Loading)

    init {
        viewModelScope.launch {
            repository.categories.collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
        viewModelScope.launch {
            repository.getQuizState().collect { state ->
                if (state != null) {
                    _uiState.update { it.copy(
                        currentQuestionIndex = state.currentQuestionIndex,
                        totalQuestions = state.totalQuestions,
                        correctAnswersCount = state.correctAnswersCount,
                        isFinished = state.isFinished
                    ) }
                    if (!state.isFinished) loadQuestion(state.currentQuestionIndex)
                } else {
                    _uiState.update { currentState ->
                        if (currentState.isFinished) currentState else currentState.copy(
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
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.fetchCategories()
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to load categories") }
            }
        }
    }

    fun selectCategory(categoryId: String) {
        val category = _uiState.value.categories.find { it.id.toString() == categoryId }
        _uiState.update { it.copy(selectedCategory = category) }
    }

    private fun selectCategory(category: Category) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun selectDifficulty(difficulty: Difficulty) {
        _uiState.update { it.copy(selectedDifficulty = difficulty) }
    }

    fun selectCategoryById(categoryId: String) {
        val category = _uiState.value.categories.firstOrNull {
            it.id.toString() == categoryId
        }
        if (category != null) {
            selectCategory(category)
        }
    }

    fun retryCategory() = fetchCategories()

    fun updateAmount(newAmount: Int) {
        _uiState.update { it.copy(amount = newAmount) }
    }

    fun startQuiz() {
        viewModelScope.launch {
            _uiState.update { it.copy(
                isLoading = true, error = null, currentQuestion = null,
                currentAnswers = emptyList(), correctAnswersCount = 0,
                currentQuestionIndex = 0, totalQuestions = 0, isFinished = false
            ) }
            try {
                repository.clearQuizState()
                val state = _uiState.value
                val questions = repository.getQuestions(
                    amount = state.amount,
                    category = state.selectedCategory?.id,
                    difficulty = state.selectedDifficulty.apiValue
                )
                if (questions.isNotEmpty()) {
                    repository.updateQuizState(QuizStateEntity(
                        currentQuestionIndex = 0,
                        totalQuestions = questions.size,
                        correctAnswersCount = 0,
                        isFinished = false
                    ))
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
            resetQuizUi()
        }
    }

    fun resetQuizUi() {
        _uiState.update { it.copy(
            currentQuestion = null, currentAnswers = emptyList(),
            correctAnswersCount = 0, currentQuestionIndex = 0,
            totalQuestions = 0, isFinished = false, error = null
        ) }
    }

    private suspend fun loadQuestion(index: Int) {
        val question = repository.getQuestionById(index)
        question?.let {
            val answers = listOf(it.correctAnswer, it.wrongAnswer1, it.wrongAnswer2, it.wrongAnswer3)
                .filter { a -> a.isNotEmpty() }.shuffled()
            _uiState.update { s -> s.copy(currentQuestion = it, currentAnswers = answers) }
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

            if (isFinished) {
                repository.saveGameResult(GameResultEntity(
                    date = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date()),
                    category = currentQuestion.category,
                    score = newCorrectCount,
                    totalQuestions = state.totalQuestions
                ))
                _uiState.update { it.copy(isFinished = true, correctAnswersCount = newCorrectCount) }
                repository.clearQuizState()
            } else {
                repository.updateQuizState(QuizStateEntity(
                    currentQuestionIndex = nextIndex,
                    totalQuestions = state.totalQuestions,
                    correctAnswersCount = newCorrectCount,
                    isFinished = false
                ))
                loadQuestion(nextIndex)
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearGameHistory()
        }
    }

    private fun QuizUiState.toCategoryUiState(): CategoryUiState {
        if (isLoading && categories.isEmpty()) return CategoryUiState.Loading
        if (error != null && categories.isEmpty()) return CategoryUiState.Error(error.orEmpty())
        if (categories.isEmpty()) return CategoryUiState.Empty
        return CategoryUiState.Content(
            title = "Choose a category",
            categories = categories.map { CategoryItemUi(it.id.toString(), it.name) },
            selectedCategoryId = selectedCategory?.id?.toString(),
            selectedDifficulty = selectedDifficulty,
            amount = amount,
            amountOptions = listOf(5, 10, 15, 20),
            inlineError = error,
            canStart = selectedCategory != null && !isLoading,
            isStartInProgress = isLoading
        )
    }

    private fun QuizUiState.toGameUiState(): GameUiState {
        if (isFinished) return GameUiState.Empty
        if (isLoading && currentQuestion == null) return GameUiState.Loading
        val q = currentQuestion ?: return GameUiState.Empty
        return GameUiState.Content(
            questionNumber = currentQuestionIndex + 1,
            totalQuestions = totalQuestions,
            questionText = q.questionText,
            answers = currentAnswers,
            progress = if (totalQuestions > 0) (currentQuestionIndex + 1).toFloat() / totalQuestions else 0f,
            correctAnswers = correctAnswersCount
        )
    }

    private fun QuizUiState.toResultUiState(): ResultUiState {
        if (!isFinished) return ResultUiState.Loading
        return ResultUiState.Content(
            title = "Quiz finished",
            summary = "Correct answers: $correctAnswersCount / $totalQuestions",
            details = listOf(
                "Category" to (selectedCategory?.name ?: "—"),
                "Difficulty" to selectedDifficulty.name
            )
        )
    }
}
