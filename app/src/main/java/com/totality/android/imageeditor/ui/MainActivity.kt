package com.totality.android.imageeditor.ui

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.jinatonic.confetti.CommonConfetti
import com.totality.android.image_editor.ImageEditorActivity
import com.totality.android.image_editor.ImageEditorActivity.Companion.ARGS_SAVED_IMAGE_PATH
import com.totality.android.image_editor.util.showSimpleToast
import com.totality.android.imageeditor.R
import com.totality.android.imageeditor.databinding.ActivityMainBinding
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val intentEditImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()
        ) {
            handleImageEditorActivityResult(it)
        }

    private lateinit var binding: ActivityMainBinding
    private var savedImagePath: String? = null

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

    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu?.findItem(R.id.action_share)?.isVisible = !savedImagePath.isNullOrBlank()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_share) {
            shareImage()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun shareImage() {
        savedImagePath?.let { path ->
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(this@MainActivity,
                        "$packageName.provider",
                        File(path)))
                type = "image/png"
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.send_to)))
        }
    }


    private fun setListeners() {
        binding.btnGallery.setOnClickListener(this)
        binding.btnSelfie.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_gallery -> {
                requestPermission()
            }
            R.id.btn_selfie -> {
                easyImage.openCameraForImage(this)
            }
        }
    }

    private val perms = arrayListOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            perms.add(Manifest.permission.ACCESS_MEDIA_LOCATION)
        }
        if (EasyPermissions.hasPermissions(this, *perms.toTypedArray())) {
            easyImage.openGallery(this)
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "ACCESS_MEDIA_LOCATION",
                100, *perms.toTypedArray());
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easyImage.handleActivityResult(requestCode,
            resultCode,
            data,
            this,
            object : DefaultCallback() {
                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    onImageReturned(imageFiles[0], source)
                }

                override fun onImagePickerError(error: Throwable, source: MediaSource) {
                    println(error.printStackTrace())
                    showSimpleToast(getString(R.string.error_loading_image))
                }

                override fun onCanceled(source: MediaSource) {
                }
            })
    }

    //on image returned from camera or gallery
    private fun onImageReturned(mediaFile: MediaFile, source: MediaSource) {
        var imageFile = mediaFile.file
        if (source == MediaSource.CAMERA_IMAGE) {
            applyRotationIfNeeded(mediaFile.file)?.let { rotatedImage ->
                imageFile = rotatedImage
            }
        }
        val intent = Intent(this, ImageEditorActivity::class.java)
        intent.putExtra(ImageEditorActivity.ARGS_IMAGE_TO_EDIT, imageFile.absolutePath)
        intentEditImage.launch(intent)

    }

    private fun applyRotationIfNeeded(imageFile: File): File? {
        val exif = ExifInterface(imageFile.absolutePath)
        val rotation = when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
        if (rotation == 0) return null
        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
        val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
        val rotatedBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()

        lateinit var out: FileOutputStream
        try {
            out = FileOutputStream(imageFile)
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        } catch (e: Exception) {

        } finally {
            rotatedBitmap.recycle()
            out.close()
        }
        return imageFile
    }

    private fun handleImageEditorActivityResult(result: ActivityResult?) {
        result?.data?.getStringExtra(ARGS_SAVED_IMAGE_PATH)?.let { path ->
            savedImagePath = path
            showSimpleToast(path)
            Glide.with(this).load(File(path))
                .apply(requestOptions).into(binding.image)
            CommonConfetti.rainingConfetti(binding.layoutRoot,
                arrayOf(Color.RED,
                    Color.BLUE,
                    Color.GREEN,
                    Color.YELLOW,
                    Color.MAGENTA).toIntArray())
                .oneShot()
        }
    }
}