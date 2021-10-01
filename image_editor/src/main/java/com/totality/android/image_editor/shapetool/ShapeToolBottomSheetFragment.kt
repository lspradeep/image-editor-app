package com.totality.android.image_editor.shapetool

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.totality.android.image_editor.R
import com.totality.android.image_editor.colorpicker.ColorPickerAdapter
import com.totality.android.image_editor.colorpicker.OnColorPickerClickListener
import com.totality.android.image_editor.databinding.FragmentShapeToolBottomSheetBinding
import ja.burhanrashid52.photoeditor.shape.ShapeType

class ShapeToolBottomSheetFragment(private val shapeToolBottomSheetClickListener: ShapeToolBottomSheetClickListener) :
    BottomSheetDialogFragment(), OnSeekBarChangeListener,
    OnColorPickerClickListener {

    private lateinit var binding: FragmentShapeToolBottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_shape_tool_bottom_sheet,
            container,
            false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // shape picker
        binding.shapeRadioGroup.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            when (checkedId) {
                R.id.lineRadioButton -> {
                    shapeToolBottomSheetClickListener.onShapePicked(ShapeType.LINE)
                }
                R.id.ovalRadioButton -> {
                    shapeToolBottomSheetClickListener.onShapePicked(ShapeType.OVAL)
                }
                R.id.rectRadioButton -> {
                    shapeToolBottomSheetClickListener.onShapePicked(ShapeType.RECTANGLE)
                }
                else -> {
                    shapeToolBottomSheetClickListener.onShapePicked(ShapeType.BRUSH)
                }
            }
        }
        binding.shapeOpacity.setOnSeekBarChangeListener(this)
        binding.shapeSize.setOnSeekBarChangeListener(this)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerColors.layoutManager = layoutManager
        binding.recyclerColors.setHasFixedSize(true)
        val colorPickerAdapter = ColorPickerAdapter(requireContext(), this)
        binding.recyclerColors.adapter = colorPickerAdapter
    }

    override fun onColorPickerClickListener(colorCode: Int) {
        dismiss()
        shapeToolBottomSheetClickListener.onColorChanged(colorCode)
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
        when (seekBar.id) {
            R.id.shapeOpacity -> shapeToolBottomSheetClickListener.onOpacityChanged(i)
            R.id.shapeSize -> shapeToolBottomSheetClickListener.onShapeSizeChanged(i)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}