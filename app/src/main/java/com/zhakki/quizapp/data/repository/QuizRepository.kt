package com.zhakki.quizapp.data.repository

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

class QuizRepository(
    private val localDataSource: LocalDataSource
) {
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private suspend fun getToken(): String? {
        val localTokenEntity = localDataSource.getToken().firstOrNull()
        val currentTime = System.currentTimeMillis()

        if (localTokenEntity != null && currentTime < localTokenEntity.timestamp) {
            // Token ok.
            return localTokenEntity.token
        }

        // Token puudub või on aegunud, küsime uue
        var response = apiService.getSessionToken("request")
        
        if (response.responseCode == 5) { // Code 5: Rate Limit
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
                // Token resetiti edukalt, uuendame kohalikku andmebaasi uue aegumisajaga
                localDataSource.updateToken(token)
            }
        }
    }

    suspend fun fetchCategories() {
        try {
            val response = apiService.getCategories()
            _categories.value = response.categories
        } catch (e: Exception) {
            // Handle error (e.g., logging)
        }
    }

    suspend fun getQuestions(amount: Int, category: Int? = null, difficulty: String? = null): List<QuestionEntity> {
        val token = getToken()
        var response = apiService.getQuestions(
            amount = amount,
            category = category,
            difficulty = difficulty,
            type = "multiple",
            token = token
        )

        // Käitleme erinevad veakoodid vastavalt OpenTDB dokumentatsioonile
        return when (response.responseCode) {
            0 -> {
                // Success: Salvestame küsimused lokaalsesse baasi
                val entities = response.results.mapIndexed { index, q ->
                    QuestionEntity(
                        id = index, // Kasutame indeksit järjekorrana
                        category = q.category,
                        difficulty = q.difficulty,
                        questionText = q.question,
                        correctAnswer = q.correctAnswer,
                        wrongAnswer1 = q.incorrectAnswers.getOrNull(0) ?: "",
                        wrongAnswer2 = q.incorrectAnswers.getOrNull(1) ?: "",
                        wrongAnswer3 = q.incorrectAnswers.getOrNull(2) ?: ""
                    )
                }
                localDataSource.clearQuestions()
                localDataSource.saveQuestions(entities)
                entities
            }
            1 -> {
                // No Results: API-l pole piisavalt küsimusi selle päringu jaoks.
                throw Exception("API-l pole piisavalt küsimusi selle valiku jaoks.")
            }
            3 -> {
                // Token Not Found: Sessiooni token on vale või aegunud.
                localDataSource.clearToken()
                getQuestions(amount, category, difficulty)
            }
            4 -> {
                // Token Empty: Kõik küsimused on juba vastatud. Reset ja uus päring.
                resetToken()
                getQuestions(amount, category, difficulty)
            }
            5 -> {
                // Rate Limit: Liiga palju päringuid. Ootame ja proovime uuesti.
                delay(5000)
                getQuestions(amount, category, difficulty)
            }
            else -> emptyList()
        }
    }

    suspend fun getQuestionById(id: Int): QuestionEntity? {
        return localDataSource.getQuestionById(id)
    }

    suspend fun updateQuizState(state: QuizStateEntity) {
        localDataSource.updateQuizState(state)
    }

    fun getQuizState(): Flow<QuizStateEntity?> {
        return localDataSource.getQuizState()
    }

    suspend fun clearQuizState() {
        localDataSource.clearQuizState()
    }
}
