package com.itsmcodez.vueplay.data.repository

import com.itsmcodez.vueplay.data.source.StreamingSourceProvider
import com.itsmcodez.vueplay.domain.model.StreamingSource
import com.itsmcodez.vueplay.domain.repository.StreamingSourceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreamingSourceRepositoryImpl @Inject constructor(
    private val provider: StreamingSourceProvider
) : StreamingSourceRepository {
    override fun getSources(): Flow<List<StreamingSource>> = provider.sources
    override suspend fun getSourceById(id: String): StreamingSource? = getSources().firstOrNull()?.find { it.id == id }
}