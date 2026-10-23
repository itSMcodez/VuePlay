package com.itsmcodez.vueplay.ui.component

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.data.local.entity.ResumableVideo
import com.itsmcodez.vueplay.domain.model.Video
import com.itsmcodez.vueplay.ui.component.list.VideoList

@Composable
fun VideoQueueDialog(
    modifier: Modifier = Modifier,
    queue: List<Video>,
    resumableVideos: List<ResumableVideo>,
    currentVideoIndex: Int,
    state: LazyListState,
    onVideoMenuClick: (Video) -> Unit,
    onVideoClick: (Video) -> Unit,
    onDismiss: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val cornerRadius = 16.dp
    var isDismissing by remember { mutableStateOf(false) }

    LaunchedEffect(state) { if(currentVideoIndex != -1) state.scrollToItem(currentVideoIndex) }

    Dialog(
        onDismissRequest = {
            isDismissing = true
            onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = modifier.clickable { isDismissing = true; onDismiss() }
                .systemBarsPadding(),
            contentAlignment = if(isLandscape) Alignment.CenterEnd else Alignment.BottomCenter
        ) {
            AnimatedVisibility(
                visible = !isDismissing,
                enter = slideInVertically(tween(800)) ,
                exit = slideOutVertically(tween(800))
            ) {
                Surface(
                    modifier = Modifier
                        .then(
                            if (isLandscape) Modifier
                                .fillMaxHeight()
                                .width(360.dp)
                            else Modifier
                                .fillMaxWidth()
                                .height(480.dp)
                        ),
                    shape = if (isLandscape) RoundedCornerShape(topStart = cornerRadius, bottomStart = cornerRadius)
                    else RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = stringResource(R.string.title_video_queue_dialog),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                            )
                            IconButton(onClick = { onDismiss() }) {
                                Icon(
                                    painter = painterResource(R.drawable.rounded_close_small_24),
                                    contentDescription = "Close"
                                )
                            }
                        }
                        VideoList(
                            modifier = Modifier.fillMaxSize(),
                            videos = queue,
                            resumableVideos = resumableVideos,
                            selectedVideoIds = emptyList(),
                            state = state,
                            onMenuClick = onVideoMenuClick,
                            onVideoClick = onVideoClick,
                            onVideoLongClick = {}
                        )
                    }
                }
            }
        }
    }
}