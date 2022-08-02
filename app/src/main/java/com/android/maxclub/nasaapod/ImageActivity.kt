package com.android.maxclub.nasaapod

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.maxclub.nasaapod.data.ImageInfo
import com.android.maxclub.nasaapod.databinding.ActivityImageBinding
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

private const val EXTRA_IMAGE_INFO = "com.android.maxclub.nasaapod.imageactyvity.imageinfo"

class ImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageBinding

    private lateinit var imageInfo: ImageInfo
    private var image: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageInfo = intent.getSerializableExtra(EXTRA_IMAGE_INFO) as ImageInfo

        binding.toolbar.apply {
            setSupportActionBar(this)
            title = imageInfo.title
            subtitle = imageInfo.copyright?.let { copyright ->
                getString(R.string.copyright_placeholder, copyright)
            }
            setNavigationOnClickListener {
                finish()
            }
        }
        Picasso.get()
            .load(imageInfo.url)
            .placeholder(R.drawable.ic_full_image_placeholder_24)
            .error(R.drawable.ic_error_full_image_placeholder_24)
            .apply {
                into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        image = bitmap
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        image = null
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    }
                })
            }
            .into(binding.imageView)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_image, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.download -> {
                tryDownloadImage()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun tryDownloadImage() {
        image?.let { bitmap ->
            MediaStore.Images.Media.insertImage(
                contentResolver,
                bitmap,
                "IMG_${imageInfo.date.time}",
                imageInfo.title
            )
            Toast.makeText(this, R.string.downloaded_successfully_message, Toast.LENGTH_SHORT)
                .show()
        } ?: Toast.makeText(this, R.string.download_error_message, Toast.LENGTH_SHORT).show()

    }

    companion object {
        fun newIntent(context: Context, imageInfo: ImageInfo): Intent =
            Intent(context, ImageActivity::class.java).apply {
                putExtra(EXTRA_IMAGE_INFO, imageInfo)
            }
    }
}