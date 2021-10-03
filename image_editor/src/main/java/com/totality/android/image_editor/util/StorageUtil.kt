package com.totality.android.image_editor.util

import android.content.Context
import android.os.Environment
import java.io.File
import java.util.*

object StorageUtil {
    fun getFile(context: Context): File {
        val name = Calendar.getInstance().timeInMillis
        val path =
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath
        return File("$path/${name}.jpeg")
    }

    fun deleteCache(context: Context) {
        try {
            val dir: File = context.cacheDir
            deleteDir(dir)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children?.indices ?: 0..0) {
                var success = false
                children?.get(i)?.let {
                    success = deleteDir(File(dir, it))
                }
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }
}