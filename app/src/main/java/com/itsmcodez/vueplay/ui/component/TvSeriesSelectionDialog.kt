package com.itsmcodez.vueplay.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.itsmcodez.themoviedbapi.model.tv.core.TvSeason
import com.itsmcodez.vueplay.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvSeriesSelectionDialog(
    modifier: Modifier = Modifier,
    seasons: List<TvSeason>,
    onSeasonSelected: (season: TvSeason) -> Unit,
    onEpisodeSelected: (season: TvSeason, episode: Int) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    var seasonDropdownExpanded by remember { mutableStateOf(false) }
    var episodeDropdownExpanded by remember { mutableStateOf(false) }
    var currentSeason by remember { mutableStateOf(seasons.first()) }
    var currentSeasonEpisode by remember(currentSeason) { mutableIntStateOf(1) }

    LaunchedEffect(Unit) { onEpisodeSelected(currentSeason, currentSeasonEpisode) }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.tv_series)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = stringResource(R.string.msg_dialog_tv_series_selection))

                ExposedDropdownMenuBox(
                    expanded = seasonDropdownExpanded,
                    onExpandedChange = { seasonDropdownExpanded = it },
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        value = "Season ${currentSeason.seasonNumber}",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.select_season)) },
                        placeholder = { Text(stringResource(R.string.select_season)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = seasonDropdownExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    )

                    ExposedDropdownMenu(
                        expanded = seasonDropdownExpanded,
                        onDismissRequest = { seasonDropdownExpanded = false },
                    ) {
                        seasons.forEach { season ->
                            DropdownMenuItem(
                                text = { Text("Season ${season.seasonNumber}") },
                                onClick = {
                                    currentSeason = season
                                    onSeasonSelected(season)
                                    seasonDropdownExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = episodeDropdownExpanded,
                    onExpandedChange = { episodeDropdownExpanded = it },
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        value = "Episode $currentSeasonEpisode",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.select_episode)) },
                        placeholder = { Text(stringResource(R.string.select_episode)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = episodeDropdownExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    )

                    ExposedDropdownMenu(
                        expanded = episodeDropdownExpanded,
                        onDismissRequest = { episodeDropdownExpanded = false },
                    ) {
                        for (episode in 1..currentSeason.episodeCount) {
                            DropdownMenuItem(
                                text = { Text("Episode $episode") },
                                onClick = {
                                    currentSeasonEpisode = episode
                                    onEpisodeSelected(currentSeason, episode)
                                    episodeDropdownExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }

            }
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text(text = stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}

