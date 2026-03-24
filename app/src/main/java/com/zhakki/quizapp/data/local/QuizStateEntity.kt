package com.zhakki.quizapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_state")
data class QuizStateEntity(
    @PrimaryKey val id: Int = 0, // Üks rida, mis hoiab hetke seisundit
    val currentQuestionIndex: Int,
    val totalQuestions: Int,
    val correctAnswersCount: Int,
    val isFinished: Boolean
)
