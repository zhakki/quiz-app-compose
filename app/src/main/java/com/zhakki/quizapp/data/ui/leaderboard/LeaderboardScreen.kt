package com.zhakki.quizapp.data.ui.leaderboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.zhakki.quizapp.ui.leaderboard.LeaderboardScreen as UiLeaderboardScreen
import com.zhakki.quizapp.viewmodel.QuizViewModel

@Composable
fun LeaderboardScreen(
    viewModel: QuizViewModel,
    onBack: () -> Unit
) {
    val leaderboardState by viewModel.leaderboardUiState.collectAsState()

    UiLeaderboardScreen(
        state = leaderboardState,
        onBack = onBack,
        onRetry = {}
    )
}
