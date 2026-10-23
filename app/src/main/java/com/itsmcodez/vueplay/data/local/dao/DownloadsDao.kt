package com.itsmcodez.vueplay.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.itsmcodez.vueplay.data.local.entity.DownloadItem
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadsDao {

    @Query("SELECT * FROM downloads ORDER BY id DESC")
    fun observeDownloads(): Flow<List<DownloadItem>>

    @Query("SELECT * FROM downloads WHERE id = :id")
    suspend fun getDownload(id: String): DownloadItem?

    @Upsert
    suspend fun upsert(download: DownloadItem)

    @Delete
    suspend fun delete(download: DownloadItem)

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun deleteById(id: String)
}