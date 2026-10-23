package com.itsmcodez.vueplay.presentation.streaming.details

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.constant.contentRadius
import com.itsmcodez.vueplay.domain.model.Movie
import com.itsmcodez.vueplay.domain.model.TvSeries
import com.itsmcodez.vueplay.manager.ClickEvent
import com.itsmcodez.vueplay.manager.VuePlayAnalyticsManager
import com.itsmcodez.vueplay.presentation.dmca.DmcaViewModel
import com.itsmcodez.vueplay.ui.component.CopyRightReportDialog
import com.itsmcodez.vueplay.ui.component.DownloadNoticeDialog
import com.itsmcodez.vueplay.ui.component.MovieAndTvSeriesActionBar
import com.itsmcodez.vueplay.ui.component.NavigableSectionHeader
import com.itsmcodez.vueplay.ui.component.TvSeriesSelectionDialog
import com.itsmcodez.vueplay.ui.component.grid.MoviesAndTvSeriesSectionGridItem
import com.itsmcodez.vueplay.ui.component.vueui.VueButton
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieAndTvSeriesDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: MovieAndTvSeriesDetailsViewModel,
    dmcaViewModel: DmcaViewModel,
    onNavigateToMovieAndTvSeriesDetailsScreen: (
        tmdbId: String,
        isTvSeries: Boolean,
        sectionId: String,
        sectionTitle: String
    ) -> Unit,
    onNavigateToMovieAndTvSeriesDownloadScreen: (
        tmdbId: String,
        isTvSeries: Boolean,
        season: Int,
        episode: Int
    ) -> Unit,
    onNavigateToMovieAndTvSeriesPlaybackScreen: (
        tmdbId: String,
        season: Int,
        episode: Int,
        isTvSeries: Boolean
    ) -> Unit,
    onNavigateToMoreResultsScreen: (sectionId: String, sectionTitle: String) -> Unit,
    onNavigateToMoviesAndTvSeriesSearchScreen: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentMovieAndTvSeriesMulti = viewModel.currentMovieAndTvSeriesMulti.collectAsStateWithLifecycle().value ?: return
    val similarList by viewModel.similarMovieAndTvSeriesMultiList.collectAsStateWithLifecycle()
    val recommendedList by viewModel.recommendedMovieAndTvSeriesMultiList.collectAsStateWithLifecycle()
    val isTvSeries = viewModel.isTvSeries
    var isBlocked by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showDownloadNoticeDialog by remember { mutableStateOf(false) }
    var showTvSeriesSelectionDialog by remember { mutableStateOf(false) }

    var currentSeason by remember { mutableIntStateOf(-1) }
    var currentSeasonEpisode by remember(currentSeason) { mutableIntStateOf(-1) }

    LaunchedEffect(currentMovieAndTvSeriesMulti) {
        isBlocked = dmcaViewModel.isTmdbIdBlocked(
            when(currentMovieAndTvSeriesMulti) {
                is Movie -> currentMovieAndTvSeriesMulti.movieId
                is TvSeries -> currentMovieAndTvSeriesMulti.seriesId
            }
        )
    }

    if(showReportDialog) {
        CopyRightReportDialog(
            title = stringResource(R.string.title_dialog_report_copyright),
            message = stringResource(R.string.msg_dialog_report_copyright),
            onDismiss = { showReportDialog = false },
            onSubmitReport = {
                reportCopyright(
                    context,
                    when(currentMovieAndTvSeriesMulti) {
                        is Movie -> currentMovieAndTvSeriesMulti.title
                        is TvSeries -> currentMovieAndTvSeriesMulti.name
                    },
                    when(currentMovieAndTvSeriesMulti) {
                        is Movie -> currentMovieAndTvSeriesMulti.movieId
                        is TvSeries -> currentMovieAndTvSeriesMulti.seriesId
                    }
                )
                showReportDialog = false
            }
        )
    }

    if(
        showTvSeriesSelectionDialog && isTvSeries
        && currentMovieAndTvSeriesMulti is TvSeries
    ) {
        TvSeriesSelectionDialog(
            seasons = currentMovieAndTvSeriesMulti.seasons,
            onSeasonSelected = { currentSeason = it.seasonNumber },
            onEpisodeSelected = { season, episode ->
                currentSeason = season.seasonNumber
                currentSeasonEpisode = episode
            },
            onSave = {
                showTvSeriesSelectionDialog = false
                showDownloadNoticeDialog = true
            },
            onDismiss = { showTvSeriesSelectionDialog = false }
        )
    }

    if(showDownloadNoticeDialog) {
        DownloadNoticeDialog(
            onProceed = {
                onNavigateToMovieAndTvSeriesDownloadScreen(
                    when(currentMovieAndTvSeriesMulti) {
                        is Movie -> currentMovieAndTvSeriesMulti.movieId
                        is TvSeries -> currentMovieAndTvSeriesMulti.seriesId
                    },
                    currentMovieAndTvSeriesMulti is TvSeries,
                    currentSeason,
                    currentSeasonEpisode
                )
                showDownloadNoticeDialog = false
            },
            onDismiss = { showDownloadNoticeDialog = false }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Details Header
            item(key = "details_header") {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface
                        )
                ) {

                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = ImageRequest.Builder(context)
                            .data(
                                when (currentMovieAndTvSeriesMulti) {
                                    is Movie -> currentMovieAndTvSeriesMulti.backdropPath
                                    is TvSeries -> currentMovieAndTvSeriesMulti.backdropPath
                                }
                            )
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface.copy(0.85f))
                    )

                    Column(
                        modifier = Modifier
                            .windowInsetsPadding(
                                WindowInsets(
                                    top = TopAppBarDefaults.TopAppBarExpandedHeight
                                )
                            )
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                Modifier
                                    .padding(horizontal = 8.dp)
                                    .width(88.dp)
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
                                    model = ImageRequest.Builder(context)
                                        .data(
                                            when (currentMovieAndTvSeriesMulti) {
                                                is Movie -> currentMovieAndTvSeriesMulti.posterPath
                                                is TvSeries -> currentMovieAndTvSeriesMulti.posterPath
                                            }
                                        )
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop
                                )

                                // Movie and Tv Badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(horizontal = 8.dp)
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .background(Color.Black.copy(0.5f)),
                                        painter = painterResource(
                                            when(currentMovieAndTvSeriesMulti) {
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
                                    currentMovieAndTvSeriesMulti is Movie && currentMovieAndTvSeriesMulti.adult
                                    || currentMovieAndTvSeriesMulti is TvSeries && currentMovieAndTvSeriesMulti.adult
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(horizontal = 8.dp)
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

                            Column(
                                modifier = Modifier.wrapContentHeight().weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = when (currentMovieAndTvSeriesMulti) {
                                        is Movie -> currentMovieAndTvSeriesMulti.title
                                        is TvSeries -> currentMovieAndTvSeriesMulti.name
                                    },
                                    style = MaterialTheme.typography.titleLarge,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = when (currentMovieAndTvSeriesMulti) {
                                        is Movie -> currentMovieAndTvSeriesMulti.tagline
                                        is TvSeries -> currentMovieAndTvSeriesMulti.tagline
                                    },
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                                )

                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    when (currentMovieAndTvSeriesMulti) {
                                        is Movie -> {
                                            Text(
                                                text = currentMovieAndTvSeriesMulti.releaseDate,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                                            )
                                            Text(
                                                text = "${currentMovieAndTvSeriesMulti.duration}mins",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                                            )
                                            Text(
                                                text = "${currentMovieAndTvSeriesMulti.voteAverage.roundToInt()}/10-rating",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                                            )
                                            Text(
                                                text = currentMovieAndTvSeriesMulti.status,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                                            )
                                        }
                                        is TvSeries -> {
                                            Text(
                                                text = currentMovieAndTvSeriesMulti.firstAirDate,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                                            )
                                            var totalDurationSum: Int by remember { mutableIntStateOf(0) }
                                            LaunchedEffect(currentMovieAndTvSeriesMulti.episodeRuntime) {
                                                currentMovieAndTvSeriesMulti.episodeRuntime.forEach { totalDurationSum += it }
                                            }
                                            Text(
                                                text = "${totalDurationSum}mins",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                                            )
                                            Text(
                                                text = "${currentMovieAndTvSeriesMulti.voteAverage.roundToInt()}/10-rating",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                                            )
                                            Text(
                                                text = currentMovieAndTvSeriesMulti.status,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                                            )
                                        }
                                    }
                                }

                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    when(currentMovieAndTvSeriesMulti) {
                                        is Movie -> {
                                            currentMovieAndTvSeriesMulti.genres.forEach { genre ->
                                                Text(
                                                    modifier = Modifier
                                                        .background(
                                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                                            shape = RoundedCornerShape(
                                                                contentRadius
                                                            )
                                                        )
                                                        .padding(
                                                            horizontal = 8.dp,
                                                            vertical = 4.dp
                                                        ),
                                                    text = genre.name,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                                                )
                                            }
                                        }
                                        is TvSeries -> {
                                            currentMovieAndTvSeriesMulti.genres.forEach { genre ->
                                                Text(
                                                    modifier = Modifier
                                                        .background(
                                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                                            shape = RoundedCornerShape(
                                                                contentRadius
                                                            )
                                                        )
                                                        .padding(
                                                            horizontal = 8.dp,
                                                            vertical = 4.dp
                                                        ),
                                                    text = genre.name,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                        }

                        MovieAndTvSeriesActionBar(
                            movieAndTvSeriesMulti = currentMovieAndTvSeriesMulti
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextButton(onClick = {
                                val openHomepage: (String) -> Unit = {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        it.toUri()
                                    )
                                    context.startActivity(intent)
                                }
                                when(currentMovieAndTvSeriesMulti) {
                                    is Movie -> openHomepage(currentMovieAndTvSeriesMulti.homepage)
                                    is TvSeries -> openHomepage(currentMovieAndTvSeriesMulti.homepage)
                                }
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.rounded_link_2_24),
                                    contentDescription = null
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(text = stringResource(R.string.homepage))
                            }

                            Spacer(Modifier.weight(1f))

                            VueButton(
                                onClick = {
                                    val season = if(currentMovieAndTvSeriesMulti is TvSeries)
                                        currentMovieAndTvSeriesMulti.seasons.firstOrNull()
                                    else null
                                    onNavigateToMovieAndTvSeriesPlaybackScreen(
                                        when(currentMovieAndTvSeriesMulti) {
                                            is Movie -> currentMovieAndTvSeriesMulti.movieId
                                            is TvSeries -> currentMovieAndTvSeriesMulti.seriesId
                                        },
                                        when(currentMovieAndTvSeriesMulti) {
                                            is Movie -> -1
                                            is TvSeries -> season?.seasonNumber ?: -1
                                        },
                                        when(currentMovieAndTvSeriesMulti) {
                                            is Movie -> -1
                                            is TvSeries -> if (season != null) 1 else -1
                                        },
                                        currentMovieAndTvSeriesMulti is TvSeries,
                                    )

                                    VuePlayAnalyticsManager.logClickEvent(
                                        ClickEvent.WATCH_BUTTON
                                    )
                                },
                                enabled = isBlocked.not(),
                                shape = RoundedCornerShape(contentRadius)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.round_play_arrow_24),
                                    contentDescription = null
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(text = stringResource(R.string.watch))
                            }

                            var showMoreMenu by remember { mutableStateOf(false) }
                            Box {
                                IconButton(enabled = isBlocked.not(), onClick = { showMoreMenu = !showMoreMenu }) {
                                    Icon(
                                        painter = painterResource(R.drawable.rounded_more_horiz_24),
                                        contentDescription = "More"
                                    )
                                }
                                DropdownMenu(
                                    expanded = showMoreMenu,
                                    onDismissRequest = { showMoreMenu = false },
                                    shape = RoundedCornerShape(contentRadius * 2),
                                    border = BorderStroke(
                                        width = Dp.Hairline,
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )
                                ) {
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.rounded_download_24),
                                                contentDescription = null
                                            )
                                        },
                                        text = { Text(text = stringResource(R.string.title_menu_item_download)) },
                                        onClick = {
                                            if (isTvSeries && currentMovieAndTvSeriesMulti is TvSeries)
                                                showTvSeriesSelectionDialog = true
                                            else
                                                showDownloadNoticeDialog = true

                                            showMoreMenu = false

                                            VuePlayAnalyticsManager.logClickEvent(
                                                ClickEvent.DOWNLOAD_MENU_ITEM
                                            )
                                        }
                                    )
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.rounded_report_24),
                                                contentDescription = null
                                            )
                                        },
                                        text = { Text(text = stringResource(R.string.title_menu_item_report)) },
                                        onClick = {
                                            showReportDialog = true
                                            showMoreMenu = false

                                            VuePlayAnalyticsManager.logClickEvent(
                                                ClickEvent.REPORT_MENU_ITEM
                                            )
                                        }
                                    )
                                }
                            }

                        }

                        if (isBlocked) {
                            Text(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                text = stringResource(R.string.blocked_content),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                            )
                        }

                    }
                }
            }

            // Overview
            item(key = "overview") {
                Box(Modifier.clickable {  }) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.overview),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = when(currentMovieAndTvSeriesMulti) {
                                is Movie -> currentMovieAndTvSeriesMulti.overview
                                is TvSeries -> currentMovieAndTvSeriesMulti.overview
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            DETAILS_ROW_SECTIONS.fastForEach { section ->
                item(key = section.id) {
                    Column {
                        val rowListState = rememberLazyListState()
                        val onSectionHeaderClick = {
                            onNavigateToMoreResultsScreen(section.id, section.title)
                        }

                        NavigableSectionHeader(
                            title = section.title,
                            subtitle = stringResource(R.string.see_more),
                            scrollToLastEnabled = rowListState.canScrollForward,
                            scrollToFirstEnabled = rowListState.canScrollBackward,
                            onScrollToFirstItem = {
                                scope.launch {
                                    rowListState.animateScrollToItem(0)
                                }
                            },
                            onScrollToLastItem = {
                                onSectionHeaderClick()
                                // TODO("Replace scroll to last item with open genre page or more results page")
                                scope.launch {
                                    rowListState.animateScrollToItem(
                                        rowListState.layoutInfo.totalItemsCount - 1
                                    )
                                }
                            },
                            icon = null,
                            onClick = onSectionHeaderClick
                        )

                        val moviesAndTvSeriesList = when(section.id) {
                            similarSectionId -> similarList
                            recommendedSectionId -> recommendedList
                            else -> emptyList()
                        }

                        if(moviesAndTvSeriesList.isEmpty()) {
                            Box(Modifier
                                .fillMaxWidth()
                                .height(200.dp), Alignment.Center) {
                                CircularProgressIndicator()
                            }

                        } else {
                            LazyRow(state = rowListState) {
                                itemsIndexed(
                                    items = moviesAndTvSeriesList,
                                    key = {_, it ->
                                        when(it) {
                                            is Movie -> it.movieId
                                            is TvSeries -> it.seriesId
                                        }
                                    }
                                ) { index, it ->
                                    MoviesAndTvSeriesSectionGridItem(
                                        modifier = Modifier.animateItem(),
                                        movieAndTvSeriesMulti = it,
                                        section = null,
                                        index = index
                                    ) {
                                        onNavigateToMovieAndTvSeriesDetailsScreen(
                                            when(it) {
                                                is Movie -> it.movieId
                                                is TvSeries -> it.seriesId
                                            },
                                            it is TvSeries,
                                            section.id,
                                            section.title
                                        )
                                    }
                                }
                            }
                        }

                    }
                }
            }


        }
    }

    TopAppBar(
        title = {  },
        navigationIcon = {
            FilledIconButton(
                onClick = onBack,
                colors = IconButtonDefaults.filledIconButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.rounded_arrow_back_24),
                    contentDescription = null
                )
            }
        },
        actions = {
            FilledIconButton(
                onClick = onNavigateToMoviesAndTvSeriesSearchScreen,
                colors = IconButtonDefaults.filledIconButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.rounded_search_24),
                    contentDescription = null
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    )

}

