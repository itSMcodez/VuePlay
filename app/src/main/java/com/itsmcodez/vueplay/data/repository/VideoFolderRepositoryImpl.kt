package com.itsmcodez.vueplay.data.repository

import android.content.Context
import com.itsmcodez.vueplay.constant.PreferenceKeys
import com.itsmcodez.vueplay.data.source.VideoMediaSource
import com.itsmcodez.vueplay.domain.model.Video
import com.itsmcodez.vueplay.domain.model.VideoFolder
import com.itsmcodez.vueplay.domain.repository.VideoFolderRepository
import com.itsmcodez.vueplay.extension.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoFolderRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val source: VideoMediaSource
) : VideoFolderRepository {
    private val sortOrder = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.VIDEOS_SORT_ORDER_KEY] ?: PreferenceKeys.SortOrder.ASCENDING.name
    }
    private val sortOption = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.VIDEOS_SORT_OPTION_KEY] ?: PreferenceKeys.SortOption.NAME.name
    }

    override suspend fun getVideoFoldersAsync(): List<VideoFolder> = withContext(Dispatchers.IO) {
        val folders = source.get<Video>(context).groupBy { it.folderName }.map { (folderName, videos) ->
            VideoFolder(
                name = folderName,
                path = videos[0].path.substring(0, videos[0].path.lastIndexOf("/")),
                videos = videos
            )
        }
        sortFolders(folders, sortOrder.first(), sortOption.first())
    }

    override fun getVideoFolders(): Flow<List<VideoFolder>> = combine(
        sortOrder,
        sortOption,
        flow { emit(source.get<Video>(context).groupBy { it.folderName }.map { (folderName, videos) ->
            VideoFolder(
                name = folderName,
                path = videos[0].path.substring(0, videos[0].path.lastIndexOf("/")),
                videos = videos
            )
        }) }
    ) { order, option, folders ->
        sortFolders(folders, order, option)
    }

    private fun sortFolders(folders: List<VideoFolder>, order: String, option: String): List<VideoFolder> {
        val sortedFolders = when (option) {
            PreferenceKeys.SortOption.NAME.name -> folders.sortedBy { it.name.lowercase() }
            PreferenceKeys.SortOption.DATE_ADDED.name -> folders.sortedBy { folder ->
                folder.videos.maxOfOrNull { it.dateAdded } ?: 0L
            }
            PreferenceKeys.SortOption.DURATION.name -> folders.sortedBy { folder ->
                folder.videos.sumOf { it.duration }
            }
            else -> folders.sortedBy { it.name.lowercase() }
        }.map { folder ->
            folder.copy(videos = sortVideos(folder.videos, order, option))
        }

        return if (order == PreferenceKeys.SortOrder.DESCENDING.name) {
            sortedFolders.reversed()
        } else {
            sortedFolders
        }
    }

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
}
