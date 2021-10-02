package com.totality.android.image_editor.texttool

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.totality.android.image_editor.R
import com.totality.android.image_editor.colorpicker.ColorPickerAdapter
import com.totality.android.image_editor.colorpicker.OnColorPickerClickListener
import com.totality.android.image_editor.databinding.FragmentTextToolDialogBinding

class TextEditorDialogFragment : DialogFragment(), OnColorPickerClickListener {

    private lateinit var binding: FragmentTextToolDialogBinding
    private var mInputMethodManager: InputMethodManager? = null
    private var mColorCode = 0
    private var mTextEditDialogListener: TextEditDialogListener? = null

    override fun onColorPickerClickListener(colorCode: Int) {
        mColorCode = colorCode
        binding.addTextEditText.setTextColor(colorCode)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        //Make dialog full screen with transparent background
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_text_tool_dialog, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mInputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        //Setup the color picker for text color
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerColors.layoutManager = layoutManager
        binding.recyclerColors.setHasFixedSize(true)
        val colorPickerAdapter = ColorPickerAdapter(requireContext(), this)
        //This listener will change the text color when clicked on any color from picker
        binding.recyclerColors.adapter = colorPickerAdapter
        if (arguments?.getString(EXTRA_INPUT_TEXT) != null) {
            binding.addTextEditText.setText(arguments?.getString(EXTRA_INPUT_TEXT))
        }
        if (arguments?.getInt(EXTRA_COLOR_CODE, -1) != -1) {
            mColorCode = arguments?.getInt(EXTRA_COLOR_CODE) ?: -1
            binding.addTextEditText.setTextColor(mColorCode)
        }
        mInputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        //Make a callback on activity when user is done with text editing
        binding.addTextDoneTv.setOnClickListener { v ->
            mInputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
            dismiss()
            val inputText = binding.addTextEditText.text.toString()
            if (!TextUtils.isEmpty(inputText) && mTextEditDialogListener != null) {
                mTextEditDialogListener?.onTextEditingDone(inputText, mColorCode)
            }
        }
    }

    //Callback to listener if user is done with text editing
    fun setOnTextEditorListener(textEditDialogListener: TextEditDialogListener?) {
        mTextEditDialogListener = textEditDialogListener
    }

    companion object {
        private val TAG = TextEditorDialogFragment::class.java.simpleName
        const val EXTRA_INPUT_TEXT = "extra_input_text"
        const val EXTRA_COLOR_CODE = "extra_color_code"

        //Show dialog with provide text and text color
        //Show dialog with default text input as empty and text color white
        @JvmOverloads
        fun show(
            appCompatActivity: AppCompatActivity,
            inputText: String =
                "",
            @ColorInt colorCode: Int = ContextCompat.getColor(appCompatActivity, R.color.white),
        ): TextEditorDialogFragment {
            val args = Bundle()
            args.putString(EXTRA_INPUT_TEXT, inputText)
            args.putInt(EXTRA_COLOR_CODE, colorCode)
            val fragment = TextEditorDialogFragment()
            fragment.arguments = args
            fragment.show(appCompatActivity.supportFragmentManager, TAG)
            return fragment
        }
    }
}