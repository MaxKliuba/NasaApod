package com.android.maxclub.nasaapod.presentation.image_viewer

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.android.maxclub.nasaapod.R
import com.android.maxclub.nasaapod.data.util.ImageInfo
import com.android.maxclub.nasaapod.databinding.ActivityImageBinding
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.launch

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
                            showSaveErrorSnackbar()
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_image, menu)
        viewModel.uiState.value.let { uiState ->
            menu.findItem(R.id.hd)?.apply {
                isVisible = uiState is ImageViewerUiState.Success
                isEnabled = uiState is ImageViewerUiState.Success && uiState.isHd
            }
        }
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
        MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            "IMG_${imageInfo.date.time}",
            imageInfo.title
        )

        Snackbar.make(binding.root, R.string.successfully_saved_message, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun showSaveErrorSnackbar() {
        Snackbar.make(binding.root, R.string.saving_error_message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_IMAGE_INFO = "com.android.maxclub.nasaapod.imageactyvity.imageinfo"

        fun newIntent(context: Context, imageInfo: ImageInfo): Intent =
            Intent(context, ImageViewerActivity::class.java).apply {
                putExtra(EXTRA_IMAGE_INFO, imageInfo)
            }
    }
}