package com.itsmcodez.vueplay.domain.model

import com.itsmcodez.themoviedbapi.model.core.Genre
import com.itsmcodez.themoviedbapi.model.tv.core.TvSeason
import com.itsmcodez.themoviedbapi.model.tv.series.TvSeriesDb
import com.itsmcodez.themoviedbapi.model.core.TvSeries as TmdbTvSeries

import kotlinx.parcelize.Parcelize

@Parcelize
data class TvSeries(
    val seriesId: String,
    val name: String,
    val tagline: String,
    val posterPath: String,
    val backdropPath: String,
    val firstAirDate: String,
    val originalLanguage: String,
    val overview: String,
    val status: String,
    val homepage: String,
    val genres: List<Genre>,
    val episodeRuntime: List<Int>,
    val seasons: List<TvSeason>,
    val voteAverage: Double,
    val voteCount: Int,
    val numberOfEpisodes: Int,
    val numberOfSeasons: Int,
    val adult: Boolean
) : MovieAndTvSeriesMulti

// Here we use TvSeriesDb to build the domain TvSeries object
fun TmdbTvSeries.toTvSeries(tvSeriesDb: TvSeriesDb) = TvSeries(
    seriesId = id.toString(),
    name = tvSeriesDb.name,
    tagline = tvSeriesDb.tagline,
    homepage = tvSeriesDb.homepage,
    posterPath = tvSeriesDb.posterPath?.let { "https://image.tmdb.org/t/p/w300$it" } ?: "",
    backdropPath = tvSeriesDb.backdropPath?.let { "https://image.tmdb.org/t/p/original$it" } ?: "",
    firstAirDate = tvSeriesDb.firstAirDate,
    originalLanguage = tvSeriesDb.originalLanguage,
    overview = tvSeriesDb.overview,
    status = tvSeriesDb.status,
    genres = tvSeriesDb.genres,
    seasons = tvSeriesDb.seasons,
    episodeRuntime = tvSeriesDb.episodeRunTime,
    voteAverage = tvSeriesDb.voteAverage,
    voteCount = tvSeriesDb.voteCount,
    numberOfEpisodes = tvSeriesDb.numberOfEpisodes,
    numberOfSeasons = tvSeriesDb.numberOfSeasons,
    adult = tvSeriesDb.adult
)
