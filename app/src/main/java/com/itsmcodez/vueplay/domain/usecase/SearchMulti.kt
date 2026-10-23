package com.itsmcodez.vueplay.domain.usecase

import com.itsmcodez.vueplay.domain.repository.MovieAndTvSeriesRepository
import javax.inject.Inject

class SearchMulti @Inject constructor(private val repository: MovieAndTvSeriesRepository) {
    operator fun invoke(
        query: String,
        includeAdult: Boolean,
        page: Int,
        onPagination: (total: Int) -> Unit
    ) = repository.searchMulti(query, includeAdult, page, onPagination)
}