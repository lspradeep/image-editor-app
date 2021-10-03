package com.totality.android.imageeditor.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.totality.android.image_editor.ImageEditorActivity
import com.totality.android.image_editor.ImageEditorActivity.Companion.ARGS_IMAGE_TO_EDIT
import com.totality.android.image_editor.ImageEditorActivity.Companion.ARGS_SAVED_IMAGE_PATH
import com.totality.android.image_editor.util.showSimpleToast
import com.totality.android.imageeditor.R
import com.totality.android.imageeditor.databinding.ActivityMainBinding
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import java.io.File


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val intentEditImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()
        ) {
            handleImageEditorActivityResult(it)
        }

    private lateinit var binding: ActivityMainBinding
    private val easyImage by lazy {
        EasyImage.Builder(this)
            .allowMultiple(false)
            .build()
    }
    private val requestOptions by lazy {
        RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_placeholder)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setListeners()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_gallery -> {
                easyImage.openGallery(this)
            }
            R.id.btn_selfie -> {
                easyImage.openCameraForImage(this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easyImage.handleActivityResult(requestCode,
            resultCode,
            data,
            this,
            object : DefaultCallback() {
                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    onImageReturned(imageFiles[0])
                }

                override fun onImagePickerError(error: Throwable, source: MediaSource) {
                    //Some error handling
                    println(error.printStackTrace())
                    showSimpleToast("Error loading image")
                }

                override fun onCanceled(source: MediaSource) {
                    //Not necessary to remove any files manually anymore
                    showSimpleToast("Cancelled")
                }
            })
    }

    private fun onImageReturned(mediaFile: MediaFile) {
        val intent = Intent(this, ImageEditorActivity::class.java)
        intent.putExtra(ARGS_IMAGE_TO_EDIT, mediaFile.file.absolutePath)
        intentEditImage.launch(intent)
    }

    private fun handleImageEditorActivityResult(result: ActivityResult?) {
        result?.data?.getStringExtra(ARGS_SAVED_IMAGE_PATH)?.let { path ->
            showSimpleToast(path)
            Glide.with(this).load(File(path))
                .apply(requestOptions).into(binding.image)
//            StorageUtil.deleteDir(File(path))
        }
    }

    private fun setListeners() {
        binding.btnGallery.setOnClickListener(this)
        binding.btnSelfie.setOnClickListener(this)
    }
}