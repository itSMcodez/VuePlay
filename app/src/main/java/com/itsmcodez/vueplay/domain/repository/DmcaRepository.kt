package com.itsmcodez.vueplay.domain.repository

interface DmcaRepository {
    suspend fun refresh()
    suspend fun isBlocked(id: String): Boolean
}