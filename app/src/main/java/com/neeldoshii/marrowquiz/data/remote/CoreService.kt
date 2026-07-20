package com.neeldoshii.marrowquiz.data.remote

import com.neeldoshii.marrowquiz.data.model.GetQuizResponse
import retrofit2.http.GET

interface CoreService {
    @GET("quiz")
    suspend fun getQuiz(): List<GetQuizResponse>
}
