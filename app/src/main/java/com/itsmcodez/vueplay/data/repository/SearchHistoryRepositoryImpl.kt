package com.itsmcodez.vueplay.data.repository

import com.itsmcodez.vueplay.data.local.dao.SearchHistoryDao
import com.itsmcodez.vueplay.data.local.entity.SearchHistory
import com.itsmcodez.vueplay.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepositoryImpl @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao
) : SearchHistoryRepository {
    override fun getRecentSearches(): Flow<List<SearchHistory>> = searchHistoryDao.getRecentSearches()

    override suspend fun deleteSearchHistory(searchHistory: SearchHistory) = searchHistoryDao.deleteSearchHistory(searchHistory)

    override suspend fun upsertSearchHistory(searchHistory: SearchHistory) = searchHistoryDao.upsertSearchHistory(searchHistory)

    override suspend fun getSearchHistory(id: Long): SearchHistory? = searchHistoryDao.getSearchHistory(id)

    override suspend fun deleteAll() = searchHistoryDao.deleteAll()
}