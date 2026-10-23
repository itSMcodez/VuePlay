package com.itsmcodez.vueplay.data.source

import android.content.Context
import com.itsmcodez.vueplay.domain.model.DmcaResponse
import com.itsmcodez.vueplay.domain.source.DmcaDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDmcaDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) : DmcaDataSource {

    private val fileName = "dmca.json"

    override suspend fun save(data: DmcaResponse) {
        val json = Json.encodeToString(data)

        context.openFileOutput(
            fileName,
            Context.MODE_PRIVATE
        ).use {
            it.write(json.toByteArray())
        }
    }

    override suspend fun read(): DmcaResponse? {
        return try {
            val json = context.openFileInput(fileName)
                .bufferedReader()
                .use { it.readText() }

            Json.decodeFromString<DmcaResponse>(json)

        } catch (e: Exception) {
            null
        }
    }
}