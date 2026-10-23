package com.itsmcodez.vueplay.presentation.streaming.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.itsmcodez.vueplay.domain.model.MovieAndTvSeriesMulti
import com.itsmcodez.vueplay.domain.usecase.GetMovieDetails
import com.itsmcodez.vueplay.domain.usecase.GetRecommendedMovies
import com.itsmcodez.vueplay.domain.usecase.GetRecommendedTvSeries
import com.itsmcodez.vueplay.domain.usecase.GetSimilarMovies
import com.itsmcodez.vueplay.domain.usecase.GetSimilarTvSeries
import com.itsmcodez.vueplay.domain.usecase.GetTvSeriesDetails
import com.itsmcodez.vueplay.presentation.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieAndTvSeriesDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getMovieDetails: GetMovieDetails,
    getTvSeriesDetails: GetTvSeriesDetails,
    private val getSimilarMovies: GetSimilarMovies,
    private val getSimilarTvSeries: GetSimilarTvSeries,
    private val getRecommendedMovies: GetRecommendedMovies,
    private val getRecommendedTvSeries: GetRecommendedTvSeries
) : ViewModel() {
    private val details = savedStateHandle.toRoute< Destination.MovieAndTvSeriesDetailsScreen>()
    val tmdbId = details.tmdbId
    val isTvSeries = details.isTvSeries

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

    fun loadSimilarMovies(tmdbId: String, page: Int, onPagination: (total: Int) -> Unit) = getSimilarMovies(
        tmdbId,
        page,
        onPagination
    )

    fun loadSimilarTvSeries(tmdbId: String, page: Int, onPagination: (total: Int) -> Unit) = getSimilarTvSeries(
        tmdbId,
        page,
        onPagination
    )

    fun loadRecommendedMovies(tmdbId: String, page: Int, onPagination: (total: Int) -> Unit) = getRecommendedMovies(
        tmdbId,
        page,
        onPagination
    )

    fun loadRecommendedTvSeries(tmdbId: String, page: Int, onPagination: (total: Int) -> Unit) = getRecommendedTvSeries(
        tmdbId,
        page,
        onPagination
    )

    fun loadSimilarMoviesAndTvSeries(tmdbId: String, page: Int, onPagination: (total: Int) -> Unit) = if (details.isTvSeries) {
        loadSimilarTvSeries(
            tmdbId,
            page,
            onPagination
        )
    } else {
        loadSimilarMovies(
            tmdbId,
            page,
            onPagination
        )
    }

    fun loadRecommendedMoviesAndTvSeries(tmdbId: String, page: Int, onPagination: (total: Int) -> Unit) = if (details.isTvSeries) {
        loadRecommendedTvSeries(
            tmdbId,
            page,
            onPagination
        )
    } else {
        loadRecommendedMovies(
            tmdbId,
            page,
            onPagination
        )
    }

}