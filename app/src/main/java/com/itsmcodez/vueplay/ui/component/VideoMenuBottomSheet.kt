package com.itsmcodez.vueplay.ui.component

import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.domain.model.Video
import com.itsmcodez.vueplay.presentation.video.browser.VideoBrowserViewModel
import com.itsmcodez.vueplay.ui.component.vueui.VueBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoMenuBottomSheet(
    modifier: Modifier = Modifier,
    viewModel: VideoBrowserViewModel,
    isVideoFolderTarget: Boolean = false,
    renameVideoTarget: Video? = null,
    shareVideosTarget: Array<Video> = emptyArray(),
    deleteVideosTarget: Array<Video> = emptyArray(),
    renameVideoLauncher: ActivityResultLauncher<IntentSenderRequest>,
    deleteVideosLauncher: ActivityResultLauncher<IntentSenderRequest>,
    onRenameNameChange: (String) -> Unit,
    onDismissRequest: (SheetState?) -> Unit
) {
    if(
        (!isVideoFolderTarget && renameVideoTarget == null) ||
        shareVideosTarget.isEmpty() ||
        deleteVideosTarget.isEmpty()
    ) return

    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val renameAction = stringResource(R.string.rename)
    val shareAction = stringResource(R.string.share)
    val deleteAction = stringResource(R.string.delete)
    val actions: List<Pair<String, Int>> = buildList {
        if (!isVideoFolderTarget)
            add(renameAction to R.drawable.rounded_edit_24)

        add(shareAction to R.drawable.rounded_share_24)

        add(deleteAction to R.drawable.rounded_delete_24)
    }

    VueBottomSheet(
        modifier = modifier,
        onDismiss = onDismissRequest
    ) {

        // Video or Folder Metadata
        if (!isVideoFolderTarget)
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var thumbnailFailed by remember { mutableStateOf(false) }
                AsyncImage(
                    modifier = Modifier.size(48.dp)
                        .aspectRatio(16f/9f)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .clip(RectangleShape),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(renameVideoTarget?.uri)
                        .build(),
                    contentDescription = renameVideoTarget?.displayName,
                    contentScale = if(thumbnailFailed) ContentScale.Inside else ContentScale.Crop,
                    placeholder = painterResource(R.drawable.rounded_movie_24),
                    error = painterResource(R.drawable.rounded_movie_24),
                    fallback = painterResource(R.drawable.rounded_movie_24),
                    onError = { thumbnailFailed = true },
                    onLoading = { thumbnailFailed = true },
                    onSuccess = { thumbnailFailed = false },
                    colorFilter = if(thumbnailFailed) ColorFilter.tint(
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                    ) else null
                )

                Text(
                    text = renameVideoTarget?.displayName ?: "<unknown>",
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                )
            }
        else
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val folderName = shareVideosTarget[0].folderName
                Image(
                    modifier = Modifier
                        .size(48.dp)
                        .aspectRatio(4f/3f)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .clip(RectangleShape),
                    painter = painterResource(R.drawable.rounded_folder_24),
                    contentDescription = folderName,
                    contentScale = ContentScale.Inside,
                    colorFilter = ColorFilter.tint(
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                    )
                )

                Text(
                    text = folderName,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                )
            }


        // Actions
        actions.fastForEach { (action, icon) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        when (action) {
                            renameAction -> showRenameDialog = true

                            shareAction -> viewModel.shareVideos(
                                *shareVideosTarget
                            )

                            deleteAction -> showDeleteDialog = true
                        }
                    }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null
                    )

                    Text(
                        text = action,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    if(showRenameDialog && renameVideoTarget != null) {
        var value by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text(text = renameAction) },
            text = {
                Column {
                    Text(
                        text = stringResource(
                            R.string.msg_rename_video_dialog,
                            renameVideoTarget.displayName
                        ),
                        maxLines = 3,
                        overflow = TextOverflow.MiddleEllipsis
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = value,
                        onValueChange = {
                            value = it
                            onRenameNameChange(it.trim())
                        },
                        label = { Text(text = stringResource(R.string.placeholder_rename_file)) },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.renameVideo(renameVideoTarget, value.trim(), renameVideoLauncher)
                        showRenameDialog = false
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                            // On old versions no dialog is shown, we can dismiss
                            onDismissRequest(null)
                        }
                    }
                ) {
                    Text(text = renameAction)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false } ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    if(showDeleteDialog && deleteVideosTarget.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = deleteAction) },
            text = {
                Text(
                    text = pluralStringResource(
                        R.plurals.msg_delete_videos,
                        deleteVideosTarget.size,
                        deleteVideosTarget.size
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteVideos(
                            *deleteVideosTarget,
                            launcher = deleteVideosLauncher
                        )
                        showDeleteDialog = false
                        onDismissRequest(null)
                    }
                ) {
                    Text(text = deleteAction)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false } ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
}