package com.itsmcodez.vueplay.presentation.dmca

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsmcodez.vueplay.domain.repository.DmcaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DmcaViewModel @Inject constructor(
    private val repository: DmcaRepository
) : ViewModel() {

    fun refreshDmca() { viewModelScope.launch { repository.refresh() } }

    init { refreshDmca() }

    suspend fun isTmdbIdBlocked(id: String): Boolean = repository.isBlocked(id)
}