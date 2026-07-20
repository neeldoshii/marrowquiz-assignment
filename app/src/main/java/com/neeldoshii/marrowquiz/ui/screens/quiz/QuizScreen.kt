package com.neeldoshii.marrowquiz.ui.screens.quiz

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neeldoshii.marrowquiz.ui.theme.QuizCorrect
import com.neeldoshii.marrowquiz.ui.theme.QuizEmber
import com.neeldoshii.marrowquiz.ui.theme.QuizEmberSoft
import com.neeldoshii.marrowquiz.ui.theme.QuizInk
import com.neeldoshii.marrowquiz.ui.theme.QuizMuted
import com.neeldoshii.marrowquiz.ui.theme.QuizOnInk
import com.neeldoshii.marrowquiz.ui.theme.QuizProgressFill
import com.neeldoshii.marrowquiz.ui.theme.QuizProgressTrack
import com.neeldoshii.marrowquiz.ui.theme.QuizSurface
import com.neeldoshii.marrowquiz.ui.theme.QuizWrong
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun QuizScreen(
    onQuizFinished: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QuizViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) onQuizFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(QuizInk, Color(0xFF12121A), QuizInk),
                ),
            )
            .statusBarsPadding(),
    ) {
        when {
            state.isLoading -> LoadingContent()
            state.errorMessage != null -> ErrorContent(
                message = state.errorMessage.orEmpty(),
                onRetry = viewModel::loadQuiz,
            )
            state.currentQuestion != null -> QuizContent(
                state = state,
                onSelectOption = viewModel::selectOption,
                onSkip = viewModel::skip,
                onCelebrationShown = viewModel::consumeStreakCelebration,
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = QuizEmber)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading questions…",
                color = QuizMuted,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Couldn’t load the quiz",
            color = QuizOnInk,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            color = QuizMuted,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = QuizOnInk,
                contentColor = QuizInk,
            ),
            shape = RoundedCornerShape(28.dp),
        ) {
            Text("Try again")
        }
    }
}

