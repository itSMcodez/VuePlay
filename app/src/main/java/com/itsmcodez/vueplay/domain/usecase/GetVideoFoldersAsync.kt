package com.itsmcodez.vueplay.domain.usecase

import com.itsmcodez.vueplay.domain.repository.VideoFolderRepository
import javax.inject.Inject

class GetVideoFoldersAsync @Inject constructor(private val repository: VideoFolderRepository) {
    suspend operator fun invoke() = repository.getVideoFoldersAsync()
}