package com.itsmcodez.vueplay.domain.usecase

import com.itsmcodez.vueplay.domain.repository.VideoRepository
import javax.inject.Inject

class LaunchVideos @Inject constructor(private val repository: VideoRepository) {
    operator fun invoke() = repository.launch()
}