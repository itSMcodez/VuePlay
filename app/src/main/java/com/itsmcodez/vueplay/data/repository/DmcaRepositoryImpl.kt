package com.itsmcodez.vueplay.data.repository

import com.itsmcodez.vueplay.data.remote.dmca.DmcaApi
import com.itsmcodez.vueplay.domain.repository.DmcaRepository
import com.itsmcodez.vueplay.domain.source.DmcaDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DmcaRepositoryImpl @Inject constructor(
    private val api: DmcaApi,
    private val local: DmcaDataSource
) : DmcaRepository {
    override suspend fun refresh() {
        try {
            val remote = api.getDmca()
            local.save(remote)
        } catch (e: Exception) {
            e.printStackTrace()
            // TODO: Handle network failure
        }
    }

    override suspend fun isBlocked(id: String): Boolean {
        val dmca = local.read() ?: return false
        return dmca.blockedIds.contains(id)
    }
}