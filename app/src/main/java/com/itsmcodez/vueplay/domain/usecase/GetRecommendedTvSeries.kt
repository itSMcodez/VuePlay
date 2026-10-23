package com.itsmcodez.vueplay.domain.usecase

import com.itsmcodez.vueplay.domain.repository.MovieAndTvSeriesRepository
import javax.inject.Inject

class GetRecommendedTvSeries @Inject constructor(private val repository: MovieAndTvSeriesRepository) {
    operator fun invoke(
        tmdbId: String,
        page: Int,
        onPagination: (total: Int) -> Unit
    ) = repository.getRecommendedTvSeries(tmdbId, page, onPagination)
}