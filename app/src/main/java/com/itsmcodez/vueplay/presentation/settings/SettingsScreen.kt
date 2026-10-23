package com.itsmcodez.vueplay.presentation.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.constant.PreferenceKeys
import com.itsmcodez.vueplay.extension.rememberPreference
import com.itsmcodez.vueplay.ui.component.NavigableButtonRow
import com.itsmcodez.vueplay.ui.component.SwitchButtonRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currentAppVersion by viewModel.currentAppVersion.collectAsStateWithLifecycle()
    val currentAppBuildNumber by viewModel.currentAppBuildNumber.collectAsStateWithLifecycle()
    val currentSystemVersion by viewModel.currentSystemVersion.collectAsStateWithLifecycle()
    var showAbout by remember { mutableStateOf(false) }

    BackHandler(enabled = showAbout) { showAbout = false }

    TopAppBar(
        title = {
            Text(
                text = stringResource(
                    if (showAbout) R.string.about
                    else R.string.settings
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                if (showAbout) showAbout = false
                else onBack()
            }) {
                Icon(
                    painter = painterResource(R.drawable.rounded_arrow_back_24),
                    contentDescription = "Back"
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
        Column(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            if (showAbout)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        modifier = Modifier
                            .size(96.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = CircleShape
                            )
                            .clip(CircleShape),
                        painter = painterResource(R.drawable.play),
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text(
                        text = "Version $currentAppVersion (Build $currentAppBuildNumber) | System $currentSystemVersion",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                    )
                }


            else {
                val (autoResumeVideos, onAutoResumeVideosChange) = context.rememberPreference(
                    PreferenceKeys.ENABLE_AUTO_RESUME_VIDEOS,
                    true
                )
                val (handleAudioFocus, onHandleAudioFocusChange) = context.rememberPreference(
                    PreferenceKeys.ENABLE_HANDLE_AUDIO_FOCUS,
                    true
                )
                val (autoPauseOnHeadsetConnect, onAutoPauseOnHeadsetConnectChange) = context.rememberPreference(
                    PreferenceKeys.ENABLE_AUTO_PAUSE_ON_HEADSET_CONNECT,
                    true
                )

                SwitchButtonRow(
                    checked = autoResumeVideos,
                    onCheckedChange = onAutoResumeVideosChange,
                    onClick = { onAutoResumeVideosChange(!autoResumeVideos) },
                    label = stringResource(R.string.title_pref_auto_resume_videos),
                    summary = stringResource(R.string.summary_pref_auto_resume_videos)
                )

                SwitchButtonRow(
                    checked = !handleAudioFocus,
                    onCheckedChange = { onHandleAudioFocusChange(!handleAudioFocus) },
                    onClick = { onHandleAudioFocusChange(!handleAudioFocus) },
                    label = stringResource(R.string.title_pref_handle_audio_focus),
                    summary = stringResource(R.string.summary_pref_handle_audio_focus)
                )

                SwitchButtonRow(
                    checked = autoPauseOnHeadsetConnect,
                    onCheckedChange = onAutoPauseOnHeadsetConnectChange,
                    onClick = { onAutoPauseOnHeadsetConnectChange(!autoPauseOnHeadsetConnect) },
                    label = stringResource(R.string.title_pref_auto_pause_on_headset_connect),
                    summary = stringResource(R.string.summary_pref_auto_pause_on_headset_connect)
                )

                NavigableButtonRow(
                    title = R.string.about,
                    onClick = { showAbout = true }
                )
            }

        }
    }
}