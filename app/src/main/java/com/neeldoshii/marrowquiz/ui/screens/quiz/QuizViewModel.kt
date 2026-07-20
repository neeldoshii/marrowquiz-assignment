package com.neeldoshii.marrowquiz.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neeldoshii.marrowquiz.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var advanceJob: Job? = null

    init {
        loadQuiz()
    }

    fun loadQuiz() {
        advanceJob?.cancel()
        viewModelScope.launch {
            _uiState.value = QuizUiState(isLoading = true)
            quizRepository.getQuiz()
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to load quiz",
                        )
                    }
                }
                .collect { questions ->
                    _uiState.value = QuizUiState(
                        isLoading = false,
                        questions = questions,
                    )
                }
        }
    }

    fun selectOption(optionIndex: Int) {
        val state = _uiState.value
        if (state.isLoading || state.isFinished || state.isAnswerRevealed) return
        val question = state.currentQuestion ?: return
        val options = question.options ?: return
        if (optionIndex !in options.indices) return

        val isCorrect = optionIndex == question.correctOptionIndex
        val newStreak = if (isCorrect) state.currentStreak + 1 else 0

        _uiState.update {
            it.copy(
                selectedOptionIndex = optionIndex,
                isAnswerRevealed = true,
                currentStreak = newStreak,
                longestStreak = maxOf(it.longestStreak, newStreak),
                correctCount = if (isCorrect) it.correctCount + 1 else it.correctCount,
                showStreakCelebration = isCorrect && newStreak >= STREAK_THRESHOLD,
            )
        }

        scheduleAdvance()
    }

    fun skip() {
        val state = _uiState.value
        if (state.isLoading || state.isFinished || state.isAnswerRevealed) return

        advanceJob?.cancel()
        _uiState.update {
            it.copy(
                skippedCount = it.skippedCount + 1,
                showStreakCelebration = false,
            )
        }
        goToNextQuestion()
    }

    fun consumeStreakCelebration() {
        _uiState.update { it.copy(showStreakCelebration = false) }
    }

    fun restartQuiz() {
        advanceJob?.cancel()
        val questions = _uiState.value.questions
        if (questions.isEmpty()) {
            loadQuiz()
            return
        }
        _uiState.value = QuizUiState(
            isLoading = false,
            questions = questions,
        )
    }

    private fun scheduleAdvance() {
        advanceJob?.cancel()
        advanceJob = viewModelScope.launch {
            delay(ANSWER_REVEAL_MS)
            goToNextQuestion()
        }
    }

    private fun goToNextQuestion() {
        val state = _uiState.value
        val nextIndex = state.currentIndex + 1
        if (nextIndex >= state.totalQuestions) {
            _uiState.update {
                it.copy(
                    isFinished = true,
                    isAnswerRevealed = false,
                    selectedOptionIndex = null,
                    showStreakCelebration = false,
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                currentIndex = nextIndex,
                selectedOptionIndex = null,
                isAnswerRevealed = false,
                showStreakCelebration = false,
            )
        }
    }

    companion object {
        const val ANSWER_REVEAL_MS = 2_000L
        const val STREAK_THRESHOLD = 3
    }
}
