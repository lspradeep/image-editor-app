package com.totality.android.image_editor.filter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.totality.android.image_editor.databinding.RowFilterViewBinding
import ja.burhanrashid52.photoeditor.PhotoFilter
import java.io.IOException
import java.io.InputStream

class FiltersViewHolder(
    private val binding: RowFilterViewBinding,
    private val filterListener: FilterListener,
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(filterPair: Pair<String, PhotoFilter>) {
        val fromAsset = getBitmapFromAsset(binding.root.context, filterPair.first)
        binding.imgFilterView.setImageBitmap(fromAsset)
        binding.txtFilterName.text = filterPair.second.name.replace("_", " ")

        binding.root.setOnClickListener {
            filterListener.onFilterSelected(filterPair.second)
        }
    }

    private fun getBitmapFromAsset(context: Context, strName: String): Bitmap? {
        val assetManager = context.assets
        var istr: InputStream? = null
        return try {
            istr = assetManager.open(strName)
            BitmapFactory.decodeStream(istr)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}