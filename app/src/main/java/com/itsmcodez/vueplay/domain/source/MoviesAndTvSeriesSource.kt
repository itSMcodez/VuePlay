package com.itsmcodez.vueplay.domain.source

import com.itsmcodez.vueplay.domain.model.Movie
import com.itsmcodez.vueplay.domain.model.MovieAndTvSeriesMulti
import com.itsmcodez.vueplay.domain.model.TvSeries

interface MoviesAndTvSeriesSource {
    suspend fun getTrending(page: Int, onPagination: ((total: Int) -> Unit)): List<MovieAndTvSeriesMulti>
    suspend fun getTopRatedMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie>
    suspend fun getPopularMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie>
    suspend fun getPopularTvSeries(page: Int, onPagination: ((total: Int) -> Unit)): List<TvSeries>
    suspend fun getNowPlayingMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie>
    suspend fun getTopRatedTvSeries(page: Int, onPagination: ((total: Int) -> Unit)): List<TvSeries>
    suspend fun getAiringTodayTvSeries(page: Int, onPagination: ((total: Int) -> Unit)): List<TvSeries>
    suspend fun getUpcomingMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie>
    suspend fun getLatestMovie(): Movie
    suspend fun getLatestTvSeries(): TvSeries
    suspend fun getDiscoverActionMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie>
    suspend fun getDiscoverComedyMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie>
    suspend fun getDiscoverThrillerMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie>
    suspend fun getDiscoverSciFiMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie>
    suspend fun getDiscoverHorrorMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie>
    suspend fun getDiscoverDramaTvSeries(page: Int, onPagination: ((total: Int) -> Unit)): List<TvSeries>
    suspend fun getDiscoverAnimeTvSeries(page: Int, onPagination: ((total: Int) -> Unit)): List<TvSeries>
    suspend fun getDiscoverKoreanDramaTvSeries(page: Int, onPagination: ((total: Int) -> Unit)): List<TvSeries>
    suspend fun getDiscoverSpanishLanguageMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie>
    suspend fun getDiscoverBollywoodMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie>
    suspend fun getDiscoverFrenchCinemaMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie>
    suspend fun getDiscoverDocumentaryMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie>
    suspend fun getDiscoverAnimationMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie>
    suspend fun getDiscoverCrimeTvSeries(page: Int, onPagination: ((total: Int) -> Unit)): List<TvSeries>
    suspend fun getDiscoverRomanceMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie>
    suspend fun getDiscoverBestOf2010Movies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie>
    suspend fun getDiscoverBestOf2000Movies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie>
    suspend fun getDiscoverClassicMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie>
    suspend fun getDiscoverAdventureMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie>
    suspend fun getDiscoverFantasyMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie>
    suspend fun getCurrentlyOnAirTvSeries(page: Int, onPagination: ((total: Int) -> Unit)): List<TvSeries>
    suspend fun searchMulti(query: String, includeAdult: Boolean, page: Int, onPagination: ((total: Int) -> Unit)): List<MovieAndTvSeriesMulti>
    suspend fun getMovieDetails(tmdbId: String): Movie
    suspend fun getTvSeriesDetails(tmdbId: String): TvSeries
    suspend fun getSimilarMovies(tmdbId: String, page: Int, onPagination: ((total: Int) -> Unit)): List<Movie>
    suspend fun getSimilarTvSeries(tmdbId: String, page: Int, onPagination: ((total: Int) -> Unit)): List<TvSeries>
    suspend fun getRecommendedMovies(tmdbId: String, page: Int, onPagination: ((total: Int) -> Unit)): List<Movie>
    suspend fun getRecommendedTvSeries(tmdbId: String, page: Int, onPagination: ((total: Int) -> Unit)): List<TvSeries>
}
