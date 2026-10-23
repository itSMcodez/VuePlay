package com.itsmcodez.vueplay.ui.component.player

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.systemGesturesPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.cast.MediaRouteButton
import androidx.media3.common.C
import androidx.media3.common.DeviceInfo
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.ContentFrame
import androidx.media3.ui.compose.material3.indicator.DurationText
import androidx.media3.ui.compose.material3.indicator.PositionText
import androidx.media3.ui.compose.material3.indicator.ProgressSlider
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.common.CountDownTimerCallback
import com.itsmcodez.vueplay.constant.PreferenceKeys
import com.itsmcodez.vueplay.data.local.entity.ResumableVideo
import com.itsmcodez.vueplay.domain.model.Video
import com.itsmcodez.vueplay.extension.getFrameAtCurrentMediaItemPosition
import com.itsmcodez.vueplay.extension.getVideoMediaItemIndexWithId
import com.itsmcodez.vueplay.extension.rememberPreference
import com.itsmcodez.vueplay.manager.LocalFileManager
import com.itsmcodez.vueplay.service.PlaybackService
import com.itsmcodez.vueplay.ui.component.MoreVideoPlayerOptionsDialog
import com.itsmcodez.vueplay.ui.component.SpeedMultiplierDialog
import com.itsmcodez.vueplay.ui.component.TimerOptionsBottomSheet
import com.itsmcodez.vueplay.ui.component.TrackSelectionDialog
import com.itsmcodez.vueplay.ui.component.VideoQueueDialog
import com.itsmcodez.vueplay.ui.state.rememberMediaControllerState
import com.itsmcodez.vueplay.ui.state.rememberSubtitleCuesState
import com.itsmcodez.vueplay.ui.theme.primaryDark
import com.itsmcodez.vueplay.util.FileUtil
import com.itsmcodez.vueplay.util.TimeFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToLong
import kotlin.time.Duration.Companion.seconds

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    currentVideo: Video?,
    externalVideoUri: String?,
    currentQueue: List<Video>,
    currentResumableVideos: List<ResumableVideo>,
    currentVideoIndex: Int,
    onSetCurrentVideo: (Video) -> Unit,
    onExternalUriHandled: (String?) -> Unit,
    onQueueVideoMenuClick: (Video) -> Unit,
    onClearResumableVideo: (Video) -> Unit,
    onInsertOrUpdateResumableVideo: (Video, Long) -> Unit,
    onToggleSystemBars: (show: Boolean) -> Unit,
    onToggleLightSystemBars: (useDarkIcons: Boolean) -> Unit,
    onEnterPipMode: () -> Unit,
    onShowSnackbar: (msg: String, action: String?, result: (SnackbarResult) -> Unit) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val configuration = LocalConfiguration.current
    val scope = rememberCoroutineScope()

    val (firstTimeVideoPlayback,
        onFirstTimeVideoPlaybackChanged
    ) = context.rememberPreference(PreferenceKeys.FIRST_TIME_VIDEO_PLAYBACK, true)

    val (autoResumeVideos) = context.rememberPreference(PreferenceKeys.ENABLE_AUTO_RESUME_VIDEOS, true)

    val resumableVideo = remember(currentVideo, currentQueue, currentResumableVideos) {
        currentResumableVideos.find { it.videoId == currentVideo?.id }
    }

    val controller = rememberMediaControllerState().value
    var currentTimerMsRemaining by remember { mutableLongStateOf(0) }
    var currentSpeedMultiplier by rememberSaveable { mutableFloatStateOf(1f) }
    var currentContentFrameScale by remember { mutableStateOf(ContentScale.Fit) }
    var currentOrientation by remember { mutableIntStateOf(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR) }
    var currentPlaybackType by remember { mutableIntStateOf(DeviceInfo.PLAYBACK_TYPE_LOCAL) }
    var currentTimerOption by rememberSaveable { mutableLongStateOf(PlaybackService.previousTimerOption) }
    var muted by rememberSaveable { mutableStateOf(false) }
    var isResumed by rememberSaveable(currentVideo?.id) { mutableStateOf(false) }
    var showQueueDialog by rememberSaveable { mutableStateOf(false) }
    var showTrackDialog by remember { mutableStateOf(false) }
    var showSpeedMultiplierDialog by remember { mutableStateOf(false) }
    var showMoreVideoPlayerOptionsDialog by remember { mutableStateOf(false) }
    var showTimerOptionsBottomSheet by remember { mutableStateOf(false) }
    var showLockedCanvasControls by rememberSaveable { mutableStateOf(false) }
    var isCanvasLocked by rememberSaveable { mutableStateOf(false) }
    var showControls by rememberSaveable { mutableStateOf(false) }
    var showNightModeMask by rememberSaveable { mutableStateOf(false) }
    var isPlayerPlaying by rememberSaveable { mutableStateOf(false) }
    var isPlayerLoading by rememberSaveable { mutableStateOf(false) }
    var isBackgroundPlaybackOn by rememberSaveable {
        mutableStateOf(PlaybackService.isBackgroundVideoPlaybackOn)
    }

    var showFastRewindIcon by remember { mutableStateOf(false) }
    var showFastForwardIcon by remember { mutableStateOf(false) }
    var showVolumeIcon by remember { mutableStateOf(false) }
    var showBrightnessIcon by remember { mutableStateOf(false) }
    var showSpeedMultiplier by remember { mutableStateOf(false) }

    val saveResumptionInfo: (Video) -> Unit = {
        if(controller != null) {
            if (
                controller.duration != C.TIME_UNSET && controller.currentPosition != C.TIME_UNSET
                && controller.currentPosition > 0 && controller.currentPosition < controller.duration
            )
                onInsertOrUpdateResumableVideo(it, controller.currentPosition)
        }
    }

    val toggleControlsWithSystemUi = {
        showControls = !showControls
        if (showControls) onToggleSystemBars(true)
        else onToggleSystemBars(false)
    }

    val toggleSpeedMultiplier: (speed: Float, fromGesture: Boolean) -> Unit = { speed, fromGesture ->
        showSpeedMultiplier = if(fromGesture) !showSpeedMultiplier else speed != 1f // Normal or Default (turn off speed multiplier)
        currentSpeedMultiplier = if(showSpeedMultiplier) speed else 1f
        controller?.setPlaybackSpeed(if (showSpeedMultiplier) speed else 1f)
        if (showSpeedMultiplier) Toast.makeText(
            context,
            context.getString(
                if (speed == 2f) R.string.msg_2x_speed_toggle
            else R.string.msg_normal_speed_toggle),
            Toast.LENGTH_LONG
        ).show()
    }

    val onBackPress = {
        currentVideo?.let { saveResumptionInfo(it) }
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        showControls = false
        onBack()
    }

    val toggleBackgroundPlayback: (clearSurface: Boolean) -> Unit = { clearSurface ->
        isBackgroundPlaybackOn = !isBackgroundPlaybackOn
        PlaybackService.isBackgroundVideoPlaybackOn = isBackgroundPlaybackOn
        if(isBackgroundPlaybackOn) {
            if(clearSurface) controller?.clearVideoSurface()
            onBackPress()
        }
    }

    val timerCallback = object : CountDownTimerCallback {
        override fun onTick(millisUntilFinished: Long) {
            currentTimerMsRemaining = millisUntilFinished
        }

        override fun onFinish() {
            controller?.let {
                if (it.isPlaying) it.pause()
            }
            currentTimerOption = 0
            currentTimerMsRemaining = 0
        }

        override fun onCancel() {
            Toast.makeText(
                context,
                context.getString(R.string.msg_timer_cancelled),
                Toast.LENGTH_SHORT
            ).show()
            currentTimerOption = 0
            currentTimerMsRemaining = 0
        }
    }

    // Use this as a fallback if onFinish() is not called by the timer
    LaunchedEffect(currentTimerMsRemaining) {
        if (currentTimerMsRemaining <= 0L)
            controller?.let {
                if (it.isPlaying) it.pause()
            }
    }

    DisposableEffect(Unit) {
        if (PlaybackService.isTimerRunning)
            PlaybackService.addTimerCallback(callback = timerCallback)

        onDispose {
            PlaybackService.removeTimerCallback(callback = timerCallback)
        }
    }

    DisposableEffect(controller, lifecycleOwner) {
        if (controller == null) return@DisposableEffect onDispose {}

        val event = LifecycleEventObserver { _, event ->
            if(!isBackgroundPlaybackOn) {
                if (event == Lifecycle.Event.ON_PAUSE) controller.pause()
                if (event == Lifecycle.Event.ON_RESUME) controller.play()
            }
        }
        lifecycleOwner.lifecycle.addObserver(event)

        // Initial controller? states
        isPlayerPlaying = controller.isPlaying
        isPlayerLoading = controller.isLoading

        // Playback listener
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                // if(!isPlaying && (!showFastForwardIcon || !showFastRewindIcon || !showVolumeIcon || !showBrightnessIcon)) showControls = true
                if(isPlaying) isPlayerLoading = false
                isPlayerPlaying = isPlaying

                // Only save resumption info for currently playing media (not previous)
                if(controller.currentMediaItem?.mediaId?.toLongOrNull() == currentVideo?.id)
                    currentVideo?.let { saveResumptionInfo(currentVideo) }
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Toast.makeText(context, error.localizedMessage, Toast.LENGTH_SHORT).show()
                onBack()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                // update currentVideo (previous video) if possible
                if(mediaItem?.mediaId?.toLongOrNull() != currentVideo?.id) {
                    // update resumption info for previous video (currentVideo) on playback completion
                    // by clearing last position and resumable flag
                    if (
                        (reason == Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT || reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO)
                        && resumableVideo != null
                    ) {
                        currentVideo?.let { onClearResumableVideo(currentVideo) }
                    }

                    // Save resumption info when user seeks
                    if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_SEEK || reason == Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED) {
                        currentVideo?.let { saveResumptionInfo(currentVideo) }
                    }

                    onSetCurrentVideo(
                        currentQueue.find { it.id == mediaItem?.mediaId?.toLongOrNull() }!!
                    )
                }
                isPlayerPlaying = controller.isPlaying
                showControls = true
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_ENDED) {
                    if(resumableVideo != null && currentVideo != null) onClearResumableVideo(currentVideo)
                    if(!externalVideoUri.isNullOrBlank() && currentVideo == null) {
                        onExternalUriHandled(externalVideoUri)
                        if (currentQueue.isNotEmpty()) {
                            controller.apply {
                                setMediaItems(
                                    currentQueue.map {
                                        MediaItem.Builder()
                                            .setMediaId(it.id.toString())
                                            .setUri(it.uri)
                                            .setMediaMetadata(
                                                MediaMetadata.Builder()
                                                    .setDisplayTitle(it.displayName)
                                                    .setTitle(it.displayName)
                                                    .setDurationMs(it.duration)
                                                    .build()
                                            )
                                            .build()
                                    },
                                    0,
                                    if (autoResumeVideos) {
                                        if (resumableVideo != null && resumableVideo.lastPosition > 0) resumableVideo.lastPosition else 0L
                                    } else 0L
                                )
                                prepare()
                                playWhenReady = true
                                volume = if (muted) 0f else 1f
                            }
                            if (resumableVideo != null) isResumed = true
                        }
                    }
                }
            }

            override fun onIsLoadingChanged(isLoading: Boolean) {
                super.onIsLoadingChanged(isLoading)
                isPlayerLoading = isLoading
            }

            override fun onDeviceInfoChanged(deviceInfo: DeviceInfo) {
                if (deviceInfo.playbackType == DeviceInfo.PLAYBACK_TYPE_LOCAL) {
                    currentPlaybackType = DeviceInfo.PLAYBACK_TYPE_LOCAL
                } else if (deviceInfo.playbackType == DeviceInfo.PLAYBACK_TYPE_REMOTE) {
                    currentPlaybackType = DeviceInfo.PLAYBACK_TYPE_REMOTE
                }
            }

        }
        controller.addListener(listener)

        onDispose {
            currentVideo?.let { saveResumptionInfo(currentVideo) }
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            lifecycleOwner.lifecycle.removeObserver(event)
            onToggleSystemBars(true)
            onToggleLightSystemBars(true)
            controller.run {
                removeListener(listener)
                if (!isBackgroundPlaybackOn) {
                    if (isPlaying) pause()
                    clearMediaItems()
                }
            }
        }
    }

    // Orientation
    DisposableEffect(activity) {
        val originalOrientation = activity?.requestedOrientation
        activity?.requestedOrientation = currentOrientation

        val originalBrightness = activity?.window?.attributes?.screenBrightness

        onDispose {
            if(originalOrientation != null)
                activity.requestedOrientation = originalOrientation

            if(originalBrightness != null)
                activity.window?.attributes = activity.window?.attributes?.apply {
                    screenBrightness = originalBrightness
                }
        }
    }

    // System UI
    LaunchedEffect(Unit) {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onToggleSystemBars(false)
        onToggleLightSystemBars(false)
    }

    // System Configuration UI updates
    LaunchedEffect(configuration) {
        // TODO: Handle configuration changes for updating ui (landscape, portrait)
        when(configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                onToggleSystemBars(false)
                onToggleLightSystemBars(false)
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                onToggleSystemBars(true)
                onToggleLightSystemBars(false)
            }
            else -> {
                onToggleSystemBars(true)
                onToggleLightSystemBars(false)
            }
        }
    }

    LaunchedEffect(
        controller,
        currentQueue,
        currentVideo,
        externalVideoUri != null,
        resumableVideo != null
    ) {
        if (controller == null) return@LaunchedEffect

        if (currentVideo == null && !externalVideoUri.isNullOrBlank()) {
            val metadata = LocalFileManager.extractFileMetadata(externalVideoUri.toUri())
            if (metadata?.hasVideo == true)
                controller.apply {
                    addMediaItem(
                        MediaItem.fromUri(externalVideoUri)
                            .buildUpon()
                            .setMediaMetadata(
                                MediaMetadata.Builder()
                                    .setDisplayTitle(metadata.title)
                                    .setDurationMs(metadata.durationMs)
                                    .build()
                            )
                            .build()
                    )
                    prepare()
                    playWhenReady = true
                    volume = if (muted) 0f else 1f
                }
            return@LaunchedEffect
        }

        // whether current video transition is triggered by user or by controller?
        val isAlreadyPlayingCurrent = controller.currentMediaItem?.mediaId?.toLongOrNull() == currentVideo?.id

        if (isAlreadyPlayingCurrent && controller.mediaItemCount > 0) {
            if (resumableVideo != null && !isResumed && controller.currentPosition < 1000) {
                if (autoResumeVideos)
                    controller.seekTo(resumableVideo.lastPosition)
                isResumed = true
            } else {
                return@LaunchedEffect
            }
        } else if (controller.mediaItemCount == 0) {
            controller.apply {
                setMediaItems(
                    currentQueue.map {
                        MediaItem.Builder()
                            .setMediaId(it.id.toString())
                            .setUri(it.uri)
                            .setMediaMetadata(
                                MediaMetadata.Builder()
                                    .setDisplayTitle(it.displayName)
                                    .setTitle(it.displayName)
                                    .setDurationMs(it.duration)
                                    .build()
                            )
                            .build()
                    },
                    currentVideoIndex,
                    if (autoResumeVideos) {
                        if (resumableVideo != null && resumableVideo.lastPosition > 0) resumableVideo.lastPosition else 0L
                    } else 0L
                )
                prepare()
                playWhenReady = true
                volume = if (muted) 0f else 1f
            }
            if (resumableVideo != null) isResumed = true
        }
        // user selected video from the queue
        else {
            currentVideo?.let {
                controller.seekTo(
                    controller.getVideoMediaItemIndexWithId(it.id),
                    if (autoResumeVideos) {
                        if (resumableVideo != null && resumableVideo.lastPosition > 0) resumableVideo.lastPosition else 0L
                    } else 0L
                )
                if (resumableVideo != null) isResumed = true
            }
        }

        if (autoResumeVideos) {
            if (
                resumableVideo != null &&
                resumableVideo.lastPosition > 0
            ) onShowSnackbar(
                context.getString(R.string.msg_video_playback_resumption),
                context.getString(R.string.reset)
            ) { result ->
                when (result) {
                    SnackbarResult.ActionPerformed -> controller.seekToDefaultPosition()
                    SnackbarResult.Dismissed -> Unit
                }
            }
        }
    }

    LaunchedEffect(showControls, showQueueDialog, isPlayerPlaying) {
        // Auto hide controls if queue dialog isn't open, controller? is playing and controls are visible
        if (showControls && isPlayerPlaying && !showQueueDialog) {
            delay(3.seconds)
            onToggleSystemBars(false)
            showControls = false
        }
    }

    LaunchedEffect(
        showFastRewindIcon,
        showFastForwardIcon,
        showVolumeIcon,
        showBrightnessIcon
    ) {
        delay(1.5.seconds)
        showFastRewindIcon = false
        showFastForwardIcon = false
        showVolumeIcon = false
        showBrightnessIcon = false
    }

    // Video Queue Dialog
    if(showQueueDialog) {
        VideoQueueDialog(
            modifier = Modifier.fillMaxSize(),
            queue = currentQueue,
            resumableVideos = currentResumableVideos,
            currentVideoIndex = currentVideoIndex,
            state = rememberLazyListState(),
            onDismiss = { showQueueDialog = false },
            onVideoMenuClick = onQueueVideoMenuClick,
            onVideoClick = { selectedVideo ->
                onSetCurrentVideo(selectedVideo)
                showQueueDialog = false
            }
        )
    }

    // Instructional Onboarding Dialog
    if(firstTimeVideoPlayback) {
        AlertDialog(
            onDismissRequest = { onFirstTimeVideoPlaybackChanged(false) },
            title = { Text(text = stringResource(R.string.title_dialog_gestures_tutorial)) },
            text = {
                Text(
                    text = stringResource(R.string.msg_dialog_gestures_tutorial).trimIndent()
                )
            },
            confirmButton = {
                TextButton(onClick = { onFirstTimeVideoPlaybackChanged(false) }) {
                    Text(text = stringResource(R.string.dismiss))
                }
            }
        )
    }

    // Track Selection Dialog
    if (showTrackDialog && controller != null) {
        TrackSelectionDialog(
            player = controller,
            onDismiss = { showTrackDialog = false }
        )
    }

    // Speed Multiplier Dialog
    if (showSpeedMultiplierDialog) {
        SpeedMultiplierDialog(
            onDismiss = { showSpeedMultiplierDialog = false },
            onSpeedChange = { toggleSpeedMultiplier(it, false) },
            currentSpeed = currentSpeedMultiplier
        )
    }

    // More Options Dialog
    if(showMoreVideoPlayerOptionsDialog) {
        MoreVideoPlayerOptionsDialog(
            isTimerRunning = PlaybackService.isTimerRunning,
            onDismiss = { showMoreVideoPlayerOptionsDialog = false },
            onUpdateRemainingTimer = { currentTimerMsRemaining },
            onTimerClick = { showTimerOptionsBottomSheet = true },
            onEqualizerClick = {}
        )
    }

    if(showTimerOptionsBottomSheet) {
        TimerOptionsBottomSheet(
            onDismiss = { showTimerOptionsBottomSheet = false },
            currentTimerOption = currentTimerOption,
            onOptionSelected = { option ->
                currentTimerOption = option
                PlaybackService.toggleTimer(
                    option,
                    timerCallback
                )
            }
        )
    }

    // Handle back press
    BackHandler(enabled = true) { onBackPress() }

    CompositionLocalProvider(LocalContentColor provides Color.White) {
        var surfaceWidthPx by remember { mutableIntStateOf(0) }
        var duration by remember { mutableLongStateOf(0L) }
        var newVolume by remember { mutableFloatStateOf(0f) }
        var newPosition by remember { mutableFloatStateOf(0f) }
        var newBrightness by remember { mutableFloatStateOf(0f) }

        // Video Surface
        Box(
            modifier = modifier
                .background(Color.Black)
                .fillMaxSize()
                .onSizeChanged { size -> surfaceWidthPx = size.width }
                .combinedClickable(
                    indication = null,
                    interactionSource = null,
                    onLongClick = { toggleSpeedMultiplier(2f, true) },
                    onDoubleClick = {
                        controller?.let {
                            if (it.isPlaying) it.pause()
                            else it.play()
                        }
                    }
                ) { toggleControlsWithSystemUi() }
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        // Seek
                        if (duration > 0) {
                            val msPerPixel = duration.toFloat() / surfaceWidthPx
                            newPosition =
                                (newPosition + delta * msPerPixel).coerceIn(0f, duration.toFloat())
                            controller?.seekTo(newPosition.roundToLong())
                        }
                    },
                    onDragStarted = {
                        showControls = true
                        newPosition = controller?.currentPosition?.toFloat() ?: 0f
                        duration = controller?.duration ?: 0
                    },
                    onDragStopped = {
                        newPosition = 0f
                        duration = 0
                    }
                )
        ) {
            if(!isBackgroundPlaybackOn)
                ContentFrame(
                    modifier = Modifier
                        .displayCutoutPadding()
                        .background(Color.Black)
                        .fillMaxSize(),
                    contentScale = currentContentFrameScale,
                    player = controller
                )

            if(isBackgroundPlaybackOn)
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
                ) {
                    Image(
                        modifier = Modifier.size(96.dp),
                        painter = painterResource(R.drawable.rounded_headphones_24),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f))
                    )
                    Text(
                        text = stringResource(R.string.title_background_playback_onboard),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text(
                        text = stringResource(R.string.msg_background_playback_onboard),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

            // Gesture zones
            Row(Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .systemGesturesPadding()
            ) {
                val screenHeightPx = with(LocalDensity.current) {
                    configuration.screenHeightDp.dp.toPx()
                }

                // Brightness + Fast rewind
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .systemBarsPadding()
                        .combinedClickable(
                            indication = null,
                            interactionSource = null,
                            onLongClick = { toggleSpeedMultiplier(2f, true) },
                            onDoubleClick = {
                                showFastRewindIcon = true
                                if (controller?.currentPosition != C.TIME_UNSET)
                                    controller?.seekTo(controller.currentPosition - 10000)
                            }
                        ) { toggleControlsWithSystemUi() }
                        .draggable(
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta ->
                                // Brightness
                                newBrightness =
                                    (newBrightness - delta / screenHeightPx).coerceIn(0f, 1f)
                                activity?.window?.attributes = activity.window?.attributes?.apply {
                                    screenBrightness = newBrightness
                                }
                            },
                            onDragStarted = {
                                showBrightnessIcon = true
                                newBrightness =
                                    activity?.window?.attributes?.screenBrightness ?: 0.5f
                            },
                            onDragStopped = {
                                showBrightnessIcon = false
                                newBrightness = 0f
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.animation.AnimatedVisibility(visible = showFastRewindIcon) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = TimeFormatter.formatTimeMilliseconds(10000, LocalLocale.current.platformLocale),
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                painter = painterResource(R.drawable.rounded_fast_rewind_24),
                                contentDescription = null
                            )
                        }
                    }
                    androidx.compose.animation.AnimatedVisibility(visible = showBrightnessIcon) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${(newBrightness * 100).toInt()}%",
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                modifier = Modifier.size(48.dp),
                                painter = painterResource(R.drawable.rounded_brightness_5_24),
                                contentDescription = null
                            )
                        }
                    }
                }

                // Speed indicator
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(2f)
                        .padding(top = 32.dp)
                        .systemBarsPadding(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    androidx.compose.animation.AnimatedVisibility(visible = showSpeedMultiplier) {
                        Icon(
                            modifier = Modifier.size(48.dp),
                            painter = painterResource(
                                when(currentSpeedMultiplier) {
                                    0.5f -> R.drawable.rounded_speed_0_5x_24
                                    1.5f -> R.drawable.rounded_speed_1_5x_24
                                    2f -> R.drawable.rounded_speed_2x_24
                                    else -> R.drawable.rounded_speed_24 // This is Default or Normal and won't be shown by default
                                }
                            ),
                            contentDescription = null
                        )
                    }
                }

                // Volume + Fast forward
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .systemBarsPadding()
                        .combinedClickable(
                            indication = null,
                            interactionSource = null,
                            onLongClick = { toggleSpeedMultiplier(2f, true) },
                            onDoubleClick = {
                                showFastForwardIcon = true
                                if (controller?.currentPosition != C.TIME_UNSET)
                                    controller?.seekTo(controller.currentPosition + 10000)
                            }
                        ) { toggleControlsWithSystemUi() }
                        .draggable(
                            state = rememberDraggableState { delta ->
                                // Volume
                                newVolume = (newVolume - delta / screenHeightPx).coerceIn(0f, 1f)
                                controller?.volume = newVolume
                            },
                            orientation = Orientation.Vertical,
                            onDragStarted = {
                                showVolumeIcon = true
                                newVolume = controller?.volume ?: 0f
                            },
                            onDragStopped = {
                                showVolumeIcon = false
                                newVolume = 0f
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.animation.AnimatedVisibility(visible = showFastForwardIcon) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.rounded_fast_forward_24),
                                contentDescription = null
                            )
                            Text(
                                text = TimeFormatter.formatTimeMilliseconds(10000, LocalLocale.current.platformLocale),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    androidx.compose.animation.AnimatedVisibility(visible = showVolumeIcon) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${(newVolume * 100).toInt()}%",
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                modifier = Modifier.size(48.dp),
                                painter = painterResource(R.drawable.rounded_volume_up_24),
                                contentDescription = null
                            )
                        }
                    }
                }

            }

            controller?.let { mediaController ->
                val subtitleCues = rememberSubtitleCuesState(mediaController)

                // Custom Manual Subtitle Overlay Layout
                if (subtitleCues.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        subtitleCues.forEach { cue ->
                            cue.text?.let { text ->
                                Text(
                                    text = text.toString(),
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier
                                        .background(
                                            color = Color.Black.copy(alpha = 0.7f)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Night mode mask
        if(showNightModeMask) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            )
        }

        // Video Controls
        if (showControls) {
            Column(
                modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .displayCutoutPadding()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f))
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = currentVideo?.displayName ?: "",
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { onBackPress() }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.rounded_arrow_back_24),
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { muted = !muted; if(muted) controller?.volume = 0f else controller?.volume = 1f }) {
                            Icon(
                                painter = painterResource(
                                    if(muted) R.drawable.rounded_volume_off_24
                                    else R.drawable.rounded_volume_up_24
                                ),
                                contentDescription = null,
                                tint = if(muted) primaryDark else LocalContentColor.current
                            )
                        }
                        CompositionLocalProvider(
                            LocalContentColor provides if(
                                currentPlaybackType == DeviceInfo.PLAYBACK_TYPE_REMOTE
                            ) primaryDark else Color.White
                        ) {
                            MediaRouteButton(
                                Modifier.pointerInput(Unit) {
                                    awaitPointerEventScope {
                                        while(true) {
                                            val event = awaitPointerEvent(PointerEventPass.Initial)
                                            val isActionUp = event.changes.any { it.changedToUp() }
                                            if (isActionUp) {
                                                if (controller?.isPlaying == true) controller.pause()
                                                showControls = true
                                            }
                                        }
                                    }
                                }
                            )
                        }
                        IconButton(onClick = { showQueueDialog = true }) {
                            Icon(
                                painter = painterResource(R.drawable.rounded_playlist_play_24),
                                contentDescription = null
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors().copy(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        navigationIconContentColor = LocalContentColor.current,
                        actionIconContentColor = LocalContentColor.current,
                        titleContentColor = LocalContentColor.current,
                        subtitleContentColor = LocalContentColor.current
                    )
                )

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ) {
                    if(controller?.hasPreviousMediaItem() == true)
                        IconButton(onClick = { controller.seekToPrevious() }) {
                            Icon(
                                modifier = Modifier.size(48.dp),
                                painter = painterResource(R.drawable.rounded_skip_previous_24),
                                contentDescription = null
                            )
                        }

                    if(!isPlayerLoading)
                        IconButton(
                            modifier = Modifier.size(64.dp),
                            onClick = { if(controller?.isPlaying == true) controller.pause() else controller?.play() }
                        ) {
                            Icon(
                                modifier = Modifier.size(64.dp),
                                painter = painterResource(
                                    if (isPlayerPlaying) R.drawable.round_pause_circle_24
                                    else R.drawable.round_play_circle_24
                                ),
                                contentDescription = null
                            )
                        }
                    else CircularProgressIndicator()

                    if(controller?.hasNextMediaItem() == true)
                        IconButton(onClick = { controller.seekToNext() }) {
                            Icon(
                                modifier = Modifier.size(48.dp),
                                painter = painterResource(R.drawable.rounded_skip_next_24),
                                contentDescription = null
                            )
                        }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = {
                            currentOrientation = when(currentOrientation) {
                                ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                                else -> ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
                            }
                            activity?.requestedOrientation = currentOrientation
                        }
                    ) {
                        Icon(
                            painter = painterResource(
                                when(currentOrientation) {
                                    ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR -> R.drawable.rounded_mobile_rotate_24
                                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE -> R.drawable.rounded_mobile_lock_landscape_24
                                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT -> R.drawable.rounded_mobile_lock_portrait_24
                                    else -> R.drawable.rounded_mobile_rotate_24
                                }
                            ),
                            contentDescription = null,
                            tint = if(currentOrientation != ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR) primaryDark else LocalContentColor.current
                        )
                    }
                    IconButton(onClick = { toggleBackgroundPlayback(true) }) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_headphones_24),
                            contentDescription = null,
                            tint = if(isBackgroundPlaybackOn) primaryDark else LocalContentColor.current
                        )
                    }
                    IconButton(onClick = { showTrackDialog = true}) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_video_settings_24),
                            contentDescription = null
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = {
                        currentContentFrameScale = when(currentContentFrameScale) {
                            ContentScale.Fit -> ContentScale.Crop
                            ContentScale.Crop -> ContentScale.FillWidth
                            ContentScale.FillWidth -> ContentScale.FillHeight
                            else -> ContentScale.Fit
                        }
                    }) {
                        Icon(
                            painter = painterResource(
                                when(currentContentFrameScale) {
                                    ContentScale.Fit -> R.drawable.rounded_fullscreen_24
                                    ContentScale.Crop -> R.drawable.rounded_aspect_ratio_24
                                    ContentScale.FillWidth -> R.drawable.rounded_fit_page_width_24
                                    ContentScale.FillHeight -> R.drawable.rounded_fit_page_height_24
                                    else -> R.drawable.rounded_fullscreen_24
                                }
                            ),
                            contentDescription = null,
                            tint = if(currentContentFrameScale != ContentScale.Fit) primaryDark else LocalContentColor.current
                        )
                    }
                    IconButton(
                        onClick = {
                            if (!isBackgroundPlaybackOn) toggleBackgroundPlayback(false)
                            onEnterPipMode()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_pip_24),
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = { if (currentVideo != null) onQueueVideoMenuClick(currentVideo) }) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_more_vert_24),
                            contentDescription = null
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Spacer(Modifier.width(2.dp))
                    PositionText(player = controller)
                    ProgressSlider(
                        modifier = Modifier.weight(1f),
                        player = controller,
                        colors = SliderDefaults.colors().copy(
                            thumbColor = Color.White,
                            activeTrackColor = Color.White,
                            inactiveTrackColor = Color.White.copy(alpha = 0.5f)
                        )
                    )
                    DurationText(player = controller)
                    Spacer(Modifier.width(2.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(
                        onClick = {
                            isCanvasLocked = true
                            showLockedCanvasControls = true
                            showControls = false
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_lock_24),
                            contentDescription = null,
                            tint = if(isCanvasLocked) primaryDark else LocalContentColor.current
                        )
                    }
                    IconButton(onClick = { showSpeedMultiplierDialog = true }) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_speed_24),
                            contentDescription = null,
                            tint = if(showSpeedMultiplier) primaryDark else LocalContentColor.current
                        )
                    }
                    IconButton(onClick = {
                        controller?.getFrameAtCurrentMediaItemPosition(
                            context = context,
                            onFrameExtracted = { bitmap ->
                                scope.launch {
                                    val fileName = "frame_${currentVideo?.displayName}_${System.currentTimeMillis()}"
                                    if (FileUtil.saveBitmapToPictures(context, bitmap, fileName)) {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.msg_image_saved),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            },
                            onThumbnailExtracted = { bitmap ->
                                scope.launch {
                                    val fileName = "thumbnail_${currentVideo?.displayName}_${System.currentTimeMillis()}"
                                    if (FileUtil.saveBitmapToPictures(context, bitmap, fileName)) {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.msg_image_saved),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            },
                            onFrameExtractionFailed = { e ->
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.msg_frame_extraction_failed),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_photo_camera_24),
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = { showNightModeMask = !showNightModeMask }) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_nightlight_24),
                            contentDescription = null,
                            tint = if(showNightModeMask) primaryDark else LocalContentColor.current
                        )
                    }
                    Box(
                        modifier = Modifier.clickable { showMoreVideoPlayerOptionsDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            modifier = Modifier.align(Alignment.TopCenter),
                            onClick = { showMoreVideoPlayerOptionsDialog = true }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.rounded_more_horiz_24),
                                contentDescription = null
                            )
                        }
                        if(PlaybackService.isTimerRunning && currentTimerOption != 0L)
                            Text(
                                modifier = Modifier.align(Alignment.BottomCenter),
                                text = rememberUpdatedState(
                                    TimeFormatter.formatTimeMilliseconds(
                                        locale = LocalLocale.current.platformLocale,
                                        milliseconds = currentTimerMsRemaining
                                    )
                                ).value,
                                color = primaryDark,
                                style = MaterialTheme.typography.labelSmall
                            )
                    }
                }

            }

        }


        var lockedCanvasBackground by remember { mutableStateOf(Color.Black.copy(0.3f)) }
        LaunchedEffect(isCanvasLocked, showLockedCanvasControls) {
            if (isCanvasLocked && showLockedCanvasControls) {
                delay(3.seconds)
                lockedCanvasBackground = Color.Transparent
                showLockedCanvasControls = false
            }
        }

        if (isCanvasLocked) {
            Box(
                modifier = Modifier
                    .clickable {
                        showLockedCanvasControls = !showLockedCanvasControls
                        lockedCanvasBackground =
                            if (showLockedCanvasControls) Color.Black.copy(0.3f)
                            else Color.Transparent
                    }
                    .fillMaxSize()
                    .background(lockedCanvasBackground),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (showLockedCanvasControls)
                    Box(Modifier.padding(horizontal = 32.dp)) {
                        IconButton(
                            onClick = {
                                isCanvasLocked = false
                                showLockedCanvasControls = false
                                showControls = true
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.rounded_lock_24),
                                contentDescription = null,
                                tint = if(isCanvasLocked) primaryDark else LocalContentColor.current
                            )
                        }
                    }
            }
        }

    }


}
