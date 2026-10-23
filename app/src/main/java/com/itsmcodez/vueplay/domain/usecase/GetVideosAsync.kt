package com.itsmcodez.vueplay.domain.usecase

import com.itsmcodez.vueplay.domain.repository.VideoRepository
import javax.inject.Inject

class GetVideosAsync @Inject constructor(private val repository: VideoRepository) {
    suspend operator fun invoke() = repository.getVideosAsync()
}