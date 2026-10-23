package com.itsmcodez.vueplay.manager

import android.app.RecoverableSecurityException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.itsmcodez.vueplay.domain.model.Video
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class VideoManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun renameVideo(
        video: Video,
        newName: String,
        launcher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        val extension = video.displayName.substringAfterLast('.', "")
        val finalName = if (extension.isNotEmpty() && !newName.endsWith(".$extension", ignoreCase = true)) {
            "$newName.$extension"
        } else {
            newName
        }

        if (video.displayName == finalName) return

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, finalName)
        }

        try {
            context.contentResolver.update(
                video.uri,
                values,
                null,
                null
            )
        } catch (securityException: SecurityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val recoverableSecurityException = securityException as? RecoverableSecurityException
                val intentSender = recoverableSecurityException?.userAction?.actionIntent?.intentSender
                    ?: if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        MediaStore.createWriteRequest(context.contentResolver, listOf(video.uri)).intentSender
                    } else {
                        throw securityException
                    }

                val intentSenderRequest = IntentSenderRequest.Builder(intentSender).build()
                launcher.launch(intentSenderRequest)
            } else {
                throw securityException
            }
        }
    }

    fun shareVideos(vararg videos: Video) {
        if (videos.isEmpty()) return

        val intent = if (videos.size == 1) {
            Intent(Intent.ACTION_SEND).apply {
                type = "video/*"
                putExtra(Intent.EXTRA_STREAM, videos[0].uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } else {
            Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "video/*"
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(videos.map { it.uri }))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }

        context.startActivity(
            Intent.createChooser(
                intent,
                "Share video${if (videos.size > 1) "s" else ""}"
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun deleteVideos(vararg videos: Video, launcher: ActivityResultLauncher<IntentSenderRequest>) {
        if (videos.isEmpty()) return

        val resolver = context.contentResolver
        val pendingUris = mutableListOf<Uri>()

        // Try direct deletion first (works if permission already granted or on old versions (Android 8 & 9) )
        for (video in videos) {
            try {
                resolver.delete(video.uri, null, null)
            } catch (s: SecurityException) {
                pendingUris.add(video.uri)
            }
        }

        if (pendingUris.isEmpty()) return

        // If we have pending URIs, we need to request permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val pendingIntent = MediaStore.createDeleteRequest(resolver, pendingUris)
                val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent.intentSender).build()
                launcher.launch(intentSenderRequest)
            } catch (e: Exception) {
                // Fallback: try one by one prompt if bulk fails
                for (uri in pendingUris) {
                    try {
                        resolver.delete(uri, null, null)
                    } catch (s: SecurityException) {
                        val intentSender = (s as? RecoverableSecurityException)?.userAction?.actionIntent?.intentSender
                            ?: MediaStore.createWriteRequest(resolver, listOf(uri)).intentSender
                        launcher.launch(IntentSenderRequest.Builder(intentSender).build())
                        break
                    }
                }
            }
        }
        // Android 10 (API 29)
        else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            for (uri in pendingUris) {
                try {
                    resolver.delete(uri, null, null)
                } catch (securityException: SecurityException) {
                    val recoverableSecurityException = securityException as? RecoverableSecurityException
                        ?: throw securityException

                    val intentSender = recoverableSecurityException.userAction.actionIntent.intentSender
                    val intentSenderRequest = IntentSenderRequest.Builder(intentSender).build()
                    launcher.launch(intentSenderRequest)
                    break
                }
            }
        }
    }

}