package com.zhakki.quizapp.data.repository

import android.text.Html
import androidx.core.text.HtmlCompat
import com.zhakki.quizapp.data.local.GameResultEntity
import com.zhakki.quizapp.data.local.LocalDataSource
import com.zhakki.quizapp.data.local.QuestionEntity
import com.zhakki.quizapp.data.local.QuizStateEntity
import com.zhakki.quizapp.data.model.Category
import com.zhakki.quizapp.data.remote.RetrofitClient.apiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import com.zhakki.quizapp.data.local.BestResult

class QuizRepository(
    private val localDataSource: LocalDataSource
) {
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private suspend fun getToken(): String? {
        val localTokenEntity = localDataSource.getToken().firstOrNull()
        val currentTime = System.currentTimeMillis()

        if (localTokenEntity != null && currentTime < localTokenEntity.timestamp) {
            return localTokenEntity.token
        }

        var response = apiService.getSessionToken("request")

        if (response.responseCode == 5) {
            delay(5000)
            response = apiService.getSessionToken("request")
        }

        return if (response.responseCode == 0) {
            localDataSource.updateToken(response.token)
            response.token
        } else {
            null
        }
    }

    suspend fun resetToken() {
        val localTokenEntity = localDataSource.getToken().firstOrNull()
        val token = localTokenEntity?.token

        if (token != null) {
            val response = apiService.getSessionToken(command = "reset", token = token)
            if (response.responseCode == 0) {
                localDataSource.updateToken(token)
            }
        }
    }

    suspend fun fetchCategories() {
        val response = apiService.getCategories()
        val decodedCategories = response.categories.map { category ->
            category.copy(name = decodeHtml(category.name))
        }
        _categories.value = decodedCategories
    }

    private fun decodeHtml(html: String): String {
        return HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    }

    suspend fun getQuestions(
        amount: Int,
        category: Int? = null,
        difficulty: String? = null,
        retryCount: Int = 0
    ): List<QuestionEntity> {
        if (retryCount > 3) {
            throw Exception("Päring ebaõnnestus pärast mitut katset. Kontrolli võrguühendust.")
        }

        val token = getToken()
        val response = apiService.getQuestions(
            amount = amount,
            category = category,
            difficulty = difficulty,
            type = "multiple",
            token = token
        )

        return when (response.responseCode) {
            0 -> {
                val entities = response.results.mapIndexed { index, q ->
                    QuestionEntity(
                        id = index, // Kasutame indeksit järjekorrana
                        category = decodeHtml(q.category),
                        difficulty = q.difficulty,
                        questionText = decodeHtml(q.question),
                        correctAnswer = decodeHtml(q.correctAnswer),
                        wrongAnswer1 = decodeHtml(q.incorrectAnswers.getOrNull(0) ?: ""),
                        wrongAnswer2 = decodeHtml(q.incorrectAnswers.getOrNull(1) ?: ""),
                        wrongAnswer3 = decodeHtml(q.incorrectAnswers.getOrNull(2) ?: "")
                    )
                }

                localDataSource.clearQuestions()
                localDataSource.saveQuestions(entities)
                entities
            }

            1 -> throw Exception("API-l pole piisavalt küsimusi selle valiku jaoks.")

            3 -> {
                localDataSource.clearToken()
                getQuestions(amount, category, difficulty, retryCount + 1)
            }

            4 -> {
                resetToken()
                getQuestions(amount, category, difficulty, retryCount + 1)
            }

            5 -> {
                delay(5000)
                getQuestions(amount, category, difficulty, retryCount + 1)
            }

            else -> throw Exception("Tundmatu viga API-st: ${response.responseCode}")
        }
    }

    suspend fun getQuestionById(id: Int): QuestionEntity? {
        return localDataSource.getQuestionById(id)
    }

    suspend fun updateQuizState(state: QuizStateEntity) {
        localDataSource.updateQuizState(state)
    }

    suspend fun markQuizAsFinished() {
        localDataSource.markQuizAsFinished()
    }

    fun getQuizState(): Flow<QuizStateEntity?> {
        return localDataSource.getQuizState()
    }

    suspend fun clearQuizState() {
        localDataSource.clearQuizState()
    }

    suspend fun saveGameResult(result: GameResultEntity) {
        localDataSource.saveGameResult(result)
    }

    fun getGameHistory(): Flow<List<GameResultEntity>> {
        return localDataSource.getGameHistory()
    }
    fun getBestResultsByCategory(): Flow<List<BestResult>> {
        return localDataSource.getBestResultsByCategory()
    }

    suspend fun clearGameHistory() {
        localDataSource.clearGameHistory()
    }
}
