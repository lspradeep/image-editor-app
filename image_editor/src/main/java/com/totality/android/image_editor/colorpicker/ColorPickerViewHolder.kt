package com.totality.android.image_editor.colorpicker

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.totality.android.image_editor.R
import com.totality.android.image_editor.databinding.ColorPickerItemListBinding

class ColorPickerViewHolder(
    private val binding: ColorPickerItemListBinding,
    private val onColorPickerClickListener: OnColorPickerClickListener,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(color: Int) {
        binding.colorPickerView.setBackgroundColor(color)
        binding.root.setOnClickListener(View.OnClickListener {
            onColorPickerClickListener.onColorPickerClickListener(
                color)
        })
    }
}