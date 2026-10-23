package com.itsmcodez.vueplay.di

import com.itsmcodez.themoviedbapi.TmdbApi
import com.itsmcodez.vueplay.data.remote.tmdb.RetrofitTmdbApi
import com.itsmcodez.vueplay.data.remote.tmdb.RetrofitTmdbRequestExecutor
import com.itsmcodez.vueplay.data.remote.tmdb.TmdbAuthInterceptor
import com.itsmcodez.vueplay.data.source.TmdbMoviesAndTvSeriesSource
import com.itsmcodez.vueplay.domain.source.MoviesAndTvSeriesSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MoviesAndTvSeriesSourceModule {
    @Provides
    @Singleton
    fun provideTmdbMoviesAndTvSeriesSource(tmdbApi: TmdbApi): MoviesAndTvSeriesSource =
        TmdbMoviesAndTvSeriesSource(tmdbApi)

    @Provides
    @Singleton
    fun provideTmdbApi(): TmdbApi {

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(
                TmdbAuthInterceptor()
            )
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(
                "https://api.themoviedb.org/"
            )
            .client(okHttpClient)
            .build()

        val api = retrofit.create(
            RetrofitTmdbApi::class.java
        )

        return TmdbApi(
            RetrofitTmdbRequestExecutor(api)
        )
    }

}

