package com.itsmcodez.vueplay.presentation.home

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.common.NetworkObserver.Status
import com.itsmcodez.vueplay.domain.model.Movie
import com.itsmcodez.vueplay.domain.model.MovieAndTvSeriesMulti
import com.itsmcodez.vueplay.domain.model.TvSeries
import com.itsmcodez.vueplay.manager.ClickEvent
import com.itsmcodez.vueplay.manager.VuePlayAnalyticsManager
import com.itsmcodez.vueplay.presentation.streaming.MoviesAndTvSeriesViewModel
import com.itsmcodez.vueplay.presentation.streaming.details.MovieAndTvSeriesDetailsViewModel
import com.itsmcodez.vueplay.presentation.streaming.details.recommendedSectionId
import com.itsmcodez.vueplay.presentation.streaming.details.similarSectionId
import com.itsmcodez.vueplay.ui.component.FeaturedMoviesAndTvSeriesCarousel
import com.itsmcodez.vueplay.ui.component.grid.MoviesAndTvSeriesSectionGrid
import com.itsmcodez.vueplay.ui.component.grid.MoviesAndTvSeriesSectionGridItem
import com.itsmcodez.vueplay.ui.component.NavigableSectionHeader
import com.itsmcodez.vueplay.ui.component.vueui.VueButton
import com.itsmcodez.vueplay.ui.component.vueui.VueColors
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds


