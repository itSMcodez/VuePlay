package com.itsmcodez.vueplay.data.repository

import com.itsmcodez.vueplay.domain.repository.MovieAndTvSeriesDownloadRepository
import com.itsmcodez.vueplay.manager.VuePlayDownloadManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieAndTvSeriesDownloadRepositoryImpl @Inject constructor(
    private val downloadManager: VuePlayDownloadManager
) : MovieAndTvSeriesDownloadRepository {
    override val downloads
        get() = downloadManager.downloads

    override fun startDownload(url: String, fileName: String, userAgent: String?): String = downloadManager.startDownload(url, fileName, userAgent)
    override fun pauseDownload(id: String) = downloadManager.pauseDownload(id)
    override fun resumeDownload(id: String, fileName: String, userAgent: String?) = downloadManager.resumeDownload(id, userAgent)
    override fun deleteDownload(id: String) = downloadManager.deleteDownload(id)
}