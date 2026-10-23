package com.itsmcodez.vueplay.presentation.streaming.download

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.itsmcodez.vueplay.domain.repository.MovieAndTvSeriesDownloadRepository
import com.itsmcodez.vueplay.presentation.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MovieAndTvSeriesDownloadViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MovieAndTvSeriesDownloadRepository
) : ViewModel() {
    val details = savedStateHandle.toRoute<Destination.MovieAndTvSeriesDownloadScreen>()
    val tmdbId: State<String> = mutableStateOf(details.tmdbId)
    val isTvSeries: State<Boolean> = mutableStateOf(details.isTvSeries)
    val season: State<Int> = mutableIntStateOf(details.season)
    val episode: State<Int> = mutableIntStateOf(details.episode)

    val downloads = repository.downloads
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun startDownload(url: String, fileName: String, userAgent: String?) = repository.startDownload(url, fileName, userAgent)
    fun pauseDownload(id: String) = repository.pauseDownload(id)
    fun resumeDownload(id: String, fileName: String, userAgent: String?) = repository.resumeDownload(id, fileName, userAgent)
    fun deleteDownload(id: String) = repository.deleteDownload(id)
}
