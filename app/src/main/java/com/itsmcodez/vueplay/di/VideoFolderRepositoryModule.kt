package com.itsmcodez.vueplay.di

import com.itsmcodez.vueplay.data.repository.VideoFolderRepositoryImpl
import com.itsmcodez.vueplay.domain.repository.VideoFolderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class VideoFolderRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindVideoFolderRepository(impl: VideoFolderRepositoryImpl): VideoFolderRepository
}