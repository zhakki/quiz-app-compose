package com.zhakki.quizapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizState(state: QuizStateEntity)

    @Query("SELECT * FROM quiz_state WHERE id = 0")
    fun getQuizState(): Flow<QuizStateEntity?>

    @Query("DELETE FROM quiz_state")
    suspend fun clearQuizState()
}
