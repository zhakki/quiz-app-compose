package com.zhakki.quizapp.data.repository

import com.zhakki.quizapp.data.local.LocalDataSource
import com.zhakki.quizapp.data.model.Category
import com.zhakki.quizapp.data.remote.RetrofitClient.apiService
import kotlinx.coroutines.delay
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
        
        if (response.responseCode == 5) { // Code 5: Rate Limit Too many requests have occurred. Each IP can only access the API once every 5 seconds.
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

    suspend fun getQuestions(amount: Int, category: Int? = null, difficulty: String? = null) {
        val token = getToken()
        var response = apiService.getQuestions(
            amount = amount,
            category = category,
            difficulty = difficulty,
            type = "multiple", // Ülesanne ütleb "Kuvatakse küsimus ja 4 vastusevarianti" e. alati "Multiple Choice"
            token = token
        )

        if (response.responseCode == 4) { // Token Empty: Session Token has returned all possible questions for the specified query. Reset is required.
            resetToken()
            val newToken = getToken()
            response = apiService.getQuestions(
                amount = amount,
                category = category,
                difficulty = difficulty,
                type = "multiple",
                token = newToken
            )
        }

        if (response.responseCode == 0) {
            // Edukas vastus
        }
    }
}
