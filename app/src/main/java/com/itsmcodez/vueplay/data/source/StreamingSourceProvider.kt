package com.itsmcodez.vueplay.data.source

import com.itsmcodez.vueplay.domain.model.StreamingSource
import com.itsmcodez.vueplay.domain.source.StreamingSourceUrlProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

const val streamingSourceNflixId = "nflix"
const val streamingSourceVidRiftId = "vidrift"
const val streamingSourceBingrId = "bingr"
const val streamingSourceVidFastId = "vidfast"
const val streamingSourceVidRockId = "vidrock"
const val streamingSourceCineSrcId = "cinesrc"

@Singleton
class StreamingSourceProvider @Inject constructor() {
    val sources: Flow<List<StreamingSource>> = flow {
        emit(
            listOf(
                /*StreamingSource(
                    id = streamingSourceNflixId,
                    name = "VuePlay Server",
                    description = "4K · best",
                    sandbox = false,
                    provider = object : StreamingSourceUrlProvider {
                        override fun getMovieUrl(movieId: String): String {
                            return "https://nflixmovies.app/movie/$movieId"
                        }

                        override fun getTvSeriesUrl(
                            seriesId: String,
                            season: Int,
                            episode: Int
                        ): String {
                            return "https://nflixmovies.app/tv/$seriesId/$season/$episode"
                        }
                    }
                ),*/
                StreamingSource(
                    id = streamingSourceVidFastId,
                    name = "VuePlay Server",
                    description = "4K · Best Quality",
                    sandbox = false,
                    provider = object : StreamingSourceUrlProvider {
                        override fun getMovieUrl(movieId: String): String {
                            return "https://vidfast.vc/movie/$movieId?autoPlay=true&theme=5170FF"
                        }

                        override fun getTvSeriesUrl(
                            seriesId: String,
                            season: Int,
                            episode: Int
                        ): String {
                            return "https://vidfast.vc/tv/$seriesId/$season/$episode?autoPlay=true&nextButton=true&autoNext=true&theme=5170FF"
                        }
                    }
                ),
                StreamingSource(
                    id = streamingSourceVidRiftId,
                    name = "Server 1",
                    description = "High Quality",
                    sandbox = false,
                    provider = object : StreamingSourceUrlProvider {
                        override fun getMovieUrl(movieId: String): String {
                            return "https://embed.vidrift.in/embed/movie/$movieId?brand=VuePlay&brandColor=%855170ff"
                        }

                        override fun getTvSeriesUrl(
                            seriesId: String,
                            season: Int,
                            episode: Int
                        ): String {
                            return "https://embed.vidrift.in/embed/tv/$seriesId/$season/$episode?brand=VuePlay&brandColor=%855170ff"
                        }
                    }
                ),
                StreamingSource(
                    id = streamingSourceBingrId,
                    name = "Server 2",
                    description = "High Quality",
                    sandbox = false,
                    provider = object : StreamingSourceUrlProvider {
                        override fun getMovieUrl(movieId: String): String {
                            return "https://bingr.one/watch/movie/$movieId"
                        }

                        override fun getTvSeriesUrl(
                            seriesId: String,
                            season: Int,
                            episode: Int
                        ): String {
                            return "https://bingr.one/watch/tv/$seriesId/$season/$episode"
                        }
                    }
                ),
                StreamingSource(
                    id = streamingSourceVidRockId,
                    name = "Server 3",
                    description = "High Quality",
                    sandbox = false,
                    provider = object : StreamingSourceUrlProvider {
                        override fun getMovieUrl(movieId: String): String {
                            return "https://vidrock.ru/movie/$movieId"
                        }

                        override fun getTvSeriesUrl(
                            seriesId: String,
                            season: Int,
                            episode: Int
                        ): String {
                            return "https://vidrock.ru/tv/$seriesId/$season/$episode"
                        }
                    }
                ),
                /*StreamingSource(
                    id = streamingSourceCineSrcId,
                    name = "Server 5",
                    description = "High quality",
                    sandbox = true,
                    provider = object : StreamingSourceUrlProvider {
                        override fun getMovieUrl(movieId: String): String {
                            return "https://cinesrc.st/embed/movie/$movieId"
                        }

                        override fun getTvSeriesUrl(
                            seriesId: String,
                            season: Int,
                            episode: Int
                        ): String {
                            return "https://cinesrc.st/embed/tv/$seriesId?s=$season&e=$episode"
                        }
                    }
                )*/
            )
        )
    }
}

