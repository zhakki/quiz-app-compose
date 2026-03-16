package com.zhakki.quizapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TokenDao {
    @Query("SELECT * FROM tokens WHERE id = 0")
    fun getToken(): Flow<TokenEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToken(token: TokenEntity)

    @Query("UPDATE tokens SET timestamp = :timestamp WHERE id = 0")
    suspend fun updateTimestamp(timestamp: Long)

    @Query("DELETE FROM tokens")
    suspend fun clearToken()
}
