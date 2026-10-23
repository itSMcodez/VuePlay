package com.itsmcodez.vueplay.presentation.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsmcodez.vueplay.domain.repository.DownloadsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DownloadsViewModel @Inject constructor(
    private val repository: DownloadsRepository
) : ViewModel() {
    val lastUserAgentString = repository.lastUserAgentString
    val downloads = repository.downloads
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun startDownload(url: String, fileName: String, userAgent: String?) = repository.startDownload(url, fileName, userAgent)
    fun pauseDownload(id: String) = repository.pauseDownload(id)
    fun resumeDownload(id: String, fileName: String, userAgent: String?) = repository.resumeDownload(id, fileName, userAgent)
    fun deleteDownload(id: String) = repository.deleteDownload(id)
}
