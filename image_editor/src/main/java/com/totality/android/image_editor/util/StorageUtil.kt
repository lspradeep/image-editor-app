package com.totality.android.image_editor.util

import android.content.Context
import java.io.File

object StorageUtil {
    private const val FILTERED_IMAGES = "filters"

    fun getFile(context: Context): File {
        return File(context.filesDir, FILTERED_IMAGES)
    }
}