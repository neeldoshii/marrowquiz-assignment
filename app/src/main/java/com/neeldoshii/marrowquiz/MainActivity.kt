package com.neeldoshii.marrowquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.neeldoshii.marrowquiz.ui.navigation.AppNavHost
import com.neeldoshii.marrowquiz.ui.theme.MarrowquizTheme
import com.neeldoshii.marrowquiz.ui.theme.QuizInk
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MarrowquizTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = QuizInk,
                ) {
                    AppNavHost()
                }
            }
        }
    }
}
