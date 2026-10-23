package com.itsmcodez.vueplay.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class DownloadItem(
    @PrimaryKey
    val id: String,
    val url: String,
    val fileName: String,
    val progress: Float = 0f,
    val bytesDownloaded: Long = 0L,
    val totalBytes: Long = 0L,
    val status: DownloadStatus = DownloadStatus.IDLE,
    val errorMessage: String? = null
)

enum class DownloadStatus {
    IDLE,
    DOWNLOADING,
    PAUSED,
    PAUSING,
    COMPLETED,
    FAILED
}