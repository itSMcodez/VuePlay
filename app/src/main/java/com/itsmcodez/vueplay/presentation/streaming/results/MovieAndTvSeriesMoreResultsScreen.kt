package com.itsmcodez.vueplay.presentation.streaming.results

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.common.NetworkObserver
import com.itsmcodez.vueplay.domain.model.Movie
import com.itsmcodez.vueplay.domain.model.MovieAndTvSeriesMulti
import com.itsmcodez.vueplay.domain.model.TvSeries
import com.itsmcodez.vueplay.presentation.home.ErrorPage
import com.itsmcodez.vueplay.presentation.home.HOME_ROW_SECTIONS
import com.itsmcodez.vueplay.presentation.home.NetworkStatusTextBanner
import com.itsmcodez.vueplay.presentation.home.getMoviesAndTvSeriesBySectionId
import com.itsmcodez.vueplay.presentation.home.loadMoviesAndTvSeriesMoreResultsBySectionId
import com.itsmcodez.vueplay.presentation.streaming.MoviesAndTvSeriesViewModel
import com.itsmcodez.vueplay.presentation.streaming.details.MovieAndTvSeriesDetailsViewModel
import com.itsmcodez.vueplay.presentation.streaming.details.recommendedSectionId
import com.itsmcodez.vueplay.presentation.streaming.details.similarSectionId
import com.itsmcodez.vueplay.ui.component.grid.MoviesAndTvSeriesSectionGrid
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun MovieAndTvSeriesMoreResultsScreen(
    modifier: Modifier = Modifier,
    viewModel: MovieAndTvSeriesMoreResultsViewModel,
    moviesAndTvSeriesViewModel: MoviesAndTvSeriesViewModel,
    movieAndTvSeriesDetailsViewModel: MovieAndTvSeriesDetailsViewModel?,
    onNavigateToMovieAndTvSeriesDetailsScreen: (String, Boolean, String, String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val networkStatus by viewModel.networkStatus.debounce(800.milliseconds)
        .collectAsStateWithLifecycle(NetworkObserver.Status.Available)
    val sectionId by viewModel.sectionId.collectAsStateWithLifecycle()
    val sectionTitle by viewModel.sectionTitle.collectAsStateWithLifecycle()
    val fromDetailsScreen by viewModel.fromDetailsScreen.collectAsStateWithLifecycle()
    val similarList = movieAndTvSeriesDetailsViewModel?.similarMovieAndTvSeriesMultiList?.collectAsStateWithLifecycle()?.value
    val recommendedList = movieAndTvSeriesDetailsViewModel?.recommendedMovieAndTvSeriesMultiList?.collectAsStateWithLifecycle()?.value
    val moviesAndTvSeriesBySectionId = getMoviesAndTvSeriesBySectionId(sectionId, moviesAndTvSeriesViewModel)
    val moviesAndTvSeriesList: List<MovieAndTvSeriesMulti?> = remember(fromDetailsScreen) {
        if (fromDetailsScreen) {
            when(sectionId) {
                similarSectionId -> similarList ?: emptyList()
                recommendedSectionId -> recommendedList ?: emptyList()

                else -> emptyList()
            }
        } else moviesAndTvSeriesBySectionId
    }
    val moreResultsList = remember {
        mutableStateListOf<MovieAndTvSeriesMulti?>().also { it.addAll(moviesAndTvSeriesList) }
    }
    val moviesAndTvSeriesGridState = rememberLazyGridState()
    var showErrorPage by remember { mutableStateOf(false) }
    var resultsLoading by rememberSaveable { mutableStateOf(true) } // true because page 2 is automatically loading
    var currentTotalPage by rememberSaveable { mutableIntStateOf(-1) }
    var currentPage by rememberSaveable { mutableIntStateOf(2) } // page 1 is already loaded
    val more by loadMoviesAndTvSeriesMoreResultsBySectionId(
        id = sectionId,
        page = currentPage,
        moviesAndTvSeriesViewModel = moviesAndTvSeriesViewModel,
        movieAndTvSeriesDetailsViewModel = movieAndTvSeriesDetailsViewModel,
        onFailure = {
            resultsLoading = false
        }
    ) {
        currentTotalPage = maxOf(currentTotalPage, it)
    }.collectAsStateWithLifecycle(emptyList())

    DisposableEffect(moviesAndTvSeriesViewModel) {
        val onFailure: (Exception) -> Unit = {
            Log.e("MovieAndTvSeriesMoreResultsScreen", "Error: ${it.message}")
            Toast.makeText(
                context,
                context.getString(R.string.msg_refresh_failed),
                Toast.LENGTH_SHORT
            ).show()
            resultsLoading = false
            showErrorPage = true
        }
        moviesAndTvSeriesViewModel.registerOnFailureCallback(onFailure)

        onDispose {
            moviesAndTvSeriesViewModel.unregisterOnFailureCallback(onFailure)
        }
    }

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

    TopAppBar(
        title = { Text(text = sectionTitle) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.rounded_arrow_back_24),
                    contentDescription = null
                )
            }
        }
    )

    Column(
        modifier = modifier.fillMaxSize()
            .windowInsetsPadding(
                WindowInsets(
                    top = TopAppBarDefaults.TopAppBarExpandedHeight
                )
            )
    ) {
        if (networkStatus == NetworkObserver.Status.Available && !showErrorPage) {
            if (moreResultsList.isEmpty()) {
                Box(
                    Modifier.fillMaxWidth().weight(1f),
                    Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

            } else {
                MoviesAndTvSeriesSectionGrid(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    moviesAndTvSeriesList = moreResultsList,
                    section = HOME_ROW_SECTIONS.find { it.id == sectionId },
                    state = moviesAndTvSeriesGridState,
                    resultsLoading = resultsLoading,
                    showLoadMoreResultsButton = currentPage != currentTotalPage,
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
                        sectionId,
                        sectionTitle
                    )
                }
            }
        }

        if (networkStatus == NetworkObserver.Status.Unavailable || showErrorPage) {
            ErrorPage(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                message = "An error occurred, try refreshing the page",
                actionText = stringResource(R.string.refresh)
            ) {
                resultsLoading = true
                showErrorPage = false
                moviesAndTvSeriesViewModel.refresh { resultsLoading = false }
            }
        }

        NetworkStatusTextBanner(networkStatus = networkStatus)

    }
}

