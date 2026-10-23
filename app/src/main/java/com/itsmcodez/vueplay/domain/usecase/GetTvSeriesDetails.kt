package com.itsmcodez.vueplay.domain.usecase

import com.itsmcodez.vueplay.domain.repository.MovieAndTvSeriesRepository
import javax.inject.Inject

class GetTvSeriesDetails @Inject constructor(private val repository: MovieAndTvSeriesRepository) {
    operator fun invoke(tmdbId: String) = repository.getTvSeriesDetails(tmdbId)
}