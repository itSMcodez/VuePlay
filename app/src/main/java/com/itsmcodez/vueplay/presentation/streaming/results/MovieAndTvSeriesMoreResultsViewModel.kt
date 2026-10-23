package com.itsmcodez.vueplay.presentation.streaming.results

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.itsmcodez.vueplay.common.NetworkObserver
import com.itsmcodez.vueplay.presentation.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MovieAndTvSeriesMoreResultsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    networkObserver: NetworkObserver
) : ViewModel() {
    val info = savedStateHandle.toRoute<Destination.MovieAndTvSeriesMoreResultsScreen>()
    val sectionId = MutableStateFlow(info.sectionId).asStateFlow()
    val sectionTitle = MutableStateFlow(info.sectionTitle).asStateFlow()
    val fromDetailsScreen = MutableStateFlow(info.fromDetailsScreen).asStateFlow()

    val networkStatus = networkObserver.networkStatus
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            NetworkObserver.Status.Unavailable
        )
}