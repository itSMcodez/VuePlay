package com.itsmcodez.vueplay.presentation.streaming.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsmcodez.vueplay.common.NetworkObserver
import com.itsmcodez.vueplay.data.local.entity.SearchHistory
import com.itsmcodez.vueplay.domain.model.MovieAndTvSeriesMulti
import com.itsmcodez.vueplay.domain.repository.SearchHistoryRepository
import com.itsmcodez.vueplay.domain.usecase.SearchMulti
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesAndTvSeriesSearchViewModel @Inject constructor(
    private val searchMulti: SearchMulti,
    private val searchHistoryRepository: SearchHistoryRepository,
    networkObserver: NetworkObserver
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val recentSearches = searchHistoryRepository.getRecentSearches()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _searchResults = MutableStateFlow<List<MovieAndTvSeriesMulti>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    val networkStatus = networkObserver.networkStatus
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            NetworkObserver.Status.Unavailable
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSubmitQuery(query: String, page: Int, onPagination: (total: Int) -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            searchMulti(
                query = query,
                includeAdult = false,
                page = page,
                onPagination = onPagination
            ).catch { onFailure(it as? Exception ?: Exception(it)) }
            .collectLatest { _searchResults.value = it }
        }
    }

    fun deleteSearchHistory(searchHistory: SearchHistory) {
        viewModelScope.launch {
            searchHistoryRepository.deleteSearchHistory(searchHistory)
        }
    }

    fun upsertSearchHistory(searchHistory: SearchHistory) {
        viewModelScope.launch {
            searchHistoryRepository.upsertSearchHistory(searchHistory)
        }
    }

    fun getSearchHistory(id: Long) {
        viewModelScope.launch {
            searchHistoryRepository.getSearchHistory(id)
        }
    }

    fun deleteAllSearchHistory() {
        viewModelScope.launch {
            searchHistoryRepository.deleteAll()
        }
    }

}