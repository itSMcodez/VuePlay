package com.itsmcodez.vueplay.di

import com.itsmcodez.vueplay.data.repository.SearchHistoryRepositoryImpl
import com.itsmcodez.vueplay.domain.repository.SearchHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchHistoryModule {
    @Singleton
    @Binds
    abstract fun bindSearchHistoryRepository(impl: SearchHistoryRepositoryImpl): SearchHistoryRepository
}