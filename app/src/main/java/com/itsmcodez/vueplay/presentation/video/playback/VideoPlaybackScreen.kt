package com.itsmcodez.vueplay.presentation.video.playback

import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itsmcodez.vueplay.domain.model.Video
import com.itsmcodez.vueplay.ui.component.player.VideoPlayer

@Composable
fun VideoPlaybackScreen(
    modifier: Modifier = Modifier,
    viewModel: VideoPlaybackViewModel,
    onQueueVideoMenuClick: (Video) -> Unit,
    onToggleSystemBars: (show: Boolean) -> Unit,
    onToggleLightSystemBars: (useDarkIcons: Boolean) -> Unit,
    onEnterPipMode: () -> Unit,
    onShowSnackbar: (msg: String, action: String?, result: (SnackbarResult) -> Unit) -> Unit,
    onBack: () -> Unit
) {
    val queue by viewModel.currentQueue.collectAsStateWithLifecycle()
    val resumableVideos by viewModel.resumableVideos.collectAsStateWithLifecycle()
    val currentVideo by viewModel.currentVideo.collectAsStateWithLifecycle()
    val externalVideoUri by viewModel.externalVideoUri.collectAsStateWithLifecycle()
    val currentVideoIndex = remember(currentVideo, queue) { queue.indexOf(currentVideo) }

    if((currentVideo == null && externalVideoUri.isNullOrBlank()) || queue.isEmpty()) return

    VideoPlayer(
        modifier = modifier,
        currentVideo = currentVideo,
        externalVideoUri = externalVideoUri,
        currentQueue = queue,
        currentResumableVideos = resumableVideos,
        currentVideoIndex = currentVideoIndex,
        onQueueVideoMenuClick = onQueueVideoMenuClick,
        onExternalUriHandled = { uri -> viewModel.setExternalVideoUri(null) },
        onSetCurrentVideo = { selectedVideo -> viewModel.setCurrentVideo(selectedVideo) },
        onClearResumableVideo = { video ->
            viewModel.clearResumableVideo(video)
        },
        onInsertOrUpdateResumableVideo = { video, lastPosition ->
            viewModel.insertOrUpdateResumableVideo(video, lastPosition)
        },
        onToggleSystemBars = onToggleSystemBars,
        onToggleLightSystemBars = onToggleLightSystemBars,
        onShowSnackbar = onShowSnackbar,
        onEnterPipMode = onEnterPipMode,
        onBack = onBack
    )
}
