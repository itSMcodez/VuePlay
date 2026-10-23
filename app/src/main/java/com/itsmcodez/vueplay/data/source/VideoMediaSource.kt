package com.itsmcodez.vueplay.data.source

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.itsmcodez.vueplay.domain.model.Video
import com.itsmcodez.vueplay.domain.source.MediaSource
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoMediaSource @Inject constructor() : MediaSource {
    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> get(context: Context): List<T> {
        val videos = mutableListOf<Video>()
        val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val notPending = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            "${MediaStore.Video.Media.IS_PENDING} = 0"
        else null

        val selection = "${ if(notPending != null) "$notPending AND" else ""} ${MediaStore.Video.Media.SIZE} > 0 AND ${MediaStore.Video.Media.DURATION} > 0".trimIndent()

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATE_ADDED
        )

        context.contentResolver.query(
            contentUri,
            projection,
            selection,
            null,
            "${MediaStore.Video.Media.DISPLAY_NAME} ASC"
        )?.use {
            val idCol = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val displayNameCol = it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val pathCol = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val folderNameCol = it.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
            val durationCol = it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val dateAddedCol = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)

            while (it.moveToNext()) {
                val id = it.getLong(idCol)
                val displayName = it.getString(displayNameCol) ?: "Invalid"
                val path = it.getString(pathCol) ?: "Invalid"
                val folderName = it.getString(folderNameCol) ?: "Unknown"
                val duration = it.getLong(durationCol)
                val dateAdded = it.getLong(dateAddedCol)
                val uri = ContentUris.withAppendedId(contentUri, id)

                try {
                    // Skip if the file doesn't exist
                    if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                        if(!File(path).exists()) continue
                    }
                } catch (e: Exception) { e.printStackTrace() }

                videos.add(
                    Video(
                        id = id,
                        displayName = displayName,
                        path = path,
                        folderName = folderName,
                        duration = duration,
                        dateAdded = dateAdded,
                        uri = uri
                    )
                )
            }
        }
        Log.d("VideoMediaSource", "Found ${videos.size} videos")
        return videos.toList() as List<T>
    }
}