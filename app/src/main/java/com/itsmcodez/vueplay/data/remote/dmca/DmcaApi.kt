package com.itsmcodez.vueplay.data.remote.dmca

import com.itsmcodez.vueplay.domain.model.DmcaResponse
import retrofit2.http.GET

interface DmcaApi {
    @GET("itSMcodez/vueplay-dmca/main/dmca.json")
    suspend fun getDmca(): DmcaResponse
}