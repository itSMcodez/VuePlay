package com.itsmcodez.vueplay.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.itsmcodez.vueplay.domain.model.Movie
import com.itsmcodez.vueplay.domain.model.MovieAndTvSeriesMulti
import com.itsmcodez.vueplay.domain.model.TvSeries
import com.itsmcodez.vueplay.ui.component.vueui.VueDotProgressIndicator
import com.itsmcodez.vueplay.ui.theme.VuePlayTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeaturedMoviesAndTvSeriesCarousel(
    modifier: Modifier = Modifier,
    autoScrollInterval: Long = 0L,
    hapticsEnabled: Boolean = false,
    featuredList: List<MovieAndTvSeriesMulti>,
    onGetPosterUrl: (MovieAndTvSeriesMulti) -> String,
    onFeaturedItemClicked: (MovieAndTvSeriesMulti) -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val state = rememberCarouselState { featuredList.size }
    val scope = rememberCoroutineScope()

    if (autoScrollInterval > 0L && featuredList.isNotEmpty()) {
        LaunchedEffect(autoScrollInterval, featuredList) {
            while (true) {
                delay(autoScrollInterval.milliseconds)
                if (state.isScrollInProgress) continue
                val nextTarget = (state.currentItem + 1) % featuredList.size
                state.animateScrollToItem(nextTarget)
            }
        }
    }

    Column(
        modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalCenteredHeroCarousel(
            modifier = Modifier.weight(1f),
            state = state,
            itemSpacing = 8.dp
        ) {
            val featuredItem = featuredList[it]

            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(0.7f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onFeaturedItemClicked(featuredItem) }
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .aspectRatio(1f),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(onGetPosterUrl(featuredItem))
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.50f)
                        .align(Alignment.BottomCenter)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.scrim.copy(alpha = 0.0f),
                                    MaterialTheme.colorScheme.scrim
                                )
                            )
                        )
                )

                Box(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(vertical = 24.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = when (featuredItem) {
                            is Movie -> featuredItem.title
                            is TvSeries -> featuredItem.name
                        },
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }
            }
        }

        VueDotProgressIndicator(
            modifier = Modifier
                .padding(vertical = 16.dp),
            carouselState = state,
            itemCount = featuredList.size,
            onPageSelected = { index ->
                if (hapticsEnabled) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                scope.launch { state.animateScrollToItem(index) }
            }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun FeaturedMoviesAndTvSeriesCarouselPreview(modifier: Modifier = Modifier) {
    VuePlayTheme {
        FeaturedMoviesAndTvSeriesCarousel(
            modifier = modifier,
            featuredList = listOf(
                Movie(
                    movieId = "TODO()",
                    imdbId = "TODO()",
                    title = "Hello Movie",
                    overview = "TODO()",
                    tagline = "TODO()",
                    posterPath = "TODO()",
                    backdropPath = "TODO()",
                    releaseDate = "TODO()",
                    originalLanguage = "TODO()",
                    duration = 0,
                    homepage = "TODO()",
                    status = "TODO()",
                    genres = emptyList(),
                    budget = 0,
                    voteAverage = 0.0,
                    voteCount = 0,
                    adult = false,
                    video = false
                ),
                Movie(
                    movieId = "TODO()",
                    imdbId = "TODO()",
                    title = "Hello Movie",
                    overview = "TODO()",
                    tagline = "TODO()",
                    posterPath = "TODO()",
                    backdropPath = "TODO()",
                    releaseDate = "TODO()",
                    originalLanguage = "TODO()",
                    duration = 0,
                    homepage = "TODO()",
                    status = "TODO()",
                    genres = emptyList(),
                    budget = 0,
                    voteAverage = 0.0,
                    voteCount = 0,
                    adult = false,
                    video = false
                ),
                TvSeries(
                    seriesId = "TODO()",
                    name = "Hello Movie",
                    overview = "TODO()",
                    tagline = "TODO()",
                    posterPath = "TODO()",
                    backdropPath = "TODO()",
                    originalLanguage = "TODO()",
                    homepage = "TODO()",
                    status = "TODO()",
                    genres = emptyList(),
                    episodeRuntime = emptyList(),
                    seasons = emptyList(),
                    voteAverage = 0.0,
                    voteCount = 0,
                    numberOfSeasons = 0,
                    numberOfEpisodes = 0,
                    adult = false,
                    firstAirDate = "TODO()",
                )
            ),
            onGetPosterUrl = {""}
        ) {}
    }
}
