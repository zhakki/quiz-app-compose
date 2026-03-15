package com.zhakki.quizapp.data.repository

import com.zhakki.quizapp.data.local.LocalDataSource

class QuizRepository(
    private val localDataSource: LocalDataSource
) {
    suspend fun refreshToken {
        //val response = apiService.refreshToken()

    }
}