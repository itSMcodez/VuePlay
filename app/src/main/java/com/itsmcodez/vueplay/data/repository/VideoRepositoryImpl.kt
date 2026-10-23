package com.itsmcodez.vueplay.data.repository

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.ContextCompat
import com.itsmcodez.vueplay.constant.PreferenceKeys
import com.itsmcodez.vueplay.data.local.dao.ResumableVideosDao
import com.itsmcodez.vueplay.data.local.entity.ResumableVideo
import com.itsmcodez.vueplay.data.source.VideoMediaSource
import com.itsmcodez.vueplay.domain.model.Video
import com.itsmcodez.vueplay.domain.repository.VideoRepository
import com.itsmcodez.vueplay.extension.dataStore
import com.itsmcodez.vueplay.extension.getSharedVideoFlow
import com.itsmcodez.vueplay.manager.VideoManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@Singleton
class VideoRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val source: VideoMediaSource,
    private val videoManager: VideoManager,
    private val resumableVideosDao: ResumableVideosDao
) : VideoRepository {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val _videosFlow = MutableStateFlow<List<Video>>(emptyList())

    private val sortOrder = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.VIDEOS_SORT_ORDER_KEY] ?: PreferenceKeys.SortOrder.ASCENDING.name
    }
    private val sortOption = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.VIDEOS_SORT_OPTION_KEY] ?: PreferenceKeys.SortOption.NAME.name
    }

    override fun launch() {
        scope.launch {
            combine(
                getSharedVideoFlow(context.contentResolver, scope).debounce(5.milliseconds),
                sortOrder,
                sortOption
            ) { _, order, option ->
                val videos = source.get<Video>(context)
                sortVideos(videos, order, option)
            }.collect {
                _videosFlow.value = it
            }
        }
    }

    init {
        // check storage permission
        val storagePermission = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_VIDEO else Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(context, storagePermission) == android.content.pm.PackageManager.PERMISSION_GRANTED)
            launch()
    }

    override suspend fun getVideosAsync(): List<Video> = withContext(Dispatchers.IO) {
        val videos = source.get<Video>(context)
        sortVideos(videos, sortOrder.first(), sortOption.first())
    }

    override fun getVideos(): Flow<List<Video>> = _videosFlow

    private fun sortVideos(videos: List<Video>, order: String, option: String): List<Video> {
        val sortedVideos = when (option) {
            PreferenceKeys.SortOption.NAME.name -> videos.sortedBy { it.displayName.lowercase() }
            PreferenceKeys.SortOption.DATE_ADDED.name -> videos.sortedBy { it.dateAdded }
            PreferenceKeys.SortOption.DURATION.name -> videos.sortedBy { it.duration }
            else -> videos.sortedBy { it.displayName.lowercase() }
        }

        return if (order == PreferenceKeys.SortOrder.DESCENDING.name) {
            sortedVideos.reversed()
        } else {
            sortedVideos
        }
    }


    override fun renameVideo(
        video: Video,
        newName: String,
        launcher: ActivityResultLauncher<IntentSenderRequest>
    ) = videoManager.renameVideo(video, newName, launcher)

    override fun shareVideos(vararg videos: Video) = videoManager.shareVideos(*videos)

    override fun deleteVideos(
        vararg videos: Video,
        launcher: ActivityResultLauncher<IntentSenderRequest>
    ) = videoManager.deleteVideos(launcher = launcher, videos = videos)



    override suspend fun getResumableVideo(videoId: Long): ResumableVideo? = resumableVideosDao.getResumableVideo(videoId)

    override fun getAllResumableVideos(): Flow<List<ResumableVideo>> = resumableVideosDao.getAllResumableVideos()

    override suspend fun insertResumableVideo(resumableVideo: ResumableVideo) = resumableVideosDao.insertResumableVideo(resumableVideo)

    override suspend fun updateResumableVideo(resumableVideo: ResumableVideo) = resumableVideosDao.updateResumableVideo(resumableVideo)

    override suspend fun deleteResumableVideo(resumableVideo: ResumableVideo) = resumableVideosDao.deleteResumableVideo(resumableVideo)

    override suspend fun deleteResumableVideoById(videoId: Long) = resumableVideosDao.deleteResumableVideoById(videoId)
}
