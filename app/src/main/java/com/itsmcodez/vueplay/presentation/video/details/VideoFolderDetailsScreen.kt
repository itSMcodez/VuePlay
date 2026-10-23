package com.itsmcodez.vueplay.presentation.video.details

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.common.MainTopBarMultiSelectionActions
import com.itsmcodez.vueplay.constant.PreferenceKeys
import com.itsmcodez.vueplay.domain.model.Video
import com.itsmcodez.vueplay.extension.rememberPreference
import com.itsmcodez.vueplay.ui.component.grid.VideoGrid
import com.itsmcodez.vueplay.ui.component.list.VideoList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoFolderDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: VideoFolderDetailsViewModel,
    selectedVideoIds: List<Long>,
    selectedVideoActions: List<MainTopBarMultiSelectionActions>,
    onSortClick: () -> Unit,
    onVideoClick: (Video) -> Unit,
    onVideoLongClick: (Video) -> Unit,
    onVideoMenuClick: (Video) -> Unit,
    onClearSelection: () -> Unit,
    onShareSelectedVideos: () -> Unit,
    onDeleteSelectedVideos: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val (sortOrder) = context.rememberPreference(PreferenceKeys.VIDEOS_SORT_ORDER_KEY, PreferenceKeys.SortOrder.ASCENDING.name)
    val (sortOption) = context.rememberPreference(PreferenceKeys.VIDEOS_SORT_OPTION_KEY, PreferenceKeys.SortOption.NAME.name)
    val (viewType, onViewTypeChanged) = context.rememberPreference(PreferenceKeys.VIEW_TYPE_KEY, PreferenceKeys.ViewType.GRID.name)
    val folderName = viewModel.folderName
    val path = viewModel.path
    val videos by viewModel.folderVideos.collectAsStateWithLifecycle()
    val resumableVideos by viewModel.resumableVideos.collectAsStateWithLifecycle()

    val lazyGridState = rememberLazyGridState()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(videos, sortOrder, sortOption) {
        if(viewType == PreferenceKeys.ViewType.GRID.name)
            lazyGridState.scrollToItem(0)
        else
            lazyListState.scrollToItem(0)
    }

    BackHandler(enabled = selectedVideoIds.isNotEmpty()) {
        onClearSelection()
    }

    if (selectedVideoIds.isEmpty())
        TopAppBar(
            title = { Text(folderName, overflow = TextOverflow.Ellipsis, maxLines = 1) },
            navigationIcon = {
                IconButton(
                    onClick = onBack
                ) { Icon(painter = painterResource(R.drawable.rounded_arrow_back_24), contentDescription = "Back") }
            },
            actions = {
                IconButton(onClick = onSortClick) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_filter_list_24),
                        contentDescription = null
                    )
                }
                IconButton(
                    onClick = {
                        onViewTypeChanged(
                            if(viewType == PreferenceKeys.ViewType.GRID.name) PreferenceKeys.ViewType.LIST.name
                            else PreferenceKeys.ViewType.GRID.name
                        )
                    }
                ) {
                    Icon(
                        painter = painterResource(
                            if(viewType == PreferenceKeys.ViewType.GRID.name) R.drawable.rounded_grid_view_24
                            else R.drawable.rounded_list_24
                        ),
                        contentDescription = viewType
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors().copy(
                actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
            )
        )

    else
        TopAppBar(
            title = {
                Text(text = selectedVideoIds.size.toString())
            },
            navigationIcon = {
                IconButton(onClick = onClearSelection) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_close_small_24),
                        contentDescription = null
                    )
                }
            },
            actions = {
                selectedVideoActions.fastForEach { action ->
                    IconButton(
                        onClick = when(action) {
                            MainTopBarMultiSelectionActions.SHARE -> onShareSelectedVideos
                            MainTopBarMultiSelectionActions.DELETE -> onDeleteSelectedVideos
                        }
                    ) {
                        Icon(
                            painter = painterResource(action.icon),
                            contentDescription = null
                        )
                    }
                }
            }
        )

    if(viewType == PreferenceKeys.ViewType.GRID.name)
        VideoGrid(
            modifier = modifier.windowInsetsPadding(
                WindowInsets(
                    top = TopAppBarDefaults.TopAppBarExpandedHeight
                )
            ),
            state = lazyGridState,
            videos = videos,
            resumableVideos = resumableVideos,
            selectedVideoIds = selectedVideoIds,
            onMenuClick = onVideoMenuClick,
            onVideoClick = onVideoClick,
            onVideoLongClick = onVideoLongClick
        )

    else
        VideoList(
            modifier = modifier.windowInsetsPadding(
                WindowInsets(
                    top = TopAppBarDefaults.TopAppBarExpandedHeight
                )
            ),
            state = lazyListState,
            videos = videos,
            resumableVideos = resumableVideos,
            selectedVideoIds = selectedVideoIds,
            onMenuClick = onVideoMenuClick,
            onVideoClick = onVideoClick,
            onVideoLongClick = onVideoLongClick
        )
}