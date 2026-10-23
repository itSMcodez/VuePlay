package com.itsmcodez.vueplay.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "resumable_videos")
data class ResumableVideo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val videoId: Long,
    val lastPosition: Long
)
