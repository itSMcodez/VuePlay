package com.itsmcodez.vueplay.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsmcodez.vueplay.domain.model.SearchResult
import com.itsmcodez.vueplay.domain.usecase.wrapper.VideoFolderUseCasesWrapper
import com.itsmcodez.vueplay.domain.usecase.wrapper.VideoUseCasesWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val videoUseCasesWrapper: VideoUseCasesWrapper,
    private val videoFolderUseCasesWrapper: VideoFolderUseCasesWrapper
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults

    val resumableVideos = videoUseCasesWrapper.getAllResumableVideos()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            combine(
                videoUseCasesWrapper.getVideos(),
                videoFolderUseCasesWrapper.getVideoFolders()
            ) { videos, videoFolders ->
                val results = mutableListOf<SearchResult>()
                if (query.isNotEmpty()) {
                    results.addAll(
                        videos.filter { it.displayName.contains(query, ignoreCase = true) }
                            .map { SearchResult.VideoItem(it) }
                    )
                    results.addAll(
                        videoFolders.filter { it.name.contains(query, ignoreCase = true) }
                            .map { SearchResult.VideoFolderItem(it) }
                    )
                }

                // return results sorted by title
                results.sortedBy { it.title }

            }.collect { results -> _searchResults.value = results }
        }
    }
}