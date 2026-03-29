package com.zhakki.quizapp.ui.leaderboard

import android.text.Html
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhakki.quizapp.ui.theme.QuizAppTheme

sealed interface LeaderboardUiState {
    data object Loading : LeaderboardUiState
    data class Error(val message: String) : LeaderboardUiState
    data object Empty : LeaderboardUiState
    data class Content(val items: List<LeaderboardItemUi>) : LeaderboardUiState
}

data class LeaderboardItemUi(
    val id: String,
    val name: String,
    val scoreText: String
)

private fun decodeDisplayText(text: String): String {
    return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString()
}

private fun medalForRank(index: Int): String = when (index) {
    0 -> "\uD83E\uDD47 " // 🥇
    1 -> "\uD83E\uDD48 " // 🥈
    2 -> "\uD83E\uDD49 " // 🥉
    else -> ""
}

@Composable
fun LeaderboardScreen(
    state: LeaderboardUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit
) {
    QuizAppTheme(darkTheme = isSystemInDarkTheme()) {
        val scheme = MaterialTheme.colorScheme
        val screenBg = Brush.verticalGradient(
            colors = listOf(
                scheme.primaryContainer.copy(alpha = 0.52f),
                scheme.surface
            )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .background(screenBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Leaderboard",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = scheme.onSurface
                    )
                    Text(
                        text = "Best results by category",
                        style = MaterialTheme.typography.bodyLarge,
                        color = scheme.onSurfaceVariant
                    )
                }

                when (state) {
                    LeaderboardUiState.Loading -> {
                        Text(
                            "Loading...",
                            style = MaterialTheme.typography.titleMedium,
                            color = scheme.onSurfaceVariant
                        )
                    }

                    is LeaderboardUiState.Error -> {
                        Text(
                            text = "Error: ${state.message}",
                            color = scheme.error,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(
                            onClick = onRetry,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = scheme.primary,
                                contentColor = scheme.onPrimary
                            )
                        ) { Text("Retry") }
                        Button(
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = scheme.primary,
                                contentColor = scheme.onPrimary
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text(
                                text = "Back",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    LeaderboardUiState.Empty -> {
                        Text(
                            "No results yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = scheme.onSurfaceVariant
                        )
                        Button(
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = scheme.primary,
                                contentColor = scheme.onPrimary
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text(
                                text = "Back",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    is LeaderboardUiState.Content -> {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(bottom = 12.dp)
                        ) {
                            itemsIndexed(
                                items = state.items,
                                key = { index, item -> "${index}_${item.id}" }
                            ) { index, item ->
                                val isFirst = index == 0
                                val isSecondOrThird = index == 1 || index == 2
                                val isTopThree = index < 3
                                val isRest = index >= 3

                                val corner = when {
                                    isFirst -> 20.dp
                                    isTopThree -> 18.dp
                                    else -> 14.dp
                                }
                                val verticalPad = when {
                                    isFirst -> 18.dp
                                    isSecondOrThird -> 15.dp
                                    else -> 10.dp
                                }
                                val horizontalPad = when {
                                    isRest -> 14.dp
                                    else -> 16.dp
                                }

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(corner),
                                    colors = CardDefaults.cardColors(
                                        containerColor = when {
                                            isFirst -> scheme.primaryContainer.copy(alpha = 0.85f)
                                            isSecondOrThird -> scheme.surface.copy(alpha = 0.97f)
                                            else -> scheme.surface.copy(alpha = 0.92f)
                                        }
                                    ),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = when {
                                            isFirst -> 8.dp
                                            isSecondOrThird -> 3.dp
                                            else -> 1.dp
                                        }
                                    ),
                                    border = when {
                                        isFirst -> BorderStroke(2.dp, scheme.primary.copy(alpha = 0.65f))
                                        else -> null
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                horizontal = horizontalPad,
                                                vertical = verticalPad
                                            ),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(
                                                if (isTopThree) 12.dp else 8.dp
                                            )
                                        ) {
                                            Text(
                                                text = if (isTopThree) {
                                                    medalForRank(index) + "${index + 1}."
                                                } else {
                                                    "${index + 1}."
                                                },
                                                style = when {
                                                    isFirst -> MaterialTheme.typography.titleLarge
                                                    isTopThree -> MaterialTheme.typography.titleMedium
                                                    else -> MaterialTheme.typography.bodyLarge
                                                },
                                                fontWeight = if (isTopThree) FontWeight.Bold else FontWeight.Medium,
                                                color = when {
                                                    isFirst -> scheme.primary
                                                    isSecondOrThird -> scheme.onSurfaceVariant
                                                    isTopThree -> scheme.primary
                                                    else -> scheme.onSurfaceVariant
                                                }
                                            )
                                            Text(
                                                text = decodeDisplayText(item.name),
                                                style = when {
                                                    isFirst -> MaterialTheme.typography.titleLarge
                                                    isTopThree -> MaterialTheme.typography.titleMedium
                                                    else -> MaterialTheme.typography.bodyLarge
                                                },
                                                fontWeight = when {
                                                    isFirst -> FontWeight.Bold
                                                    isSecondOrThird -> FontWeight.Medium
                                                    isTopThree -> FontWeight.SemiBold
                                                    else -> FontWeight.Normal
                                                },
                                                color = scheme.onSurface,
                                                maxLines = if (isRest) 2 else 3,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f, fill = false)
                                            )
                                        }
                                        Text(
                                            text = decodeDisplayText(item.scoreText),
                                            style = when {
                                                isFirst -> MaterialTheme.typography.headlineSmall
                                                isSecondOrThird -> MaterialTheme.typography.titleMedium
                                                isTopThree -> MaterialTheme.typography.titleLarge
                                                else -> MaterialTheme.typography.titleMedium
                                            },
                                            fontWeight = when {
                                                isFirst -> FontWeight.Bold
                                                isSecondOrThird -> FontWeight.SemiBold
                                                else -> FontWeight.Bold
                                            },
                                            color = when {
                                                isFirst -> scheme.primary
                                                isSecondOrThird -> scheme.onSurface
                                                isTopThree -> scheme.primary
                                                else -> scheme.onSurface
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = scheme.primary,
                                contentColor = scheme.onPrimary
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text(
                                text = "Back",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
