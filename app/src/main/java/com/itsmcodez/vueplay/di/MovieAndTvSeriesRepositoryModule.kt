package com.itsmcodez.vueplay.di

import com.itsmcodez.vueplay.data.repository.MovieAndTvSeriesRepositoryImpl
import com.itsmcodez.vueplay.domain.repository.MovieAndTvSeriesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MovieAndTvSeriesRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindMovieAndTvSeriesRepository(repository: MovieAndTvSeriesRepositoryImpl): MovieAndTvSeriesRepository
}