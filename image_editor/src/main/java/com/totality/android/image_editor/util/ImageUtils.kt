package com.totality.android.image_editor.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.File


object ImageUtils {
    fun convertImageToByteArray(imageFile: File): ByteArray {
        val bmp = BitmapFactory.decodeFile(imageFile.path)
        val stream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    fun convertByteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size);
    }
}