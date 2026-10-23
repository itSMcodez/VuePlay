package com.itsmcodez.vueplay.data.source

import com.itsmcodez.themoviedbapi.TmdbApi
import com.itsmcodez.themoviedbapi.model.core.multi.Multi
import com.itsmcodez.themoviedbapi.model.core.multi.MultiMovie
import com.itsmcodez.themoviedbapi.model.core.multi.MultiTvSeries
import com.itsmcodez.themoviedbapi.tools.builders.discover.DiscoverMovieParamBuilder
import com.itsmcodez.themoviedbapi.tools.builders.discover.DiscoverTvParamBuilder
import com.itsmcodez.themoviedbapi.tools.sortby.DiscoverMovieSortBy
import com.itsmcodez.themoviedbapi.tools.sortby.DiscoverTvSortBy
import com.itsmcodez.vueplay.domain.model.Movie
import com.itsmcodez.vueplay.domain.model.MovieAndTvSeriesMulti
import com.itsmcodez.vueplay.domain.model.TvSeries
import com.itsmcodez.vueplay.domain.model.toMovie
import com.itsmcodez.vueplay.domain.model.toTvSeries
import com.itsmcodez.vueplay.domain.source.MoviesAndTvSeriesSource
import javax.inject.Inject
import javax.inject.Singleton

const val GENRE_MOVIE_ACTION = 28
const val GENRE_MOVIE_COMEDY = 35
const val GENRE_MOVIE_THRILLER = 53
const val GENRE_MOVIE_SCI_FI = 878
const val GENRE_MOVIE_HORROR = 27
const val GENRE_MOVIE_DOCUMENTARY = 99
const val GENRE_MOVIE_ANIMATION = 16
const val GENRE_MOVIE_ROMANCE = 10749
const val GENRE_MOVIE_ADVENTURE = 12
const val GENRE_MOVIE_FANTASY = 14
const val GENRE_TV_DRAMA = 18
const val GENRE_TV_ANIME = 16
const val GENRE_TV_CRIME = 80
const val LANGUAGE_SPANISH = "es"
const val LANGUAGE_KOREAN = "ko"
const val LANGUAGE_FRENCH = "fr"
const val LANGUAGE_HINDI = "hi"
const val LANGUAGE_JAPANESE = "ja"

@Singleton
class TmdbMoviesAndTvSeriesSource @Inject constructor(private val tmdbApi: TmdbApi) : MoviesAndTvSeriesSource {

    override suspend fun getTrending(page: Int, onPagination: ((total: Int) -> Unit)): List<MovieAndTvSeriesMulti> = tmdbApi.trending.getAll(
        com.itsmcodez.themoviedbapi.tools.model.time.TimeWindow.WEEK,
        "en-US",
        page
    ).also { onPagination(it.totalPages) }.results.mapNotNull {
        when(it.mediaType) {
            Multi.MediaType.MOVIE -> (it as? MultiMovie)?.toMovie(
                tmdbApi.movies.getDetails(
                    it.id,
                    "en-US" 
                )
            )
            Multi.MediaType.TV_SERIES -> (it as? MultiTvSeries)?.toTvSeries(
                tmdbApi.tvSeries.getDetails(
                    it.id,
                    "en-US" 
                )
            )
            else -> null
        }
    }

