package com.itsmcodez.vueplay.domain.repository

import com.itsmcodez.vueplay.domain.model.Movie
import com.itsmcodez.vueplay.domain.model.MovieAndTvSeriesMulti
import com.itsmcodez.vueplay.domain.model.TvSeries
import kotlinx.coroutines.flow.Flow

interface MovieAndTvSeriesRepository {
    fun getTrending(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<MovieAndTvSeriesMulti>>
    fun getTopRatedMovies(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<Movie>>
    fun getPopularMovies(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<Movie>>
    fun getPopularTvSeries(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<TvSeries>>
    fun getNowPlayingMovies(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<Movie>>
    fun getTopRatedTvSeries(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<TvSeries>>
    fun getAiringTodayTvSeries(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<TvSeries>>
    fun getUpcomingMovies(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<Movie>>
    fun getLatestMovie(): Flow<Movie?>
    fun getLatestTvSeries(): Flow<TvSeries?>
    fun getDiscoverActionMovies(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<Movie>>
    fun getDiscoverComedyMovies(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<Movie>>
    fun getDiscoverThrillerMovies(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<Movie>>
    fun getDiscoverSciFiMovies(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<Movie>>
    fun getDiscoverHorrorMovies(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<Movie>>
    fun getDiscoverDramaTvSeries(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<TvSeries>>
    fun getDiscoverAnimeTvSeries(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<TvSeries>>
    fun getDiscoverKoreanDramaTvSeries(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<TvSeries>>
    fun getDiscoverSpanishLanguageMovies(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<Movie>>
    fun getDiscoverBollywoodMovies(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<Movie>>
    fun getDiscoverFrenchCinemaMovies(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<Movie>>
    fun getDiscoverDocumentaryMovies(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<Movie>>
    fun getDiscoverAnimationMovies(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<Movie>>
    fun getDiscoverCrimeTvSeries(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<TvSeries>>
    fun getDiscoverRomanceMovies(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<Movie>>
    fun getDiscoverBestOf2010Movies(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<Movie>>
    fun getDiscoverBestOf2000Movies(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<Movie>>
    fun getDiscoverClassicMovies(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<Movie>>
    fun getDiscoverAdventureMovies(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<Movie>>
    fun getDiscoverFantasyMovies(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<Movie>>
    fun getCurrentlyOnAirTvSeries(page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<TvSeries>>
    fun searchMulti(query: String, includeAdult: Boolean = false, page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<MovieAndTvSeriesMulti>>
    fun getMovieDetails(tmdbId: String): Flow<Movie>
    fun getTvSeriesDetails(tmdbId: String): Flow<TvSeries>
    fun getSimilarMovies(tmdbId: String, page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<Movie>>
    fun getSimilarTvSeries(tmdbId: String, page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<TvSeries>>
    fun getRecommendedMovies(tmdbId: String, page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<Movie>>
    fun getRecommendedTvSeries(tmdbId: String, page: Int = 1, onPagination: ((total: Int) -> Unit) = { }): Flow<List<TvSeries>>
}