package com.itsmcodez.vueplay.manager

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri

data class LocalFileMetadata(
    val title: String?,
    val durationMs: Long,
    val width: Int,
    val height: Int,
    val hasVideo: Boolean
)

object LocalFileManager {
    fun extractDownloadedFileMetadata(context: Context, fileName: String): LocalFileMetadata? {
        val file = VuePlayDownloadManager.getDownloadFile(context, fileName)
        if (!file.exists()) return null

        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(file.absolutePath)
            val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
            val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
            val hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO) == "yes"

            LocalFileMetadata(title ?: fileName, duration, width, height, hasVideo)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }

    fun extractFileMetadata(uri: Uri): LocalFileMetadata? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(uri.toString())
            val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
            val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
            val hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO) == "yes"

            LocalFileMetadata(title, duration, width, height, hasVideo)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }
}