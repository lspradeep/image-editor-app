package com.totality.android.image_editor.tools

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.totality.android.image_editor.R
import com.totality.android.image_editor.databinding.RowEditingToolsBinding
import java.util.*

class EditingToolsAdapter(private val onItemSelected: OnItemSelected) :
    RecyclerView.Adapter<ToolsViewHolder>() {

    private val mToolList: MutableList<ToolModel> = ArrayList()

    init {
        mToolList.add(ToolModel("Shape", R.drawable.ic_oval, ToolType.SHAPE))
        mToolList.add(ToolModel("Text", R.drawable.ic_text, ToolType.TEXT))
        mToolList.add(ToolModel("Crop", R.drawable.ic_crop, ToolType.CROP))
        mToolList.add(ToolModel("Rotate", R.drawable.ic_rotate, ToolType.ROTATE))
        mToolList.add(ToolModel("Eraser", R.drawable.ic_eraser, ToolType.ERASER))
        mToolList.add(ToolModel("Filter", R.drawable.ic_photo_filter, ToolType.FILTER))
        mToolList.add(ToolModel("Emoji", R.drawable.ic_insert_emoticon, ToolType.EMOJI))
        mToolList.add(ToolModel("Sticker", R.drawable.ic_sticker, ToolType.STICKER))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolsViewHolder {
        val binding =
            DataBindingUtil.inflate<RowEditingToolsBinding>(LayoutInflater.from(parent.context),
                R.layout.row_editing_tools,
                parent,
                false)
        return ToolsViewHolder(binding, onItemSelected)
    }

    override fun onBindViewHolder(holder: ToolsViewHolder, position: Int) {
        holder.bind(mToolList[position])
    }

    override fun getItemCount(): Int {
        return mToolList.size
    }

}