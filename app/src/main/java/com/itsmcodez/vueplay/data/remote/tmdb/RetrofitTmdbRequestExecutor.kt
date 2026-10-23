package com.itsmcodez.vueplay.data.remote.tmdb

import com.itsmcodez.themoviedbapi.model.core.responses.TmdbResponseException
import com.itsmcodez.themoviedbapi.tools.RequestType
import com.itsmcodez.themoviedbapi.tools.TmdbRequest
import com.itsmcodez.themoviedbapi.tools.TmdbRequestExecutor
import com.itsmcodez.themoviedbapi.tools.TmdbResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

internal class RetrofitTmdbRequestExecutor(
    private val api: RetrofitTmdbApi
) : TmdbRequestExecutor {

    companion object {
        private const val MAX_RATE_LIMIT_RETRIES = 3
        private const val DEFAULT_RETRY_DELAY_SECONDS = 1L
    }

    override fun execute(
        request: TmdbRequest
    ): TmdbResponse {

        for (attempt in 0..MAX_RATE_LIMIT_RETRIES) {

            val response = executeRequest(request)

            if (response.code() != 429 ||
                attempt == MAX_RATE_LIMIT_RETRIES
            ) {
                return TmdbResponse(
                    response.code(),
                    response.body()?.string().orEmpty()
                )
            }

            val retryAfter = response
                .headers()["Retry-After"]
                ?.trim()
                ?.toLongOrNull()
                ?: DEFAULT_RETRY_DELAY_SECONDS

            response.body()?.close()

            try {
                Thread.sleep(
                    retryAfter * 1_000L
                )
            } catch (exception: InterruptedException) {
                Thread.currentThread().interrupt()
                throw TmdbResponseException(exception)
            }
        }

        error("Unreachable")
    }

    private fun executeRequest(
        request: TmdbRequest
    ): retrofit2.Response<okhttp3.ResponseBody> {

        return try {
            when (request.requestType()) {

                RequestType.GET ->
                    api.get(request.url()).execute()

                RequestType.POST -> {

                    val body = request.jsonBody()
                        ?.toRequestBody(
                            "application/json".toMediaType()
                        )

                    api.post(
                        request.url(),
                        body
                    ).execute()
                }

                RequestType.DELETE ->
                    api.delete(request.url()).execute()
            }

        } catch (exception: Exception) {
            throw TmdbResponseException(exception)
        }
    }
}