package com.itsmcodez.vueplay.domain.usecase

import com.itsmcodez.vueplay.domain.model.Video
import com.itsmcodez.vueplay.domain.repository.VideoRepository
import javax.inject.Inject

class ShareVideos @Inject constructor(private val repository: VideoRepository) {
    operator fun invoke(vararg videos: Video) = repository.shareVideos(*videos)
}