package com.zhakki.quizapp.data.ui.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhakki.quizapp.data.model.Difficulty
import com.zhakki.quizapp.viewmodel.QuizViewModel

@Composable
fun StartScreen(
    viewModel: QuizViewModel,
    onStartQuiz: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val categories = uiState.categories
    val selectedCategory = uiState.selectedCategory
    val selectedDifficulty = uiState.selectedDifficulty
    val amount = uiState.amount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Vali kategooria",
            style = MaterialTheme.typography.headlineSmall
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory?.id == category.id

                OutlinedButton(
                    onClick = { viewModel.selectCategory(category) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isSelected) "✓ ${category.name}" else category.name
                    )
                }
            }
        }

        Text(
            text = "Raskusaste: ${selectedDifficulty.name}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Difficulty.entries.forEach { difficulty ->
                OutlinedButton(
                    onClick = { viewModel.selectDifficulty(difficulty) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(difficulty.name)
                }
            }
        }

        Text(
            text = "Küsimuste arv: $amount",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(5, 10, 15).forEach { value ->
                OutlinedButton(
                    onClick = { viewModel.updateAmount(value) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(value.toString())
                }
            }
        }

        if (uiState.error != null) {
            Text(
                text = "Viga: ${uiState.error}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = {
                viewModel.startQuiz()
                onStartQuiz()
            },
            enabled = selectedCategory != null && !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(if (uiState.isLoading) "Laadimine..." else "Alusta mängu")
        }
    }
}