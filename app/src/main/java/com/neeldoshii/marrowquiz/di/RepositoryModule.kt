package com.neeldoshii.marrowquiz.di

import com.neeldoshii.marrowquiz.data.repository.QuizRepository
import com.neeldoshii.marrowquiz.data.repository.QuizRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindQuizRepository(impl: QuizRepositoryImpl): QuizRepository
}
