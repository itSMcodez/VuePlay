package com.itsmcodez.vueplay.presentation.streaming.search

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.common.NetworkObserver
import com.itsmcodez.vueplay.data.local.entity.SearchHistory
import com.itsmcodez.vueplay.domain.model.Movie
import com.itsmcodez.vueplay.domain.model.MovieAndTvSeriesMulti
import com.itsmcodez.vueplay.domain.model.TvSeries
import com.itsmcodez.vueplay.manager.ClickEvent
import com.itsmcodez.vueplay.manager.VuePlayAnalyticsManager
import com.itsmcodez.vueplay.presentation.home.ErrorPage
import com.itsmcodez.vueplay.presentation.home.HomeRowSection
import com.itsmcodez.vueplay.presentation.home.NetworkStatusTextBanner
import com.itsmcodez.vueplay.ui.component.grid.MoviesAndTvSeriesSectionGrid
import com.itsmcodez.vueplay.ui.component.vueui.VueSearchField
import com.itsmcodez.vueplay.ui.component.vueui.VueSearchFieldDefaults
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun MoviesAndTvSeriesSearchScreen(
    modifier: Modifier = Modifier,
    viewModel: MoviesAndTvSeriesSearchViewModel,
    onNavigateToMovieAndTvSeriesDetailsScreen: (
        tmdbId: String,
        isTvSeries: Boolean,
        sectionId: String,
        sectionTitle: String
    ) -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val section = remember { HomeRowSection("Search", "search") }
    val networkStatus by viewModel.networkStatus.debounce(800.milliseconds)
        .collectAsStateWithLifecycle(NetworkObserver.Status.Available)
    val moreResultsList = remember { mutableStateListOf<MovieAndTvSeriesMulti?>() }
    val moviesAndTvSeriesGridState = rememberLazyGridState()
    var showErrorPage by remember { mutableStateOf(false) }
    var resultsLoading by rememberSaveable { mutableStateOf(false) }
    var currentTotalPage by rememberSaveable { mutableIntStateOf(-1) }
    var currentPage by rememberSaveable { mutableIntStateOf(1) }
    var isOnSearchHomepage by rememberSaveable { mutableStateOf(true) }
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val searchHistory by viewModel.recentSearches.collectAsStateWithLifecycle()
    val searchFieldState = rememberSaveable(saver = TextFieldState.Saver) {
        TextFieldState(searchQuery)
    }

    val onSearchFailure: (Exception) -> Unit = { e ->
        Log.e("MoviesAndTvSeriesSearchScreen", "Error: ${e.message}")
        resultsLoading = false
        showErrorPage = true
    }

    LaunchedEffect(searchResults) {
        if (searchResults.isEmpty()) return@LaunchedEffect

        val list = (moreResultsList + searchResults).distinctBy {
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

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(isOnSearchHomepage) { if (!isOnSearchHomepage) focusRequester.requestFocus() }

    BackHandler(!isOnSearchHomepage) { isOnSearchHomepage = true }

    TopAppBar(
        title = {
            if(isOnSearchHomepage) Text(text = stringResource(R.string.search))
            else {
                VueSearchField(
                    Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    state = searchFieldState,
                    onSearch = {
                        val query = it.toString().trim()
                        if (query.isNotBlank()) {
                            if (moreResultsList.isNotEmpty()) {
                                scope.launch { moviesAndTvSeriesGridState.scrollToItem(0) }
                                moreResultsList.clear()
                            }
                            viewModel.upsertSearchHistory(SearchHistory(query = query))
                            resultsLoading = true
                            viewModel.onSubmitQuery(
                                query = query,
                                page = currentPage,
                                onPagination = { total -> currentTotalPage = total },
                                onFailure = onSearchFailure
                            )

                            VuePlayAnalyticsManager.logClickEvent(
                                ClickEvent.MOVIES_TV_SHOWS_SEARCH
                            )
                        }
                    },
                    onSearchQueryChange = {
                        viewModel.onSearchQueryChange(it.toString())
                    }
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { if (isOnSearchHomepage) onBack() else isOnSearchHomepage = true }) {
                Icon(
                    painter = painterResource(R.drawable.rounded_arrow_back_24),
                    contentDescription = "Back"
                )
            }
        }
    )

    if(isOnSearchHomepage) {
        Column (
            modifier = modifier
                .padding(16.dp)
                .fillMaxSize()
                .windowInsetsPadding(
                    WindowInsets(
                        top = TopAppBarDefaults.TopAppBarExpandedHeight
                    )
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            Box {
                VueSearchField(
                    modifier = Modifier.fillMaxWidth(),
                    state = TextFieldState(),
                    readOnly = true,
                    onSearch = {},
                    onSearchQueryChange = {},
                    colors = VueSearchFieldDefaults.colors().copy(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Box(
                    modifier = Modifier.fillMaxWidth().height(48.dp).clickable(
                        indication = null,
                        interactionSource = interactionSource
                    ) { isOnSearchHomepage = false }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.history),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                )
                TextButton(
                    onClick = { viewModel.deleteAllSearchHistory() },
                    colors = ButtonDefaults.textButtonColors().copy(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_delete_24),
                        contentDescription = "Delete"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.clear))
                }
            }

            FlowRow(
                modifier = Modifier.weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                searchHistory.forEach {
                    Box(Modifier.padding(8.dp)) {
                        AssistChip(
                            label = { Text(it.query) },
                            onClick = {
                                if (moreResultsList.isNotEmpty()) {
                                    scope.launch { moviesAndTvSeriesGridState.scrollToItem(0) }
                                    moreResultsList.clear()
                                }
                                isOnSearchHomepage = false
                                resultsLoading = true
                                searchFieldState.setTextAndPlaceCursorAtEnd(it.query)
                                viewModel.onSubmitQuery(
                                    query = it.query,
                                    page = currentPage,
                                    onPagination = { total -> currentTotalPage = total },
                                    onFailure = onSearchFailure
                                )

                                VuePlayAnalyticsManager.logClickEvent(
                                    ClickEvent.SEARCH_HISTORY_ITEM
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    painter = painterResource(R.drawable.rounded_search_24),
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    modifier = Modifier.size(24.dp),
                                    onClick = { viewModel.deleteSearchHistory(it) }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.rounded_close_small_24),
                                        contentDescription = "Delete"
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }

    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    WindowInsets(
                        top = TopAppBarDefaults.TopAppBarExpandedHeight
                    )
                )
        ) {
            if (networkStatus == NetworkObserver.Status.Available && !showErrorPage) {
                if (moreResultsList.isEmpty() && resultsLoading) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                } else {
                    MoviesAndTvSeriesSectionGrid(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        moviesAndTvSeriesList = moreResultsList,
                        section = section,
                        state = moviesAndTvSeriesGridState,
                        resultsLoading = resultsLoading,
                        showLoadMoreResultsButton = currentPage != currentTotalPage,
                        onLoadMoreResultsButtonClick = {
                            resultsLoading = true
                            if (currentPage < currentTotalPage) currentPage++
                            viewModel.onSubmitQuery(
                                query = searchFieldState.text.toString(),
                                page = currentPage,
                                onPagination = { total -> currentTotalPage = total },
                                onFailure = onSearchFailure
                            )
                        }
                    ) { item ->
                        onNavigateToMovieAndTvSeriesDetailsScreen(
                            when(item) {
                                is Movie -> item.movieId
                                is TvSeries -> item.seriesId
                            },
                            item is TvSeries,
                            section.id,
                            section.title
                        )
                    }
                }
            }

            if (networkStatus == NetworkObserver.Status.Unavailable || showErrorPage) {
                ErrorPage(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    message = "An error occurred, try again",
                    actionText = stringResource(R.string.retry)
                ) {
                    resultsLoading = true
                    showErrorPage = false
                    if(searchFieldState.text.isNotBlank() || searchQuery.isNotBlank()) viewModel.onSubmitQuery(
                        query = searchFieldState.text.toString(),
                        page = currentPage,
                        onPagination = { total -> currentTotalPage = total },
                        onFailure = onSearchFailure
                    )
                }
            }

            NetworkStatusTextBanner(networkStatus = networkStatus)

        }
    }

}

