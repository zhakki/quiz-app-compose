package com.zhakki.quizapp.ui

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuizScreen(
    onFinishClick: (Int) -> Unit = {}
) {

    val questions = listOf(
        Question(
            text = "Milleks kasutatakse tänapäeval Aidu karjääri?",
            options = listOf("Põlevkivi kaevandamiseks", "Sõjalisteks õppusteks", "Veespordialadeks (SUP, kajak, aerutamine)", "Kalapüügiks"),
            correctIndex = 2
        ),
        Question(
            text = "Mis on selle rahvuspargi nimi, mis on tuntud „viienda aastaaja“ poolest?",
            options = listOf("Lahemaa rahvuspark", "Soomaa rahvuspark", "Karula rahvuspark", "Matsalu rahvuspark"),
            correctIndex = 1
        )
    )

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }

    val question = questions[currentQuestionIndex]

    Column(

        modifier = Modifier

            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🔥 Progress bar
        LinearProgressIndicator(
            progress = (currentQuestionIndex + 1) / questions.size.toFloat(),
            modifier = Modifier
                .padding(bottom = 24.dp)
        )
        Text(
            text = "Question ${currentQuestionIndex + 1} / ${questions.size}",
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = question.text,
            style = MaterialTheme.typography.headlineMedium
        )

        question.options.forEachIndexed { index, option ->
            Button(
                onClick = {
                    if (index == question.correctIndex) {
                        score++
                    }

                    if (currentQuestionIndex < questions.lastIndex) {
                        currentQuestionIndex++
                    } else {
                        onFinishClick(score)
                    }
                },
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text(option)
            }
        }
    }
}