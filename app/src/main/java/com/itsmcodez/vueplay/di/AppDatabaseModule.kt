package com.itsmcodez.vueplay.di

import android.app.Application
import androidx.room.Room
import com.itsmcodez.vueplay.data.local.AppDatabase
import com.itsmcodez.vueplay.data.local.dao.DownloadsDao
import com.itsmcodez.vueplay.data.local.dao.ResumableVideosDao
import com.itsmcodez.vueplay.data.local.dao.SearchHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object AppDatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "vueplay_db"
        ).build()
    }

    @Provides
    fun provideResumableVideosDao(appDatabase: AppDatabase): ResumableVideosDao = appDatabase.resumableVideosDao()

    @Provides
    fun provideSearchHistoryDao(appDatabase: AppDatabase): SearchHistoryDao = appDatabase.searchHistoryDao()

    @Provides
    fun provideDownloadsDao(appDatabase: AppDatabase): DownloadsDao = appDatabase.downloadsDao()
}