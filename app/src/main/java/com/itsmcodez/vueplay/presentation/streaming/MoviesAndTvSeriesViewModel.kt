package com.itsmcodez.vueplay.presentation.streaming

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsmcodez.vueplay.domain.repository.MovieAndTvSeriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MoviesAndTvSeriesViewModel @Inject constructor(
    private val repository: MovieAndTvSeriesRepository
) : ViewModel() {
    private val refreshTrigger = MutableSharedFlow<Unit>()

    private fun <T> getData(source: Flow<List<T>>): StateFlow<List<T>> = refreshTrigger
        .onStart { emit(Unit) }
        .flatMapLatest { source }
        .catch { notifyOnFailureCallbacks(it as? Exception ?: Exception(it)) }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            emptyList()
        )

    val trending = getData(repository.getTrending())
    val topRatedMovies = getData(repository.getTopRatedMovies())
    val popularMovies = getData(repository.getPopularMovies())
    val popularTvSeries = getData(repository.getPopularTvSeries())
    val nowPlayingMovies = getData(repository.getNowPlayingMovies())
    val topRatedTvSeries = getData(repository.getTopRatedTvSeries())
    val airingTodayTvSeries = getData(repository.getAiringTodayTvSeries())
    val upcomingMovies = getData(repository.getUpcomingMovies())
    val latestMovie = getData(repository.getLatestMovie().map { listOf(it) })
    val latestTvSeries = getData(repository.getLatestTvSeries().map { listOf(it) })
    val discoverActionMovies = getData(repository.getDiscoverActionMovies())
    val discoverComedyMovies = getData(repository.getDiscoverComedyMovies())
    val discoverThrillerMovies = getData(repository.getDiscoverThrillerMovies())
    val discoverSciFiMovies = getData(repository.getDiscoverSciFiMovies())
    val discoverHorrorMovies = getData(repository.getDiscoverHorrorMovies())
    val discoverDramaTvSeries = getData(repository.getDiscoverDramaTvSeries())
    val discoverAnimeTvSeries = getData(repository.getDiscoverAnimeTvSeries())
    val discoverKoreanDramaTvSeries = getData(repository.getDiscoverKoreanDramaTvSeries())
    val discoverSpanishLanguageMovies = getData(repository.getDiscoverSpanishLanguageMovies())
    val discoverBollywoodMovies = getData(repository.getDiscoverBollywoodMovies())
    val discoverFrenchCinemaMovies = getData(repository.getDiscoverFrenchCinemaMovies())
    val discoverDocumentaryMovies = getData(repository.getDiscoverDocumentaryMovies())
    val discoverAnimationMovies = getData(repository.getDiscoverAnimationMovies())
    val discoverCrimeTvSeries = getData(repository.getDiscoverCrimeTvSeries())
    val discoverRomanceMovies = getData(repository.getDiscoverRomanceMovies())
    val discoverBestOf2010Movies = getData(repository.getDiscoverBestOf2010Movies())
    val discoverBestOf2000Movies = getData(repository.getDiscoverBestOf2000Movies())
    val discoverClassicMovies = getData(repository.getDiscoverClassicMovies())
    val discoverAdventureMovies = getData(repository.getDiscoverAdventureMovies())
    val discoverFantasyMovies = getData(repository.getDiscoverFantasyMovies())
    val currentlyOnAirTvSeries = getData(repository.getCurrentlyOnAirTvSeries())

    private val onFailureCallbacks = mutableListOf<(Exception) -> Unit>()
    fun registerOnFailureCallback(callback: (Exception) -> Unit) { onFailureCallbacks.add(callback) }
    fun unregisterOnFailureCallback(callback: (Exception) -> Unit) { onFailureCallbacks.remove(callback) }
    fun notifyOnFailureCallbacks(exception: Exception) { onFailureCallbacks.forEach { it(exception) } }

    fun refresh(onRefreshed: () -> Unit) {
        viewModelScope.launch {
            refreshTrigger.emit(Unit)
            onRefreshed()
        }
    }

    fun loadTrending(page: Int, onPagination: (total: Int) -> Unit) = repository.getTrending(page, onPagination)
    fun loadTopRatedMovies(page: Int, onPagination: (total: Int) -> Unit) = repository.getTopRatedMovies(page, onPagination)
    fun loadPopularMovies(page: Int, onPagination: (total: Int) -> Unit) = repository.getPopularMovies(page, onPagination)
    fun loadPopularTvSeries(page: Int, onPagination: (total: Int) -> Unit) = repository.getPopularTvSeries(page, onPagination)
    fun loadNowPlayingMovies(page: Int, onPagination: (total: Int) -> Unit) = repository.getNowPlayingMovies(page, onPagination)
    fun loadTopRatedTvSeries(page: Int, onPagination: (total: Int) -> Unit) = repository.getTopRatedTvSeries(page, onPagination)
    fun loadAiringTodayTvSeries(page: Int, onPagination: (total: Int) -> Unit) = repository.getAiringTodayTvSeries(page, onPagination)
    fun loadUpcomingMovies(page: Int, onPagination: (total: Int) -> Unit) = repository.getUpcomingMovies(page, onPagination)
    fun loadLatestMovie() = repository.getLatestMovie()
    fun loadLatestTvSeries() = repository.getLatestTvSeries()
    fun loadDiscoverActionMovies(page: Int, onPagination: (total: Int) -> Unit) = repository.getDiscoverActionMovies(page, onPagination)
    fun loadDiscoverComedyMovies(page: Int, onPagination: (total: Int) -> Unit) = repository.getDiscoverComedyMovies(page, onPagination)
    fun loadDiscoverThrillerMovies(page: Int, onPagination: (total: Int) -> Unit) = repository.getDiscoverThrillerMovies(page, onPagination)
    fun loadDiscoverSciFiMovies(page: Int, onPagination: (total: Int) -> Unit) = repository.getDiscoverSciFiMovies(page, onPagination)
    fun loadDiscoverHorrorMovies(page: Int, onPagination: (total: Int) -> Unit) = repository.getDiscoverHorrorMovies(page, onPagination)
    fun loadDiscoverDramaTvSeries(page: Int, onPagination: (total: Int) -> Unit) = repository.getDiscoverDramaTvSeries(page, onPagination)
    fun loadDiscoverAnimeTvSeries(page: Int, onPagination: (total: Int) -> Unit) = repository.getDiscoverAnimeTvSeries(page, onPagination)
    fun loadDiscoverKoreanDramaTvSeries(page: Int, onPagination: (total: Int) -> Unit) = repository.getDiscoverKoreanDramaTvSeries(page, onPagination)
    fun loadDiscoverSpanishLanguageMovies(page: Int, onPagination: (total: Int) -> Unit) = repository.getDiscoverSpanishLanguageMovies(page, onPagination)
    fun loadDiscoverBollywoodMovies(page: Int, onPagination: (total: Int) -> Unit) = repository.getDiscoverBollywoodMovies(page, onPagination)
    fun loadDiscoverFrenchCinemaMovies(page: Int, onPagination: (total: Int) -> Unit) = repository.getDiscoverFrenchCinemaMovies(page, onPagination)
    fun loadDiscoverDocumentaryMovies(page: Int, onPagination: (total: Int) -> Unit) = repository.getDiscoverDocumentaryMovies(page, onPagination)
    fun loadDiscoverAnimationMovies(page: Int, onPagination: (total: Int) -> Unit) = repository.getDiscoverAnimationMovies(page, onPagination)
    fun loadDiscoverCrimeTvSeries(page: Int, onPagination: (total: Int) -> Unit) = repository.getDiscoverCrimeTvSeries(page, onPagination)
    fun loadDiscoverRomanceMovies(page: Int, onPagination: (total: Int) -> Unit) = repository.getDiscoverRomanceMovies(page, onPagination)
    fun loadDiscoverBestOf2010Movies(page: Int, onPagination: (total: Int) -> Unit) = repository.getDiscoverBestOf2010Movies(page, onPagination)
    fun loadDiscoverBestOf2000Movies(page: Int, onPagination: (total: Int) -> Unit) = repository.getDiscoverBestOf2000Movies(page, onPagination)
    fun loadDiscoverClassicMovies(page: Int, onPagination: (total: Int) -> Unit) = repository.getDiscoverClassicMovies(page, onPagination)
    fun loadDiscoverAdventureMovies(page: Int, onPagination: (total: Int) -> Unit) = repository.getDiscoverAdventureMovies(page, onPagination)
    fun loadDiscoverFantasyMovies(page: Int, onPagination: (total: Int) -> Unit) = repository.getDiscoverFantasyMovies(page, onPagination)
    fun loadCurrentlyOnAirTvSeries(page: Int, onPagination: (total: Int) -> Unit) = repository.getCurrentlyOnAirTvSeries(page, onPagination)

}
