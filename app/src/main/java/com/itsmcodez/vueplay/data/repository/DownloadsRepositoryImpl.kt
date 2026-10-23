package com.itsmcodez.vueplay.data.repository

import com.itsmcodez.vueplay.domain.repository.DownloadsRepository
import com.itsmcodez.vueplay.manager.VuePlayDownloadManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadsRepositoryImpl @Inject constructor(
    private val downloadManager: VuePlayDownloadManager
) : DownloadsRepository {
    override val lastUserAgentString: String?
        get() = downloadManager.lastUserAgentString

    override val downloads
        get() = downloadManager.downloads

    override fun startDownload(url: String, fileName: String, userAgent: String?): String = downloadManager.startDownload(
        url,
        fileName,
        userAgent
    )
    override fun pauseDownload(id: String) = downloadManager.pauseDownload(id)
    override fun resumeDownload(id: String, fileName: String, userAgent: String?) = downloadManager.resumeDownload(id, userAgent)
    override fun deleteDownload(id: String) = downloadManager.deleteDownload(id)
}