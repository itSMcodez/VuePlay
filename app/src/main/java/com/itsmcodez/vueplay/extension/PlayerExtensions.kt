package com.itsmcodez.vueplay.extension

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.inspector.frame.FrameExtractor
import com.google.common.util.concurrent.MoreExecutors

val Player.INVALID_MEDIA_ITEM_ID: Long
    get() = -1L
fun Player.getVideoMediaItemIndexWithId(videoId: Long): Int {
    if (videoId == INVALID_MEDIA_ITEM_ID) return -1
    for (i in 0 until mediaItemCount) {
        if (getMediaItemAt(i).mediaId.toLong() == videoId) return i
    }
    return -1
}

@OptIn(UnstableApi::class)
fun Player.getFrameAtCurrentMediaItemPosition(
    context: Context,
    onFrameExtracted: (frame: Bitmap) -> Unit,
    onThumbnailExtracted: (thumbnail: Bitmap) -> Unit,
    onFrameExtractionFailed: (e: Exception) -> Unit
) {
    if (currentMediaItem != null && duration > 0) {
        try {
            var frame: FrameExtractor.Frame?
            var thumbnail: FrameExtractor.Frame?

            FrameExtractor.Builder(context, currentMediaItem!!).build().use {
                val frameFuture = it.getFrame(currentPosition)
                frameFuture.addListener({
                    frame = frameFuture.get()
                    onFrameExtracted(frame.bitmap)
                }, MoreExecutors.directExecutor())

                val thumbnailFuture = it.thumbnail
                thumbnailFuture.addListener({
                    thumbnail = thumbnailFuture.get()
                    onThumbnailExtracted(thumbnail.bitmap)
                }, MoreExecutors.directExecutor())

            }
        } catch (e: Exception) { onFrameExtractionFailed(e) }
    }
}
