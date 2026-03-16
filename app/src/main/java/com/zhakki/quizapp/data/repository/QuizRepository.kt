package com.zhakki.quizapp.data.repository

import com.zhakki.quizapp.data.local.LocalDataSource
import com.zhakki.quizapp.data.remote.RetrofitClient.apiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull

class QuizRepository(
    private val localDataSource: LocalDataSource
) {
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

    suspend fun getQuestions(amount: Int, category: Int? = null, difficulty: String? = null) {
        val token = getToken()
        val response = apiService.getQuestions(
            amount = amount,
            category = category,
            difficulty = difficulty,
            token = token
        )

        if (response.responseCode == 0) {
        }
    }
}
