package com.itsmcodez.vueplay.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.Player
import androidx.media3.common.text.Cue
import androidx.media3.common.util.UnstableApi
import com.itsmcodez.vueplay.domain.model.MediaTrackOption

@Composable
fun rememberSubtitleCuesState(player: Player): List<Cue> {
    var cues by remember { mutableStateOf<List<Cue>>(emptyList()) }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onCues(cueGroup: androidx.media3.common.text.CueGroup) {
                cues = cueGroup.cues
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }
    return cues
}

@androidx.annotation.OptIn(UnstableApi::class)
fun getTrackOptions(player: Player, trackType: Int): List<MediaTrackOption> {
    val options = mutableListOf<MediaTrackOption>()
    val currentTracks = player.currentTracks

    currentTracks.groups.forEachIndexed { groupIndex, trackGroup ->
        if (trackGroup.type == trackType) {
            for (trackIndex in 0 until trackGroup.length) {
                val format = trackGroup.getTrackFormat(trackIndex)
                val isSelected = trackGroup.isTrackSelected(trackIndex)

                // Fallback to track index string if language tag is missing
                val lang = format.language ?: "und"
                val label = when (lang) {
                    "und" -> "Track ${options.size + 1}"
                    else -> lang.uppercase()
                }

                options.add(
                    MediaTrackOption(
                        label = label,
                        language = lang,
                        groupIndex = groupIndex,
                        trackIndex = trackIndex,
                        isSelected = isSelected
                    )
                )
            }
        }
    }
    return options
}

fun selectSpecificTrack(player: Player, option: MediaTrackOption, trackType: Int) {
    val trackGroup = player.currentTracks.groups[option.groupIndex].mediaTrackGroup

    val newParams = player.trackSelectionParameters
        .buildUpon()
        .setTrackTypeDisabled(trackType, false)
        .setOverrideForType(androidx.media3.common.TrackSelectionOverride(trackGroup, option.trackIndex))
        .build()

    player.trackSelectionParameters = newParams
}

fun disableTrackType(player: Player, trackType: Int) {
    val newParams = player.trackSelectionParameters
        .buildUpon()
        .setTrackTypeDisabled(trackType, true)
        .build()
    player.trackSelectionParameters = newParams
}
