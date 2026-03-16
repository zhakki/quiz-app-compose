package com.zhakki.quizapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tokens")
data class TokenEntity(
    @PrimaryKey val id: Int = 0, // Always use the same ID to keep only one token
    val token: String,
    val timestamp: Long
)
