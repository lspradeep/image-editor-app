package com.totality.android.image_editor.colorpicker

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import com.totality.android.image_editor.colorpicker.OnColorPickerClickListener
import com.totality.android.image_editor.colorpicker.ColorPickerAdapter
import android.view.ViewGroup
import com.totality.android.image_editor.colorpicker.ColorPickerViewHolder
import com.totality.android.image_editor.R
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.totality.android.image_editor.databinding.ColorPickerItemListBinding
import java.util.ArrayList

class ColorPickerAdapter internal constructor(
    context: Context,
    private val onColorPickerClickListener: OnColorPickerClickListener,
) : RecyclerView.Adapter<ColorPickerViewHolder>() {

    private val colorPickerColors = mutableListOf<Int>()

    init {
        colorPickerColors.add(ContextCompat.getColor(context, R.color.blue_color_picker))
        colorPickerColors.add(ContextCompat.getColor(context, R.color.brown_color_picker))
        colorPickerColors.add(ContextCompat.getColor(context, R.color.green_color_picker))
        colorPickerColors.add(ContextCompat.getColor(context, R.color.orange_color_picker))
        colorPickerColors.add(ContextCompat.getColor(context, R.color.red_color_picker))
        colorPickerColors.add(ContextCompat.getColor(context, R.color.black))
        colorPickerColors.add(ContextCompat.getColor(context, R.color.red_orange_color_picker))
        colorPickerColors.add(ContextCompat.getColor(context, R.color.sky_blue_color_picker))
        colorPickerColors.add(ContextCompat.getColor(context, R.color.violet_color_picker))
        colorPickerColors.add(ContextCompat.getColor(context, R.color.white))
        colorPickerColors.add(ContextCompat.getColor(context, R.color.yellow_color_picker))
        colorPickerColors.add(ContextCompat.getColor(context,
            R.color.yellow_green_color_picker))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorPickerViewHolder {
        val binding =
            DataBindingUtil.inflate<ColorPickerItemListBinding>(LayoutInflater.from(parent.context),
                R.layout.color_picker_item_list, parent, false)
        return ColorPickerViewHolder(binding, onColorPickerClickListener)
    }

    override fun onBindViewHolder(holder: ColorPickerViewHolder, position: Int) {
        holder.bind(colorPickerColors[position])
    }

    override fun getItemCount(): Int {
        return colorPickerColors.size
    }
}