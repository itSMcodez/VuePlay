package com.itsmcodez.vueplay.domain.usecase

import com.itsmcodez.vueplay.domain.repository.VideoFolderRepository
import javax.inject.Inject

class GetVideoFolders @Inject constructor(private val repository: VideoFolderRepository) {
    operator fun invoke() = repository.getVideoFolders()
}