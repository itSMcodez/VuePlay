package com.itsmcodez.vueplay.presentation.video.browser

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itsmcodez.vueplay.constant.PreferenceKeys
import com.itsmcodez.vueplay.domain.model.VideoFolder
import com.itsmcodez.vueplay.extension.rememberPreference
import com.itsmcodez.vueplay.ui.component.grid.VideoFolderGrid
import com.itsmcodez.vueplay.ui.component.list.VideoFolderList

@Composable
fun VideoFolderBrowserScreen(
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState,
    lazyListState: LazyListState,
    viewModel: VideoFolderBrowserViewModel,
    onMenuClick: (VideoFolder) -> Unit,
    onFolderClick: (VideoFolder) -> Unit
) {
    val context = LocalContext.current
    val videoFolders by viewModel.videoFolders.collectAsStateWithLifecycle()
    val (viewType) = context.rememberPreference(PreferenceKeys.VIEW_TYPE_KEY, PreferenceKeys.ViewType.GRID.name)

    if (viewType == PreferenceKeys.ViewType.GRID.name)
        VideoFolderGrid(
            modifier = modifier,
            videoFolders = videoFolders,
            state = lazyGridState,
            onMenuClick = onMenuClick,
            onFolderClick = onFolderClick
        )

    else
        VideoFolderList(
            modifier = modifier,
            videoFolders = videoFolders,
            state = lazyListState,
            onMenuClick = onMenuClick,
            onFolderClick = onFolderClick
        )
}