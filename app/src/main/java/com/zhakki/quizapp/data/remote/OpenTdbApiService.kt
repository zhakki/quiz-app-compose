package com.zhakki.quizapp.data.remote

import com.zhakki.quizapp.data.model.CategoryResponse
import com.zhakki.quizapp.data.model.QuestionResponse
import com.zhakki.quizapp.data.model.TokenResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenTdbApiService {

    @GET("api_category.php")
    suspend fun getCategories(): CategoryResponse

    @GET("api_token.php")
    suspend fun getSessionToken(
        @Query("command") command: String = "request",
        @Query("token") token: String? = null
    ): TokenResponse

    @GET("api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int,
        @Query("category") category: Int? = null,
        @Query("difficulty") difficulty: String? = null,
        @Query("type") type: String? = null,
        @Query("token") token: String? = null
    ): QuestionResponse
}
