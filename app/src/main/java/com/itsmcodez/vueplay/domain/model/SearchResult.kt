package com.itsmcodez.vueplay.domain.model

sealed interface SearchResult {
    val title: String
    val origin: String

    data class VideoItem(
        val video: Video,
        override val title: String = video.displayName,
        override val origin: String = video.path
    ) : SearchResult

    data class VideoFolderItem(
        val videoFolder: VideoFolder,
        override val title: String = videoFolder.name,
        override val origin: String = videoFolder.path
    ) : SearchResult
}