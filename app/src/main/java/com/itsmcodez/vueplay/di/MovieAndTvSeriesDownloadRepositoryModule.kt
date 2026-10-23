package com.itsmcodez.vueplay.di

import com.itsmcodez.vueplay.data.repository.MovieAndTvSeriesDownloadRepositoryImpl
import com.itsmcodez.vueplay.domain.repository.MovieAndTvSeriesDownloadRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MovieAndTvSeriesDownloadRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindMovieAndTvSeriesDownloadRepository(
        impl: MovieAndTvSeriesDownloadRepositoryImpl
    ): MovieAndTvSeriesDownloadRepository
}