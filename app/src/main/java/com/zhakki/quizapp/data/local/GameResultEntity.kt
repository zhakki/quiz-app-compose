package com.zhakki.quizapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_history")
data class GameResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val category: String,
    val score: Int,
    val totalQuestions: Int
)