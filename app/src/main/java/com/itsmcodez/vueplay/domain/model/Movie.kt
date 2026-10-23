package com.itsmcodez.vueplay.domain.model

import com.itsmcodez.themoviedbapi.model.core.Genre
import com.itsmcodez.themoviedbapi.model.movies.MovieDb
import com.itsmcodez.themoviedbapi.model.core.Movie as TmdbMovie

import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val movieId: String,
    val imdbId: String,
    val title: String,
    val overview: String,
    val tagline: String,
    val posterPath: String,
    val backdropPath: String,
    val releaseDate: String,
    val originalLanguage: String,
    val duration: Int,
    val homepage: String,
    val status: String,
    val genres: List<Genre>,
    val budget: Int,
    val voteAverage: Double,
    val voteCount: Int,
    val adult: Boolean,
    val video: Boolean
) : MovieAndTvSeriesMulti

// Here we use MovieDb to build the domain Movie object
fun TmdbMovie.toMovie(movieDb: MovieDb) = Movie(
    movieId = id.toString(),
    imdbId = movieDb.imdbID,
    title = movieDb.title,
    overview = movieDb.overview,
    tagline = movieDb.tagline,
    posterPath = movieDb.posterPath?.let { "https://image.tmdb.org/t/p/w300$it" } ?: "",
    backdropPath = movieDb.backdropPath?.let { "https://image.tmdb.org/t/p/original$it" } ?: "",
    releaseDate = movieDb.releaseDate,
    originalLanguage = movieDb.originalLanguage,
    duration = movieDb.runtime,
    homepage = movieDb.homepage,
    status = movieDb.status,
    genres = movieDb.genres,
    budget = movieDb.budget,
    voteAverage = movieDb.voteAverage,
    voteCount = movieDb.voteCount,
    adult = movieDb.adult,
    video = movieDb.video
)
