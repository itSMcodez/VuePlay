package com.itsmcodez.vueplay.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination {

    @Serializable
    object MainScreen : Destination

    @Serializable
    object SearchScreen : Destination

    @Serializable
    data class VideoFolderDetailsScreen(val folderName: String, val path: String) : Destination

    @Serializable
    data class VideoPlaybackScreen(val videoId: Long, val externalUri: String? = null) : Destination

    @Serializable
    object PipVideoPlaybackScreen

    @Serializable
    object SettingsScreen : Destination

    @Serializable
    object HomeScreen : Destination

    @Serializable
    object LocalVideoBrowserScreen : Destination

    @Serializable
    data class MovieAndTvSeriesMoreResultsScreen(val sectionId: String, val sectionTitle: String, val fromDetailsScreen: Boolean) : Destination

    @Serializable
    object MoviesAndTvSeriesSearchScreen : Destination

    @Serializable
    data class MovieAndTvSeriesDetailsScreen(val tmdbId: String, val isTvSeries: Boolean, val sectionId: String, val sectionTitle: String) : Destination

    @Serializable
    data class MovieAndTvSeriesDownloadScreen(val tmdbId: String, val season: Int = 0, val episode: Int = 0, val isTvSeries: Boolean) : Destination

    @Serializable
    data class MovieAndTvSeriesPlaybackScreen(val tmdbId: String, val season: Int = 0, val episode: Int = 0, val isTvSeries: Boolean) : Destination

    @Serializable
    object DownloadsScreen : Destination

}