package com.totality.android.image_editor.tools

data class ToolModel(
    @JvmField
    var mToolName: String,
    @JvmField
    var mToolIcon: Int = 0,
    @JvmField
    var mToolType: ToolType,
)