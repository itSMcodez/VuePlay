package com.itsmcodez.vueplay.data.repository

import com.itsmcodez.vueplay.domain.model.Movie
import com.itsmcodez.vueplay.domain.model.MovieAndTvSeriesMulti
import com.itsmcodez.vueplay.domain.model.TvSeries
import com.itsmcodez.vueplay.domain.repository.MovieAndTvSeriesRepository
import com.itsmcodez.vueplay.domain.source.MoviesAndTvSeriesSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieAndTvSeriesRepositoryImpl @Inject constructor(
    private val source: MoviesAndTvSeriesSource
) : MovieAndTvSeriesRepository {

    private fun <T> getData(source: suspend () -> List<T>) = flow {
        emit( source() )
    }.flowOn(Dispatchers.IO)

    override fun getTrending(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<MovieAndTvSeriesMulti>> = getData { source.getTrending(page, onPagination) }
    override fun getTopRatedMovies(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<Movie>> = getData { source.getTopRatedMovies(page, onPagination) }
    override fun getPopularMovies(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<Movie>> = getData { source.getPopularMovies(page, onPagination) }
    override fun getPopularTvSeries(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<TvSeries>> = getData { source.getPopularTvSeries(page, onPagination) }
    override fun getNowPlayingMovies(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<Movie>> = getData { source.getNowPlayingMovies(page, onPagination) }
    override fun getTopRatedTvSeries(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<TvSeries>> = getData { source.getTopRatedTvSeries(page, onPagination) }
    override fun getAiringTodayTvSeries(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<TvSeries>> = getData { source.getAiringTodayTvSeries(page, onPagination) }
    override fun getUpcomingMovies(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<Movie>> = getData { source.getUpcomingMovies(page, onPagination) }
    override fun getLatestMovie(): Flow<Movie?> = getData { listOf(source.getLatestMovie()) }.map { it.firstOrNull()}
    override fun getLatestTvSeries(): Flow<TvSeries?> = getData { listOf(source.getLatestTvSeries()) }.map { it.firstOrNull()}
    override fun getDiscoverActionMovies(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<Movie>> = getData { source.getDiscoverActionMovies(page, onPagination) }
    override fun getDiscoverComedyMovies(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<Movie>> = getData { source.getDiscoverComedyMovies(page, onPagination) }
    override fun getDiscoverThrillerMovies(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<Movie>> = getData { source.getDiscoverThrillerMovies(page, onPagination) }
    override fun getDiscoverSciFiMovies(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<Movie>> = getData { source.getDiscoverSciFiMovies(page, onPagination) }
    override fun getDiscoverHorrorMovies(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<Movie>> = getData { source.getDiscoverHorrorMovies(page, onPagination) }
    override fun getDiscoverDramaTvSeries(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<TvSeries>> = getData { source.getDiscoverDramaTvSeries(page, onPagination) }
    override fun getDiscoverAnimeTvSeries(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<TvSeries>> = getData { source.getDiscoverAnimeTvSeries(page, onPagination) }
    override fun getDiscoverKoreanDramaTvSeries(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<TvSeries>> = getData { source.getDiscoverKoreanDramaTvSeries(page, onPagination) }
    override fun getDiscoverSpanishLanguageMovies(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<Movie>> = getData { source.getDiscoverSpanishLanguageMovies(page, onPagination) }
    override fun getDiscoverBollywoodMovies(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<Movie>> = getData { source.getDiscoverBollywoodMovies(page, onPagination) }
    override fun getDiscoverFrenchCinemaMovies(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<Movie>> = getData { source.getDiscoverFrenchCinemaMovies(page, onPagination) }
    override fun getDiscoverDocumentaryMovies(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<Movie>> = getData { source.getDiscoverDocumentaryMovies(page, onPagination) }
    override fun getDiscoverAnimationMovies(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<Movie>> = getData { source.getDiscoverAnimationMovies(page, onPagination) }
    override fun getDiscoverCrimeTvSeries(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<TvSeries>> = getData { source.getDiscoverCrimeTvSeries(page, onPagination) }
    override fun getDiscoverRomanceMovies(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<Movie>> = getData { source.getDiscoverRomanceMovies(page, onPagination) }
    override fun getDiscoverBestOf2010Movies(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<Movie>> = getData { source.getDiscoverBestOf2010Movies(page, onPagination) }
    override fun getDiscoverBestOf2000Movies(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<Movie>> = getData { source.getDiscoverBestOf2000Movies(page, onPagination) }
    override fun getDiscoverClassicMovies(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<Movie>> = getData { source.getDiscoverClassicMovies(page, onPagination) }
    override fun getDiscoverAdventureMovies(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<Movie>> = getData { source.getDiscoverAdventureMovies(page, onPagination) }
    override fun getDiscoverFantasyMovies(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<Movie>> = getData { source.getDiscoverFantasyMovies(page, onPagination) }
    override fun getCurrentlyOnAirTvSeries(page: Int, onPagination: ((total: Int) -> Unit)): Flow<List<TvSeries>> = getData { source.getCurrentlyOnAirTvSeries(page, onPagination) }
    override fun getMovieDetails(tmdbId: String): Flow<Movie> = getData { listOf(source.getMovieDetails(tmdbId)) }.map { it.first() }
    override fun getTvSeriesDetails(tmdbId: String): Flow<TvSeries> = getData { listOf(source.getTvSeriesDetails(tmdbId)) }.map { it.first() }

    override fun searchMulti(
        query: String,
        includeAdult: Boolean,
        page: Int,
        onPagination: (total: Int) -> Unit
    ): Flow<List<MovieAndTvSeriesMulti>> = getData { source.searchMulti(query, includeAdult, page, onPagination) }

    override fun getSimilarMovies(
        tmdbId: String,
        page: Int,
        onPagination: (total: Int) -> Unit
    ): Flow<List<Movie>> = getData { source.getSimilarMovies(tmdbId, page, onPagination) }

    override fun getSimilarTvSeries(
        tmdbId: String,
        page: Int,
        onPagination: (total: Int) -> Unit
    ): Flow<List<TvSeries>> = getData { source.getSimilarTvSeries(tmdbId, page, onPagination) }

    override fun getRecommendedMovies(
        tmdbId: String,
        page: Int,
        onPagination: (total: Int) -> Unit
    ): Flow<List<Movie>> = getData { source.getRecommendedMovies(tmdbId, page, onPagination) }

    override fun getRecommendedTvSeries(
        tmdbId: String,
        page: Int,
        onPagination: (total: Int) -> Unit
    ): Flow<List<TvSeries>> = getData { source.getRecommendedTvSeries(tmdbId, page, onPagination) }

}

