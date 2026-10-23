package com.itsmcodez.vueplay.ui.component.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.constant.contentRadius
import com.itsmcodez.vueplay.data.local.entity.ResumableVideo
import com.itsmcodez.vueplay.domain.model.Video
import com.itsmcodez.vueplay.util.TimeFormatter

@Composable
fun VideoGrid(
    modifier: Modifier = Modifier,
    videos: List<Video>,
    selectedVideoIds: List<Long>,
    resumableVideos: List<ResumableVideo>,
    state: LazyGridState,
    onMenuClick: (Video) -> Unit,
    onVideoClick: (Video) -> Unit,
    onVideoLongClick: (Video) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        state = state,
        columns = GridCells.Adaptive(160.dp)
    ) {
        items(videos, key = {video -> video.id }) { video ->
            VideoGridItem(
                modifier = Modifier.animateItem(),
                video = video,
                resumableVideo = resumableVideos.find { it.videoId == video.id },
                isSelected = selectedVideoIds.any { it == video.id },
                inSelectionMode = selectedVideoIds.isNotEmpty(),
                onMenuClick = onMenuClick,
                onVideoClick = onVideoClick,
                onVideoLongClick = onVideoLongClick
            )
        }

        item {
            Spacer(Modifier.height(120.dp))
        }
    }
}

@Composable
fun VideoGridItem(
    modifier: Modifier = Modifier,
    video: Video,
    resumableVideo: ResumableVideo?,
    isSelected: Boolean,
    inSelectionMode: Boolean,
    onMenuClick: (Video) -> Unit,
    onVideoClick: (Video) -> Unit,
    onVideoLongClick: (Video) -> Unit
) {
    val context = LocalContext.current
    Box(
        modifier = modifier.combinedClickable(
            onLongClick = { onVideoLongClick(video) },
        ) { onVideoClick(video) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(0.7f),
                    shape = RoundedCornerShape(contentRadius)
                )
                .clip(RoundedCornerShape(contentRadius)),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            var thumbnailFailed by remember { mutableStateOf(false) }
            Box {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f/9f)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .clip(RectangleShape),
                    model = ImageRequest.Builder(context)
                        .data(video.uri)
                        .build(),
                    contentDescription = video.displayName,
                    contentScale = if(thumbnailFailed) ContentScale.Inside else ContentScale.Crop,
                    error = painterResource(R.drawable.rounded_movie_24),
                    fallback = painterResource(R.drawable.rounded_movie_24),
                    placeholder = painterResource(R.drawable.rounded_movie_24),
                    onError = { thumbnailFailed = true },
                    onLoading = { thumbnailFailed = true },
                    onSuccess = { thumbnailFailed = false },
                    colorFilter = if(thumbnailFailed) ColorFilter.tint(
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                    ) else null
                )
                if(resumableVideo != null && resumableVideo.lastPosition > 0) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(6.dp).align(Alignment.BottomCenter),
                        progress = { resumableVideo.lastPosition.toFloat() / video.duration.toFloat() }
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = video.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = """
                            ${
                                if (resumableVideo != null && resumableVideo.lastPosition > 0)
                                    TimeFormatter.formatTimeMilliseconds(resumableVideo.lastPosition, LocalLocale.current.platformLocale) + " | " else ""
                                }
                            ${ TimeFormatter.formatTimeMilliseconds(video.duration, LocalLocale.current.platformLocale) }
                        """.trimIndent().replace("\n", ""),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                    )
                }
                if(inSelectionMode) {
                    var isChecked by remember(isSelected) { mutableStateOf(isSelected) }
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            onVideoLongClick(video)
                            isChecked = it
                        }
                    )
                } else {
                    IconButton(
                        modifier = Modifier.size(32.dp),
                        onClick = { onMenuClick(video) },
                        colors = IconButtonDefaults.iconButtonColors().copy(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_more_vert_24),
                            contentDescription = "Menu"
                        )
                    }
                }
            }
        }
    }
}
