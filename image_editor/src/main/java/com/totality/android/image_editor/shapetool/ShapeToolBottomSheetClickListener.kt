package com.totality.android.image_editor.shapetool

import ja.burhanrashid52.photoeditor.shape.ShapeType

interface ShapeToolBottomSheetClickListener {
    fun onColorChanged(colorCode: Int)
    fun onOpacityChanged(opacity: Int)
    fun onShapeSizeChanged(shapeSize: Int)
    fun onShapePicked(shapeType: ShapeType)
}