@OptIn(FlowPreview::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    moviesAndTvSeriesViewModel: MoviesAndTvSeriesViewModel,
    movieAndTvSeriesDetailsViewModel: MovieAndTvSeriesDetailsViewModel?,
    onNavigateToMovieAndTvSeriesDetailsScreen: (String, Boolean, String, String) -> Unit,
    onNavigateToMoreResultsScreen: (String, String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val networkStatus by viewModel.networkStatus.debounce(800.milliseconds)
        .collectAsStateWithLifecycle(Status.Available)
    val moviesAndTvSeriesListState = rememberLazyListState()
    val trending by moviesAndTvSeriesViewModel.trending.collectAsStateWithLifecycle()

    var isHomeRefreshing by remember { mutableStateOf(false) }
    var showErrorPage by remember { mutableStateOf(false) }

    var currentTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val genreTabs = listOf(
        discoverActionMoviesSectionId,
        discoverAnimeTvSeriesSectionId,
        discoverComedyMoviesSectionId,
        discoverThrillerMoviesSectionId,
        discoverHorrorMoviesSectionId,
        discoverSciFiMoviesSectionId,
        discoverDramaTvSeriesSectionId,
        discoverKoreanDramaTvSeriesSectionId,
        discoverRomanceMoviesSectionId
    )

    val tabs = mutableListOf(
        stringResource(R.string.home)
    ) + HOME_ROW_SECTIONS.filter { rowSection ->
        rowSection.id in genreTabs
    }.map { it.title }

    val getSectionIdFromCurrentTabIndex: () -> String = {
        HOME_ROW_SECTIONS.find {
            tabs[currentTabIndex] == it.title
        }?.id ?: ""
    }

    // HomeScreen tab row
    val genresTabRow: @Composable (() -> Unit) = {
        PrimaryScrollableTabRow(
            modifier = Modifier
                .fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.background,
            selectedTabIndex = currentTabIndex,
            edgePadding = 0.dp,
            indicator = {
                TabRowDefaults.PrimaryIndicator(
                    Modifier.tabIndicatorOffset(
                        currentTabIndex
                    )
                )
            }
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        0.7f
                    ),
                    selected = index == currentTabIndex,
                    text = { Text(tab) },
                    onClick = {
                        currentTabIndex = index
                        VuePlayAnalyticsManager.logClickEvent(
                            ClickEvent.MOVIES_TV_SHOWS_GENRE_TAB_ROW
                        )
                    }
                )
            }
        }
    }


    DisposableEffect(moviesAndTvSeriesViewModel) {
        val onFailure: (Exception) -> Unit = {
            Log.e("HomeScreen", "Error: ${it.message}")
            Toast.makeText(
                context,
                context.getString(R.string.msg_refresh_failed),
                Toast.LENGTH_SHORT
            ).show()
            showErrorPage = true
            isHomeRefreshing = false
        }
        moviesAndTvSeriesViewModel.registerOnFailureCallback(onFailure)

        onDispose {
            moviesAndTvSeriesViewModel.unregisterOnFailureCallback(onFailure)
        }
    }

    BackHandler(currentTabIndex != 0) {
        // Return to Homepage on back press
        currentTabIndex = 0
        Toast.makeText(
            context,
            context.getString(R.string.msg_confirm_app_exit),
            Toast.LENGTH_LONG
        ).show()
    }

    PullToRefreshBox(
        modifier = modifier.fillMaxSize(),
        isRefreshing = isHomeRefreshing,
        onRefresh = {
            isHomeRefreshing = true
            moviesAndTvSeriesViewModel.refresh {
                isHomeRefreshing = false
                showErrorPage = false
            }
        }
    ) {
        Column(Modifier.fillMaxSize()) {

            if (networkStatus == Status.Available && !showErrorPage) {
                when(currentTabIndex) {
                    // Home: mixed genres and trending
                    0 -> LazyColumn(
                        Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        state = moviesAndTvSeriesListState
                    ) {

                        item(key = featuredCarouselSectionId) {
                            val featuredList = trending.shuffled().take(5)
                            if(featuredList.isEmpty()) {
                                Box(Modifier
                                    .fillMaxWidth()
                                    .height(320.dp), Alignment.Center) {
                                    CircularProgressIndicator()
                                }

                            } else {
                                FeaturedMoviesAndTvSeriesCarousel(
                                    autoScrollInterval = 3500L,
                                    hapticsEnabled = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(320.dp),
                                    featuredList = featuredList,
                                    onGetPosterUrl = {
                                        when(it) {
                                            is Movie -> it.backdropPath
                                            is TvSeries -> it.backdropPath
                                        }
                                    }
                                ) {
                                    onNavigateToMovieAndTvSeriesDetailsScreen(
                                        when(it) {
                                            is Movie -> it.movieId
                                            is TvSeries -> it.seriesId
                                        },
                                        it is TvSeries,
                                        featuredCarouselSectionId,
                                        HOME_ROW_SECTIONS.find { that -> that.id == featuredCarouselSectionId }?.title ?: ""
                                    )
                                    VuePlayAnalyticsManager.logClickEvent(
                                        ClickEvent.FEATURED_MOVIES_TV_SHOWS_CAROUSEL
                                    )
                                }
                            }
                        }

                        stickyHeader(key = "home_tab_row") { genresTabRow() }

                        HOME_ROW_SECTIONS.fastForEach { section ->
                            if (section.id == featuredCarouselSectionId) return@fastForEach // featured section is already handled
                            item(key = section.id) {
                                Column {
                                    val rowListState = rememberLazyListState()
                                    val onSectionHeaderClick = {
                                        when(section.id) {
                                            in genreTabs -> {
                                                val tabIndex = tabs.indexOf(section.title)
                                                if(tabIndex != -1) currentTabIndex = tabIndex
                                            }

                                            else -> {
                                                if (
                                                    section.id != latestMovieSectionId &&
                                                    section.id != latestTvSeriesSectionId
                                                )
                                                onNavigateToMoreResultsScreen(section.id, section.title)
                                            }
                                        }
                                    }

                                    NavigableSectionHeader(
                                        title = section.title,
                                        subtitle = if (
                                            section.id == latestMovieSectionId ||
                                            section.id == latestTvSeriesSectionId
                                        ) null else stringResource(R.string.see_more),
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
                                        icon = when(section.id) {
                                            trendingSectionId -> {
                                                {
                                                    Image(
                                                        painter = painterResource(R.drawable.trending),
                                                        contentDescription = null
                                                    )
                                                }
                                            }
                                            topRatedSectionId -> {
                                                {
                                                    Image(
                                                        painter = painterResource(R.drawable.favorite),
                                                        contentDescription = null
                                                    )
                                                }
                                            }
                                            else -> { null }
                                        },
                                        onClick = onSectionHeaderClick
                                    )

                                    val moviesAndTvSeriesList = getMoviesAndTvSeriesBySectionId(section.id, moviesAndTvSeriesViewModel)

                                    if(moviesAndTvSeriesList.isEmpty()) {
                                        Box(Modifier
                                            .fillMaxWidth()
                                            .height(200.dp), Alignment.Center) {
                                            CircularProgressIndicator()
                                        }

                                    } else {
                                        LazyRow(state = rowListState) {
                                            itemsIndexed(
                                                items = if (section.id == topRatedSectionId) moviesAndTvSeriesList.take(10)
                                                else moviesAndTvSeriesList,
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

                    // Genre: different genres
                    else -> {
                        val currentSectionId by remember(currentTabIndex) { mutableStateOf(getSectionIdFromCurrentTabIndex()) }
                        val moviesAndTvSeriesList = getMoviesAndTvSeriesBySectionId(
                            currentSectionId, moviesAndTvSeriesViewModel
                        )
                        val moreResultsList = remember(currentTabIndex, moviesAndTvSeriesList) {
                            mutableStateListOf<MovieAndTvSeriesMulti?>().also { it.addAll(moviesAndTvSeriesList) }
                        }
                        val moviesAndTvSeriesGridState = rememberLazyGridState()
                        var resultsLoading by rememberSaveable(currentTabIndex) { mutableStateOf(true) } // true because page 2 is automatically loading
                        var currentTotalPage by rememberSaveable(currentTabIndex) { mutableIntStateOf(-1) }
                        var currentPage by rememberSaveable(currentTabIndex) { mutableIntStateOf(2) } // page 1 is already loaded
                        val more by loadMoviesAndTvSeriesMoreResultsBySectionId(
                            id = currentSectionId,
                            page = currentPage,
                            moviesAndTvSeriesViewModel = moviesAndTvSeriesViewModel,
                            movieAndTvSeriesDetailsViewModel = movieAndTvSeriesDetailsViewModel,
                            onFailure = {
                                resultsLoading = false
                                showErrorPage = true
                            }
                        ) {
                            currentTotalPage = maxOf(currentTotalPage, it)
                        }.collectAsStateWithLifecycle(emptyList())

                        LaunchedEffect(more) {
                            if (more.isEmpty()) return@LaunchedEffect

                            val list = (moreResultsList + more).distinctBy {
                                when(it) {
                                    is Movie -> it.movieId
                                    is TvSeries -> it.seriesId
                                    else -> ""
                                }
                            }
                            moreResultsList.apply {
                                clear()
                                addAll(list)
                            }

                            resultsLoading = false
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            genresTabRow()
                            if (moreResultsList.isEmpty()) {
                                Box(
                                    Modifier.fillMaxSize(),
                                    Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }

                            } else {
                                MoviesAndTvSeriesSectionGrid(
                                    modifier = Modifier.weight(1f),
                                    moviesAndTvSeriesList = moreResultsList,
                                    section = HOME_ROW_SECTIONS.find { it.id == currentSectionId },
                                    state = moviesAndTvSeriesGridState,
                                    showLoadMoreResultsButton = currentPage != currentTotalPage,
                                    resultsLoading = resultsLoading,
                                    onLoadMoreResultsButtonClick = {
                                        resultsLoading = true
                                        if (currentPage < currentTotalPage) currentPage++
                                    }
                                ) { item ->
                                    onNavigateToMovieAndTvSeriesDetailsScreen(
                                        when(item) {
                                            is Movie -> item.movieId
                                            is TvSeries -> item.seriesId
                                        },
                                        item is TvSeries,
                                        currentSectionId,
                                        HOME_ROW_SECTIONS.find { it.id == currentSectionId }?.title ?: ""
                                    )
                                }
                            }
                        }
                    }
                }

            }

            if (networkStatus == Status.Unavailable || showErrorPage) {
                ErrorPage(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    message = "An error occurred, try refreshing the page",
                    actionText = stringResource(R.string.refresh)
                ) {
                    isHomeRefreshing = true
                    showErrorPage = false
                    moviesAndTvSeriesViewModel.refresh { isHomeRefreshing = false }
                }
            }

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.notice_tmdb_data_disclosure),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f),
                textAlign = TextAlign.Center,
            )

            NetworkStatusTextBanner(networkStatus = networkStatus)

        }
    }
}

@Composable
fun ErrorPage(
    modifier: Modifier = Modifier,
    message: String,
    actionText: String?,
    onActionClick: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
        )
        onActionClick?.let { VueButton(onClick = it) { actionText?.let { text -> Text(text = text) } } }
    }
}

@Composable
fun NetworkStatusTextBanner(
    modifier: Modifier = Modifier,
    networkStatus: Status
) {
    if (networkStatus == Status.Unavailable) {
        Text(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    if (networkStatus == Status.Unavailable) VueColors.Semantics.InfoContainer
                    else VueColors.Semantics.SuccessContainer
                ),
            text = if(networkStatus == Status.Unavailable) stringResource(R.string.msg_internet_unavailable)
            else stringResource(R.string.msg_internet_available),
            style = MaterialTheme.typography.labelMedium,
            color = if(networkStatus == Status.Unavailable) VueColors.Semantics.InfoOnContainer
            else VueColors.Semantics.SuccessOnContainer,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun getMoviesAndTvSeriesBySectionId(
    id: String,
    moviesAndTvSeriesViewModel: MoviesAndTvSeriesViewModel
): List<MovieAndTvSeriesMulti?> = when (id) {
    trendingSectionId -> moviesAndTvSeriesViewModel.trending.collectAsStateWithLifecycle().value
    topRatedSectionId -> moviesAndTvSeriesViewModel.topRatedMovies.collectAsStateWithLifecycle().value
    popularMoviesSectionId -> moviesAndTvSeriesViewModel.popularMovies.collectAsStateWithLifecycle().value
    popularTvSeriesSectionId -> moviesAndTvSeriesViewModel.popularTvSeries.collectAsStateWithLifecycle().value
    nowPlayingSectionId -> moviesAndTvSeriesViewModel.nowPlayingMovies.collectAsStateWithLifecycle().value
    topRatedTvSeriesSectionId -> moviesAndTvSeriesViewModel.topRatedTvSeries.collectAsStateWithLifecycle().value
    airingTodaySectionId -> moviesAndTvSeriesViewModel.airingTodayTvSeries.collectAsStateWithLifecycle().value
    upcomingSectionId -> moviesAndTvSeriesViewModel.upcomingMovies.collectAsStateWithLifecycle().value
    latestMovieSectionId -> moviesAndTvSeriesViewModel.latestMovie.collectAsStateWithLifecycle().value
    latestTvSeriesSectionId -> moviesAndTvSeriesViewModel.latestTvSeries.collectAsStateWithLifecycle().value
    discoverActionMoviesSectionId -> moviesAndTvSeriesViewModel.discoverActionMovies.collectAsStateWithLifecycle().value
    discoverComedyMoviesSectionId -> moviesAndTvSeriesViewModel.discoverComedyMovies.collectAsStateWithLifecycle().value
    discoverThrillerMoviesSectionId -> moviesAndTvSeriesViewModel.discoverThrillerMovies.collectAsStateWithLifecycle().value
    discoverSciFiMoviesSectionId -> moviesAndTvSeriesViewModel.discoverSciFiMovies.collectAsStateWithLifecycle().value
    discoverHorrorMoviesSectionId -> moviesAndTvSeriesViewModel.discoverHorrorMovies.collectAsStateWithLifecycle().value
    discoverDramaTvSeriesSectionId -> moviesAndTvSeriesViewModel.discoverDramaTvSeries.collectAsStateWithLifecycle().value
    discoverAnimeTvSeriesSectionId -> moviesAndTvSeriesViewModel.discoverAnimeTvSeries.collectAsStateWithLifecycle().value
    discoverKoreanDramaTvSeriesSectionId -> moviesAndTvSeriesViewModel.discoverKoreanDramaTvSeries.collectAsStateWithLifecycle().value
    discoverSpanishLanguageMoviesSectionId -> moviesAndTvSeriesViewModel.discoverSpanishLanguageMovies.collectAsStateWithLifecycle().value
    discoverBollywoodMoviesSectionId -> moviesAndTvSeriesViewModel.discoverBollywoodMovies.collectAsStateWithLifecycle().value
    discoverFrenchCinemaMoviesSectionId -> moviesAndTvSeriesViewModel.discoverFrenchCinemaMovies.collectAsStateWithLifecycle().value
    discoverDocumentaryMoviesSectionId -> moviesAndTvSeriesViewModel.discoverDocumentaryMovies.collectAsStateWithLifecycle().value
    discoverAnimationMoviesSectionId -> moviesAndTvSeriesViewModel.discoverAnimationMovies.collectAsStateWithLifecycle().value
    discoverCrimeTvSeriesSectionId -> moviesAndTvSeriesViewModel.discoverCrimeTvSeries.collectAsStateWithLifecycle().value
    discoverRomanceMoviesSectionId -> moviesAndTvSeriesViewModel.discoverRomanceMovies.collectAsStateWithLifecycle().value
    discoverBestOf2010MoviesSectionId -> moviesAndTvSeriesViewModel.discoverBestOf2010Movies.collectAsStateWithLifecycle().value
    discoverBestOf2000MoviesSectionId -> moviesAndTvSeriesViewModel.discoverBestOf2000Movies.collectAsStateWithLifecycle().value
    discoverClassicMoviesSectionId -> moviesAndTvSeriesViewModel.discoverClassicMovies.collectAsStateWithLifecycle().value
    discoverAdventureMoviesSectionId -> moviesAndTvSeriesViewModel.discoverAdventureMovies.collectAsStateWithLifecycle().value
    discoverFantasyMoviesSectionId -> moviesAndTvSeriesViewModel.discoverFantasyMovies.collectAsStateWithLifecycle().value
    currentlyOnAirTvSeriesSectionId -> moviesAndTvSeriesViewModel.currentlyOnAirTvSeries.collectAsStateWithLifecycle().value
    else -> emptyList()
}

@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun loadMoviesAndTvSeriesMoreResultsBySectionId(
    id: String,
    page: Int,
    moviesAndTvSeriesViewModel: MoviesAndTvSeriesViewModel,
    movieAndTvSeriesDetailsViewModel: MovieAndTvSeriesDetailsViewModel?,
    onFailure: (Exception) -> Unit,
    onPagination: (Int) -> Unit
): Flow<List<MovieAndTvSeriesMulti>> = when(id) {
    trendingSectionId -> moviesAndTvSeriesViewModel.loadTrending(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    topRatedSectionId -> moviesAndTvSeriesViewModel.loadTopRatedMovies(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    popularMoviesSectionId -> moviesAndTvSeriesViewModel.loadPopularMovies(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    popularTvSeriesSectionId -> moviesAndTvSeriesViewModel.loadPopularTvSeries(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    nowPlayingSectionId -> moviesAndTvSeriesViewModel.loadNowPlayingMovies(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    topRatedTvSeriesSectionId -> moviesAndTvSeriesViewModel.loadTopRatedTvSeries(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    airingTodaySectionId -> moviesAndTvSeriesViewModel.loadAiringTodayTvSeries(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    upcomingSectionId -> moviesAndTvSeriesViewModel.loadUpcomingMovies(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    discoverActionMoviesSectionId -> moviesAndTvSeriesViewModel.loadDiscoverActionMovies(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    discoverComedyMoviesSectionId -> moviesAndTvSeriesViewModel.loadDiscoverComedyMovies(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    discoverThrillerMoviesSectionId -> moviesAndTvSeriesViewModel.loadDiscoverThrillerMovies(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    discoverSciFiMoviesSectionId -> moviesAndTvSeriesViewModel.loadDiscoverSciFiMovies(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    discoverHorrorMoviesSectionId -> moviesAndTvSeriesViewModel.loadDiscoverHorrorMovies(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    discoverDramaTvSeriesSectionId -> moviesAndTvSeriesViewModel.loadDiscoverDramaTvSeries(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    discoverAnimeTvSeriesSectionId -> moviesAndTvSeriesViewModel.loadDiscoverAnimeTvSeries(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    discoverKoreanDramaTvSeriesSectionId -> moviesAndTvSeriesViewModel.loadDiscoverKoreanDramaTvSeries(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    discoverSpanishLanguageMoviesSectionId -> moviesAndTvSeriesViewModel.loadDiscoverSpanishLanguageMovies(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    discoverBollywoodMoviesSectionId -> moviesAndTvSeriesViewModel.loadDiscoverBollywoodMovies(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    discoverFrenchCinemaMoviesSectionId -> moviesAndTvSeriesViewModel.loadDiscoverFrenchCinemaMovies(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    discoverDocumentaryMoviesSectionId -> moviesAndTvSeriesViewModel.loadDiscoverDocumentaryMovies(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    discoverAnimationMoviesSectionId -> moviesAndTvSeriesViewModel.loadDiscoverAnimationMovies(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    discoverCrimeTvSeriesSectionId -> moviesAndTvSeriesViewModel.loadDiscoverCrimeTvSeries(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    discoverRomanceMoviesSectionId -> moviesAndTvSeriesViewModel.loadDiscoverRomanceMovies(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    discoverBestOf2010MoviesSectionId -> moviesAndTvSeriesViewModel.loadDiscoverBestOf2010Movies(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    discoverBestOf2000MoviesSectionId -> moviesAndTvSeriesViewModel.loadDiscoverBestOf2000Movies(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    discoverClassicMoviesSectionId -> moviesAndTvSeriesViewModel.loadDiscoverClassicMovies(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    discoverAdventureMoviesSectionId -> moviesAndTvSeriesViewModel.loadDiscoverAdventureMovies(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    discoverFantasyMoviesSectionId -> moviesAndTvSeriesViewModel.loadDiscoverFantasyMovies(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    currentlyOnAirTvSeriesSectionId -> moviesAndTvSeriesViewModel.loadCurrentlyOnAirTvSeries(page, onPagination).catch { onFailure(it as? Exception ?: Exception(it)) }
    similarSectionId -> movieAndTvSeriesDetailsViewModel?.loadSimilarMoviesAndTvSeries(
        movieAndTvSeriesDetailsViewModel.tmdbId,
        page,
        onPagination
    )?.catch { onFailure(it as? Exception ?: Exception(it)) }
        ?: emptyFlow()
    recommendedSectionId -> movieAndTvSeriesDetailsViewModel?.loadRecommendedMoviesAndTvSeries(
        movieAndTvSeriesDetailsViewModel.tmdbId,
        page,
        onPagination
    )?.catch { onFailure(it as? Exception ?: Exception(it)) }
        ?: emptyFlow()
    else -> { emptyFlow() }
}


@Immutable
data class HomeRowSection (
    val title: String,
    val id: String
)

const val featuredCarouselSectionId = "featured_carousel"
const val trendingSectionId = "trending"
const val topRatedSectionId = "top_rated"
const val popularMoviesSectionId = "popular_movies"
const val popularTvSeriesSectionId = "popular_tv_series"
const val nowPlayingSectionId = "now_playing"
const val topRatedTvSeriesSectionId = "top_rated_tv_series"
const val airingTodaySectionId = "airing_today"
const val upcomingSectionId = "upcoming"
const val latestMovieSectionId = "latest_movie"
const val latestTvSeriesSectionId = "latest_tv_series"
const val discoverActionMoviesSectionId = "discover_action_movies"
const val discoverComedyMoviesSectionId = "discover_comedy_movies"
const val discoverThrillerMoviesSectionId = "discover_thriller_movies"
const val discoverSciFiMoviesSectionId = "discover_sci_fi_movies"
const val discoverHorrorMoviesSectionId = "discover_horror_movies"
const val discoverDramaTvSeriesSectionId = "discover_drama_tv_series"
const val discoverAnimeTvSeriesSectionId = "discover_anime_tv_series"
const val discoverKoreanDramaTvSeriesSectionId = "discover_korean_drama_tv_series"
const val discoverSpanishLanguageMoviesSectionId = "discover_spanish_language_movies"
const val discoverBollywoodMoviesSectionId = "discover_bollywood_movies"
const val discoverFrenchCinemaMoviesSectionId = "discover_french_cinema_movies"
const val discoverDocumentaryMoviesSectionId = "discover_documentary_movies"
const val discoverAnimationMoviesSectionId = "discover_animation_movies"
const val discoverCrimeTvSeriesSectionId = "discover_crime_tv_series"
const val discoverRomanceMoviesSectionId = "discover_romance_movies"
const val discoverBestOf2010MoviesSectionId = "discover_best_of_2010_movies"
const val discoverBestOf2000MoviesSectionId = "discover_best_of_2000_movies"
const val discoverClassicMoviesSectionId = "discover_classic_movies"
const val discoverAdventureMoviesSectionId = "discover_adventure_movies"
const val discoverFantasyMoviesSectionId = "discover_fantasy_movies"
const val currentlyOnAirTvSeriesSectionId = "currently_on_air_tv_series"


val HOME_ROW_SECTIONS = listOf(
    HomeRowSection("Featured", featuredCarouselSectionId),
    HomeRowSection("Trending", trendingSectionId),
    HomeRowSection("Top 10", topRatedSectionId),
    HomeRowSection("Popular Movies", popularMoviesSectionId),
    HomeRowSection("Popular Tv Series", popularTvSeriesSectionId),
    HomeRowSection("Now Playing", nowPlayingSectionId),
    HomeRowSection("Top Rated Tv Series", topRatedTvSeriesSectionId),
    HomeRowSection("Airing Today", airingTodaySectionId),
    HomeRowSection("Upcoming Movies", upcomingSectionId),
    HomeRowSection("Latest Movie", latestMovieSectionId),
    HomeRowSection("Latest Tv Series", latestTvSeriesSectionId),
    HomeRowSection("Action", discoverActionMoviesSectionId),
    HomeRowSection("Comedy", discoverComedyMoviesSectionId),
    HomeRowSection("Thriller", discoverThrillerMoviesSectionId),
    HomeRowSection("Sci-Fi", discoverSciFiMoviesSectionId),
    HomeRowSection("Horror", discoverHorrorMoviesSectionId),
    HomeRowSection("Drama", discoverDramaTvSeriesSectionId),
    HomeRowSection("Anime", discoverAnimeTvSeriesSectionId),
    HomeRowSection("Korean Drama", discoverKoreanDramaTvSeriesSectionId),
    HomeRowSection("Spanish Language", discoverSpanishLanguageMoviesSectionId),
    HomeRowSection("Bollywood", discoverBollywoodMoviesSectionId),
    HomeRowSection("French Cinema", discoverFrenchCinemaMoviesSectionId),
    HomeRowSection("Documentary", discoverDocumentaryMoviesSectionId),
    HomeRowSection("Animation", discoverAnimationMoviesSectionId),
    HomeRowSection("Crime", discoverCrimeTvSeriesSectionId),
    HomeRowSection("Romance", discoverRomanceMoviesSectionId),
    HomeRowSection("Best of 2010", discoverBestOf2010MoviesSectionId),
    HomeRowSection("Best of 2000", discoverBestOf2000MoviesSectionId),
    HomeRowSection("Classic", discoverClassicMoviesSectionId),
    HomeRowSection("Adventure", discoverAdventureMoviesSectionId),
    HomeRowSection("Fantasy", discoverFantasyMoviesSectionId),
    HomeRowSection("Currently On Air", currentlyOnAirTvSeriesSectionId)
)

