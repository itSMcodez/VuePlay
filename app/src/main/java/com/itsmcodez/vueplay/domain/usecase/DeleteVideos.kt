package com.itsmcodez.vueplay.domain.usecase

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.itsmcodez.vueplay.domain.model.Video
import com.itsmcodez.vueplay.domain.repository.VideoRepository
import javax.inject.Inject

class DeleteVideos @Inject constructor(private val repository: VideoRepository) {
    operator fun invoke(
        vararg videos: Video,
        launcher: ActivityResultLauncher<IntentSenderRequest>
    ) = repository.deleteVideos(launcher = launcher, videos = videos)
}