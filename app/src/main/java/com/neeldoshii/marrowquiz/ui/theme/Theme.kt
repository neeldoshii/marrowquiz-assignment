package com.neeldoshii.marrowquiz.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val QuizColorScheme = darkColorScheme(
    primary = QuizProgressFill,
    onPrimary = QuizInk,
    secondary = QuizEmber,
    onSecondary = QuizOnInk,
    background = QuizInk,
    onBackground = QuizOnInk,
    surface = QuizSurface,
    onSurface = QuizOnInk,
    surfaceVariant = QuizSurfaceElevated,
    onSurfaceVariant = QuizMuted,
    error = QuizWrong,
    onError = QuizOnInk,
)

@Composable
fun MarrowquizTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = QuizColorScheme,
        typography = Typography,
        content = content,
    )
}
