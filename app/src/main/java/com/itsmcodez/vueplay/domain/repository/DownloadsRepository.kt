package com.itsmcodez.vueplay.domain.repository

import com.itsmcodez.vueplay.data.local.entity.DownloadItem
import kotlinx.coroutines.flow.Flow

interface DownloadsRepository {
    val lastUserAgentString: String?
    val downloads: Flow<List<DownloadItem>>
    fun startDownload(url: String, fileName: String, userAgent: String?): String
    fun pauseDownload(id: String)
    fun resumeDownload(id: String, fileName: String, userAgent: String?)
    fun deleteDownload(id: String)
}