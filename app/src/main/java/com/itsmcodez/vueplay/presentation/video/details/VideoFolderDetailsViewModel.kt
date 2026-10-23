package com.itsmcodez.vueplay.presentation.video.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.itsmcodez.vueplay.domain.usecase.wrapper.VideoFolderUseCasesWrapper
import com.itsmcodez.vueplay.domain.usecase.wrapper.VideoUseCasesWrapper
import com.itsmcodez.vueplay.presentation.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class VideoFolderDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    videoFolderUseCasesWrapper: VideoFolderUseCasesWrapper,
    videoUseCasesWrapper: VideoUseCasesWrapper
) : ViewModel() {
    private val details = savedStateHandle.toRoute<Destination.VideoFolderDetailsScreen>()
    val folderName = details.folderName
    val path = details.path

    val folderVideos = videoFolderUseCasesWrapper.getVideoFolders()
        .map { folder -> folder.first { it.path == path && it.name == folderName }.videos }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val resumableVideos = videoUseCasesWrapper.getAllResumableVideos()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
}