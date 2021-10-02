package com.totality.android.image_editor.util

import android.content.Context
import android.widget.Toast

fun Context.showSimpleToast(msg: String) {
    Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
}