package com.itsmcodez.vueplay.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.C
import androidx.media3.common.Player
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.ui.state.disableTrackType
import com.itsmcodez.vueplay.ui.state.getTrackOptions
import com.itsmcodez.vueplay.ui.state.selectSpecificTrack

@Composable
fun TrackSelectionDialog(player: Player, onDismiss: () -> Unit) {
    val subtitleOptions = getTrackOptions(player, C.TRACK_TYPE_TEXT)
    val audioOptions = getTrackOptions(player, C.TRACK_TYPE_AUDIO)
    val isSubtitleDisabled = player.trackSelectionParameters.disabledTrackTypes.contains(C.TRACK_TYPE_TEXT)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.video_settings)) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                // Audio Selection Header
                Text(stringResource(R.string.audio_tracks), style = MaterialTheme.typography.titleMedium)
                audioOptions.forEach { option ->
                    RadioButtonRow(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { selectSpecificTrack(player, option, C.TRACK_TYPE_AUDIO) },
                        selected = option.isSelected,
                        label = option.label
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Subtitle Selection Header
                Text(stringResource(R.string.subtitles), style = MaterialTheme.typography.titleMedium)

                // Off option
                RadioButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { disableTrackType(player, C.TRACK_TYPE_TEXT) },
                    selected = isSubtitleDisabled,
                    label = stringResource(R.string.off)
                )

                subtitleOptions.forEach { option ->
                    RadioButtonRow(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { selectSpecificTrack(player, option, C.TRACK_TYPE_TEXT) },
                        selected = (!isSubtitleDisabled && option.isSelected),
                        label = option.label
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.dismiss)) }
        }
    )
}