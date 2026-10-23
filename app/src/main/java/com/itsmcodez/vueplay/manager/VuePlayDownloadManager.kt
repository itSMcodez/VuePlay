package com.itsmcodez.vueplay.manager

import android.content.Context
import android.webkit.CookieManager
import com.itsmcodez.vueplay.data.local.dao.DownloadsDao
import com.itsmcodez.vueplay.data.local.entity.DownloadItem
import com.itsmcodez.vueplay.data.local.entity.DownloadStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VuePlayDownloadManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val downloadsDao: DownloadsDao
) {

    var lastUserAgentString: String? = null

    val downloads = downloadsDao.observeDownloads()

    private val client = OkHttpClient.Builder().build()

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    /**
     * Keeps track of downloads that are currently running.
     *
     * Room stores the persistent state.
     * This map only stores runtime Jobs.
     */
    private val activeJobs = ConcurrentHashMap<String, Job>()

    /**
     * Starts a new download.
     */
    fun startDownload(
        url: String,
        fileName: String,
        userAgent: String?
    ): String {

        val id = UUID.randomUUID().toString()

        val download = DownloadItem(
            id = id,
            url = url,
            fileName = fileName,
            progress = 0f,
            bytesDownloaded = 0L,
            totalBytes = 0L,
            status = DownloadStatus.DOWNLOADING,
            errorMessage = null
        )

        val job = scope.launch {

            downloadsDao.upsert(download)

            runDownload(
                id = id,
                url = url,
                fileName = fileName,
                userAgent = userAgent
            )
        }

        activeJobs[id] = job

        return id
    }

    /**
     * Pauses an active download.
     *
     * The partially downloaded file is intentionally kept so that
     * the download can continue later using HTTP Range requests.
     */
    fun pauseDownload(id: String) {

        val job = activeJobs[id] ?: return

        scope.launch {

            val current = downloadsDao.getDownload(id)
                ?: return@launch

            if (current.status != DownloadStatus.DOWNLOADING) {
                return@launch
            }

            downloadsDao.upsert(
                current.copy(
                    status = DownloadStatus.PAUSING
                )
            )

            job.cancel()
        }
    }

    /**
     * Resumes a paused or failed download.
     *
     * The URL and file name are retrieved from Room.
     */
    fun resumeDownload(id: String, userAgent: String?) {

        if (activeJobs.containsKey(id)) return

        val job = scope.launch {

            val current = downloadsDao.getDownload(id)
                ?: return@launch

            if (
                current.status != DownloadStatus.PAUSED &&
                current.status != DownloadStatus.FAILED
            ) {
                return@launch
            }

            downloadsDao.upsert(
                current.copy(
                    status = DownloadStatus.DOWNLOADING,
                    errorMessage = null
                )
            )

            runDownload(
                id = current.id,
                url = current.url,
                fileName = current.fileName,
                userAgent = userAgent
            )
        }

        activeJobs[id] = job
    }

    /**
     * Deletes both the database record and the downloaded file.
     */
    fun deleteDownload(id: String) {

        // Stop the running download if there is one.
        activeJobs.remove(id)?.cancel()

        scope.launch {

            val download = downloadsDao.getDownload(id)

            if (download != null) {
                val targetFile = getDownloadFile(context, download.fileName)

                if (targetFile.exists()) {
                    targetFile.delete()
                }
            }

            downloadsDao.deleteById(id)
        }
    }

    /**
     * Executes the actual HTTP download.
     */
    private suspend fun runDownload(
        id: String,
        url: String,
        fileName: String,
        userAgent: String?
    ) {

        val targetFile = getDownloadFile(context, fileName)

        try {

            val existingBytes =
                if (targetFile.exists()) {
                    targetFile.length()
                } else {
                    0L
                }

            val requestBuilder = Request.Builder()
                .url(url)
                .addHeader("Referer", "https://vidvault.ru/") // TODO: Replace this hardcoded header

            if (userAgent != null) {
                requestBuilder.header("User-Agent", userAgent).apply {
                    CookieManager
                        .getInstance()
                        .getCookie(url)
                        ?.let { header("Cookie", it) }
                }
                lastUserAgentString = userAgent
            }

            if (existingBytes > 0L) {
                requestBuilder.header(
                    "Range",
                    "bytes=$existingBytes-"
                )
            }

            val request = requestBuilder.build()

            client.newCall(request)
                .execute()
                .use { response ->

                    if (!response.isSuccessful) {
                        throw Exception(
                            "Unexpected server response code: ${response.code}"
                        )
                    }

                    val body = response.body
                        ?: throw Exception("Response body is empty")

                    /*
                     * If we requested a Range but the server responds
                     * with 200 instead of 206, it ignored our Range request.
                     *
                     * In that case we MUST overwrite the existing file
                     * instead of appending the complete response.
                     */
                    val append = response.code == 206

                    val startingBytes =
                        if (append) {
                            existingBytes
                        } else {
                            0L
                        }

                    val serverBytes = body.contentLength()

                    val totalBytes =
                        if (serverBytes > 0L) {
                            startingBytes + serverBytes
                        } else {
                            0L
                        }

                    /*
                     * If the server ignored Range and returned the complete
                     * file, overwrite the existing partial file.
                     */
                    FileOutputStream(
                        targetFile,
                        append
                    ).use { output ->

                        body.byteStream().use { input ->

                            val buffer = ByteArray(8192)

                            var bytesRead: Int
                            var bytesDownloaded = startingBytes

                            var lastPersistedBytes = startingBytes

                            while (
                                input.read(buffer)
                                    .also { bytesRead = it } != -1
                            ) {

                                /*
                                 * CancellationException will normally be
                                 * thrown by the coroutine when pauseDownload()
                                 * cancels the Job.
                                 */
                                currentCoroutineContext().ensureActive()

                                output.write(
                                    buffer,
                                    0,
                                    bytesRead
                                )

                                bytesDownloaded += bytesRead

                                /*
                                 * Don't write to Room for every 8 KB chunk.
                                 *
                                 * Persist approximately every 512 KB.
                                 */
                                val shouldPersist =
                                    bytesDownloaded - lastPersistedBytes >=
                                            PROGRESS_PERSIST_INTERVAL

                                if (shouldPersist) {

                                    persistProgress(
                                        id = id,
                                        url = url,
                                        fileName = fileName,
                                        bytesDownloaded = bytesDownloaded,
                                        totalBytes = totalBytes
                                    )

                                    lastPersistedBytes = bytesDownloaded
                                }
                            }

                            output.flush()

                            /*
                             * Always persist the final number of bytes.
                             */
                            persistProgress(
                                id = id,
                                url = url,
                                fileName = fileName,
                                bytesDownloaded = bytesDownloaded,
                                totalBytes = totalBytes
                            )
                        }
                    }

                    /*
                     * If the download wasn't cancelled, we're done.
                     */
                    val current = downloadsDao.getDownload(id)
                        ?: return

                    downloadsDao.upsert(
                        current.copy(
                            progress = 1f,
                            bytesDownloaded = targetFile.length(),
                            totalBytes = if (totalBytes > 0L) {
                                totalBytes
                            } else {
                                targetFile.length()
                            },
                            status = DownloadStatus.COMPLETED,
                            errorMessage = null
                        )
                    )
                }

        } catch (e: CancellationException) {
            /*
             * Cancellation is expected when pausing/deleting.
             *
             * Don't mark the download as FAILED.
             */
            withContext(NonCancellable) {
                val current = downloadsDao.getDownload(id)
                if (current?.status == DownloadStatus.PAUSING) {
                    downloadsDao.upsert(
                        current.copy(
                            status = DownloadStatus.PAUSED,
                            bytesDownloaded = targetFile.length()
                        )
                    )
                }
            }

            throw e

        } catch (e: Exception) {

            val current = downloadsDao.getDownload(id)

            if (current != null) {

                downloadsDao.upsert(
                    current.copy(
                        status = DownloadStatus.FAILED,
                        bytesDownloaded = targetFile.length(),
                        errorMessage = e.localizedMessage
                    )
                )
            }

        } finally {

            activeJobs.remove(id)
        }
    }

    /**
     * Persists download progress to Room.
     */
    private suspend fun persistProgress(
        id: String,
        url: String,
        fileName: String,
        bytesDownloaded: Long,
        totalBytes: Long
    ) {

        val current = downloadsDao.getDownload(id)
            ?: return

        val progress =
            if (totalBytes > 0L) {
                (bytesDownloaded.toFloat() / totalBytes)
                    .coerceIn(0f, 1f)
            } else {
                0f
            }

        downloadsDao.upsert(
            current.copy(
                url = url,
                fileName = fileName,
                progress = progress,
                bytesDownloaded = bytesDownloaded,
                totalBytes = totalBytes,
                status = DownloadStatus.DOWNLOADING,
                errorMessage = null
            )
        )
    }


    companion object {

        /**
         * Persist progress roughly every 512 KB.
         */
        private const val PROGRESS_PERSIST_INTERVAL =
            512 * 1024L

        /**
         * Returns the location where VuePlay stores downloads.
         */
        fun getDownloadFile(context: Context, fileName: String): File {
            return File(
                context.getExternalFilesDir(null),
                fileName
            )
        }
    }
}

