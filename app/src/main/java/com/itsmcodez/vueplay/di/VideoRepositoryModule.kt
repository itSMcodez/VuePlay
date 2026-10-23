package com.itsmcodez.vueplay.di

import com.itsmcodez.vueplay.data.repository.VideoRepositoryImpl
import com.itsmcodez.vueplay.domain.repository.VideoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class VideoRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindVideoRepository(impl: VideoRepositoryImpl): VideoRepository
}