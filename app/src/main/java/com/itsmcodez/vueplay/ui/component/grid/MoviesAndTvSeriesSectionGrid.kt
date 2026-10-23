package com.itsmcodez.vueplay.ui.component.grid

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.constant.contentRadius
import com.itsmcodez.vueplay.domain.model.Movie
import com.itsmcodez.vueplay.domain.model.MovieAndTvSeriesMulti
import com.itsmcodez.vueplay.domain.model.TvSeries
import com.itsmcodez.vueplay.presentation.home.HomeRowSection
import com.itsmcodez.vueplay.presentation.home.topRatedSectionId
import com.itsmcodez.vueplay.ui.component.vueui.VueButton
import com.itsmcodez.vueplay.ui.component.vueui.VueButtonDefaults
import com.itsmcodez.vueplay.ui.component.vueui.VueButtonState


@Composable
fun MoviesAndTvSeriesSectionGrid(
    modifier: Modifier = Modifier,
    moviesAndTvSeriesList: List<MovieAndTvSeriesMulti?>,
    section: HomeRowSection?,
    state: LazyGridState,
    showLoadMoreResultsButton: Boolean = false,
    resultsLoading: Boolean = false,
    onLoadMoreResultsButtonClick: () -> Unit,
    onItemClick: (MovieAndTvSeriesMulti) -> Unit
) {
    var loadMoreResultsButtonState by remember(resultsLoading) {
        mutableStateOf(
            if (resultsLoading) VueButtonState.LOADING
            else VueButtonState.NORMAL
        )
    }

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(minSize = 88.dp),
        state = state
    ) {

        itemsIndexed(
            items = moviesAndTvSeriesList,
            key = {_, it ->
                when(it) {
                    is Movie -> it.movieId
                    is TvSeries -> it.seriesId
                    else -> ""
                }
            }
        ) { index, it ->
            if (it != null)
                MoviesAndTvSeriesSectionGridItem(
                    modifier = Modifier.animateItem(),
                    movieAndTvSeriesMulti = it,
                    section = section,
                    index = index,
                    onClick = onItemClick
                )
        }

        if (moviesAndTvSeriesList.isNotEmpty() && showLoadMoreResultsButton) {
            item(
                key = "load_more_results_button",
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    VueButton(
                        style = VueButtonDefaults.Style.stateful(
                            currentState = loadMoreResultsButtonState
                        ),
                        onClick = onLoadMoreResultsButtonClick
                    ) { Text(text = stringResource(R.string.load_more_results)) }
                }
            }
        }
    }
}

@Composable
fun MoviesAndTvSeriesSectionGridItem(
    modifier: Modifier = Modifier,
    movieAndTvSeriesMulti: MovieAndTvSeriesMulti,
    section: HomeRowSection?,
    index: Int,
    onClick: (MovieAndTvSeriesMulti) -> Unit
) {

    Box(
        modifier = modifier
            .clickable { onClick(movieAndTvSeriesMulti) }
    ) {
        Column(
            Modifier
                .padding(8.dp)
                .width(88.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(
                            0.7f
                        ),
                        shape = RoundedCornerShape(
                            contentRadius
                        )
                    )
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(contentRadius)
                    )
                    .clip(
                        RoundedCornerShape(
                            contentRadius
                        )
                    )
                    .aspectRatio(9f / 16f),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(
                        when (movieAndTvSeriesMulti) {
                            is Movie -> movieAndTvSeriesMulti.posterPath
                            is TvSeries -> movieAndTvSeriesMulti.posterPath
                        }
                    )
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            Text(
                text = if (movieAndTvSeriesMulti is Movie) movieAndTvSeriesMulti.title else (movieAndTvSeriesMulti as TvSeries).name,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Top 10 Badge
        if (section?.id == topRatedSectionId && index in 0..9) {
            Image(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopStart),
                painter = painterResource(
                    when(index) {
                        0 -> R.drawable.number_one
                        1 -> R.drawable.number_two
                        2 -> R.drawable.number_three
                        3 -> R.drawable.number_four
                        4 -> R.drawable.number_five
                        5 -> R.drawable.number_six
                        6 -> R.drawable.number_seven
                        7 -> R.drawable.number_eight
                        8 -> R.drawable.number_nine
                        9 -> R.drawable.number_ten
                        else -> R.drawable.number_one
                    }
                ),
                contentDescription = null
            )
        }

        // Movie and Tv Badge
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
        ) {
            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .background(Color.Black.copy(0.5f)),
                painter = painterResource(
                    when(movieAndTvSeriesMulti) {
                        is Movie -> R.drawable.rounded_movie_24
                        is TvSeries -> R.drawable.rounded_tv_24
                    }
                ),
                contentDescription = null,
                tint = Color.White
            )
        }

        // Adult badge
        if (
            movieAndTvSeriesMulti is Movie && movieAndTvSeriesMulti.adult
            || movieAndTvSeriesMulti is TvSeries && movieAndTvSeriesMulti.adult
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    )
            ) {
                Text(
                    modifier = Modifier.background(Color.Black.copy(0.5f)),
                    text = "18+",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
            }
        }
    }
}

