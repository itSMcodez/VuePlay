package com.itsmcodez.vueplay.domain.repository

import com.itsmcodez.vueplay.data.local.entity.SearchHistory
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun getRecentSearches(): Flow<List<SearchHistory>>
    suspend fun deleteSearchHistory(searchHistory: SearchHistory)
    suspend fun upsertSearchHistory(searchHistory: SearchHistory)
    suspend fun getSearchHistory(id: Long): SearchHistory?
    suspend fun deleteAll()
}