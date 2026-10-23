package com.itsmcodez.vueplay.di

import com.itsmcodez.vueplay.data.remote.dmca.DmcaApi
import com.itsmcodez.vueplay.data.source.LocalDmcaDataSource
import com.itsmcodez.vueplay.domain.source.DmcaDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DmcaModule {
    @Singleton
    @Provides
    fun provideDmcaApi(): DmcaApi {

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/")
            .addConverterFactory(
                Json.asConverterFactory(
                    "application/json".toMediaType()
                )
            )
            .client(okHttpClient)
            .build()

        return retrofit.create(DmcaApi::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DmcaDataSource {
    @Binds
    @Singleton
    abstract fun bindDmcaDataSource(
        local: LocalDmcaDataSource
    ): DmcaDataSource
}