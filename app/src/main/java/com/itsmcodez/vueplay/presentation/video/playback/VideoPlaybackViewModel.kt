package com.itsmcodez.vueplay.presentation.video.playback

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.itsmcodez.vueplay.data.local.entity.ResumableVideo
import com.itsmcodez.vueplay.domain.model.Video
import com.itsmcodez.vueplay.domain.usecase.wrapper.VideoUseCasesWrapper
import com.itsmcodez.vueplay.presentation.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlaybackViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val videoUseCasesWrapper: VideoUseCasesWrapper
) : ViewModel() {
    private val videoInfo = savedStateHandle.toRoute<Destination.VideoPlaybackScreen>()

    private val _externalVideoUri = MutableStateFlow(videoInfo.externalUri)
    val externalVideoUri = _externalVideoUri.asStateFlow()

    private val _currentVideo = MutableStateFlow<Video?>(null)
    val currentVideo = _currentVideo.asStateFlow()

    private val _resumableVideos = MutableStateFlow<List<ResumableVideo>>(emptyList())
    val resumableVideos = _resumableVideos.asStateFlow()

    private val _currentQueue = MutableStateFlow<List<Video>>(emptyList())
    val currentQueue = _currentQueue.asStateFlow()

    init {
        // Current Video
        viewModelScope.launch {
            videoUseCasesWrapper.getVideos()
                .map { it.find { video -> video.id == videoInfo.videoId } }
                .collect { video ->
                    _currentVideo.value = video
                }
        }

        // Video Queue
        viewModelScope.launch {
            videoUseCasesWrapper.getVideos()
                .collect { videos ->
                    _currentQueue.value = videos
                }
        }

        // Resumable Videos
        viewModelScope.launch {
            videoUseCasesWrapper.getAllResumableVideos()
                .collect { resumableVideos ->
                    _resumableVideos.value = resumableVideos
                }
        }
    }

    fun setCurrentVideo(video: Video) { _currentVideo.value = video }

    fun setExternalVideoUri(uri: String?) { _externalVideoUri.value = uri }

    fun clearResumableVideo(video: Video) {
        viewModelScope.launch {
            val resumableVideo = videoUseCasesWrapper.getResumableVideo(video.id)
            if (resumableVideo != null) videoUseCasesWrapper.deleteResumableVideo(resumableVideo)
        }
    }

    fun insertOrUpdateResumableVideo(video: Video, lastPosition: Long) {
        viewModelScope.launch {
            val resumableVideo = videoUseCasesWrapper.getResumableVideo(video.id)
            if(resumableVideo != null && resumableVideo.lastPosition > 0)
                videoUseCasesWrapper.updateResumableVideo(
                    resumableVideo.copy(lastPosition = lastPosition)
                )
            else
                videoUseCasesWrapper.insertResumableVideo(
                    ResumableVideo(videoId = video.id, lastPosition = lastPosition)
                )
        }
    }
}