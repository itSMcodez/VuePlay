package com.itsmcodez.vueplay.data.remote.tmdb

import okhttp3.Interceptor
import okhttp3.Response

internal class TmdbAuthInterceptor(
    private val accessToken: String = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI2ZmEwYjkxZjVhNGJiNzBkZDg5YjlmOGM3ZGU5YWU3NCIsIm5iZiI6MTc4MDkwNzc3OS43MSwic3ViIjoiNmEyNjdmMDNlZjllMjVhZWQ1ZTZhZjg5Iiwic2NvcGVzIjpbImFwaV9yZWFkIl0sInZlcnNpb24iOjF9.ePhZ74ZDLIdtoaQzckn71ftbIhtO8eMPtd5DMzk07vw"
) : Interceptor {

    override fun intercept(
        chain: Interceptor.Chain
    ): Response {

        val request = chain.request()
            .newBuilder()
            .header(
                "Authorization",
                "Bearer $accessToken"
            )
            .header(
                "Accept",
                "application/json"
            )
            .build()

        return chain.proceed(request)
    }
}