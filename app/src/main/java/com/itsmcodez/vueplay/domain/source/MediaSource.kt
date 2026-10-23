package com.itsmcodez.vueplay.domain.source

import android.content.Context

interface MediaSource {
    suspend fun <T> get(context: Context): List<T>
}