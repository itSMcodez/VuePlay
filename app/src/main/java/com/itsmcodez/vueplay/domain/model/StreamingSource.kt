package com.itsmcodez.vueplay.domain.model

import com.itsmcodez.vueplay.domain.source.StreamingSourceUrlProvider

data class StreamingSource(
    val id: String,
    val name: String,
    val description: String,
    val sandbox: Boolean,
    val provider: StreamingSourceUrlProvider
)