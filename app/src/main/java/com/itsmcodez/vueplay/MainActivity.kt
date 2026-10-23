package com.itsmcodez.vueplay

import android.Manifest
import android.app.PictureInPictureParams
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.pip.PictureInPictureDelegate
import androidx.core.pip.VideoPlaybackPictureInPicture
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.ui.compose.ContentFrame
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.itsmcodez.vueplay.common.MainTopBarMultiSelectionActions
import com.itsmcodez.vueplay.constant.PreferenceKeys
import com.itsmcodez.vueplay.data.local.entity.DownloadStatus
import com.itsmcodez.vueplay.domain.model.Video
import com.itsmcodez.vueplay.domain.model.VideoFolder
import com.itsmcodez.vueplay.extension.rememberPreference
import com.itsmcodez.vueplay.manager.ClickEvent
import com.itsmcodez.vueplay.manager.VuePlayAnalyticsManager
import com.itsmcodez.vueplay.presentation.dmca.DmcaViewModel
import com.itsmcodez.vueplay.presentation.downloads.DownloadsScreen
import com.itsmcodez.vueplay.presentation.downloads.DownloadsViewModel
import com.itsmcodez.vueplay.presentation.home.HomeScreen
import com.itsmcodez.vueplay.presentation.home.HomeViewModel
import com.itsmcodez.vueplay.presentation.navigation.Destination
import com.itsmcodez.vueplay.presentation.search.SearchScreen
import com.itsmcodez.vueplay.presentation.search.SearchViewModel
import com.itsmcodez.vueplay.presentation.settings.SettingsScreen
import com.itsmcodez.vueplay.presentation.settings.SettingsViewModel
import com.itsmcodez.vueplay.presentation.streaming.MoviesAndTvSeriesViewModel
import com.itsmcodez.vueplay.presentation.streaming.details.MovieAndTvSeriesDetailsScreen
import com.itsmcodez.vueplay.presentation.streaming.details.MovieAndTvSeriesDetailsViewModel
import com.itsmcodez.vueplay.presentation.streaming.download.MovieAndTvSeriesDownloadScreen
import com.itsmcodez.vueplay.presentation.streaming.download.MovieAndTvSeriesDownloadViewModel
import com.itsmcodez.vueplay.presentation.streaming.playback.MovieAndTvSeriesPlaybackScreen
import com.itsmcodez.vueplay.presentation.streaming.playback.MovieAndTvSeriesPlaybackViewModel
import com.itsmcodez.vueplay.presentation.streaming.results.MovieAndTvSeriesMoreResultsScreen
import com.itsmcodez.vueplay.presentation.streaming.results.MovieAndTvSeriesMoreResultsViewModel
import com.itsmcodez.vueplay.presentation.streaming.search.MoviesAndTvSeriesSearchScreen
import com.itsmcodez.vueplay.presentation.streaming.search.MoviesAndTvSeriesSearchViewModel
import com.itsmcodez.vueplay.presentation.video.browser.VideoBrowserScreen
import com.itsmcodez.vueplay.presentation.video.browser.VideoBrowserViewModel
import com.itsmcodez.vueplay.presentation.video.browser.VideoFolderBrowserScreen
import com.itsmcodez.vueplay.presentation.video.browser.VideoFolderBrowserViewModel
import com.itsmcodez.vueplay.presentation.video.details.VideoFolderDetailsScreen
import com.itsmcodez.vueplay.presentation.video.details.VideoFolderDetailsViewModel
import com.itsmcodez.vueplay.presentation.video.playback.VideoPlaybackScreen
import com.itsmcodez.vueplay.presentation.video.playback.VideoPlaybackViewModel
import com.itsmcodez.vueplay.ui.component.VideoMenuBottomSheet
import com.itsmcodez.vueplay.ui.component.VideosSortBottomSheet
import com.itsmcodez.vueplay.ui.component.vueui.VueColors
import com.itsmcodez.vueplay.ui.component.vueui.VueNavigationBar
import com.itsmcodez.vueplay.ui.component.vueui.VueNavigationBarItem
import com.itsmcodez.vueplay.ui.state.rememberMediaControllerState
import com.itsmcodez.vueplay.ui.theme.VuePlayTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity(), PictureInPictureDelegate.OnPictureInPictureEventListener {

    private var deleteVideosTargetSize = 0
    private var videoBrowserViewModel by mutableStateOf<VideoBrowserViewModel?>(null)
    private var movieAndTvSeriesDetailsViewModel by mutableStateOf<MovieAndTvSeriesDetailsViewModel?>(null)
    private var downloadsViewModel by mutableStateOf<DownloadsViewModel?>(null)
    private lateinit var pictureInPictureImpl: VideoPlaybackPictureInPicture
    private lateinit var navController: NavHostController

    private var externalVideoUri: Uri? by mutableStateOf(null)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        val showSystemBars = { windowInsetsController.show(WindowInsetsCompat.Type.systemBars()) }
        val hideSystemBars = { windowInsetsController.hide(WindowInsetsCompat.Type.systemBars()) }
        val toggleSystemBars: (show: Boolean) -> Unit = { show -> if(show) showSystemBars() else hideSystemBars() }
        var inDarkMode = false
        val toggleLightSystemBars: (useDarkIcons: Boolean) -> Unit = { useDarkIcons ->
            if (!inDarkMode) {
                windowInsetsController.isAppearanceLightStatusBars = useDarkIcons
                windowInsetsController.isAppearanceLightNavigationBars = useDarkIcons
            }
        }

        // Pip video playback
        pictureInPictureImpl = VideoPlaybackPictureInPicture(
            this,
            ContextCompat.getMainExecutor(this)
        )
        pictureInPictureImpl.addOnPictureInPictureEventListener(
            ContextCompat.getMainExecutor(this),
            this
        )

        // Handle any initial intent
        handleIntent(intent)

        setContent {
            DisposableEffect(pictureInPictureImpl) {
                onDispose {
                    pictureInPictureImpl.close()
                    pictureInPictureImpl.removeOnPictureInPictureEventListener(this@MainActivity)
                }
            }

            VuePlayTheme {
                inDarkMode = isSystemInDarkTheme()
                val scope = rememberCoroutineScope()
                val tabs = arrayOf(
                    stringResource(R.string.videos),
                    stringResource(R.string.folders)
                )
                val pagerState = rememberPagerState { tabs.size }

                // Navigation
                navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                // whether we are on top level screen (Home, Local, Settings)
                val isOnTopLevelScreen = navBackStackEntry?.destination?.hasRoute<Destination.HomeScreen>() == true ||
                        navBackStackEntry?.destination?.hasRoute<Destination.LocalVideoBrowserScreen>() == true ||
                        navBackStackEntry?.destination?.hasRoute<Destination.SettingsScreen>() == true
                val isOnHomeScreen = navBackStackEntry?.destination?.hasRoute<Destination.HomeScreen>() == true
                val isOnSettingsScreen = navBackStackEntry?.destination?.hasRoute<Destination.SettingsScreen>() == true
                val isOnVideoFolderDetailsScreen = navBackStackEntry?.destination?.hasRoute<Destination.VideoFolderDetailsScreen>() == true
                val isOnLocalVideoBrowserScreen = navBackStackEntry?.destination?.hasRoute<Destination.LocalVideoBrowserScreen>() == true

                // View Type
                val (viewType, onViewTypeChanged) = rememberPreference(
                    PreferenceKeys.VIEW_TYPE_KEY,
                    PreferenceKeys.ViewType.GRID.name
                )

                // Sort
                val (sortOption) = rememberPreference(PreferenceKeys.VIDEOS_SORT_OPTION_KEY, PreferenceKeys.SortOption.NAME.name)
                var showVideosSortBottomSheet by remember { mutableStateOf(false) }

                // Autoplay all videos (start video)
                var autoplayStartVideo by remember { mutableStateOf<Video?>(null) }

                // Video and folder actions targets
                var showVideoMenu by remember { mutableStateOf(false) }
                var showVideoFolderMenu by remember { mutableStateOf(false) }
                var showDeleteVideosDialog by remember { mutableStateOf(false) }
                var renameVideoTarget: Video? by remember { mutableStateOf(null) }
                var pendingRenameName by remember { mutableStateOf("") }
                var deleteVideosTarget: Array<Video> by remember { mutableStateOf(emptyArray()) }
                var shareVideosTarget: Array<Video> by remember { mutableStateOf(emptyArray()) }

                // Video Browser Selection
                val videoBrowserVideos = videoBrowserViewModel?.videos?.collectAsStateWithLifecycle()?.value ?: emptyList()
                val isVideoBrowserInSelectionMode = videoBrowserViewModel?.inSelectionMode?.collectAsStateWithLifecycle()?.value ?: false
                val videoBrowserSelectionList = videoBrowserViewModel?.selectedVideoIds?.collectAsStateWithLifecycle()?.value ?: emptySet()
                val videoBrowserSelectionActions = videoBrowserViewModel?.selectionActions?.collectAsStateWithLifecycle()?.value ?: emptyList()
                LaunchedEffect(isVideoBrowserInSelectionMode, videoBrowserSelectionList) {
                    deleteVideosTarget = videoBrowserVideos.filter { it.id in videoBrowserSelectionList }.toTypedArray()
                    shareVideosTarget = videoBrowserVideos.filter { it.id in videoBrowserSelectionList }.toTypedArray()
                    deleteVideosTargetSize = videoBrowserSelectionList.size
                }


                // Placeholder
                val dummyLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.StartIntentSenderForResult()
                ) {}

                val renameVideoLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.StartIntentSenderForResult()
                ) { result ->
                    if (result.resultCode == RESULT_OK && renameVideoTarget != null) {
                        videoBrowserViewModel?.renameVideo(renameVideoTarget!!, pendingRenameName, dummyLauncher)
                        Toast.makeText(this, getString(R.string.msg_rename_video_success), Toast.LENGTH_SHORT).show()
                    }
                    // Clear targets after the launcher is done
                    renameVideoTarget = null
                    pendingRenameName = ""
                    showVideoMenu = false
                    showVideoFolderMenu = false
                }

                val deleteVideosLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.StartIntentSenderForResult()
                ) { result ->
                    if (result.resultCode == RESULT_OK) {
                        val targets = deleteVideosTarget
                        if (targets.isNotEmpty()) {
                            // Android 11+ automatically handles deletion after permission is granted
                            // Only retry on Android 10
                            if(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                                videoBrowserViewModel?.deleteVideos(
                                    *targets,
                                    launcher = dummyLauncher
                                )
                            }
                        }
                        Toast.makeText(
                            this,
                            resources.getQuantityString(
                                R.plurals.msg_delete_videos_success,
                                deleteVideosTargetSize,
                                deleteVideosTargetSize
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    // Clear targets after the launcher is done
                    deleteVideosTarget = emptyArray()
                    showVideoMenu = false
                    showVideoFolderMenu = false
                }

                // Snackbar
                val snackbarHostState = remember{ SnackbarHostState() }
                val showSnackbar: (
                    msg: String,
                    action: String?,
                    result: (SnackbarResult) -> Unit
                ) -> Unit = { msg, action, onResult ->
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = msg,
                            actionLabel = action,
                            withDismissAction = true,
                            duration = SnackbarDuration.Short
                        )
                        onResult(result)
                    }
                }

                val mainToolbarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(mainToolbarScrollBehavior.nestedScrollConnection),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        if (isOnTopLevelScreen && !isVideoBrowserInSelectionMode)
                            Column {

                                if (!isOnSettingsScreen) {
                                    TopAppBar(
                                        title = { Text(stringResource(R.string.app_name)) },
                                        scrollBehavior = mainToolbarScrollBehavior,
                                        navigationIcon = {
                                            Box(Modifier.padding(8.dp)) {
                                                Image(
                                                    modifier = Modifier.size(36.dp),
                                                    painter = painterResource(R.drawable.play),
                                                    contentDescription = null
                                                )
                                            }
                                        },
                                        actions = {
                                            if (isOnHomeScreen) {
                                                val downloads = downloadsViewModel?.downloads?.collectAsStateWithLifecycle()?.value
                                                val anyDownloadInProgress = remember(downloads) { downloads?.any { item ->
                                                    item.status == DownloadStatus.DOWNLOADING || item.status == DownloadStatus.PAUSED
                                                } == true }
                                                IconButton(
                                                    onClick = {
                                                        navController.navigate(Destination.DownloadsScreen)
                                                        VuePlayAnalyticsManager.logClickEvent(
                                                            ClickEvent.DOWNLOAD_MENU_ITEM
                                                        )
                                                    }
                                                ) {
                                                    BadgedBox(
                                                        badge = {
                                                            if (anyDownloadInProgress) Badge(
                                                                containerColor = VueColors.materialColorScheme().tertiary
                                                            ) {
                                                                val downloadCount = downloads?.count { item ->
                                                                    item.status == DownloadStatus.DOWNLOADING || item.status == DownloadStatus.PAUSED
                                                                } ?: 0
                                                                if(downloadCount > 0) { Text(text = downloadCount.toString()) }
                                                            }
                                                        }
                                                    ) {
                                                        Icon(
                                                            painter = painterResource(
                                                                if(anyDownloadInProgress) R.drawable.rounded_downloading_24
                                                                else R.drawable.rounded_download_24
                                                            ),
                                                            contentDescription = null
                                                        )
                                                    }
                                                }
                                            }
                                            IconButton(
                                                onClick = {
                                                    if (isOnLocalVideoBrowserScreen)
                                                        navController.navigate(Destination.SearchScreen)
                                                    else
                                                        navController.navigate(Destination.MoviesAndTvSeriesSearchScreen)
                                                }
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.rounded_search_24),
                                                    contentDescription = null
                                                )
                                            }
                                        },
                                        colors = TopAppBarDefaults.topAppBarColors().copy(
                                            scrolledContainerColor = VueColors.materialColorScheme().surface,
                                            containerColor = VueColors.materialColorScheme().surface
                                        )
                                    )
                                }

                                if (isOnLocalVideoBrowserScreen) {
                                    val padding = TopAppBarDefaults.windowInsets.asPaddingValues()
                                    PrimaryScrollableTabRow(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                PaddingValues(
                                                    start = padding.calculateStartPadding(
                                                        LocalLayoutDirection.current
                                                    ),
                                                    end = padding.calculateEndPadding(
                                                        LocalLayoutDirection.current
                                                    )
                                                )
                                            ),
                                        selectedTabIndex = pagerState.currentPage,
                                        edgePadding = 0.dp,
                                        indicator = {
                                            TabRowDefaults.PrimaryIndicator(
                                                Modifier.tabIndicatorOffset(
                                                    pagerState.currentPage
                                                )
                                            )
                                        }
                                    ) {
                                        tabs.forEach { tab ->
                                            Tab(
                                                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                    0.7f
                                                ),
                                                selected = tab == tabs[pagerState.currentPage],
                                                text = { Text(tab) },
                                                onClick = {
                                                    scope.launch {
                                                        pagerState.animateScrollToPage(tabs.indexOf(tab))
                                                    }
                                                }
                                            )
                                        }
                                    }

                                    Box {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(
                                                    PaddingValues(
                                                        start = padding.calculateStartPadding(
                                                            LocalLayoutDirection.current
                                                        ),
                                                        end = padding.calculateEndPadding(
                                                            LocalLayoutDirection.current
                                                        )
                                                    )
                                                ),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            TextButton(
                                                onClick = { showVideosSortBottomSheet = true },
                                                colors = ButtonDefaults.textButtonColors().copy(
                                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                        0.7f
                                                    )
                                                )
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.rounded_filter_list_24),
                                                    contentDescription = null
                                                )
                                                Spacer(Modifier.width(8.dp))
                                                Text(
                                                    text = PreferenceKeys.SortOption.entries.find { option ->
                                                        option.name == sortOption
                                                    }?.label ?: ""
                                                )
                                            }
                                            IconButton(
                                                colors = IconButtonDefaults.iconButtonColors().copy(
                                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                        0.7f
                                                    )
                                                ),
                                                onClick = {
                                                    onViewTypeChanged(
                                                        if (viewType == PreferenceKeys.ViewType.GRID.name) PreferenceKeys.ViewType.LIST.name
                                                        else PreferenceKeys.ViewType.GRID.name
                                                    )
                                                }
                                            ) {
                                                Icon(
                                                    painter = painterResource(
                                                        if (viewType == PreferenceKeys.ViewType.GRID.name) R.drawable.rounded_grid_view_24
                                                        else R.drawable.rounded_list_24
                                                    ),
                                                    contentDescription = viewType
                                                )
                                            }
                                        }
                                    }
                                }

                            }

                        else if (isOnLocalVideoBrowserScreen && isVideoBrowserInSelectionMode)
                            TopAppBar(
                                title = {
                                    Text(text = videoBrowserSelectionList.size.toString())
                                },
                                navigationIcon = {
                                    IconButton(onClick = { videoBrowserViewModel?.clearSelection() }) {
                                        Icon(
                                            painter = painterResource(R.drawable.rounded_close_small_24),
                                            contentDescription = null
                                        )
                                    }
                                },
                                actions = {
                                    videoBrowserSelectionActions.fastForEach { action ->
                                        IconButton(onClick = {
                                            when(action) {
                                                MainTopBarMultiSelectionActions.SHARE -> videoBrowserViewModel?.shareVideos(
                                                    *shareVideosTarget
                                                )
                                                MainTopBarMultiSelectionActions.DELETE -> showDeleteVideosDialog = true
                                            }
                                        }) {
                                            Icon(
                                                painter = painterResource(action.icon),
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }
                            )
                    },
                    floatingActionButton = {
                        if(isOnLocalVideoBrowserScreen || isOnVideoFolderDetailsScreen)
                            FloatingActionButton(
                                shape = CircleShape,
                                onClick = {
                                    when {
                                        isOnLocalVideoBrowserScreen && pagerState.currentPage == 0 -> if(autoplayStartVideo != null) {
                                            navController.navigate(Destination.VideoPlaybackScreen(autoplayStartVideo!!.id))
                                            autoplayStartVideo = null
                                        }

                                        isOnLocalVideoBrowserScreen && pagerState.currentPage == 1 -> Toast.makeText(
                                            this, "Add folder will be available soon", Toast.LENGTH_SHORT
                                        ).show()

                                        isOnVideoFolderDetailsScreen -> if(autoplayStartVideo != null) {
                                            navController.navigate(Destination.VideoPlaybackScreen(autoplayStartVideo!!.id))
                                            autoplayStartVideo = null
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(
                                        if(
                                                isOnLocalVideoBrowserScreen && pagerState.currentPage == 0 ||
                                                isOnVideoFolderDetailsScreen
                                            ) R.drawable.rounded_play_arrow_24
                                        else R.drawable.rounded_add_24
                                    ),
                                    contentDescription = null
                                )
                            }
                    },
                    bottomBar = {
                        if(isOnTopLevelScreen && !isVideoBrowserInSelectionMode) {
                            val navItems = remember(
                                isOnHomeScreen,
                                isOnLocalVideoBrowserScreen,
                                isOnSettingsScreen
                            ) {
                                listOf(
                                    VueNavigationBarItem(
                                        isOnHomeScreen,
                                        getString(R.string.home),
                                        R.drawable.rounded_home_24
                                    ),
                                    VueNavigationBarItem(
                                        isOnLocalVideoBrowserScreen,
                                        getString(R.string.local),
                                        R.drawable.rounded_movie_24
                                    ),
                                    VueNavigationBarItem(
                                        isOnSettingsScreen,
                                        getString(R.string.settings),
                                        R.drawable.rounded_settings_24
                                    )
                                )
                            }

                            VueNavigationBar(
                                selectedColor = MaterialTheme.colorScheme.primary,
                                items = navItems
                            ) { item ->
                                videoBrowserViewModel?.clearSelection()
                                when(item.label) {
                                    getString(R.string.home) -> navController.navigate(Destination.HomeScreen) {
                                        popUpTo(Destination.HomeScreen) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }

                                    getString(R.string.local) -> {
                                        navController.navigate(Destination.LocalVideoBrowserScreen) {
                                            popUpTo(Destination.HomeScreen) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }

                                        VuePlayAnalyticsManager.logClickEvent(
                                            ClickEvent.LOCAL_NAV_BAR_ITEM
                                        )
                                    }

                                    getString(R.string.settings) -> {
                                        navController.navigate(Destination.SettingsScreen) {
                                            popUpTo(Destination.HomeScreen) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }

                                        VuePlayAnalyticsManager.logClickEvent(
                                            ClickEvent.SETTINGS_NAV_BAR_ITEM
                                        )
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    val storagePermission = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        Manifest.permission.READ_MEDIA_VIDEO else Manifest.permission.READ_EXTERNAL_STORAGE
                    var storagePermissionGranted by remember { mutableStateOf(false) }
                    var shouldShowPermissionRationale  by remember { mutableStateOf(false) }
                    val storagePermissionLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { granted ->
                        when {
                            granted -> {
                                storagePermissionGranted = true
                                shouldShowPermissionRationale = false
                                videoBrowserViewModel?.launchVideos() // refresh videos
                            }
                            shouldShowRequestPermissionRationale(storagePermission) -> shouldShowPermissionRationale = true
                            else -> Toast.makeText(
                                this,
                                getString(R.string.permission_req_denied_rationale),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    LaunchedEffect(storagePermission, storagePermissionLauncher) {
                        // First time user, request for permission
                        // Else user may have mistakenly disabled storage access from settings, observe permission state
                        repeatOnLifecycle(Lifecycle.State.RESUMED) {
                            if (checkSelfPermission(storagePermission) == PackageManager.PERMISSION_GRANTED)
                                storagePermissionGranted = true
                            else if(!shouldShowRequestPermissionRationale(storagePermission)) {
                                storagePermissionGranted = false
                                storagePermissionLauncher.launch(storagePermission)
                            } else shouldShowPermissionRationale = true
                        }
                    }

                    if (shouldShowPermissionRationale) {
                        AlertDialog(
                            properties = DialogProperties(dismissOnClickOutside = false),
                            onDismissRequest = { finish() },
                            confirmButton = {
                                TextButton(
                                    onClick = { storagePermissionLauncher.launch(storagePermission) }
                                ) { Text(stringResource(R.string.allow)) }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { finish() }
                                ) { Text(stringResource(R.string.dismiss)) }
                            },
                            title = {
                                Text(stringResource(R.string.permission_request))
                            },
                            text = {
                                Text(stringResource(R.string.permission_req_rationale))
                            }
                        )
                    }

                    if(showVideosSortBottomSheet) {
                        VideosSortBottomSheet { sheetState ->
                            if(!sheetState.isVisible) showVideosSortBottomSheet = false
                        }
                    }

                    if(showVideoMenu || showVideoFolderMenu) {
                        if(videoBrowserViewModel == null) return@Scaffold
                        VideoMenuBottomSheet(
                            viewModel = videoBrowserViewModel!!,
                            isVideoFolderTarget = showVideoFolderMenu,
                            renameVideoTarget = renameVideoTarget,
                            shareVideosTarget = shareVideosTarget,
                            deleteVideosTarget = deleteVideosTarget,
                            deleteVideosLauncher = deleteVideosLauncher,
                            renameVideoLauncher = renameVideoLauncher,
                            onRenameNameChange = { pendingRenameName = it }
                        ) {
                            showVideoMenu = false
                            showVideoFolderMenu = false
                            // Targets are now cleared in the launchers or when explicitly canceled
                        }
                    }

                    if(showDeleteVideosDialog && deleteVideosTarget.isNotEmpty()) {
                        AlertDialog(
                            onDismissRequest = { showDeleteVideosDialog = false },
                            title = { Text(text = stringResource(R.string.delete)) },
                            text = {
                                Text(
                                    text = pluralStringResource(
                                        R.plurals.msg_delete_videos,
                                        deleteVideosTarget.size,
                                        deleteVideosTarget.size
                                    )
                                )
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        videoBrowserViewModel?.deleteVideos(
                                            *deleteVideosTarget,
                                            launcher = deleteVideosLauncher
                                        )
                                        showDeleteVideosDialog = false
                                    }
                                ) {
                                    Text(text = stringResource(R.string.delete))
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteVideosDialog = false } ) {
                                    Text(text = stringResource(R.string.cancel))
                                }
                            }
                        )
                    }

                    if (storagePermissionGranted) {
                        val onFolderClick: (VideoFolder) -> Unit = { folder ->
                            navController.navigate(
                                Destination.VideoFolderDetailsScreen(
                                    folder.name,
                                    folder.path
                                )
                            )
                            VuePlayAnalyticsManager.logClickEvent(
                                ClickEvent.LOCAL_VIDEO_FOLDER_ITEM
                            )
                        }
                        val onVideoClick: (Video) -> Unit = {
                            if (isVideoBrowserInSelectionMode) videoBrowserViewModel?.toggleSelection(it)
                            else {
                                navController.navigate(Destination.VideoPlaybackScreen(videoId = it.id))
                                VuePlayAnalyticsManager.logClickEvent(
                                    ClickEvent.LOCAL_VIDEO_ITEM
                                )
                            }
                        }
                        val onVideoMenuClick: (Video) -> Unit = { video ->
                            renameVideoTarget = video
                            deleteVideosTarget = arrayOf(video)
                            shareVideosTarget = arrayOf(video)
                            deleteVideosTargetSize = 1
                            showVideoMenu = true
                        }
                        val onVideoFolderMenuClick: (VideoFolder) -> Unit = { videoFolder ->
                            renameVideoTarget = null
                            deleteVideosTarget = videoFolder.videos.toTypedArray()
                            shareVideosTarget = videoFolder.videos.toTypedArray()
                            deleteVideosTargetSize = videoFolder.videos.size
                            showVideoFolderMenu = true
                        }

                        val moviesAndTvSeriesViewModel = hiltViewModel<MoviesAndTvSeriesViewModel>()

                        val navigateToMovieAndTvSeriesDetailsScreen: (
                            tmdbId: String,
                            isTvSeries: Boolean,
                            sectionId: String,
                            sectionTitle: String
                        ) -> Unit = { tmdbId, isTvSeries, sectionId, sectionTitle ->
                            navController.navigate(
                                Destination.MovieAndTvSeriesDetailsScreen(
                                    tmdbId,
                                    isTvSeries,
                                    sectionId,
                                    sectionTitle
                                )
                            )
                            VuePlayAnalyticsManager.logClickEvent(
                                ClickEvent.MOVIE_TV_SHOW_ITEM
                            )
                        }

                        val navigateToMoreResultsScreen: (
                            sectionId: String,
                            sectionTitle: String,
                            fromDetailsScreen: Boolean
                        ) -> Unit = { sectionId, sectionTitle, fromDetailsScreen ->
                            navController.navigate(
                                Destination.MovieAndTvSeriesMoreResultsScreen(
                                    sectionId = sectionId,
                                    sectionTitle = sectionTitle,
                                    fromDetailsScreen = fromDetailsScreen
                                )
                            )
                            VuePlayAnalyticsManager.logClickEvent(
                                ClickEvent.MOVIES_TV_SHOWS_NAVIGABLE_SECTION_HEADER
                            )
                        }

                        NavHost(
                            navController = navController,
                            startDestination = Destination.HomeScreen
                        ) {

                            composable<Destination.HomeScreen> {
                                val homeViewModel = hiltViewModel<HomeViewModel>()

                                HomeScreen(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                    viewModel = homeViewModel,
                                    moviesAndTvSeriesViewModel = moviesAndTvSeriesViewModel,
                                    movieAndTvSeriesDetailsViewModel = movieAndTvSeriesDetailsViewModel,
                                    onNavigateToMovieAndTvSeriesDetailsScreen = { tmdbId, isTvSeries, sectionId, sectionTitle ->
                                        navigateToMovieAndTvSeriesDetailsScreen(
                                            tmdbId,
                                            isTvSeries,
                                            sectionId,
                                            sectionTitle
                                        )
                                    },
                                    onNavigateToMoreResultsScreen = { sectionId, sectionTitle ->
                                        navigateToMoreResultsScreen(
                                            sectionId,
                                            sectionTitle,
                                            false
                                        )
                                    }
                                )
                            }

                            composable<Destination.LocalVideoBrowserScreen> {
                                val videoBrowserViewModel = hiltViewModel<VideoBrowserViewModel>()
                                this@MainActivity.videoBrowserViewModel = videoBrowserViewModel
                                val videos by videoBrowserViewModel.videos.collectAsStateWithLifecycle()

                                val videoFolderBrowserViewModel = hiltViewModel<VideoFolderBrowserViewModel>()
                                val videoFolders by videoFolderBrowserViewModel.videoFolders.collectAsStateWithLifecycle()

                                val videoGridState = rememberLazyGridState()
                                val videoListState = rememberLazyListState()
                                val videoFolderGridState = rememberLazyGridState()
                                val videoFolderListState = rememberLazyListState()

                                if (videos.isNotEmpty() && videoFolders.isNotEmpty()) {

                                    LaunchedEffect(pagerState.currentPage) {
                                        if (isVideoBrowserInSelectionMode)
                                            videoBrowserViewModel.clearSelection()
                                    }

                                    LaunchedEffect(videos, videoFolders, isOnLocalVideoBrowserScreen) {
                                        if(isOnLocalVideoBrowserScreen && pagerState.currentPage == 0) autoplayStartVideo = videos.first()
                                    }

                                    BackHandler(enabled = isVideoBrowserInSelectionMode) {
                                        videoBrowserViewModel.clearSelection()
                                    }

                                    HorizontalPager(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding),
                                        state = pagerState
                                    ) { page ->
                                        when (page) {
                                            0 -> VideoBrowserScreen(
                                                modifier = Modifier.fillMaxSize(),
                                                lazyGridState = videoGridState,
                                                lazyListState = videoListState,
                                                viewModel = videoBrowserViewModel,
                                                onMenuClick = onVideoMenuClick,
                                                onVideoClick = onVideoClick
                                            )

                                            1 -> VideoFolderBrowserScreen(
                                                modifier = Modifier.fillMaxSize(),
                                                lazyGridState = videoFolderGridState,
                                                lazyListState = videoFolderListState,
                                                viewModel = videoFolderBrowserViewModel,
                                                onMenuClick = onVideoFolderMenuClick,
                                                onFolderClick = onFolderClick
                                            )
                                        }
                                    }
                                }

                                if (videos.isEmpty() && videoFolders.isEmpty())
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            stringResource(R.string.no_videos_found),
                                            fontWeight = FontWeight.Thin,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                                        )
                                    }
                            }

                            composable<Destination.SearchScreen> {
                                val searchViewModel = hiltViewModel<SearchViewModel>()

                                SearchScreen(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                    viewModel = searchViewModel,
                                    onFolderClick = onFolderClick,
                                    onVideoClick = onVideoClick,
                                    onVideoMenuClick = onVideoMenuClick,
                                    onVideoFolderMenuClick = onVideoFolderMenuClick,
                                    onBack = { navController.navigateUp() }
                                )
                            }

                            composable<Destination.VideoFolderDetailsScreen> {
                                val videoFolderDetailsViewModel = hiltViewModel<VideoFolderDetailsViewModel>()
                                val videos by videoFolderDetailsViewModel.folderVideos.collectAsStateWithLifecycle()

                                LaunchedEffect(videos, isOnVideoFolderDetailsScreen) {
                                    if(isOnVideoFolderDetailsScreen && videos.isNotEmpty()) autoplayStartVideo = videos.first()
                                }

                                VideoFolderDetailsScreen(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                    viewModel = videoFolderDetailsViewModel,
                                    selectedVideoIds = videoBrowserSelectionList.toList(),
                                    selectedVideoActions = videoBrowserSelectionActions,
                                    onSortClick = { showVideosSortBottomSheet = true },
                                    onVideoClick = onVideoClick,
                                    onVideoLongClick = { videoBrowserViewModel?.toggleSelection(it) },
                                    onVideoMenuClick = onVideoMenuClick,
                                    onClearSelection = { videoBrowserViewModel?.clearSelection() },
                                    onShareSelectedVideos = { videoBrowserViewModel?.shareVideos(*shareVideosTarget) },
                                    onDeleteSelectedVideos = { showDeleteVideosDialog = true },
                                    onBack = { navController.navigateUp() }
                                )
                            }

                            composable<Destination.VideoPlaybackScreen> {
                                val videoPlaybackViewModel = hiltViewModel<VideoPlaybackViewModel>()

                                VideoPlaybackScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    viewModel = videoPlaybackViewModel,
                                    onToggleSystemBars = toggleSystemBars,
                                    onToggleLightSystemBars = toggleLightSystemBars,
                                    onShowSnackbar = showSnackbar,
                                    onQueueVideoMenuClick = onVideoMenuClick,
                                    onEnterPipMode = {
                                        pictureInPictureImpl.setAspectRatio(Rational(16, 9))
                                        pictureInPictureImpl.commit()
                                        this@MainActivity.enterPictureInPictureMode(
                                            PictureInPictureParams.Builder().build()
                                        )
                                    },
                                    onBack = { navController.navigateUp() }
                                )
                            }

                            composable<Destination.PipVideoPlaybackScreen> {
                                val controller = rememberMediaControllerState().value

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .aspectRatio(16f / 9f)
                                        .background(Color.Black)
                                ) {
                                    ContentFrame(
                                        modifier = Modifier.fillMaxSize(),
                                        player = controller
                                    )
                                }
                            }

                            composable<Destination.SettingsScreen> {
                                val settingsViewModel = hiltViewModel<SettingsViewModel>()

                                SettingsScreen(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                    viewModel = settingsViewModel,
                                    onBack = { navController.navigateUp() }
                                )
                            }

                            composable<Destination.MovieAndTvSeriesMoreResultsScreen> {
                                val viewModel = hiltViewModel<MovieAndTvSeriesMoreResultsViewModel>()

                                MovieAndTvSeriesMoreResultsScreen(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                    moviesAndTvSeriesViewModel = moviesAndTvSeriesViewModel,
                                    movieAndTvSeriesDetailsViewModel = movieAndTvSeriesDetailsViewModel,
                                    viewModel = viewModel,
                                    onNavigateToMovieAndTvSeriesDetailsScreen = { tmdbId, isTvSeries, sectionId, sectionTitle ->
                                        navigateToMovieAndTvSeriesDetailsScreen(
                                            tmdbId,
                                            isTvSeries,
                                            sectionId,
                                            sectionTitle
                                        )
                                    },
                                    onBack = { navController.navigateUp() }
                                )
                            }

                            composable<Destination.MoviesAndTvSeriesSearchScreen> {
                                val viewModel = hiltViewModel<MoviesAndTvSeriesSearchViewModel>()

                                MoviesAndTvSeriesSearchScreen(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                    viewModel = viewModel,
                                    onNavigateToMovieAndTvSeriesDetailsScreen = { tmdbId, isTvSeries, sectionId, sectionTitle ->
                                        navigateToMovieAndTvSeriesDetailsScreen(
                                            tmdbId,
                                            isTvSeries,
                                            sectionId,
                                            sectionTitle
                                        )
                                    },
                                    onBack = { navController.navigateUp() }
                                )
                            }

                            composable<Destination.MovieAndTvSeriesDetailsScreen> {
                                val dmcaViewModel = hiltViewModel<DmcaViewModel>()
                                val viewModel = hiltViewModel<MovieAndTvSeriesDetailsViewModel>()
                                movieAndTvSeriesDetailsViewModel = viewModel

                                MovieAndTvSeriesDetailsScreen(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                    viewModel = viewModel,
                                    dmcaViewModel = dmcaViewModel,
                                    onNavigateToMoviesAndTvSeriesSearchScreen = {
                                        navController.navigate(Destination.MoviesAndTvSeriesSearchScreen)
                                    },
                                    onNavigateToMovieAndTvSeriesDetailsScreen = { tmdbId, isTvSeries, sectionId, sectionTitle ->
                                        navigateToMovieAndTvSeriesDetailsScreen(
                                            tmdbId,
                                            isTvSeries,
                                            sectionId,
                                            sectionTitle
                                        )
                                    },
                                    onNavigateToMovieAndTvSeriesDownloadScreen = { tmdbId, isTvSeries, season, episode ->
                                        navController.navigate(
                                            Destination.MovieAndTvSeriesDownloadScreen(
                                                tmdbId = tmdbId,
                                                season = season,
                                                episode = episode,
                                                isTvSeries = isTvSeries
                                            )
                                        )
                                    },
                                    onNavigateToMoreResultsScreen = { sectionId, sectionTitle ->
                                        navigateToMoreResultsScreen(
                                            sectionId,
                                            sectionTitle,
                                            true
                                        )
                                    },
                                    onNavigateToMovieAndTvSeriesPlaybackScreen = { tmdbId, season, episode, isTvSeries ->
                                        navController.navigate(
                                            Destination.MovieAndTvSeriesPlaybackScreen(
                                                tmdbId = tmdbId,
                                                season = season,
                                                episode = episode,
                                                isTvSeries = isTvSeries
                                            )
                                        )
                                    },
                                    onBack = { navController.navigateUp() }
                                )
                            }

                            composable<Destination.MovieAndTvSeriesDownloadScreen> {
                                val viewModel = hiltViewModel<MovieAndTvSeriesDownloadViewModel>()

                                MovieAndTvSeriesDownloadScreen(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                    viewModel = viewModel,
                                    onNavigateToDownloadsScreen = { navController.navigate(Destination.DownloadsScreen) },
                                    onShowSnackbar = showSnackbar,
                                    onBack = { navController.navigateUp() }
                                )
                            }

                            composable<Destination.DownloadsScreen> {
                                val viewModel = hiltViewModel<DownloadsViewModel>()
                                downloadsViewModel = viewModel

                                DownloadsScreen(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                    viewModel = viewModel,
                                    onNavigateToVideoPlaybackScreen = {
                                        externalVideoUri = it.toUri()
                                    },
                                    onBack = { navController.navigateUp() }
                                )
                            }

                            composable<Destination.MovieAndTvSeriesPlaybackScreen> {
                                val viewModel = hiltViewModel<MovieAndTvSeriesPlaybackViewModel>()

                                MovieAndTvSeriesPlaybackScreen(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                    viewModel = viewModel,
                                    onNavigateToMovieAndTvSeriesDetailsScreen = { tmdbId, isTvSeries, sectionId, sectionTitle ->
                                        navigateToMovieAndTvSeriesDetailsScreen(
                                            tmdbId,
                                            isTvSeries,
                                            sectionId,
                                            sectionTitle
                                        )
                                    },
                                    onNavigateToMovieAndTvSeriesDownloadScreen = { tmdbId, isTvSeries, season, episode ->
                                        navController.navigate(
                                            Destination.MovieAndTvSeriesDownloadScreen(
                                                tmdbId = tmdbId,
                                                season = season,
                                                episode = episode,
                                                isTvSeries = isTvSeries
                                            )
                                        )
                                    },
                                    onNavigateToMoreResultsScreen = { sectionId, sectionTitle ->
                                        navigateToMoreResultsScreen(
                                            sectionId,
                                            sectionTitle,
                                            true // true here, we need similar and recommendations list
                                        )
                                    },
                                    onToggleSystemBars = toggleSystemBars,
                                    onToggleLightSystemBars = toggleLightSystemBars,
                                    onBack = { navController.navigateUp() }
                                )
                            }

                        }

                        // Note: ensure NavGraph is initialized to play external video
                        LaunchedEffect(externalVideoUri) {
                            if(externalVideoUri != null) {
                                navController.navigate(
                                    Destination.VideoPlaybackScreen(
                                        -1,
                                        externalVideoUri?.toString() ?: ""
                                    )
                                )
                                externalVideoUri = null
                            }
                        }

                    }
                }
            }
        }
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return

        externalVideoUri = when (intent.action) {
            Intent.ACTION_VIEW -> intent.data
            Intent.ACTION_SEND -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri
                }
            }
            else -> null
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Update the activity intent to the latest one
        setIntent(intent)

        // Handle the new intent
        handleIntent(intent)
    }

    override fun onPictureInPictureEvent(
        event: PictureInPictureDelegate.Event,
        config: Configuration?
    ) {
        if(::navController.isInitialized)
            when (event) {
                PictureInPictureDelegate.Event.ENTERED -> {
                    navController.navigate(Destination.PipVideoPlaybackScreen)
                }

                PictureInPictureDelegate.Event.EXITED -> {
                    navController.navigateUp()
                }

                PictureInPictureDelegate.Event.ENTER_ANIMATION_START -> { /* Hide overlays */ }
                PictureInPictureDelegate.Event.ENTER_ANIMATION_END -> { /* Animation finished */ }

                PictureInPictureDelegate.Event.STASHED -> { /* PiP stashed */ }
                PictureInPictureDelegate.Event.UNSTASHED -> { /* PiP unstashed */ }
            }
    }

}
