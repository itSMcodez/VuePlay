package com.itsmcodez.vueplay.extension

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn

/**
 * Checks if the media at the given [uri] is currently accessible.
 *
 * This is determined by attempting to open a file descriptor in read-only mode ("r").
 *
 * @param uri The [Uri] of the media to check.
 * @return `true` if the media is accessible, `false` otherwise.
 */
fun ContentResolver.isMediaAccessible(uri: Uri): Boolean {
    return try {
        openFileDescriptor(uri, "r")?.use { true } ?: false
    } catch (_: Exception) {
        false
    }
}

/**
 * Observes changes to the content at the specified [uri] and emits them as a [Flow].
 *
 * This uses a [ContentObserver] to monitor changes. The [Flow] will emit the [uri]
 * whenever a change is detected.
 *
 * @param uri The [Uri] to observe.
 * @return A [Flow] that emits the [Uri] on each change.
 */
fun ContentResolver.observeChangesAsFlow(uri: Uri): Flow<Uri> = callbackFlow { 
    val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            trySend(uri)
        }
    }
    
    this@observeChangesAsFlow.registerContentObserver(uri, true, observer)
    
    awaitClose { this@observeChangesAsFlow.unregisterContentObserver(observer) }
}

/**
 * Provides a shared [Flow] for observing content changes at a specific [uri].
 *
 * The flow is shared using [shareIn] to avoid multiple observers for the same content.
 * It uses [SharingStarted.WhileSubscribed] with a 5-second timeout to stop observing
 * when there are no active subscribers.
 *
 * @param contentResolver The [ContentResolver] to use for observation.
 * @param uri The [Uri] to observe.
 * @param scope The [CoroutineScope] in which the sharing is started.
 * @return A shared [Flow] of [Uri] changes.
 */
fun getSharedContentFlow(
    contentResolver: ContentResolver,
    uri: Uri,
    scope: CoroutineScope
) = contentResolver.observeChangesAsFlow(uri)
    .shareIn(
        scope,
        SharingStarted.WhileSubscribed(5000),
        replay = 0
    )

/**
 * Provides a shared [Flow] specifically for observing changes to the system's video media.
 *
 * This is a convenience wrapper around [getSharedContentFlow] for
 * [android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI].
 * It additionally emits the video URI immediately on start to trigger initial content loading.
 *
 * @param contentResolver The [ContentResolver] to use for observation.
 * @param scope The [CoroutineScope] in which the sharing is started.
 * @return A shared [Flow] that emits when the video media store changes.
 */
fun getSharedVideoFlow(
    contentResolver: ContentResolver,
    scope: CoroutineScope
) = getSharedContentFlow(contentResolver, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, scope)
    .onStart { emit(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI) }
