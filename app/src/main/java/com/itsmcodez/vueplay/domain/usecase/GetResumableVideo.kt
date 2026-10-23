package com.itsmcodez.vueplay.domain.usecase

import com.itsmcodez.vueplay.domain.repository.VideoRepository
import javax.inject.Inject

class GetResumableVideo @Inject constructor(private val repository: VideoRepository) {
    suspend operator fun invoke(videoId: Long) = repository.getResumableVideo(videoId)
}