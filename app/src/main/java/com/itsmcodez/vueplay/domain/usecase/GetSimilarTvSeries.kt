package com.itsmcodez.vueplay.domain.usecase

import com.itsmcodez.vueplay.domain.repository.MovieAndTvSeriesRepository
import javax.inject.Inject

class GetSimilarTvSeries @Inject constructor(private val repository: MovieAndTvSeriesRepository) {
    operator fun invoke(
        tmdbId: String,
        page: Int,
        onPagination: (total: Int) -> Unit
    ) = repository.getSimilarTvSeries(tmdbId, page, onPagination)
}