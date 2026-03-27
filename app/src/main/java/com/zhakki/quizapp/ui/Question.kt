package com.zhakki.quizapp.ui

data class Question(
    val text: String,
    val options: List<String>,
    val correctIndex: Int
)