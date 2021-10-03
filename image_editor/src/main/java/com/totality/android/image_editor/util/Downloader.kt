package com.totality.android.image_editor.util

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore.Images
import java.io.File
import java.io.FileOutputStream


object Downloader {
    fun addImageToGallery(filePath: String, context: Context) {
        val values = ContentValues()
        values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis())
        values.put(Images.Media.MIME_TYPE, "image/jpeg")
        values.put(Images.Media.DATA, filePath)
        context.contentResolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values)
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