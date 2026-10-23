package com.itsmcodez.vueplay.ui.component.list

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
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
fun VideoFolderList(
    modifier: Modifier = Modifier,
    videoFolders: List<VideoFolder>,
    state: LazyListState,
    onMenuClick: (VideoFolder) -> Unit,
    onFolderClick: (VideoFolder) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        state = state
    ) {
        items(videoFolders, key = { videoFolder -> videoFolder.name }) { videoFolder ->
            VideoFolderListItem(
                modifier = Modifier.fillMaxWidth().animateItem(),
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
fun VideoFolderListItem(
    modifier: Modifier = Modifier,
    videoFolder: VideoFolder,
    onMenuClick: (VideoFolder) -> Unit,
    onFolderClick: (VideoFolder) -> Unit
) {
    Box(
        modifier = modifier.clickable { onFolderClick(videoFolder) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(8.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(0.7f),
                    shape = RoundedCornerShape(contentRadius)
                )
                .clip(RoundedCornerShape(contentRadius)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Image(
                modifier = Modifier
                    .width(100.dp)
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

            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
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
