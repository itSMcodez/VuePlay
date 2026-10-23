package com.itsmcodez.vueplay.domain.model

data class VideoFolder(
    val name: String,
    val path: String,
    val videos: List<Video>
)
