package com.itsmcodez.vueplay.ui.component.grid

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.constant.contentRadius
import com.itsmcodez.vueplay.domain.model.VideoFolder

@Composable
fun VideoFolderGrid(
    modifier: Modifier = Modifier,
    videoFolders: List<VideoFolder>,
    state: LazyGridState,
    onMenuClick: (VideoFolder) -> Unit,
    onFolderClick: (VideoFolder) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(120.dp),
        state = state
    ) {
        items(videoFolders, key = { videoFolder -> videoFolder.name }) { videoFolder ->
            VideoFolderGridItem(
                modifier = Modifier.animateItem(),
                videoFolder = videoFolder,
                onMenuClick = onMenuClick,
                onFolderClick = onFolderClick
            )
        }

        item {
            Spacer(Modifier.height(120.dp))
        }
    }
}

@Composable
fun VideoFolderGridItem(
    modifier: Modifier = Modifier,
    videoFolder: VideoFolder,
    onMenuClick: (VideoFolder) -> Unit,
    onFolderClick: (VideoFolder) -> Unit
) {
    Box(
        modifier = modifier.clickable { onFolderClick(videoFolder) }
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
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f/3f)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .clip(RectangleShape),
                painter = painterResource(R.drawable.rounded_folder_24),
                contentDescription = videoFolder.name,
                contentScale = ContentScale.Inside,
                colorFilter = ColorFilter.tint(
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = videoFolder.name,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = pluralStringResource(
                            R.plurals.video_count,
                            videoFolder.videos.size,
                            videoFolder.videos.size
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                    )
                }
                IconButton(
                    modifier = Modifier.size(32.dp),
                    onClick = { onMenuClick(videoFolder) },
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
