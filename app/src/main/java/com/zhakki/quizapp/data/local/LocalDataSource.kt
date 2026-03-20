package com.zhakki.quizapp.data.local

import kotlinx.coroutines.flow.Flow

class LocalDataSource(
    private val questionDao: QuestionDao,
    private val gameResultDao: GameResultDao,
    private val tokenDao: TokenDao,
    private val quizStateDao: QuizStateDao
) {

    suspend fun saveQuestions(questions: List<QuestionEntity>) {
        questionDao.insertQuestions(questions)
    }

    suspend fun getQuestionsByCategory(category: String): List<QuestionEntity> {
        return questionDao.getQuestionsByCategory(category)
    }

    suspend fun getQuestionById(id: Int): QuestionEntity? {
        return questionDao.getQuestionById(id)
    }

    suspend fun clearQuestions() {
        questionDao.clearAllQuestions()
    }

    suspend fun saveGameResult(result: GameResultEntity) {
        gameResultDao.insertGameResult(result)
    }

    fun getGameHistory(): Flow<List<GameResultEntity>> {
        return gameResultDao.getGameHistory()
    }

    fun getTopResultsByCategory(category: String): Flow<List<GameResultEntity>> {
        return gameResultDao.getTopResultsByCategory(category)
    }

    fun getToken(): Flow<TokenEntity?> {
        return tokenDao.getToken()
    }

    suspend fun updateTimestamp() {
        tokenDao.updateTimestamp(calculateExpiryTime())
    }

    suspend fun updateToken(token: String) {
        tokenDao.insertToken(TokenEntity(token = token, timestamp = calculateExpiryTime()))
    }

    suspend fun clearToken() {
        tokenDao.clearToken()
    }

    suspend fun updateQuizState(state: QuizStateEntity) {
        quizStateDao.insertQuizState(state)
    }

    fun getQuizState(): Flow<QuizStateEntity?> {
        return quizStateDao.getQuizState()
    }

    suspend fun clearQuizState() {
        quizStateDao.clearQuizState()
    }

    private fun calculateExpiryTime(): Long {
        return System.currentTimeMillis() + (6 * 60 * 60 * 1000) // +6 hours in milliseconds
    }

    suspend fun deleteGameResultById(id: Int) {
        gameResultDao.deleteGameResultById(id)
    }

    suspend fun clearGameHistory() {
        gameResultDao.clearGameHistory()
    }

    fun getTopResults(): Flow<List<GameResultEntity>> {
        return gameResultDao.getTopResults()
    }

    suspend fun getAllQuestions(): List<QuestionEntity> {
        return questionDao.getAllQuestions()
    }
}
