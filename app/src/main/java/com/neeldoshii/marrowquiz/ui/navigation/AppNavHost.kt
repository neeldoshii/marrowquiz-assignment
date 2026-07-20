package com.neeldoshii.marrowquiz.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.neeldoshii.marrowquiz.ui.screens.quiz.QuizScreen
import com.neeldoshii.marrowquiz.ui.screens.quiz.QuizViewModel
import com.neeldoshii.marrowquiz.ui.screens.results.ResultsScreen
import com.neeldoshii.marrowquiz.ui.screens.splash.SplashScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Route.Splash.path,
) {
    val activityOwner = LocalViewModelStoreOwner.current
        ?: LocalContext.current as ViewModelStoreOwner
    val quizViewModel: QuizViewModel = hiltViewModel(activityOwner)
    val quizState by quizViewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(Route.Splash.path) {
            SplashScreen(
                isReady = !quizState.isLoading &&
                    quizState.errorMessage == null &&
                    quizState.questions.isNotEmpty(),
                errorMessage = quizState.errorMessage,
                onRetry = quizViewModel::loadQuiz,
                onReady = {
                    navController.navigate(Route.Quiz.path) {
                        popUpTo(Route.Splash.path) { inclusive = true }
                    }
                },
            )
        }
        composable(Route.Quiz.path) {
            QuizScreen(
                viewModel = quizViewModel,
                onQuizFinished = {
                    navController.navigate(Route.Results.path) {
                        popUpTo(Route.Quiz.path) { inclusive = true }
                    }
                },
            )
        }
        composable(Route.Results.path) {
            ResultsScreen(
                correctCount = quizState.correctCount,
                totalQuestions = quizState.totalQuestions,
                longestStreak = quizState.longestStreak,
                onRestart = {
                    quizViewModel.restartQuiz()
                    navController.navigate(Route.Quiz.path) {
                        popUpTo(Route.Results.path) { inclusive = true }
                    }
                },
            )
        }
    }
}
