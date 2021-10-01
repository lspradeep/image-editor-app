package com.totality.android.image_editor

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.totality.android.image_editor.databinding.ActivityImageEditorBinding
import com.totality.android.image_editor.shapetool.ShapeToolBottomSheetClickListener
import com.totality.android.image_editor.shapetool.ShapeToolBottomSheetFragment
import com.totality.android.image_editor.texttool.TextEditor
import com.totality.android.image_editor.texttool.TextEditorDialogFragment
import com.totality.android.image_editor.tools.EditingToolsAdapter
import com.totality.android.image_editor.tools.OnItemSelected
import com.totality.android.image_editor.tools.ToolType
import com.totality.android.image_editor.util.ImageUtils
import com.totality.android.image_editor.util.showErrorToast
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.TextStyleBuilder
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder
import ja.burhanrashid52.photoeditor.shape.ShapeType

class ImageEditorActivity : AppCompatActivity(), OnItemSelected, ShapeToolBottomSheetClickListener,
    View.OnClickListener, TextEditor {

    private lateinit var binding: ActivityImageEditorBinding
    private lateinit var photoEditor: PhotoEditor
    private val toolsAdapter = EditingToolsAdapter(this)
    private var mShapeBuilder: ShapeBuilder? = null
    private val shapeToolBottomSheetFragment = ShapeToolBottomSheetFragment(this)
    private val textEditorDialogFragment = TextEditorDialogFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_editor)
        getArgs()
        initView()
        setListeners()
        setupToolsRecycler()
    }

    private fun setListeners() {
        binding.imgUndo.setOnClickListener(this)
        binding.imgRedo.setOnClickListener(this)
    }

    private fun setupToolsRecycler() {
        binding.recyclerTools.apply {
            adapter = toolsAdapter
            layoutManager =
                LinearLayoutManager(
                    this@ImageEditorActivity,
                    RecyclerView.HORIZONTAL,
                    false
                )
        }
    }

    private fun initView() {
        supportActionBar?.title = getString(R.string.edit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        photoEditor = PhotoEditor.Builder(this, binding.photoEditorView)
            .setPinchTextScalable(true)
            .setClipSourceImage(true)
            .build()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_image, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            R.id.action_save -> {
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgUndo -> {
                photoEditor.undo()
            }
            R.id.imgRedo -> {
                photoEditor.redo()
            }
        }
    }

    private fun getArgs() {
        intent.extras?.getByteArray(ARGS_IMAGE_TO_EDIT)?.let {
            val bitmap = ImageUtils.convertByteArrayToBitmap(it)
            binding.photoEditorView.source.setImageBitmap(bitmap)
        } ?: run {
            showErrorToast("Error loading image!")
        }
    }

    override fun onToolSelected(toolType: ToolType) {
        when (toolType) {
            ToolType.SHAPE -> {
                photoEditor.setBrushDrawingMode(true)
                mShapeBuilder = ShapeBuilder()
                photoEditor.setShape(mShapeBuilder)
                showBottomSheetDialogFragment(shapeToolBottomSheetFragment)
            }
            ToolType.TEXT -> {
                textEditorDialogFragment.setOnTextEditorListener(this)
                textEditorDialogFragment.show(supportFragmentManager,null)
            }
//            ToolType.ERASER -> {
//                photoEditor.brushEraser()
//            }
//            ToolType.FILTER -> {
//                showFilter(true)
//            }
//            ToolType.EMOJI -> {
//                showBottomSheetDialogFragment(mEmojiBSFragment)
//            }
//            ToolType.STICKER -> {
//                showBottomSheetDialogFragment(mStickerBSFragment)
//            }
        }
    }

    private fun showBottomSheetDialogFragment(fragment: BottomSheetDialogFragment?) {
        if (fragment == null || fragment.isAdded) {
            return
        }
        fragment.show(supportFragmentManager, fragment.tag)
    }

    override fun onColorChanged(colorCode: Int) {
        photoEditor.setShape(mShapeBuilder?.withShapeColor(colorCode))
    }

    override fun onOpacityChanged(opacity: Int) {
        photoEditor.setShape(mShapeBuilder?.withShapeOpacity(opacity))
    }

    override fun onShapeSizeChanged(shapeSize: Int) {
        photoEditor.setShape(mShapeBuilder?.withShapeSize(shapeSize.toFloat()))
    }

    override fun onShapePicked(shapeType: ShapeType) {
        photoEditor.setShape(mShapeBuilder?.withShapeType(shapeType))
    }

    override fun onDone(inputText: String?, colorCode: Int) {
        val styleBuilder = TextStyleBuilder()
        styleBuilder.withTextColor(colorCode)
        photoEditor.addText(inputText, styleBuilder)
    }

    companion object {
        const val ARGS_IMAGE_TO_EDIT = "IMAGE_TO_EDIT"
    }



}