package com.itsmcodez.vueplay.domain.usecase

import com.itsmcodez.vueplay.data.local.entity.ResumableVideo
import com.itsmcodez.vueplay.domain.repository.VideoRepository
import javax.inject.Inject

class DeleteResumableVideo @Inject constructor(private val repository: VideoRepository) {
    suspend operator fun invoke(resumableVideo: ResumableVideo) = repository.deleteResumableVideo(resumableVideo)
}