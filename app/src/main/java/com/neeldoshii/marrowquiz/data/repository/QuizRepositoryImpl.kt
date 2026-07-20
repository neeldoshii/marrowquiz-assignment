package com.neeldoshii.marrowquiz.data.repository

import com.neeldoshii.marrowquiz.data.model.GetQuizResponse
import com.neeldoshii.marrowquiz.data.remote.CoreService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepositoryImpl @Inject constructor(
    private val coreService: CoreService,
) : QuizRepository {

    override fun getQuiz(): Flow<List<GetQuizResponse>> = flow {
        emit(coreService.getQuiz())
    }
}
