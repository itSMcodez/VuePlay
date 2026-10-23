package com.itsmcodez.vueplay.di

import com.itsmcodez.vueplay.data.repository.DownloadsRepositoryImpl
import com.itsmcodez.vueplay.domain.repository.DownloadsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DownloadsRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindDownloadsRepository(
        impl: DownloadsRepositoryImpl
    ): DownloadsRepository
}