    override suspend fun getTopRatedMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie> = tmdbApi.movieLists.getTopRated(
        "en-US",
        page,
        "US"
    ).also { onPagination(it.totalPages) }.results.map {
        it.toMovie(
                tmdbApi.movies.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getPopularMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie> = tmdbApi.movieLists.getPopular(
        "en-US",
        page,
        "US"
    ).also { onPagination(it.totalPages) }.results.map {
        it.toMovie(
                tmdbApi.movies.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getPopularTvSeries(page: Int, onPagination: ((total: Int) -> Unit)): List<TvSeries> = tmdbApi.tvSeriesLists.getPopular(
        "en-US",
        page
    ).also { onPagination(it.totalPages) }.results.map {
        it.toTvSeries(
                tmdbApi.tvSeries.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getNowPlayingMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie> = tmdbApi.movieLists.getNowPlaying(
        "en-US",
        page,
        "US"
    ).also { onPagination(it.totalPages) }.results.map {
        it.toMovie(
                tmdbApi.movies.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getTopRatedTvSeries(page: Int, onPagination: ((total: Int) -> Unit)): List<TvSeries> = tmdbApi.tvSeriesLists.getTopRated(
        "en-US",
        page
    ).also { onPagination(it.totalPages) }.results.map {
        it.toTvSeries(
                tmdbApi.tvSeries.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getAiringTodayTvSeries(page: Int, onPagination: ((total: Int) -> Unit)): List<TvSeries> = tmdbApi.tvSeriesLists.getAiringToday(
        "en-US",
        page,
        null
    ).also { onPagination(it.totalPages) }.results.map {
        it.toTvSeries(
                tmdbApi.tvSeries.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getUpcomingMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie> = tmdbApi.movieLists.getUpcoming(
        "en-US",
        page,
        "US"
    ).also { onPagination(it.totalPages) }.results.map {
        it.toMovie(
                tmdbApi.movies.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getLatestMovie(): Movie = tmdbApi.movies.latest.run {
        Movie(
            movieId = id.toString(),
            imdbId = imdbID,
            title = title,
            tagline = tagline,
            posterPath = posterPath?.let { "https://image.tmdb.org/t/p/w300$it" } ?: "",
            backdropPath = backdropPath?.let { "https://image.tmdb.org/t/p/original$it" } ?: "",
            releaseDate = releaseDate,
            originalLanguage = originalLanguage,
            overview = overview,
            status = status,
            genres = genres,
            budget = budget,
            homepage = homepage,
            duration = runtime,
            voteAverage = voteAverage,
            voteCount = voteCount,
            adult = adult,
            video = video
        )
    }

    override suspend fun getLatestTvSeries(): TvSeries = tmdbApi.tvSeries.latest.run {
        TvSeries(
            seriesId = id.toString(),
            name = name,
            tagline = tagline,
            posterPath = posterPath?.let { "https://image.tmdb.org/t/p/w300$it" } ?: "",
            backdropPath = backdropPath?.let { "https://image.tmdb.org/t/p/original$it" } ?: "",
            firstAirDate = firstAirDate,
            originalLanguage = originalLanguage,
            overview = overview,
            status = status,
            homepage = homepage,
            genres = genres,
            episodeRuntime = episodeRunTime,
            seasons = seasons,
            voteAverage = voteAverage,
            voteCount = voteCount,
            numberOfEpisodes = numberOfEpisodes,
            numberOfSeasons = numberOfSeasons,
            adult = adult
        )
    }

    override suspend fun getDiscoverActionMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie> = tmdbApi.discover.getMovie(
        DiscoverMovieParamBuilder()
            .withGenres(
                listOf(GENRE_MOVIE_ACTION),
                false // get movies with this genre only or included
            )
            .sortBy(DiscoverMovieSortBy.POPULARITY_DESC),
        page
    ).also { onPagination(it.totalPages) }.map {
        it.toMovie(
                tmdbApi.movies.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getDiscoverComedyMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie> = tmdbApi.discover.getMovie(
        DiscoverMovieParamBuilder()
            .withGenres(
                listOf(GENRE_MOVIE_COMEDY),
                false // get movies with this genre only or included
            )
            .sortBy(DiscoverMovieSortBy.POPULARITY_DESC),
        page
    ).also { onPagination(it.totalPages) }.map {
        it.toMovie(
                tmdbApi.movies.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getDiscoverThrillerMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie> = tmdbApi.discover.getMovie(
        DiscoverMovieParamBuilder()
            .withGenres(
                listOf(GENRE_MOVIE_THRILLER),
                false // get movies with this genre only or included
            )
            .sortBy(DiscoverMovieSortBy.POPULARITY_DESC),
        page
    ).also { onPagination(it.totalPages) }.map {
        it.toMovie(
                tmdbApi.movies.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getDiscoverSciFiMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie> = tmdbApi.discover.getMovie(
        DiscoverMovieParamBuilder()
            .withGenres(
                listOf(GENRE_MOVIE_SCI_FI),
                false // get movies with this genre only or included
            )
            .sortBy(DiscoverMovieSortBy.POPULARITY_DESC),
        page
    ).also { onPagination(it.totalPages) }.map {
        it.toMovie(
                tmdbApi.movies.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getDiscoverHorrorMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie> = tmdbApi.discover.getMovie(
        DiscoverMovieParamBuilder()
            .withGenres(
                listOf(GENRE_MOVIE_HORROR),
                false // get movies with this genre only or included
            )
            .sortBy(DiscoverMovieSortBy.POPULARITY_DESC),
        page
    ).also { onPagination(it.totalPages) }.map {
        it.toMovie(
                tmdbApi.movies.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getDiscoverDramaTvSeries(page: Int, onPagination: ((total: Int) -> Unit)): List<TvSeries> = tmdbApi.discover.getTv(
        DiscoverTvParamBuilder()
            .withGenres(
                listOf(GENRE_TV_DRAMA),
                false // get movies with this genre only or included
            )
            .sortBy(DiscoverTvSortBy.POPULARITY_DESC),
        page
    ).also { onPagination(it.totalPages) }.map {
        it.toTvSeries(
                tmdbApi.tvSeries.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getDiscoverAnimeTvSeries(page: Int, onPagination: ((total: Int) -> Unit)): List<TvSeries> = tmdbApi.discover.getTv(
        DiscoverTvParamBuilder()
            .withGenres(
                listOf(GENRE_TV_ANIME),
                false // get movies with this genre only or included
            )
            .withOriginalLanguage(LANGUAGE_JAPANESE)
            .sortBy(DiscoverTvSortBy.POPULARITY_DESC),
        page
    ).also { onPagination(it.totalPages) }.map {
        it.toTvSeries(
                tmdbApi.tvSeries.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getDiscoverKoreanDramaTvSeries(page: Int, onPagination: ((total: Int) -> Unit)): List<TvSeries> = tmdbApi.discover.getTv(
        DiscoverTvParamBuilder()
            .withOriginalLanguage(LANGUAGE_KOREAN)
            .sortBy(DiscoverTvSortBy.POPULARITY_DESC),
        page
    ).also { onPagination(it.totalPages) }.map {
        it.toTvSeries(
                tmdbApi.tvSeries.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getDiscoverSpanishLanguageMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie> = tmdbApi.discover.getMovie(
        DiscoverMovieParamBuilder()
            .withOriginalLanguage(LANGUAGE_SPANISH)
            .sortBy(DiscoverMovieSortBy.POPULARITY_DESC),
        page
    ).also { onPagination(it.totalPages) }.map {
        it.toMovie(
                tmdbApi.movies.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getDiscoverBollywoodMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie> = tmdbApi.discover.getMovie(
        DiscoverMovieParamBuilder()
            .withOriginalLanguage(LANGUAGE_HINDI)
            .sortBy(DiscoverMovieSortBy.POPULARITY_DESC),
        page
    ).also { onPagination(it.totalPages) }.map {
        it.toMovie(
                tmdbApi.movies.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getDiscoverFrenchCinemaMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie> = tmdbApi.discover.getMovie(
        DiscoverMovieParamBuilder()
            .withOriginalLanguage(LANGUAGE_FRENCH)
            .sortBy(DiscoverMovieSortBy.POPULARITY_DESC),
        page
    ).also { onPagination(it.totalPages) }.map {
        it.toMovie(
                tmdbApi.movies.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getDiscoverDocumentaryMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie> = tmdbApi.discover.getMovie(
        DiscoverMovieParamBuilder()
            .withGenres(
                listOf(GENRE_MOVIE_DOCUMENTARY),
                false // get movies with this genre only or included
            )
            .sortBy(DiscoverMovieSortBy.POPULARITY_DESC),
        page
    ).also { onPagination(it.totalPages) }.map {
        it.toMovie(
                tmdbApi.movies.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getDiscoverAnimationMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie> = tmdbApi.discover.getMovie(
        DiscoverMovieParamBuilder()
            .withGenres(
                listOf(GENRE_MOVIE_ANIMATION),
                false // get movies with this genre only or included
            )
            .sortBy(DiscoverMovieSortBy.POPULARITY_DESC),
        page
    ).also { onPagination(it.totalPages) }.map {
        it.toMovie(
                tmdbApi.movies.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getDiscoverCrimeTvSeries(page: Int, onPagination: ((total: Int) -> Unit)): List<TvSeries> = tmdbApi.discover.getTv(
        DiscoverTvParamBuilder()
            .withGenres(
                listOf(GENRE_TV_CRIME),
                false // get movies with this genre only or included
            )
            .sortBy(DiscoverTvSortBy.POPULARITY_DESC),
        page
    ).also { onPagination(it.totalPages) }.map {
        it.toTvSeries(
                tmdbApi.tvSeries.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getDiscoverRomanceMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie> = tmdbApi.discover.getMovie(
        DiscoverMovieParamBuilder()
            .withGenres(
                listOf(GENRE_MOVIE_ROMANCE),
                false // get movies with this genre only or included
            )
            .sortBy(DiscoverMovieSortBy.POPULARITY_DESC),
        page
    ).also { onPagination(it.totalPages) }.map {
        it.toMovie(
                tmdbApi.movies.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getDiscoverBestOf2010Movies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie> = tmdbApi.discover.getMovie(
        DiscoverMovieParamBuilder()
            .primaryReleaseDateGte("2010-01-01")
            .primaryReleaseDateLte("2019-12-31")
            .sortBy(DiscoverMovieSortBy.VOTE_AVERAGE_DESC)
            .voteCountGte(1000.0),
        page
    ).also { onPagination(it.totalPages) }.map {
        it.toMovie(
                tmdbApi.movies.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getDiscoverBestOf2000Movies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie> = tmdbApi.discover.getMovie(
        DiscoverMovieParamBuilder()
            .primaryReleaseDateGte("2000-01-01")
            .primaryReleaseDateLte("2009-12-31")
            .sortBy(DiscoverMovieSortBy.VOTE_AVERAGE_DESC)
            .voteCountGte(500.0),
        page
    ).also { onPagination(it.totalPages) }.map {
        it.toMovie(
                tmdbApi.movies.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getDiscoverClassicMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie> = tmdbApi.discover.getMovie(
        DiscoverMovieParamBuilder()
            .primaryReleaseDateLte("1999-12-31")
            .sortBy(DiscoverMovieSortBy.VOTE_AVERAGE_DESC)
            .voteCountGte(1000.0),
        page
    ).also { onPagination(it.totalPages) }.map {
        it.toMovie(
                tmdbApi.movies.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getDiscoverAdventureMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie> = tmdbApi.discover.getMovie(
        DiscoverMovieParamBuilder()
            .withGenres(
                listOf(GENRE_MOVIE_ADVENTURE),
                false // get movies with this genre only or included
            )
            .sortBy(DiscoverMovieSortBy.POPULARITY_DESC),
        page
    ).also { onPagination(it.totalPages) }.map {
        it.toMovie(
                tmdbApi.movies.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getDiscoverFantasyMovies(page: Int, onPagination: ((total: Int) -> Unit)): List<Movie> = tmdbApi.discover.getMovie(
        DiscoverMovieParamBuilder()
            .withGenres(
                listOf(GENRE_MOVIE_FANTASY),
                false // get movies with this genre only or included
            )
            .sortBy(DiscoverMovieSortBy.POPULARITY_DESC),
        page
    ).also { onPagination(it.totalPages) }.map {
        it.toMovie(
                tmdbApi.movies.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun getCurrentlyOnAirTvSeries(page: Int, onPagination: ((total: Int) -> Unit)): List<TvSeries> = tmdbApi.tvSeriesLists.getOnTheAir(
        "en-US",
        1,
        null
    ).also { onPagination(it.totalPages) }.results.map {
        it.toTvSeries(
                tmdbApi.tvSeries.getDetails(
                    it.id,
                    "en-US" 
                )
            )
    }

    override suspend fun searchMulti(
        query: String,
        includeAdult: Boolean,
        page: Int,
        onPagination: (total: Int) -> Unit
    ): List<MovieAndTvSeriesMulti> = tmdbApi.search.searchMulti(
        query,
        includeAdult,
        "en-US",
        page
    ).also { onPagination(it.totalPages) }.results.mapNotNull {
        when(it.mediaType) {
            Multi.MediaType.MOVIE -> (it as? MultiMovie)?.toMovie(
                tmdbApi.movies.getDetails(
                    it.id,
                    "en-US"
                )
            )
            Multi.MediaType.TV_SERIES -> (it as? MultiTvSeries)?.toTvSeries(
                tmdbApi.tvSeries.getDetails(
                    it.id,
                    "en-US"
                )
            )
            else -> null
        }
    }

    override suspend fun getMovieDetails(tmdbId: String): Movie = tmdbApi.movies.getDetails(
        tmdbId.toInt(),
        "en-US"
    ).run {
        Movie(
            movieId = id.toString(),
            imdbId = imdbID,
            title = title,
            tagline = tagline,
            posterPath = posterPath?.let { "https://image.tmdb.org/t/p/w300$it" } ?: "",
            backdropPath = backdropPath?.let { "https://image.tmdb.org/t/p/original$it" } ?: "",
            releaseDate = releaseDate,
            originalLanguage = originalLanguage,
            overview = overview,
            status = status,
            genres = genres,
            budget = budget,
            homepage = homepage,
            duration = runtime,
            voteAverage = voteAverage,
            voteCount = voteCount,
            adult = adult,
            video = video
        )
    }

    override suspend fun getTvSeriesDetails(tmdbId: String): TvSeries = tmdbApi.tvSeries.getDetails(
        tmdbId.toInt(),
        "en-US"
    ).run {
        TvSeries(
            seriesId = id.toString(),
            name = name,
            tagline = tagline,
            posterPath = posterPath?.let { "https://image.tmdb.org/t/p/w300$it" } ?: "",
            backdropPath = backdropPath?.let { "https://image.tmdb.org/t/p/original$it" } ?: "",
            firstAirDate = firstAirDate,
            originalLanguage = originalLanguage,
            overview = overview,
            status = status,
            homepage = homepage,
            genres = genres,
            episodeRuntime = episodeRunTime,
            seasons = seasons,
            voteAverage = voteAverage,
            voteCount = voteCount,
            numberOfEpisodes = numberOfEpisodes,
            numberOfSeasons = numberOfSeasons,
            adult = adult
        )
    }

    override suspend fun getSimilarMovies(tmdbId: String, page: Int, onPagination: ((total: Int) -> Unit)): List<Movie> = tmdbApi.movies.getSimilar(
        tmdbId.toInt(),
        "en-US",
        page
    ).also { onPagination(it.totalPages) }.results.map {
        it.toMovie(
            tmdbApi.movies.getDetails(
                it.id,
                "en-US"
            )
        )
    }

    override suspend fun getSimilarTvSeries(tmdbId: String, page: Int, onPagination: ((total: Int) -> Unit)): List<TvSeries> = tmdbApi.tvSeries.getSimilar(
        tmdbId.toInt(),
        "en-US",
        page
    ).also { onPagination(it.totalPages) }.results.map {
        it.toTvSeries(
            tmdbApi.tvSeries.getDetails(
                it.id,
                "en-US"
            )
        )
    }

    override suspend fun getRecommendedMovies(tmdbId: String, page: Int, onPagination: ((total: Int) -> Unit)): List<Movie> = tmdbApi.movies.getRecommendations(
        tmdbId.toInt(),
        "en-US",
        page
    ).also { onPagination(it.totalPages) }.results.map {
        it.toMovie(
            tmdbApi.movies.getDetails(
                it.id,
                "en-US"
            )
        )
    }

    override suspend fun getRecommendedTvSeries(tmdbId: String, page: Int, onPagination: ((total: Int) -> Unit)): List<TvSeries> = tmdbApi.tvSeries.getRecommendations(
        tmdbId.toInt(),
        "en-US",
        page
    ).also { onPagination(it.totalPages) }.results.map {
        it.toTvSeries(
            tmdbApi.tvSeries.getDetails(
                it.id,
                "en-US"
            )
        )
    }

}