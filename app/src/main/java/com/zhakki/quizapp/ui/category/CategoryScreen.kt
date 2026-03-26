package com.zhakki.quizapp.ui.category

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhakki.quizapp.data.model.Difficulty
import com.zhakki.quizapp.ui.theme.QuizAppTheme

sealed interface CategoryUiState {
    data object Loading : CategoryUiState
    data class Error(val message: String) : CategoryUiState
    data object Empty : CategoryUiState
    data class Content(
        val title: String,
        val categories: List<CategoryItemUi>,
        val selectedCategoryId: String?,
        val selectedDifficulty: Difficulty,
        val amount: Int,
        val amountOptions: List<Int>,
        /** Ошибка при уже загруженном списке категорий (например, старт квиза). */
        val inlineError: String?,
        val canStart: Boolean,
        val isStartInProgress: Boolean
    ) : CategoryUiState
}

data class CategoryItemUi(
    val id: String,
    val name: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    state: CategoryUiState,
    onCategoryClick: (String) -> Unit,
    onDifficultySelected: (Difficulty) -> Unit = {},
    onAmountSelected: (Int) -> Unit = {},
    onStartClick: () -> Unit,
    onOpenHistory: () -> Unit = {},
    onOpenLeaderboard: () -> Unit = {},
    historyEnabled: Boolean = false,
    leaderboardEnabled: Boolean = false,
    onRetry: () -> Unit
) {
    QuizAppTheme(darkTheme = isSystemInDarkTheme()) {
        val scheme = MaterialTheme.colorScheme
        val screenBg = Brush.verticalGradient(
            colors = listOf(
                scheme.primaryContainer.copy(alpha = 0.45f),
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
                    .padding(horizontal = 20.dp)
            ) {
                CategoryHeroHeader()

                when (state) {
                    CategoryUiState.Loading -> {
                        Spacer(Modifier.height(32.dp))
                        Text(
                            text = "Loading...",
                            style = MaterialTheme.typography.titleMedium,
                            color = scheme.onSurfaceVariant
                        )
                    }

                    is CategoryUiState.Error -> {
                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = "Error: ${state.message}",
                            color = scheme.error,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = onRetry,
                            shape = RoundedCornerShape(14.dp)
                        ) { Text("Retry") }
                    }

                    CategoryUiState.Empty -> {
                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = "No categories found",
                            style = MaterialTheme.typography.titleMedium,
                            color = scheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = onRetry,
                            shape = RoundedCornerShape(14.dp)
                        ) { Text("Refresh") }
                    }

                    is CategoryUiState.Content -> {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = state.title,
                            style = MaterialTheme.typography.headlineSmall,
                            color = scheme.onSurface
                        )
                        Spacer(Modifier.height(16.dp))

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(state.categories, key = { it.id }) { item ->
                                val selected = item.id == state.selectedCategoryId
                                CategoryCard(
                                    name = item.name,
                                    selected = selected,
                                    onClick = { onCategoryClick(item.id) }
                                )
                            }
                        }

                        SectionLabel("Difficulty · ${state.selectedDifficulty.name}")
                        Spacer(Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Difficulty.entries.forEach { difficulty ->
                                val selected = difficulty == state.selectedDifficulty
                                ChoiceChip(
                                    label = difficulty.name,
                                    selected = selected,
                                    onClick = { onDifficultySelected(difficulty) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))
                        SectionLabel("Number of questions · ${state.amount}")
                        Spacer(Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            state.amountOptions.forEach { value ->
                                val selected = value == state.amount
                                ChoiceChip(
                                    label = value.toString(),
                                    selected = selected,
                                    onClick = { onAmountSelected(value) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        state.inlineError?.let { err ->
                            Text(
                                text = "Error: $err",
                                color = scheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        GradientStartButton(
                            enabled = state.canStart,
                            loading = state.isStartInProgress,
                            onClick = onStartClick
                        )

                        Spacer(Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onOpenHistory,
                                enabled = historyEnabled,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp)
                            ) { Text("History") }

                            OutlinedButton(
                                onClick = onOpenLeaderboard,
                                enabled = leaderboardEnabled,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp)
                            ) { Text("Leaderboard") }
                        }

                        Spacer(Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryHeroHeader() {
    val scheme = MaterialTheme.colorScheme
    val headerBrush = Brush.horizontalGradient(
        colors = listOf(
            scheme.primary.copy(alpha = 0.92f),
            scheme.tertiary.copy(alpha = 0.75f),
            scheme.secondary.copy(alpha = 0.65f)
        )
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .shadow(12.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(headerBrush)
            .padding(horizontal = 22.dp, vertical = 22.dp)
    ) {
        Column {
            Text(
                text = "Quiz",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Choose a category and round settings",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.92f)
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryCard(
    name: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.02f else 1f,
        animationSpec = tween(220),
        label = "cardScale"
    )
    val cardColor = if (selected) {
        scheme.primaryContainer
    } else {
        scheme.surface.copy(alpha = 0.92f)
    }
    val elevation = if (selected) 8.dp else 2.dp

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(elevation, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = if (selected) {
            BorderStroke(2.dp, scheme.primary.copy(alpha = 0.55f))
        } else {
            null
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(horizontal = 18.dp, vertical = 16.dp)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = if (selected) scheme.onPrimaryContainer else scheme.onSurface
            )
            if (selected) {
                Spacer(Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = scheme.primary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "Selected",
                        style = MaterialTheme.typography.labelLarge,
                        color = scheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChoiceChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.04f else 1f,
        animationSpec = tween(200),
        label = "chipScale"
    )
    val bg = if (selected) {
        Brush.horizontalGradient(
            listOf(
                scheme.primary.copy(alpha = 0.92f),
                scheme.tertiary.copy(alpha = 0.75f)
            )
        )
    } else {
        Brush.linearGradient(listOf(scheme.surfaceVariant.copy(alpha = 0.85f), scheme.surfaceVariant))
    }
    val contentColor = if (selected) Color.White else scheme.onSurfaceVariant

    Surface(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = if (selected) 6.dp else 1.dp,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun GradientStartButton(
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val brush = Brush.horizontalGradient(
        colors = listOf(
            scheme.primary,
            scheme.tertiary.copy(alpha = 0.95f),
            scheme.secondary.copy(alpha = 0.85f)
        )
    )
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(10.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = scheme.surfaceVariant.copy(alpha = 0.6f),
            contentColor = Color.White,
            disabledContentColor = scheme.onSurfaceVariant
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (enabled) Modifier.background(brush) else Modifier.background(scheme.surfaceVariant.copy(alpha = 0.5f))
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (loading) "Loading..." else "Start Game",
                style = MaterialTheme.typography.titleMedium,
                color = if (enabled) Color.White else scheme.onSurfaceVariant
            )
        }
    }
}
