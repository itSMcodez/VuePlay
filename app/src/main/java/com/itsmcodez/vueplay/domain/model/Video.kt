package com.itsmcodez.vueplay.domain.model

import android.net.Uri

data class Video(
    val id: Long,
    val displayName: String,
    val folderName: String,
    val path: String,
    val duration: Long,
    val dateAdded: Long,
    val uri: Uri
)
