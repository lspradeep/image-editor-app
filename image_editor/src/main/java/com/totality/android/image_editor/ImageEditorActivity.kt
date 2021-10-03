package com.totality.android.image_editor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.theartofdev.edmodo.cropper.CropImageView
import com.totality.android.image_editor.databinding.ActivityImageEditorBinding
import com.totality.android.image_editor.filter.FilterListener
import com.totality.android.image_editor.filter.FilterViewAdapter
import com.totality.android.image_editor.shapetool.ShapeToolBottomSheetClickListener
import com.totality.android.image_editor.shapetool.ShapeToolBottomSheetFragment
import com.totality.android.image_editor.texttool.TextEditDialogListener
import com.totality.android.image_editor.texttool.TextEditorDialogFragment
import com.totality.android.image_editor.tools.EditingToolsAdapter
import com.totality.android.image_editor.tools.OnItemSelected
import com.totality.android.image_editor.tools.ToolType
import com.totality.android.image_editor.util.Downloader
import com.totality.android.image_editor.util.StorageUtil
import com.totality.android.image_editor.util.showSimpleToast
import ja.burhanrashid52.photoeditor.OnSaveBitmap
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoFilter
import ja.burhanrashid52.photoeditor.TextStyleBuilder
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder
import ja.burhanrashid52.photoeditor.shape.ShapeType
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class ImageEditorActivity : AppCompatActivity(), OnItemSelected,
    ShapeToolBottomSheetClickListener,
    View.OnClickListener, TextEditDialogListener, CropImageView.OnCropImageCompleteListener,
    FilterListener {

    private lateinit var binding: ActivityImageEditorBinding
    private lateinit var photoEditor: PhotoEditor
    private val toolsAdapter = EditingToolsAdapter(this)
    private var mShapeBuilder: ShapeBuilder? = null
    private val shapeToolBottomSheetFragment = ShapeToolBottomSheetFragment(this)
    private val textEditorDialogFragment = TextEditorDialogFragment()
    private val mFilterViewAdapter = FilterViewAdapter(this)
    var perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_editor)
        getArgs()
        initView()
        setListeners()
        setupToolsRecycler()
        setUpFiltersRecycler()
    }

    private fun setUpFiltersRecycler() {
        binding.recyclerFilters.apply {
            adapter = mFilterViewAdapter
            layoutManager =
                LinearLayoutManager(this@ImageEditorActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setListeners() {
        binding.imgUndo.setOnClickListener(this)
        binding.imgRedo.setOnClickListener(this)
        binding.cropImageView.setOnCropImageCompleteListener(this)
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
        //for image cropper
        menu?.findItem(R.id.action_rotate)?.isVisible = binding.cropImageView.isVisible
        menu?.findItem(R.id.action_done)?.isVisible =
            binding.cropImageView.isVisible || binding.recyclerFilters.isVisible
        //for downloading
        menu?.findItem(R.id.action_save)?.isVisible =
            binding.layoutEdit.isVisible && !binding.cropImageView.isVisible && !binding.recyclerFilters.isVisible
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        if (binding.cropImageView.isVisible) {
            togglePhotoEditorAndCrop()
            invalidateOptionsMenu()
            return
        }
        if (binding.recyclerFilters.isVisible) {
            toggleFiltersLayout()
            invalidateOptionsMenu()
            return
        }
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            R.id.action_save -> {
                requestPermission()
            }
            R.id.action_rotate -> {
                binding.cropImageView.rotatedDegrees =
                    if ((binding.cropImageView.rotatedDegrees + 90) > 360) 0 else (binding.cropImageView.rotatedDegrees + 90)
            }
            R.id.action_done -> {
                if (binding.cropImageView.isVisible) {
                    binding.cropImageView.getCroppedImageAsync()
                } else if (binding.recyclerFilters.isVisible) {
                    toggleFiltersLayout()
                    invalidateOptionsMenu()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun savePhoto() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        photoEditor.saveAsFile(StorageUtil.getFile(this).absolutePath,
            object : PhotoEditor.OnSaveListener {
                override fun onSuccess(imagePath: String) {
                    showSimpleToast("Image saved successfully!")
                    Downloader.addImageToGallery(imagePath,
                        this@ImageEditorActivity)?.let { uri ->
                        val path = StorageUtil.getRealPathFromURI(this@ImageEditorActivity, uri)
                        val intent = Intent()
                        intent.putExtra(ARGS_SAVED_IMAGE_PATH, path)
                        setResult(RESULT_OK, intent)
                        finish()
                        StorageUtil.deleteDir(File(imagePath))
                    } ?: run {
                        showSimpleToast("Error saving image")
                    }
                }

                override fun onFailure(exception: java.lang.Exception) {
                    showSimpleToast("Error saving image")
                }

            })
    }

    private fun requestPermission() {
        if (EasyPermissions.hasPermissions(this, *perms)) {
            savePhoto()
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this,
                "To save photo, write to storage permission is required.",
                100,
                *perms);
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_undo -> {
                photoEditor.undo()
            }
            R.id.img_redo -> {
                photoEditor.redo()
            }
        }
    }

    private fun getArgs() {
        intent.extras?.getString(ARGS_IMAGE_TO_EDIT)?.let { path ->
            binding.photoEditorView.source.setImageURI(Uri.fromFile(File(path)))
        } ?: run {
            showSimpleToast("Error loading image!")
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
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
                textEditorDialogFragment.show(supportFragmentManager, null)
            }
            ToolType.CROP, ToolType.ROTATE -> {
                getImageFromEditor()
            }
            ToolType.ERASER -> {
                photoEditor.brushEraser()
            }
            ToolType.FILTER -> {
                toggleFiltersLayout()
                invalidateOptionsMenu()
            }
//            ToolType.EMOJI -> {
//                showBottomSheetDialogFragment(mEmojiBSFragment)
//            }
//            ToolType.STICKER -> {
//                showBottomSheetDialogFragment(mStickerBSFragment)
//            }
            else -> {

            }
        }
    }

    private fun toggleFiltersLayout() {
        binding.recyclerFilters.isVisible = !binding.recyclerFilters.isVisible

        binding.recyclerTools.isInvisible = !binding.recyclerTools.isInvisible
        binding.recyclerTools.isEnabled = !binding.recyclerTools.isInvisible

    }

    private fun getImageFromEditor() {
        photoEditor.saveAsBitmap(object : OnSaveBitmap {
            override fun onBitmapReady(saveBitmap: Bitmap) {
                togglePhotoEditorAndCrop()
                invalidateOptionsMenu()
                binding.cropImageView.setImageBitmap(saveBitmap)
            }

            override fun onFailure(e: Exception?) {
                showSimpleToast("Something went wrong")
            }
        })
    }

    fun togglePhotoEditorAndCrop() {
        binding.layoutEdit.isVisible = !binding.layoutEdit.isVisible
        binding.cropImageView.isVisible = !binding.cropImageView.isVisible
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

    override fun onTextEditingDone(inputText: String?, colorCode: Int) {
        val styleBuilder = TextStyleBuilder()
        styleBuilder.withTextColor(colorCode)
        photoEditor.addText(inputText, styleBuilder)
    }

    override fun onCropImageComplete(view: CropImageView?, result: CropImageView.CropResult?) {
        togglePhotoEditorAndCrop()
        invalidateOptionsMenu()
        binding.photoEditorView.source.setImageBitmap(result?.bitmap)
    }

    override fun onFilterSelected(photoFilter: PhotoFilter) {
        try {
            photoEditor.setFilterEffect(photoFilter)
        } catch (e: Exception) {

        }
    }

    companion object {
        const val ARGS_IMAGE_TO_EDIT = "IMAGE_TO_EDIT"
        const val ARGS_SAVED_IMAGE_PATH = "SAVED_IMAGE_PATH"
    }
}