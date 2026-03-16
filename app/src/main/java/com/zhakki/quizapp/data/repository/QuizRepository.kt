package com.zhakki.quizapp.data.repository

import com.zhakki.quizapp.data.remote.RetrofitClient.apiService

class QuizRepository(
    //private val localDataSource: LocalDataSource,
    //private val apiService: OpenTdbApiService
) {
    private var _token: String? = null

    suspend fun getToken() {
        /*
        * TODO:
        *  * load from local storage (if exists)
        *  * check if still valid
        *       * if yes, return token
        *  * if not, request new token
        * */
        var token: String? = null
        val response = apiService.getSessionToken("request")
        if (response.responseCode == 0) {
            //localDataSource.token.setToken(response.token)
            _token = response.token
        }
    }
    suspend fun getQuestions(){
        val token = _token

    }
}