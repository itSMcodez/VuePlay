package com.itsmcodez.vueplay.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.itsmcodez.vueplay.data.local.entity.SearchHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 10")
    fun getRecentSearches(): Flow<List<SearchHistory>>

    @Delete
    suspend fun deleteSearchHistory(searchHistory: SearchHistory)

    @Upsert
    suspend fun upsertSearchHistory(searchHistory: SearchHistory)

    @Query("SELECT * FROM search_history WHERE id = :id")
    suspend fun getSearchHistory(id: Long): SearchHistory?

    @Query("DELETE FROM search_history")
    suspend fun deleteAll()
}