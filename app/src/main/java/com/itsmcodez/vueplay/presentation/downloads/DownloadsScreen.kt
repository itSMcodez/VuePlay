package com.itsmcodez.vueplay.presentation.downloads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.data.local.entity.DownloadItem
import com.itsmcodez.vueplay.data.local.entity.DownloadStatus
import com.itsmcodez.vueplay.manager.VuePlayDownloadManager
import com.itsmcodez.vueplay.ui.component.list.DownloadList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(
    modifier: Modifier = Modifier,
    viewModel: DownloadsViewModel,
    onNavigateToVideoPlaybackScreen: (uriString: String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val downloads by viewModel.downloads.collectAsStateWithLifecycle()
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var target by remember { mutableStateOf<DownloadItem?>(null) }

    if (showDeleteConfirmationDialog && target != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            title = { Text(stringResource(R.string.delete)) },
            text = { Text("Are you sure you want to delete this file?") },
            confirmButton = {
                TextButton(onClick = {
                    target?.let { viewModel.deleteDownload(it.id) }
                    showDeleteConfirmationDialog = false
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmationDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    TopAppBar(
        title = { Text(text = stringResource(R.string.downloads)) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.rounded_arrow_back_24),
                    contentDescription = null
                )
            }
        }
    )

    Box(
        modifier = modifier.fillMaxSize().windowInsetsPadding(
            WindowInsets(
                top = TopAppBarDefaults.TopAppBarExpandedHeight
            )
        )
    ) {
        if(downloads.isNotEmpty())
            DownloadList(
            downloads = downloads,
            onResumeClick = { viewModel.resumeDownload(it.id, it.fileName, viewModel.lastUserAgentString) },
            onRetryClick = { viewModel.resumeDownload(it.id, it.fileName, viewModel.lastUserAgentString) },
            onPauseClick = { viewModel.pauseDownload(it.id) },
            onDeleteClick = {
                target = it
                showDeleteConfirmationDialog = true
            },
            onDownloadItemClick = {
                when(it.status) {
                    DownloadStatus.COMPLETED -> {
                        onNavigateToVideoPlaybackScreen(
                            VuePlayDownloadManager.getDownloadFile(
                                context,
                                it.fileName
                            ).absolutePath
                        )
                    }
                    DownloadStatus.DOWNLOADING -> { viewModel.pauseDownload(it.id) }
                    DownloadStatus.PAUSING,
                    DownloadStatus.PAUSED,
                    DownloadStatus.FAILED -> { viewModel.resumeDownload(it.id, it.fileName, viewModel.lastUserAgentString) }
                    else -> {}
                }
            }
        )

        else
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.no_downloads))
            }
    }

}