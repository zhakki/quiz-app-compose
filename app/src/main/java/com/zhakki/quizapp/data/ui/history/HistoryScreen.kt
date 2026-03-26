package com.zhakki.quizapp.data.ui.history

import android.text.Html
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhakki.quizapp.viewmodel.QuizViewModel

private fun decodeHtml(text: String): String {
    return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString()
}

@Composable
fun HistoryScreen(
    viewModel: QuizViewModel,
    onBack: () -> Unit
) {
    val history by viewModel.gameHistory.collectAsState()
    val listState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Mängu ajalugu",
            style = MaterialTheme.typography.headlineLarge
        )

        if (history.isEmpty()) {
            Text(
                text = "Ajalugu puudub",
                style = MaterialTheme.typography.titleMedium
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    top = 4.dp,
                    bottom = 24.dp
                )
            ) {
                items(history) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Kuupäev: ${item.date}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Kategooria: ${decodeHtml(item.category)}"
                            )
                            Text(
                                text = "Tulemus: ${item.score} / ${item.totalQuestions}"
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tagasi")
        }
    }
}