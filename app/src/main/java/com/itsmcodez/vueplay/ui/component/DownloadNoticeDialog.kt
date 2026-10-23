package com.itsmcodez.vueplay.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.itsmcodez.vueplay.R

@Composable
fun DownloadNoticeDialog(
    modifier: Modifier = Modifier,
    onProceed: () -> Unit,
    onDismiss: () -> Unit,
) {
    var shouldProceed by remember { mutableStateOf(false) }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.title_dialog_download_notice))
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = stringResource(R.string.msg_dialog_download_notice))
                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable {
                            shouldProceed = !shouldProceed
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = shouldProceed,
                        onCheckedChange = { shouldProceed = it }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = stringResource(R.string.title_download_notice_checkbox_proceed))
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = shouldProceed,
                onClick = onProceed
            ) {
                Text(text = stringResource(R.string.proceed))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}