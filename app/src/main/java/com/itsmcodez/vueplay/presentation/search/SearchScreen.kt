package com.itsmcodez.vueplay.presentation.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.domain.model.SearchResult
import com.itsmcodez.vueplay.domain.model.Video
import com.itsmcodez.vueplay.domain.model.VideoFolder
import com.itsmcodez.vueplay.ui.component.list.VideoFolderListItem
import com.itsmcodez.vueplay.ui.component.list.VideoListItem
import com.itsmcodez.vueplay.ui.component.vueui.VueSearchField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel,
    onFolderClick: (VideoFolder) -> Unit,
    onVideoFolderMenuClick: (VideoFolder) -> Unit,
    onVideoClick: (Video) -> Unit,
    onVideoMenuClick: (Video) -> Unit,
    onBack: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val searchFieldState = rememberSaveable(saver = TextFieldState.Saver) {
        TextFieldState(searchQuery)
    }
    val resumableVideos by viewModel.resumableVideos.collectAsStateWithLifecycle()

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    TopAppBar(
        title = {
            VueSearchField(
                Modifier.fillMaxWidth()
                    .focusRequester(focusRequester),
                state = searchFieldState,
                onSearch = {
                    viewModel.onSearchQueryChange(it.toString())
                },
                onSearchQueryChange = {
                    viewModel.onSearchQueryChange(it.toString())
                }
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.rounded_arrow_back_24),
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(
                onClick = { viewModel.onSearchQueryChange(""); searchFieldState.clearText() },
                colors = IconButtonDefaults.iconButtonColors().copy(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.rounded_close_small_24),
                    contentDescription = "Clear"
                )
            }
        }
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(
                WindowInsets(
                    top = TopAppBarDefaults.TopAppBarExpandedHeight
                )
            )
    ) {
        items(
            searchResults.size,
            key = { index ->
                // Avoid duplicate keys
                "$index ${searchResults[index].title} ${searchResults[index].origin}"
            }
        ) {
            when(val result = searchResults[it]) {
                is SearchResult.VideoItem -> VideoListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem(),
                    video = result.video,
                    resumableVideo = resumableVideos.find { resumableVideo ->  resumableVideo.videoId == result.video.id },
                    isSelected = false,
                    inSelectionMode = false,
                    onMenuClick = onVideoMenuClick,
                    onVideoClick = onVideoClick,
                    onVideoLongClick = {}
                )
                is SearchResult.VideoFolderItem -> VideoFolderListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem(),
                    videoFolder = result.videoFolder,
                    onMenuClick = onVideoFolderMenuClick,
                    onFolderClick = onFolderClick
                )
            }
        }

        item {
            Spacer(Modifier.height(120.dp))
        }
    }

    if(searchResults.isEmpty())
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                buildString {
                    append(stringResource(R.string.no_results_found))
                    if (searchQuery.isNotEmpty()) append(
                        " " +
                        stringResource(
                            R.string.for_search,
                            searchQuery
                        )
                    )
                    append("\n")
                    append(stringResource(R.string.no_search_results_rationale))
                },
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Thin,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
            )
        }
}
