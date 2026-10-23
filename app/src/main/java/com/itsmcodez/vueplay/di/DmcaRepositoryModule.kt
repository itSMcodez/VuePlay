package com.itsmcodez.vueplay.di

import com.itsmcodez.vueplay.data.repository.DmcaRepositoryImpl
import com.itsmcodez.vueplay.domain.repository.DmcaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DmcaRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindDmcaRepository(repository: DmcaRepositoryImpl): DmcaRepository
}