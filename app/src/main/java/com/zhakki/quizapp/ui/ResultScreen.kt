package com.zhakki.quizapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ResultScreen(
    score: Int,
    onRestartClick: () -> Unit = {}

) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your Result",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Score: $score / 2"
        )

        Button(
            onClick = onRestartClick,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Play Again")
        }
    }
}