package com.itsmcodez.vueplay.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsmcodez.vueplay.common.NetworkObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    networkObserver: NetworkObserver
) : ViewModel() {
    val networkStatus = networkObserver.networkStatus
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            NetworkObserver.Status.Unavailable
        )
}