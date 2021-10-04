package com.totality.android.image_editor.util

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.io.File


object StorageUtil {
    fun getFile(context: Context): File {
        val path =
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath
        return File(path, "edit${System.currentTimeMillis()}.png")
    }

    fun getRealPathFromURI(context: Context, contentURI: Uri): String {
        val result: String
        val cursor = context.contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path.toString()
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
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