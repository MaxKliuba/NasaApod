package com.android.maxclub.nasaapod.fragments

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.net.toUri
import com.android.maxclub.nasaapod.ImageActivity
import com.android.maxclub.nasaapod.R
import com.android.maxclub.nasaapod.data.Apod
import com.android.maxclub.nasaapod.data.ImageInfo
import com.android.maxclub.nasaapod.data.MediaType
import com.android.maxclub.nasaapod.utils.formatDate
import java.util.*

abstract class BaseApodFragment : Fragment() {
    protected fun tryOpenApodMedia(apod: Apod?, contextView: View) {
        if (apod != null && apod.mediaType == MediaType.IMAGE) {
            val imageInfo = ImageInfo(
                date = apod.date,
                title = apod.title,
                url = apod.url,
                hdUrl = apod.hdUrl,
                copyright = apod.copyright
            )
            ImageActivity.newIntent(requireContext(), imageInfo).also { intent ->
                startActivity(intent)
            }
        } else if (apod != null && apod.mediaType == MediaType.VIDEO) {
            Intent(Intent.ACTION_VIEW, apod.url.toUri()).also { intent ->
                startActivity(intent)
            }
        } else {
            Toast.makeText(context, R.string.show_error_message, Toast.LENGTH_SHORT).show()
        }
    }

    protected fun shareApod(apod: Apod) {
        val subject = "${getString(R.string.app_name)} - ${apod.title}"
        val text = """
                |${apod.title}
                |
                |${
            formatDate(
                apod.date,
                getString(R.string.date_format_pattern),
                Locale(getString(R.string.language), getString(R.string.country))
            )
        }
                |
                |${apod.explanation}
                |${
            apod.copyright?.let { copyright ->
                "\n${getString(R.string.copyright_placeholder, copyright)}\n"
            } ?: ""
        }
                |${apod.url}
        """.trimMargin()

        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }.let { intent ->
            Intent.createChooser(intent, null)
        }.also { chooserIntent ->
            startActivity(chooserIntent)
        }
    }
}