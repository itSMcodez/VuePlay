package com.itsmcodez.vueplay.data.remote.tmdb

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

internal interface RetrofitTmdbApi {

    @GET
    fun get(
        @Url url: String
    ): Call<ResponseBody>

    @POST
    fun post(
        @Url url: String,
        @Body body: RequestBody?
    ): Call<ResponseBody>

    @DELETE
    fun delete(
        @Url url: String
    ): Call<ResponseBody>
}