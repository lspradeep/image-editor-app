package com.totality.android.image_editor.tools

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.totality.android.image_editor.databinding.RowEditingToolsBinding

class ToolsViewHolder(
    private val binding: RowEditingToolsBinding,
    private val onItemSelected: OnItemSelected,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(toolModel: ToolModel) {
        val (mToolName, mToolIcon) = toolModel
        binding.txtTool.text = mToolName
        binding.imgToolIcon.setImageResource(mToolIcon)
        binding.root.setOnClickListener { v: View? ->
            onItemSelected.onToolSelected(toolModel.mToolType)
        }
    }
}