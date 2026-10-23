package com.itsmcodez.vueplay.ui.state

import android.content.ComponentName
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.itsmcodez.vueplay.service.PlaybackService

@OptIn(UnstableApi::class)
@Composable
fun rememberMediaControllerState(): State<MediaController?> {
    val context = LocalContext.current
    val controller = remember { mutableStateOf<MediaController?>(null) }

    DisposableEffect(Unit) {

        val sessionToken = SessionToken(
            context,
            ComponentName(
                context,
                PlaybackService::class.java
            )
        )

        val controllerFuture = MediaController.Builder(
            context, sessionToken
        ).buildAsync()

        controllerFuture.addListener(
            { controller.value =  controllerFuture.get() },
            MoreExecutors.directExecutor(),
        )

        onDispose {
            MediaController.releaseFuture(controllerFuture)
            controller.value?.release()
            controller.value = null
        }
    }

    return controller
}