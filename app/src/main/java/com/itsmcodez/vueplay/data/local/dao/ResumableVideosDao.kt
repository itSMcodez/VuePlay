package com.itsmcodez.vueplay.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.itsmcodez.vueplay.data.local.entity.ResumableVideo
import kotlinx.coroutines.flow.Flow

@Dao
interface ResumableVideosDao {
    @Query("SELECT * FROM resumable_videos WHERE videoId = :videoId")
    suspend fun getResumableVideo(videoId: Long): ResumableVideo?

    @Query("SELECT * FROM resumable_videos")
    fun getAllResumableVideos(): Flow<List<ResumableVideo>>

    @Insert
    suspend fun insertResumableVideo(resumableVideo: ResumableVideo)

    @Update
    suspend fun updateResumableVideo(resumableVideo: ResumableVideo)

    @Delete
    suspend fun deleteResumableVideo(resumableVideo: ResumableVideo)

    @Query("DELETE FROM resumable_videos WHERE videoId = :videoId")
    suspend fun deleteResumableVideoById(videoId: Long)
}