const val similarSectionId = "similar"
const val recommendedSectionId = "recommended"

@Immutable
data class DetailsRowSection(
    val title: String,
    val id: String
)

val DETAILS_ROW_SECTIONS = listOf(
    DetailsRowSection("Similar To This", similarSectionId),
    DetailsRowSection("Recommended For You", recommendedSectionId)
)

fun reportCopyright(
    context: Context,
    title: String,
    contentId: String,
) {
    val contact = "contact@vueplay.cc"

    val subject = "Copyright Infringement Report — $title"

    val body = """
        Hello VuePlay Copyright Team,

        I am submitting a copyright infringement report regarding the following content:

        Content Title: $title
        Content ID: $contentId

        Copyright Owner / Authorized Reporter:
        Name: [Insert full name]
        Email: [Insert email address]

        Identification of the Copyrighted Work:
        [Briefly describe the copyrighted work and provide any relevant information that identifies it.]

        Proof of Ownership:
        [Insert URL, registration number, document, or other evidence of ownership.]

        Location of the Allegedly Infringing Content:
        [Insert URL or other information that allows the content to be identified.]

        Good Faith Statement:
        I have a good faith belief that the use of the reported material is not authorized by the copyright owner, its agent, or the law.

        Accuracy Statement:
        I confirm that the information provided in this report is accurate and correct, and that I am the copyright owner or authorized to act on behalf of the copyright owner.

        Electronic Signature:
        [Type full legal name as your electronic signature]

        Please review the reported content and take appropriate action if infringement is confirmed.

        Thank you.
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf(contact))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        // open text editor
        val editorIntent = Intent(Intent.ACTION_EDIT).apply {
            data = "mailto:$contact\n\n$subject\n$body".toUri()
        }
        if (editorIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(editorIntent)
        } else {
            Toast.makeText(
                context,
                "No email app found, contact support directly: $contact",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
