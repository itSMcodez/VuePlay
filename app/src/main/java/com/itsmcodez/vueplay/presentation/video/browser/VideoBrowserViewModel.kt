package com.itsmcodez.vueplay.presentation.video.browser

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsmcodez.vueplay.common.MainTopBarMultiSelectionActions
import com.itsmcodez.vueplay.domain.model.Video
import com.itsmcodez.vueplay.domain.usecase.wrapper.VideoUseCasesWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class VideoBrowserViewModel @Inject constructor(
    private val videoUseCasesWrapper: VideoUseCasesWrapper
) : ViewModel() {
    val videos = videoUseCasesWrapper.getVideos()
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            emptyList()
        )

    val resumableVideos = videoUseCasesWrapper.getAllResumableVideos()
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            emptyList()
        )

    private val _selectedVideoIds = MutableStateFlow(emptySet<Long>())
    val selectedVideoIds = _selectedVideoIds.asStateFlow()

    private val _inSelectionMode = MutableStateFlow(false)
    val inSelectionMode = _inSelectionMode.asStateFlow()

    private val _selectionActions = MutableStateFlow(
        emptyList<MainTopBarMultiSelectionActions>()
    )
    val selectionActions = _selectionActions.asStateFlow()

    fun clearSelection() {
        _selectedVideoIds.value = emptySet()
        _selectionActions.value = emptyList()
        _inSelectionMode.value = false
    }

    fun toggleSelection(video: Video) {
        // 1. Build actions
        if(selectionActions.value.isEmpty()) {
            _selectionActions.value = listOf(
                MainTopBarMultiSelectionActions.SHARE,
                MainTopBarMultiSelectionActions.DELETE
            )
        }
        // 2. Add or remove from selection list
        if(_selectedVideoIds.value.contains(video.id)) _selectedVideoIds.value -= video.id
        else _selectedVideoIds.value += video.id
        // 3. Update state
        if (_selectedVideoIds.value.isEmpty()) {
            _selectionActions.value = emptyList()
            _inSelectionMode.value = false
        }
        else _inSelectionMode.value = true
    }

    fun launchVideos() = videoUseCasesWrapper.launchVideos()

    fun renameVideo(
        video: Video,
        newName: String,
        launcher: ActivityResultLauncher<IntentSenderRequest>
    ) = videoUseCasesWrapper.renameVideo(video, newName, launcher)

    fun shareVideos(vararg videos: Video) = videoUseCasesWrapper.shareVideos(*videos)

    fun deleteVideos(
        vararg videos: Video,
        launcher: ActivityResultLauncher<IntentSenderRequest>
    ) = videoUseCasesWrapper.deleteVideos(launcher = launcher, videos = videos)
}