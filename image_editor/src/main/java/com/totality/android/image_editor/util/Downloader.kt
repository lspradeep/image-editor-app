package com.totality.android.image_editor.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.Images
import androidx.annotation.ChecksSdkIntAtLeast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


object Downloader {
    fun addImageToGallery(filePath: String, context: Context): Uri? {
        val imageCollection = sdk29AndUp {
            Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } ?: Images.Media.EXTERNAL_CONTENT_URI

        val values = ContentValues()
        values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis())
        values.put(Images.Media.MIME_TYPE, "image/jpeg")
        return context.contentResolver.insert(imageCollection, values)?.also { uri->
            context.contentResolver.openOutputStream(uri).use { outputStream ->
                if(!ImageUtils.convertByteArrayToBitmap(ImageUtils.convertImageToByteArray(File(filePath)))
                        .compress(Bitmap.CompressFormat.JPEG, 100, outputStream)) {
                    throw IOException("Couldn't save bitmap")
                }
            }
        }
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q, lambda = 0)
    private inline fun <T> sdk29AndUp(onSdk29: () -> T): T? {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            onSdk29()
        } else null
    }

    fun saveToGallery(file: File): Boolean {
        return try {
            val fos = FileOutputStream(file)
            fos.write(ImageUtils.convertImageToByteArray(file))
            fos.close();
            true
        } catch (e: Exception) {
            false
        }
    }
}