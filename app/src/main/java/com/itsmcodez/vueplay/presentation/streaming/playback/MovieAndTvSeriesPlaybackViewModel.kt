package com.itsmcodez.vueplay.presentation.streaming.playback

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.itsmcodez.vueplay.domain.model.MovieAndTvSeriesMulti
import com.itsmcodez.vueplay.domain.model.StreamingSource
import com.itsmcodez.vueplay.domain.repository.StreamingSourceRepository
import com.itsmcodez.vueplay.domain.usecase.GetMovieDetails
import com.itsmcodez.vueplay.domain.usecase.GetRecommendedMovies
import com.itsmcodez.vueplay.domain.usecase.GetRecommendedTvSeries
import com.itsmcodez.vueplay.domain.usecase.GetSimilarMovies
import com.itsmcodez.vueplay.domain.usecase.GetSimilarTvSeries
import com.itsmcodez.vueplay.domain.usecase.GetTvSeriesDetails
import com.itsmcodez.vueplay.presentation.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MovieAndTvSeriesPlaybackViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    streamingSourceRepository: StreamingSourceRepository,
    getMovieDetails: GetMovieDetails,
    getTvSeriesDetails: GetTvSeriesDetails,
    getSimilarMovies: GetSimilarMovies,
    getSimilarTvSeries: GetSimilarTvSeries,
    getRecommendedMovies: GetRecommendedMovies,
    getRecommendedTvSeries: GetRecommendedTvSeries
) : ViewModel() {
    private val details = savedStateHandle.toRoute<Destination.MovieAndTvSeriesPlaybackScreen>()
    val tmdbId = details.tmdbId
    val season = details.season
    val episode = details.episode
    val isTvSeries = details.isTvSeries

    private val _currentStreamingSource = MutableStateFlow<StreamingSource?>(null)
    val currentStreamingSource = _currentStreamingSource.asStateFlow()

    val sources: StateFlow<List<StreamingSource>> = streamingSourceRepository.getSources()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val currentMovieAndTvSeriesMulti = if (details.isTvSeries) {
        getTvSeriesDetails(tmdbId)
            .catch { e -> }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                null
            )
    } else {
        getMovieDetails(tmdbId)
            .catch { e ->  }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                null
            )
    }

    val similarMovieAndTvSeriesMultiList: StateFlow<List<MovieAndTvSeriesMulti>> = if (details.isTvSeries) {
        getSimilarTvSeries(
            tmdbId,
            1
        ) { total -> }
            .catch { e ->  }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
    } else {
        getSimilarMovies(
            tmdbId,
            1
        ) { total -> }
            .catch { e ->  }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
    }

    val recommendedMovieAndTvSeriesMultiList: StateFlow<List<MovieAndTvSeriesMulti>> = if (details.isTvSeries) {
        getRecommendedTvSeries(
            tmdbId,
            1
        ) { total -> }
            .catch { e ->  }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
    } else {
        getRecommendedMovies(
            tmdbId,
            1
        ) { total -> }
            .catch { e ->  }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
    }

    fun setCurrentStreamingSource(source: StreamingSource) { _currentStreamingSource.value = source }
}