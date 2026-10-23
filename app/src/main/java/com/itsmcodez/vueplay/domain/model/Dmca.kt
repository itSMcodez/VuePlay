package com.itsmcodez.vueplay.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DmcaResponse(
    val blockedIds: List<String>
)

// TODO: Implement this Later
/*
@Serializable
data class DmcaResponse(
    val version: Int,
    val updatedAt: String,
    val blocked: List<DmcaEntry>
)

@Serializable
data class DmcaEntry(
    val id: String,
    val reason: String,
    val blockedAt: String
)*/
