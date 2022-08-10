package com.android.maxclub.nasaapod.presentation.image_viewer

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.android.maxclub.nasaapod.R
import com.android.maxclub.nasaapod.domain.model.ImageInfo
import com.android.maxclub.nasaapod.databinding.ActivityImageBinding
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*

class ImageViewerActivity : AppCompatActivity() {
    private val viewModel: ImageViewerViewModel by viewModels()

    private lateinit var binding: ActivityImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.apply {
            setSupportActionBar(this)
            viewModel.imageInfo?.let { imageInfo ->
                title = imageInfo.title
                subtitle = imageInfo.copyright?.let { copyright ->
                    getString(R.string.copyright_placeholder, copyright)
                }
            }
            setNavigationOnClickListener {
                finish()
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is ImageViewerUiState.UrlLoading -> {
                            preloadBitmap(uiState.url)
                        }
                        is ImageViewerUiState.HdUrlLoading -> {
                            binding.imageView.setImageBitmap(uiState.bitmap)
                            loadHdBitmap(uiState.hdUrl)
                        }
                        is ImageViewerUiState.Success -> {
                            binding.imageView.setImageBitmap(uiState.bitmap)
                        }
                        ImageViewerUiState.Error -> {
                            binding.imageView.setImageResource(R.drawable.ic_error_full_image_placeholder_24)
                        }
                    }
                    invalidateOptionsMenu()
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { uiEvent ->
                    when (uiEvent) {
                        is ImageViewerUiEvent.OnSave -> {
                            saveImage(uiEvent.bitmap, uiEvent.imageInfo)
                        }
                        is ImageViewerUiEvent.OnShowSaveError -> {
                            showBitmapNotFoundErrorSnackbar()
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_image_viewer, menu)

        viewModel.uiState.value.let { uiState ->
            menu.findItem(R.id.hd)?.apply {
                isVisible = uiState is ImageViewerUiState.Success
                isEnabled = uiState is ImageViewerUiState.Success && uiState.isHd
            }
        }
        menu.findItem(R.id.save)?.isEnabled = viewModel.bitmap != null

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.save -> {
                viewModel.onEvent(ImageViewerEvent.OnSave)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun preloadBitmap(url: String) {
        Picasso.get()
            .load(url)
            .placeholder(R.drawable.ic_full_image_placeholder_24)
            .error(R.drawable.ic_error_full_image_placeholder_24)
            .apply {
                into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
                        viewModel.onEvent(ImageViewerEvent.OnUrlLoaded(bitmap))
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        viewModel.onEvent(ImageViewerEvent.OnUrlLoadingError)
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    }
                })
            }.into(binding.imageView)
    }

    private fun loadHdBitmap(hdUrl: String) {
        Picasso.get()
            .load(hdUrl)
            .into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
                    viewModel.onEvent(ImageViewerEvent.OnHdUrlLoaded(bitmap))
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    viewModel.onEvent(ImageViewerEvent.OnHdUrlLoadingError)
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                }
            })
    }

    private fun saveImage(bitmap: Bitmap, imageInfo: ImageInfo) {
        if (hasStoragePermission()) {
            val filename = "IMG_${imageInfo.date.time}.jpg"
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val fileOutputStream: OutputStream =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val contentValues = ContentValues().apply {
                                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                                put(
                                    MediaStore.MediaColumns.RELATIVE_PATH,
                                    Environment.DIRECTORY_PICTURES
                                )
                            }
                            contentResolver.insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                contentValues
                            )?.let { imageUri ->
                                contentResolver.openOutputStream(imageUri)
                            } ?: throw FileNotFoundException()
                        } else {

                            FileOutputStream(
                                File(
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                    filename,
                                )
                            )
                        }

                    fileOutputStream.use { outputStream ->
                        if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)) {
                            showSuccessfullySavedSnackbar()
                        } else {
                            throw FileNotFoundException()
                        }
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    showSaveErrorSnackbar()
                }
            }
        } else {
            requestStoragePermission()
        }
    }

    private fun showBitmapNotFoundErrorSnackbar() {
        Snackbar.make(binding.root, R.string.bitmap_not_found_error_message, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun showSaveErrorSnackbar() {
        Snackbar.make(binding.root, R.string.save_error_message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showSuccessfullySavedSnackbar() {
        Snackbar.make(binding.root, R.string.successfully_saved_message, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun hasStoragePermission(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            true
        } else {
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

    private fun requestStoragePermission() {
        if (!hasStoragePermission()) {
            requestStoragePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private val requestStoragePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.onEvent(ImageViewerEvent.OnSave)
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_permission_24)
                        .setTitle(R.string.permission_dialog_title)
                        .setMessage(R.string.permission_dialog_text)
                        .setPositiveButton(R.string.permission_settings_button_text) { _, _ ->
                            launchPermissionSettings()
                        }
                        .create()
                        .show()
                }
            }
        }

    private fun launchPermissionSettings() {
        Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            data = Uri.fromParts("package", packageName, null)
        }.also { intent ->
            startActivity(intent)
        }
    }

    companion object {
        const val EXTRA_IMAGE_INFO = "com.android.maxclub.nasaapod.imagevieweractivity.imageinfo"

        fun newIntent(context: Context, imageInfo: ImageInfo): Intent =
            Intent(context, ImageViewerActivity::class.java).apply {
                putExtra(EXTRA_IMAGE_INFO, imageInfo)
            }
    }
}