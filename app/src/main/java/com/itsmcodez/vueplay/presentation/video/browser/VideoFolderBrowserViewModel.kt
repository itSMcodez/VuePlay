package com.itsmcodez.vueplay.presentation.video.browser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsmcodez.vueplay.domain.usecase.wrapper.VideoFolderUseCasesWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class VideoFolderBrowserViewModel @Inject constructor(
    videoFolderUseCasesWrapper: VideoFolderUseCasesWrapper
) : ViewModel() {
    val videoFolders = videoFolderUseCasesWrapper.getVideoFolders
        .invoke()
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            emptyList()
        )
}