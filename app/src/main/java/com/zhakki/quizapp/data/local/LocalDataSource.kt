package com.zhakki.quizapp.data.local

import kotlinx.coroutines.flow.Flow

class LocalDataSource(
    private val questionDao: QuestionDao,
    private val gameResultDao: GameResultDao
) {

    suspend fun saveQuestions(questions: List<QuestionEntity>) {
        questionDao.insertQuestions(questions)
    }

    suspend fun getQuestionsByCategory(category: String): List<QuestionEntity> {
        return questionDao.getQuestionsByCategory(category)
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
}