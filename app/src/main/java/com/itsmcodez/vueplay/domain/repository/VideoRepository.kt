package com.itsmcodez.vueplay.domain.repository

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.itsmcodez.vueplay.data.local.entity.ResumableVideo
import com.itsmcodez.vueplay.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    suspend fun getVideosAsync(): List<Video>
    fun getVideos(): Flow<List<Video>>
    suspend fun getResumableVideo(videoId: Long): ResumableVideo?
    fun getAllResumableVideos(): Flow<List<ResumableVideo>>
    suspend fun updateResumableVideo(resumableVideo: ResumableVideo)
    suspend fun deleteResumableVideo(resumableVideo: ResumableVideo)
    suspend fun deleteResumableVideoById(videoId: Long)
    suspend fun insertResumableVideo(resumableVideo: ResumableVideo): Any
    fun renameVideo(video: Video, newName: String, launcher: ActivityResultLauncher<IntentSenderRequest>)
    fun shareVideos(vararg videos: Video)
    fun deleteVideos(vararg videos: Video, launcher: ActivityResultLauncher<IntentSenderRequest>)
    fun launch()
}