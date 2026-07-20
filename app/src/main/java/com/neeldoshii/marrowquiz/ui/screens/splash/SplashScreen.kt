package com.neeldoshii.marrowquiz.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neeldoshii.marrowquiz.ui.theme.QuizEmber
import com.neeldoshii.marrowquiz.ui.theme.QuizInk
import com.neeldoshii.marrowquiz.ui.theme.QuizMuted
import com.neeldoshii.marrowquiz.ui.theme.QuizOnInk

@Composable
fun SplashScreen(
    isReady: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,
    onReady: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(isReady) {
        if (isReady) onReady()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(QuizInk, Color(0xFF14141C), QuizInk),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp),
        ) {
            Text(
                text = "Marrow Quiz",
                color = QuizOnInk,
                style = MaterialTheme.typography.headlineLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sharpen your Android instincts",
                color = QuizMuted,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(32.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = QuizMuted,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = QuizOnInk,
                        contentColor = QuizInk,
                    ),
                    shape = RoundedCornerShape(28.dp),
                ) {
                    Text("Retry")
                }
            } else {
                CircularProgressIndicator(color = QuizEmber)
            }
        }
    }
}
