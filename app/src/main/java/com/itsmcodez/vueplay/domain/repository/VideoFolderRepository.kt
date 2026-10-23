package com.itsmcodez.vueplay.domain.repository

import com.itsmcodez.vueplay.domain.model.VideoFolder
import kotlinx.coroutines.flow.Flow

interface VideoFolderRepository {
    suspend fun getVideoFoldersAsync(): List<VideoFolder>
    fun getVideoFolders(): Flow<List<VideoFolder>>
}