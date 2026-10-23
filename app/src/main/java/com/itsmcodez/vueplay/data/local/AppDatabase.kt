package com.itsmcodez.vueplay.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.itsmcodez.vueplay.data.local.dao.DownloadsDao
import com.itsmcodez.vueplay.data.local.dao.ResumableVideosDao
import com.itsmcodez.vueplay.data.local.dao.SearchHistoryDao
import com.itsmcodez.vueplay.data.local.entity.DownloadItem
import com.itsmcodez.vueplay.data.local.entity.ResumableVideo
import com.itsmcodez.vueplay.data.local.entity.SearchHistory

@Database(
    entities = [
        ResumableVideo::class,
        SearchHistory::class,
        DownloadItem::class
    ],
    autoMigrations = [
        AutoMigration(1, 2),
        AutoMigration(2, 3)
    ],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun resumableVideosDao(): ResumableVideosDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun downloadsDao(): DownloadsDao
}