package com.zhakki.quizapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameResultDao {

    @Insert
    suspend fun insertGameResult(result: GameResultEntity)

    @Query("SELECT * FROM game_history ORDER BY id DESC")
    fun getGameHistory(): Flow<List<GameResultEntity>>

    @Query("""
        SELECT * FROM game_history
        WHERE category = :category
        ORDER BY score DESC, id DESC
        LIMIT 5
    """)
    fun getTopResultsByCategory(category: String): Flow<List<GameResultEntity>>

    @Query("DELETE FROM game_history WHERE id = :id")
    suspend fun deleteGameResultById(id: Int)

    @Query("DELETE FROM game_history")
    suspend fun clearGameHistory()

    @Query("""
    SELECT * FROM game_history
    ORDER BY score DESC, id DESC
    LIMIT 10
""")
    fun getTopResults(): Flow<List<GameResultEntity>>
}