@Composable
private fun QuizContent(
    state: QuizUiState,
    onSelectOption: (Int) -> Unit,
    onSkip: () -> Unit,
    onCelebrationShown: () -> Unit,
) {
    val question = state.currentQuestion ?: return
    val canSwipeSkip = !state.isAnswerRevealed
    val scope = rememberCoroutineScope()
    val swipeOffset = remember { Animatable(0f) }
    val skipThresholdPx = with(LocalDensity.current) { 96.dp.toPx() }

    LaunchedEffect(state.currentIndex) {
        swipeOffset.snapTo(0f)
    }

    LaunchedEffect(state.showStreakCelebration) {
        if (state.showStreakCelebration) {
            delay(1_600)
            onCelebrationShown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .offset { IntOffset(swipeOffset.value.roundToInt(), 0) }
            .graphicsLayer {
                val progress =
                    (kotlin.math.abs(swipeOffset.value) / (skipThresholdPx * 2f)).coerceIn(0f, 1f)
                alpha = 1f - progress * 0.25f
            }
            .pointerInput(canSwipeSkip, state.currentIndex) {
                if (!canSwipeSkip) return@pointerInput
                detectHorizontalDragGestures(
                    onDragEnd = {
                        scope.launch {
                            if (kotlin.math.abs(swipeOffset.value) >= skipThresholdPx) {
                                onSkip()
                                swipeOffset.snapTo(0f)
                            } else {
                                swipeOffset.animateTo(
                                    0f,
                                    spring(stiffness = Spring.StiffnessMedium),
                                )
                            }
                        }
                    },
                    onDragCancel = {
                        scope.launch {
                            swipeOffset.animateTo(
                                0f,
                                spring(stiffness = Spring.StiffnessMedium),
                            )
                        }
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        val max = skipThresholdPx * 2.2f
                        val next = (swipeOffset.value + dragAmount).coerceIn(-max, max)
                        scope.launch { swipeOffset.snapTo(next) }
                    },
                )
            },
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Quiz",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = QuizOnInk,
            style = MaterialTheme.typography.titleLarge,
        )

        Spacer(modifier = Modifier.height(16.dp))

        StreakHeader(
            streak = state.currentStreak,
            isLit = state.isStreakLit,
            showCelebration = state.showStreakCelebration,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Question ${state.questionNumber} of ${state.totalQuestions}",
            color = QuizMuted,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))

        val animatedProgress by animateFloatAsState(
            targetValue = state.progress,
            animationSpec = tween(500, easing = FastOutSlowInEasing),
            label = "progress",
        )
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(50)),
            color = QuizProgressFill,
            trackColor = QuizProgressTrack,
            gapSize = 0.dp,
            drawStopIndicator = {},
        )

        Spacer(modifier = Modifier.height(28.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            AnimatedContent(
                targetState = state.currentIndex to question,
                transitionSpec = {
                    (fadeIn(tween(280)) + slideInVertically { it / 8 })
                        .togetherWith(fadeOut(tween(200)) + slideOutVertically { -it / 10 })
                },
                label = "question",
            ) { (_, current) ->
                Column {
                    Text(
                        text = current.question,
                        color = QuizOnInk,
                        style = MaterialTheme.typography.headlineMedium,
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    current.options.forEachIndexed { index, option ->
                        val appear = remember(current.id) { Animatable(0f) }
                        LaunchedEffect(current.id) {
                            appear.snapTo(0f)
                            delay(index * 55L)
                            appear.animateTo(
                                1f,
                                spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            )
                        }

                        OptionButton(
                            text = option,
                            enabled = !state.isAnswerRevealed,
                            background = optionBackground(
                                index = index,
                                selected = state.selectedOptionIndex,
                                correct = current.correctOptionIndex,
                                revealed = state.isAnswerRevealed,
                            ),
                            modifier = Modifier
                                .graphicsLayer {
                                    alpha = appear.value
                                    translationY = (1f - appear.value) * 18f
                                }
                                .padding(bottom = 12.dp),
                            onClick = { onSelectOption(index) },
                        )
                    }
                }
            }
        }

        Text(
            text = "Swipe to skip",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = QuizMuted.copy(alpha = if (canSwipeSkip) 0.7f else 0.35f),
            style = MaterialTheme.typography.bodyMedium,
        )

        TextButton(
            onClick = onSkip,
            enabled = !state.isAnswerRevealed,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 16.dp),
        ) {
            Text(
                text = "Skip",
                color = if (state.isAnswerRevealed) QuizMuted.copy(alpha = 0.4f) else QuizMuted,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun StreakHeader(
    streak: Int,
    isLit: Boolean,
    showCelebration: Boolean,
) {
    val pulse = rememberInfiniteTransition(label = "flamePulse")
    val glow by pulse.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(4) { index ->
                val filled = streak > index
                Text(
                    text = "🔥",
                    fontSize = 22.sp,
                    modifier = Modifier
                        .scale(if (isLit && filled) glow else 1f)
                        .graphicsLayer {
                            alpha = if (filled) 1f else 0.22f
                        },
                )
            }
        }

        AnimatedVisibility(
            visible = isLit,
            enter = fadeIn() + scaleIn(initialScale = 0.85f),
            exit = fadeOut(),
        ) {
            Text(
                text = if (showCelebration) {
                    "$streak questions streak achieved !!"
                } else {
                    "$streak streak · on fire"
                },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(QuizEmberSoft)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                color = QuizEmber,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun OptionButton(
    text: String,
    enabled: Boolean,
    background: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "press",
    )
    val animatedBackground by animateColorAsState(
        targetValue = background,
        animationSpec = tween(320),
        label = "optionColor",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(animatedBackground)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.06f),
                shape = RoundedCornerShape(16.dp),
            )
            .clickable(
                enabled = enabled,
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(vertical = 18.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = QuizOnInk,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
        )
    }
}

private fun optionBackground(
    index: Int,
    selected: Int?,
    correct: Int,
    revealed: Boolean,
): Color {
    if (!revealed) return QuizSurface
    return when {
        index == correct -> QuizCorrect
        index == selected -> QuizWrong
        else -> QuizSurface.copy(alpha = 0.55f)
    }
}
