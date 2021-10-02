package com.totality.android.image_editor.filter

import android.util.Pair
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.totality.android.image_editor.R
import com.totality.android.image_editor.databinding.RowFilterViewBinding
import ja.burhanrashid52.photoeditor.PhotoFilter
import java.util.*

class FilterViewAdapter(private val filterListener: FilterListener) :
    RecyclerView.Adapter<FiltersViewHolder>() {
    private val mPairList: MutableList<Pair<String, PhotoFilter>> = ArrayList()

    init {
        setupFilters()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FiltersViewHolder {
        val binding: RowFilterViewBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.row_filter_view,
                parent,
                false)
        return FiltersViewHolder(binding, filterListener)
    }

    override fun onBindViewHolder(holder: FiltersViewHolder, position: Int) {
        holder.bind(mPairList[position])
    }

    override fun getItemCount(): Int {
        return mPairList.size
    }

    private fun setupFilters() {
        mPairList.add(Pair("filters/original.jpg", PhotoFilter.NONE))
        mPairList.add(Pair("filters/auto_fix.png", PhotoFilter.AUTO_FIX))
        mPairList.add(Pair("filters/brightness.png", PhotoFilter.BRIGHTNESS))
        mPairList.add(Pair("filters/contrast.png", PhotoFilter.CONTRAST))
        mPairList.add(Pair("filters/documentary.png", PhotoFilter.DOCUMENTARY))
        mPairList.add(Pair("filters/dual_tone.png", PhotoFilter.DUE_TONE))
        mPairList.add(Pair("filters/fill_light.png", PhotoFilter.FILL_LIGHT))
        mPairList.add(Pair("filters/fish_eye.png", PhotoFilter.FISH_EYE))
        mPairList.add(Pair("filters/grain.png", PhotoFilter.GRAIN))
        mPairList.add(Pair("filters/gray_scale.png", PhotoFilter.GRAY_SCALE))
        mPairList.add(Pair("filters/lomish.png", PhotoFilter.LOMISH))
        mPairList.add(Pair("filters/negative.png", PhotoFilter.NEGATIVE))
        mPairList.add(Pair("filters/posterize.png", PhotoFilter.POSTERIZE))
        mPairList.add(Pair("filters/saturate.png", PhotoFilter.SATURATE))
        mPairList.add(Pair("filters/sepia.png", PhotoFilter.SEPIA))
        mPairList.add(Pair("filters/sharpen.png", PhotoFilter.SHARPEN))
        mPairList.add(Pair("filters/temprature.png", PhotoFilter.TEMPERATURE))
        mPairList.add(Pair("filters/tint.png", PhotoFilter.TINT))
        mPairList.add(Pair("filters/vignette.png", PhotoFilter.VIGNETTE))
        mPairList.add(Pair("filters/cross_process.png", PhotoFilter.CROSS_PROCESS))
        mPairList.add(Pair("filters/b_n_w.png", PhotoFilter.BLACK_WHITE))
        mPairList.add(Pair("filters/flip_horizental.png", PhotoFilter.FLIP_HORIZONTAL))
        mPairList.add(Pair("filters/flip_vertical.png", PhotoFilter.FLIP_VERTICAL))
        mPairList.add(Pair("filters/rotate.png", PhotoFilter.ROTATE))
    }

}