package com.zhakki.quizapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val category: String,
    val difficulty: String,
    val questionText: String,
    val correctAnswer: String,
    val wrongAnswer1: String,
    val wrongAnswer2: String,
    val wrongAnswer3: String
)