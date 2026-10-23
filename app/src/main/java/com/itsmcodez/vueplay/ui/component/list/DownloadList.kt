package com.itsmcodez.vueplay.ui.component.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.data.local.entity.DownloadStatus
import com.itsmcodez.vueplay.util.FileUtil
import com.itsmcodez.vueplay.data.local.entity.DownloadItem as Download

@Composable
fun DownloadList(
    modifier: Modifier = Modifier,
    downloads: List<Download>,
    onResumeClick: (Download) -> Unit,
    onPauseClick: (Download) -> Unit,
    onRetryClick: (Download) -> Unit,
    onDeleteClick: (Download) -> Unit,
    onDownloadItemClick: (Download) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        itemsIndexed(
            downloads,
            key = { _, download -> download.id }
        ) { index, download ->
            Column {
                DownloadItem(
                    modifier = Modifier.animateItem(),
                    download = download,
                    onResumeClick = onResumeClick,
                    onPauseClick = onPauseClick,
                    onRetryClick = onRetryClick,
                    onDeleteClick = onDeleteClick,
                    onClick = onDownloadItemClick
                )
                if (index != downloads.lastIndex) {
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
fun DownloadItem(
    modifier: Modifier = Modifier,
    download: Download,
    onResumeClick: (Download) -> Unit,
    onPauseClick: (Download) -> Unit,
    onRetryClick: (Download) -> Unit,
    onDeleteClick: (Download) -> Unit,
    onClick: (Download) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(download) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // TODO: replace with thumbnail/artwork
                Icon(
                    painter = painterResource(
                        when(download.status) {
                            DownloadStatus.PAUSING,
                            DownloadStatus.PAUSED,
                            DownloadStatus.DOWNLOADING -> R.drawable.rounded_downloading_24
                            DownloadStatus.COMPLETED -> R.drawable.rounded_download_done_24
                            DownloadStatus.FAILED -> R.drawable.rounded_error_24
                            else -> R.drawable.rounded_download_24
                        }
                    ),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = download.fileName,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = when(download.status) {
                            DownloadStatus.PAUSING,
                            DownloadStatus.PAUSED,
                            DownloadStatus.DOWNLOADING -> "${
                                FileUtil.formatSizeMb(
                                    LocalLocale.current.platformLocale,
                                    download.bytesDownloaded.toFloat()
                                )
                            }/${
                                FileUtil.formatSizeMb(
                                    LocalLocale.current.platformLocale,
                                    download.totalBytes.toFloat()
                                )
                            }    (${
                                String.format(
                                    LocalLocale.current.platformLocale,
                                    "%.2f%%",
                                    download.progress * 100
                                )
                            })".trimIndent()
                            DownloadStatus.COMPLETED -> FileUtil.formatSizeMb(
                                LocalLocale.current.platformLocale,
                                download.bytesDownloaded.toFloat()
                            )
                            DownloadStatus.FAILED -> stringResource(R.string.failed)
                            else -> ""
                        },
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                    )
                }

                when(download.status) {
                    DownloadStatus.FAILED,
                    DownloadStatus.PAUSING,
                    DownloadStatus.PAUSED,
                    DownloadStatus.DOWNLOADING -> {
                        IconButton(
                            onClick = {
                                when(download.status) {
                                    DownloadStatus.PAUSING,
                                    DownloadStatus.PAUSED -> onResumeClick(download)
                                    DownloadStatus.DOWNLOADING -> onPauseClick(download)
                                    DownloadStatus.FAILED -> onRetryClick(download)
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(
                                    when(download.status) {
                                        DownloadStatus.PAUSING,
                                        DownloadStatus.PAUSED -> R.drawable.round_play_circle_24
                                        DownloadStatus.DOWNLOADING -> R.drawable.round_pause_circle_24
                                        DownloadStatus.FAILED -> R.drawable.rounded_refresh_24
                                    }
                                ),
                                contentDescription = null
                            )
                        }
                    }
                    else -> {}
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when(download.status) {
                    DownloadStatus.PAUSING,
                    DownloadStatus.PAUSED,
                    DownloadStatus.DOWNLOADING -> {
                        LinearProgressIndicator(
                            modifier = Modifier.weight(1f),
                            progress = { download.progress }
                        )
                    }
                    else -> {}
                }
                when(download.status) {
                    DownloadStatus.FAILED,
                    DownloadStatus.PAUSING,
                    DownloadStatus.PAUSED,
                    DownloadStatus.DOWNLOADING,
                    DownloadStatus.COMPLETED -> {
                        TextButton(onClick = { onDeleteClick(download) }) {
                            Text(
                                text = stringResource(
                                    when(download.status) {
                                        DownloadStatus.COMPLETED -> R.string.delete
                                        else -> R.string.cancel
                                    }
                                )
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}
