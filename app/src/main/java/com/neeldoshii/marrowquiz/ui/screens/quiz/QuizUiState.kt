package com.neeldoshii.marrowquiz.ui.screens.quiz

import com.neeldoshii.marrowquiz.data.model.GetQuizResponse

data class QuizUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val questions: List<GetQuizResponse> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerRevealed: Boolean = false,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val correctCount: Int = 0,
    val skippedCount: Int = 0,
    val showStreakCelebration: Boolean = false,
    val isFinished: Boolean = false,
) {
    val totalQuestions: Int get() = questions.size

    val currentQuestion: GetQuizResponse?
        get() = questions.getOrNull(currentIndex)

    val questionNumber: Int get() = currentIndex + 1

    val progress: Float
        get() = if (totalQuestions == 0) 0f else questionNumber / totalQuestions.toFloat()

    val isStreakLit: Boolean get() = currentStreak >= 3
}
