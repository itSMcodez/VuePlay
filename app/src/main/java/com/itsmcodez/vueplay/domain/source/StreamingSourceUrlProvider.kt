package com.itsmcodez.vueplay.domain.source

interface StreamingSourceUrlProvider {
    fun getMovieUrl(movieId: String): String
    fun getTvSeriesUrl(seriesId: String, season: Int, episode: Int): String
}