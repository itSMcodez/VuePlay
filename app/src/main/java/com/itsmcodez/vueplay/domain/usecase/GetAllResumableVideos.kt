package com.itsmcodez.vueplay.domain.usecase

import com.itsmcodez.vueplay.domain.repository.VideoRepository
import javax.inject.Inject

class GetAllResumableVideos @Inject constructor(private val videoRepository: VideoRepository) {
    operator fun invoke() = videoRepository.getAllResumableVideos()
}