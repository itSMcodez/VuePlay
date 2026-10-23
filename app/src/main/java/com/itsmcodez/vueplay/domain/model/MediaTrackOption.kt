package com.itsmcodez.vueplay.domain.model

data class MediaTrackOption(
    val label: String,
    val language: String,
    val groupIndex: Int,
    val trackIndex: Int,
    val isSelected: Boolean
)