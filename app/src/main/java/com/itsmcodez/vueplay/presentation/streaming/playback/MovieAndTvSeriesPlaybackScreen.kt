package com.itsmcodez.vueplay.presentation.streaming.playback

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.constant.contentRadius
import com.itsmcodez.vueplay.domain.model.Movie
import com.itsmcodez.vueplay.domain.model.TvSeries
import com.itsmcodez.vueplay.presentation.streaming.details.DETAILS_ROW_SECTIONS
import com.itsmcodez.vueplay.presentation.streaming.details.recommendedSectionId
import com.itsmcodez.vueplay.presentation.streaming.details.reportCopyright
import com.itsmcodez.vueplay.presentation.streaming.details.similarSectionId
import com.itsmcodez.vueplay.ui.component.CopyRightReportDialog
import com.itsmcodez.vueplay.ui.component.DownloadNoticeDialog
import com.itsmcodez.vueplay.ui.component.MovieAndTvSeriesActionBar
import com.itsmcodez.vueplay.ui.component.NavigableSectionHeader
import com.itsmcodez.vueplay.ui.component.TvSeriesSelectionDialog
import com.itsmcodez.vueplay.ui.component.grid.MoviesAndTvSeriesSectionGridItem
import com.itsmcodez.vueplay.ui.component.player.WebViewVideoPlayer
import com.itsmcodez.vueplay.ui.component.vueui.VueButton
import com.itsmcodez.vueplay.ui.component.vueui.VueColors
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.core.net.toUri
import com.itsmcodez.vueplay.manager.ClickEvent
import com.itsmcodez.vueplay.manager.VuePlayAnalyticsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieAndTvSeriesPlaybackScreen(
    modifier: Modifier = Modifier,
    viewModel: MovieAndTvSeriesPlaybackViewModel,
    onNavigateToMovieAndTvSeriesDownloadScreen: (
        tmdbId: String,
        isTvSeries: Boolean,
        season: Int,
        episode: Int
    ) -> Unit,
    onNavigateToMovieAndTvSeriesDetailsScreen: (
        tmdbId: String,
        isTvSeries: Boolean,
        sectionId: String,
        sectionTitle: String
    ) -> Unit,
    onNavigateToMoreResultsScreen: (sectionId: String, sectionTitle: String) -> Unit,
    onToggleSystemBars: (show: Boolean) -> Unit,
    onToggleLightSystemBars: (useDarkIcons: Boolean) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tmdbId = viewModel.tmdbId
    val season = viewModel.season
    val episode = viewModel.episode
    val isTvSeries = viewModel.isTvSeries
    val streamingSources by viewModel.sources.collectAsStateWithLifecycle()
    val currentStreamingSource by viewModel.currentStreamingSource.collectAsStateWithLifecycle()
    val allowedHosts = remember(streamingSources) {
        streamingSources.flatMap { source ->
            // Test extracting domains from both movie and series template URLs
            val movieHost = source.provider.getMovieUrl("test").toUri().host
            val tvHost = source.provider.getTvSeriesUrl("test", 1, 1).toUri().host
            listOfNotNull(movieHost, tvHost)
        }.toSet() + "vueplay.cc" // Always keep base domain on the allowlist
    }
    val currentMovieAndTvSeriesMulti = viewModel.currentMovieAndTvSeriesMulti.collectAsStateWithLifecycle().value ?: return
    val similarList by viewModel.similarMovieAndTvSeriesMultiList.collectAsStateWithLifecycle()
    val recommendedList by viewModel.recommendedMovieAndTvSeriesMultiList.collectAsStateWithLifecycle()
    var showReportDialog by remember { mutableStateOf(false) }
    var showDownloadNoticeDialog by remember { mutableStateOf(false) }
    var showTvSeriesSelectionDialog by remember { mutableStateOf(false) }
    var sourcesDropdownExpanded by remember { mutableStateOf(false) }
    var seasonAndEpisodeSelectorExpanded by remember { mutableStateOf(false) }

    var currentSeason by remember(currentMovieAndTvSeriesMulti) { mutableIntStateOf(season) }
    var currentSeasonEpisode by remember(currentSeason) { mutableIntStateOf(episode) }


    var webView by remember { mutableStateOf<android.webkit.WebView?>(null) }
    val activity = LocalActivity.current
    val configuration = LocalConfiguration.current
    var currentOrientation by remember { mutableIntStateOf(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) }
    var fullScreenView by remember { mutableStateOf<View?>(null) }
    var fullScreenCallback by remember { mutableStateOf<WebChromeClient.CustomViewCallback?>(null) }
    var isFullScreen by remember { mutableStateOf(false) }

    val onBackPress: () -> Unit = {
        webView?.apply {
            stopLoading()
            loadUrl("about:blank")
            onPause()
            (this.parent as? ViewGroup)?.removeView(this)
            destroy()
        }
        onBack()
    }

    LaunchedEffect(isFullScreen) {
        if (isFullScreen) {
            currentOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            activity?.requestedOrientation = currentOrientation
            // System UI
            onToggleSystemBars(false)
            onToggleLightSystemBars(false)
        } else {
            fullScreenView = null
            fullScreenCallback?.onCustomViewHidden()
            fullScreenCallback = null
            currentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            activity?.requestedOrientation = currentOrientation
        }
    }

    DisposableEffect(webView, activity) {
        // Orientation
        val originalOrientation = activity?.requestedOrientation
        activity?.requestedOrientation = currentOrientation

        val originalBrightness = activity?.window?.attributes?.screenBrightness

        // System Ui
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        onDispose {
            // Orientation
            if(originalOrientation != null)
                activity.requestedOrientation = originalOrientation

            if(originalBrightness != null)
                activity.window?.attributes = activity.window?.attributes?.apply {
                    screenBrightness = originalBrightness
                }

            // System UI
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            onToggleSystemBars(true)
            onToggleLightSystemBars(true)
            webView = null
        }
    }

    LaunchedEffect(streamingSources) {
        val default = streamingSources.firstOrNull()
        if (default != null && currentStreamingSource == null)
            viewModel.setCurrentStreamingSource(default)
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
                showDownloadNoticeDialog = !seasonAndEpisodeSelectorExpanded
                seasonAndEpisodeSelectorExpanded = false
            },
            onDismiss = {
                showTvSeriesSelectionDialog = false
                seasonAndEpisodeSelectorExpanded = false
            }
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

    BackHandler(true) {
        if(isFullScreen) isFullScreen = false
        else onBackPress()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Playback screen
        TopAppBar(
            title = {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ExposedDropdownMenuBox(
                        modifier = Modifier.wrapContentHeight(),
                        expanded = sourcesDropdownExpanded,
                        onExpandedChange = { sourcesDropdownExpanded = it },
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(),
                            value = currentStreamingSource?.name ?: stringResource(R.string.select_server),
                            onValueChange = {},
                            readOnly = true,
                            textStyle = MaterialTheme.typography.titleMedium,
                            label = { Text(stringResource(R.string.select_server)) },
                            placeholder = { Text(stringResource(R.string.select_server)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sourcesDropdownExpanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        )

                        ExposedDropdownMenu(
                            expanded = sourcesDropdownExpanded,
                            onDismissRequest = { sourcesDropdownExpanded = false },
                        ) {
                            streamingSources.forEach { source ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(text = source.name)
                                            Text(
                                                text = source.description,
                                                color = VueColors.materialColorScheme().onSurfaceVariant.copy(
                                                    0.7f
                                                )
                                            )
                                        }
                                    },
                                    onClick = {
                                        viewModel.setCurrentStreamingSource(source = source)
                                        sourcesDropdownExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onBackPress) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_arrow_back_24),
                        contentDescription = null
                    )
                }
            }
        )

        Box(
            modifier = modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    WindowInsets(
                        top = TopAppBarDefaults.TopAppBarExpandedHeight
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item(key = "header") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp)
                                .fillMaxSize(),
                            text = stringResource(R.string.msg_video_source_not_playing),
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center,
                            color = VueColors.materialColorScheme().onSurfaceVariant.copy(0.7f)
                        )

                        // WebView Video Player
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f/9f)
                                .background(Color.Black, RoundedCornerShape(contentRadius))
                                .clip(RoundedCornerShape(contentRadius)),
                            contentAlignment = Alignment.Center
                        ) {
                            var isLoading by remember { mutableStateOf(false) }
                            var showCustomErrorPage by remember { mutableStateOf(false) }

                            if (showCustomErrorPage)
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        text = stringResource(R.string.msg_refresh_failed),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                                    )
                                    VueButton(onClick = { webView?.reload() }) {
                                        Text(text = stringResource(R.string.refresh))
                                    }
                                }

                            if(showCustomErrorPage.not())
                                WebViewVideoPlayer(
                                    allowedHosts = allowedHosts,
                                    videoUrl = when(currentMovieAndTvSeriesMulti) {
                                        is Movie -> currentStreamingSource?.provider?.getMovieUrl(tmdbId) ?: ""
                                        is TvSeries -> currentStreamingSource?.provider?.getTvSeriesUrl(
                                            tmdbId,
                                            currentSeason,
                                            currentSeasonEpisode
                                        ) ?: ""
                                    },
                                    sandbox = currentStreamingSource?.sandbox ?: false,
                                    onProgressChanged = {},
                                    onReceivedError = { _, request ->
                                        if(request.isForMainFrame) {
                                            showCustomErrorPage = true
                                            isLoading = false
                                            webView?.stopLoading()
                                        }
                                    },
                                    onPageStarted = {
                                        isLoading = true
                                        showCustomErrorPage = false
                                    },
                                    onPageFinished = { isLoading = false },
                                    onShowCustomView = { callback, view ->
                                        if(fullScreenView != null) {
                                            callback?.onCustomViewHidden()
                                            return@WebViewVideoPlayer
                                        }
                                        fullScreenView = view
                                        fullScreenCallback = callback
                                        isFullScreen = true
                                    },
                                    onHideCustomView = { isFullScreen = false },
                                    onWebViewCreated = { webView = it }
                                )

                            if(isLoading) CircularProgressIndicator(
                                color = Color.White,
                                trackColor = Color.Transparent
                            )
                        }

                        // Details
                        Text(
                            text = when(currentMovieAndTvSeriesMulti) {
                                is Movie -> currentMovieAndTvSeriesMulti.title
                                is TvSeries -> currentMovieAndTvSeriesMulti.name
                            },
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
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

                        if (isTvSeries && currentMovieAndTvSeriesMulti is TvSeries) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                ExposedDropdownMenuBox(
                                    expanded = seasonAndEpisodeSelectorExpanded,
                                    onExpandedChange = {
                                        seasonAndEpisodeSelectorExpanded = it
                                        if(seasonAndEpisodeSelectorExpanded) {
                                            showTvSeriesSelectionDialog = true
                                        }
                                    },
                                ) {
                                    OutlinedTextField(
                                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                            .fillMaxWidth(),
                                        value = "Season $currentSeason, Episode $currentSeasonEpisode",
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text(stringResource(R.string.select_season_episode)) },
                                        placeholder = { Text(stringResource(R.string.select_season_episode)) },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = seasonAndEpisodeSelectorExpanded) },
                                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                                    )
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                        }

                        MovieAndTvSeriesActionBar(
                            movieAndTvSeriesMulti = currentMovieAndTvSeriesMulti
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(Modifier.weight(1f)) {
                                VueButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        if (isTvSeries && currentMovieAndTvSeriesMulti is TvSeries) {
                                            showTvSeriesSelectionDialog = true
                                            seasonAndEpisodeSelectorExpanded = false
                                        } else
                                            showDownloadNoticeDialog = true

                                        VuePlayAnalyticsManager.logClickEvent(
                                            ClickEvent.DOWNLOAD_BUTTON
                                        )
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.rounded_download_24),
                                        contentDescription = null
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.title_menu_item_download),
                                    )
                                }
                            }
                            Box(Modifier.weight(1f)) {
                                VueButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        showReportDialog = true
                                        VuePlayAnalyticsManager.logClickEvent(
                                            ClickEvent.REPORT_BUTTON
                                        )
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.rounded_report_24),
                                        contentDescription = null
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.title_menu_item_report),
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                    }
                }

                // Similar and Recommendations
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

        // Full screen player
        if(isFullScreen && fullScreenView != null) {
            Box(
                modifier = Modifier
                    .clickable { /* Consume click to prevent click through parent */ }
            )
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    fullScreenView!!.apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                update = { fullScreenView = it }
            )
        }
    }

}
