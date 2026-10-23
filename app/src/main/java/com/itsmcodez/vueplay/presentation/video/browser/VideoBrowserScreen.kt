package com.itsmcodez.vueplay.presentation.video.browser

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itsmcodez.vueplay.constant.PreferenceKeys
import com.itsmcodez.vueplay.domain.model.Video
import com.itsmcodez.vueplay.extension.rememberPreference
import com.itsmcodez.vueplay.ui.component.grid.VideoGrid
import com.itsmcodez.vueplay.ui.component.list.VideoList

@Composable
fun VideoBrowserScreen(
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState,
    lazyListState: LazyListState,
    viewModel: VideoBrowserViewModel,
    onMenuClick: (Video) -> Unit,
    onVideoClick: (Video) -> Unit
) {
    val context = LocalContext.current
    val videos by viewModel.videos.collectAsStateWithLifecycle()
    val resumableVideos by viewModel.resumableVideos.collectAsStateWithLifecycle()
    val selectedVideoIds by viewModel.selectedVideoIds.collectAsStateWithLifecycle()
    val (viewType) = context.rememberPreference(PreferenceKeys.VIEW_TYPE_KEY, PreferenceKeys.ViewType.GRID.name)

    if(viewType == PreferenceKeys.ViewType.GRID.name)
        VideoGrid(
            modifier = modifier,
            state = lazyGridState,
            videos = videos,
            resumableVideos = resumableVideos,
            selectedVideoIds = selectedVideoIds.toList(),
            onMenuClick = onMenuClick,
            onVideoClick = onVideoClick,
            onVideoLongClick = { viewModel.toggleSelection(it) }
        )

    else
        VideoList(
            modifier = modifier,
            state = lazyListState,
            videos = videos,
            resumableVideos = resumableVideos,
            selectedVideoIds = selectedVideoIds.toList(),
            onMenuClick = onMenuClick,
            onVideoClick = onVideoClick,
            onVideoLongClick = { viewModel.toggleSelection(it) }
        )
}