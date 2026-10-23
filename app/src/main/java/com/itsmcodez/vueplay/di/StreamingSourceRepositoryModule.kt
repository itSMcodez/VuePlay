package com.itsmcodez.vueplay.di

import com.itsmcodez.vueplay.data.repository.StreamingSourceRepositoryImpl
import com.itsmcodez.vueplay.domain.repository.StreamingSourceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StreamingSourceRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindStreamingSourceRepository(
        impl: StreamingSourceRepositoryImpl
    ) : StreamingSourceRepository
}