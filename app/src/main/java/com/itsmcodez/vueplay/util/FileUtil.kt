package com.itsmcodez.vueplay.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

object FileUtil {

    /**
     * Saves a bitmap to the Pictures/VuePlay folder.
     * This is a suspend function and should be called from a coroutine scope.
     */
    suspend fun saveBitmapToPictures(context: Context, bitmap: Bitmap, fileName: String): Boolean =
        withContext(Dispatchers.IO) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver

                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "${fileName.trim()}.png")
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        "${Environment.DIRECTORY_PICTURES}/VuePlay"
                    )
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }

                val imageUri =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                        ?: return@withContext false

                try {
                    resolver.openOutputStream(imageUri)?.use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    }

                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(imageUri, contentValues, null, null)
                    true

                } catch (e: Exception) {
                    e.printStackTrace()
                    resolver.delete(imageUri, null, null)
                    false
                }

            } else {
                val picturesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val folder = java.io.File(picturesDir, "VuePlay")

                // Ensure the directory exists
                if (!folder.exists() && !folder.mkdirs()) {
                    return@withContext false
                }

                val file = java.io.File(folder, "${fileName.trim()}.png")

                try {
                    file.outputStream().use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        stream.flush()
                    }

                    // Trigger media scanner so the image appears in the Gallery immediately
                    MediaScannerConnection.scanFile(context, arrayOf(file.toString()), null, null)
                    true
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
        }

    fun formatSizeMb(
        locale: Locale,
        bytes: Float
    ): String {
        if (bytes < 0) return ""
        return String.format(
            locale,
            "%.2f MB",
            bytes / (1024f * 1024f)
        )
    }
}