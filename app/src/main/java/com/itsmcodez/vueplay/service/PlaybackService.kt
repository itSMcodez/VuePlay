package com.itsmcodez.vueplay.service

import android.app.PendingIntent
import android.content.Intent
import android.os.CountDownTimer
import android.os.Looper
import androidx.annotation.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import androidx.media3.cast.CastPlayer
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.itsmcodez.vueplay.MainActivity
import com.itsmcodez.vueplay.common.CountDownTimerCallback
import com.itsmcodez.vueplay.constant.PreferenceKeys
import com.itsmcodez.vueplay.extension.dataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    private var handleAudioFocus = true
    private val handleAudioFocusFlow = dataStore.data
        .map { it[PreferenceKeys.ENABLE_HANDLE_AUDIO_FOCUS] ?: handleAudioFocus }
        .distinctUntilChanged()

    private var autoPauseOnHeadsetConnect = true
    private val autoPauseOnHeadsetConnectFlow = dataStore.data
        .map { it[PreferenceKeys.ENABLE_AUTO_PAUSE_ON_HEADSET_CONNECT] ?: autoPauseOnHeadsetConnect }
        .distinctUntilChanged()

    companion object {
        var isBackgroundVideoPlaybackOn = false

        /* Count Down Timer */
        private var countDownTimer: CountDownTimer? = null
        private var timerCallbacks = mutableListOf<CountDownTimerCallback>()
        var previousTimerOption = 0L
        var isTimerRunning by mutableStateOf( countDownTimer != null)

        fun addTimerCallback(callback: CountDownTimerCallback) = timerCallbacks.add(callback)

        fun removeTimerCallback(callback: CountDownTimerCallback) = timerCallbacks.remove(callback)

        fun toggleTimer(ms: Long, callback: CountDownTimerCallback) {
            if (isTimerRunning && ms <= 0L) {
                timerCallbacks.forEach { callback ->
                    callback.onCancel()
                }
                countDownTimer?.cancel()
                countDownTimer = null
                previousTimerOption = 0L
                isTimerRunning = false
                timerCallbacks.clear()

            } else {
                // reset callbacks state
                timerCallbacks.clear()
                timerCallbacks.add(callback)
                // reset timer if running
                if (countDownTimer != null) {
                    countDownTimer?.cancel()
                    countDownTimer = null
                }
                countDownTimer = object : CountDownTimer(ms, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        timerCallbacks.forEach { callback ->
                            callback.onTick(millisUntilFinished)
                        }
                    }

                    override fun onFinish() {
                        timerCallbacks.forEach { callback ->
                            callback.onFinish()
                        }
                        previousTimerOption = 0L
                        isTimerRunning = false
                        countDownTimer = null
                        timerCallbacks.clear()
                    }
                }.start()
                previousTimerOption = ms
                isTimerRunning = true
            }
        }
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        // Audio Focus Preference
        lifecycleScope.launch {
            handleAudioFocusFlow.collect {
                handleAudioFocus = it
                mediaSession?.player?.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                        .setUsage(C.USAGE_MEDIA)
                        .build(),
                    handleAudioFocus
                )
            }
        }

        // Auto Pause on Headset Connect Preference
        lifecycleScope.launch {
            autoPauseOnHeadsetConnectFlow.collect {
                autoPauseOnHeadsetConnect = it
                when(val player = mediaSession?.player) {
                    is ExoPlayer -> player.setHandleAudioBecomingNoisy(autoPauseOnHeadsetConnect)
                    is CastPlayer -> {
                        val player = player.wrappedPlayer as? ExoPlayer
                        player?.setHandleAudioBecomingNoisy(autoPauseOnHeadsetConnect)
                    }
                }
            }
        }

        val player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                handleAudioFocus
            )
            .setHandleAudioBecomingNoisy(autoPauseOnHeadsetConnect)
            .setDeviceVolumeControlEnabled(true)
            .setLooper(Looper.getMainLooper())
            .build()

        val castPlayer = CastPlayer.Builder(this).setLocalPlayer(player).build()

        mediaSession = MediaSession.Builder(this, castPlayer)
            .setSessionActivity(
                PendingIntent.getActivity(
                    this,
                    1000,
                    Intent(this, MainActivity::class.java)
                        .addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        ),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .build()
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession
}