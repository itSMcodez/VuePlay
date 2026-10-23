package com.itsmcodez.vueplay.domain.source

import com.itsmcodez.vueplay.domain.model.DmcaResponse

interface DmcaDataSource {
    suspend fun save(data: DmcaResponse)
    suspend fun read(): DmcaResponse?
}