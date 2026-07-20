package com.neeldoshii.marrowquiz.data.repository

import com.neeldoshii.marrowquiz.data.model.GetQuizResponse
import kotlinx.coroutines.flow.Flow

interface QuizRepository {
    fun getQuiz(): Flow<List<GetQuizResponse>>
}
