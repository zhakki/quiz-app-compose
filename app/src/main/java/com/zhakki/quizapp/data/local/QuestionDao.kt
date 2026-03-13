package com.zhakki.quizapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Query("SELECT * FROM questions WHERE category = :category")
    suspend fun getQuestionsByCategory(category: String): List<QuestionEntity>

    @Query("DELETE FROM questions")
    suspend fun clearAllQuestions()
}