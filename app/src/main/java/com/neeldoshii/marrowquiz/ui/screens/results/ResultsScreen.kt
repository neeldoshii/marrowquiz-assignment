package com.neeldoshii.marrowquiz.ui.screens.results

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neeldoshii.marrowquiz.ui.theme.QuizInk
import com.neeldoshii.marrowquiz.ui.theme.QuizMuted
import com.neeldoshii.marrowquiz.ui.theme.QuizOnInk
import com.neeldoshii.marrowquiz.ui.theme.QuizSurface

@Composable
fun ResultsScreen(
    correctCount: Int,
    totalQuestions: Int,
    longestStreak: Int,
    skippedCount: Int,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(QuizInk, Color(0xFF12121A), QuizInk),
                ),
            )
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Quiz Results",
            color = QuizOnInk,
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Congratulations!",
            color = QuizOnInk,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "You’ve completed the quiz. Here’s your performance summary:",
            color = QuizMuted,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard(
                label = "Correct Answers",
                value = "$correctCount/$totalQuestions",
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = "Highest Streak",
                value = longestStreak.toString(),
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        StatCard(
            label = "Skipped",
            value = skippedCount.toString(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onRestart,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 28.dp)
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = QuizOnInk,
                contentColor = QuizInk,
            ),
            shape = RoundedCornerShape(28.dp),
        ) {
            Text(
                text = "Restart Quiz",
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(QuizSurface)
            .padding(vertical = 20.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = label, color = QuizMuted, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = value, color = QuizOnInk, style = MaterialTheme.typography.headlineMedium)
    }
}
