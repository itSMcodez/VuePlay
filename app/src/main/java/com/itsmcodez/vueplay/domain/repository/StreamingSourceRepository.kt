package com.itsmcodez.vueplay.domain.repository

import com.itsmcodez.vueplay.domain.model.StreamingSource
import kotlinx.coroutines.flow.Flow

interface StreamingSourceRepository {
    fun getSources(): Flow<List<StreamingSource>>
    suspend fun getSourceById(id: String): StreamingSource?
}