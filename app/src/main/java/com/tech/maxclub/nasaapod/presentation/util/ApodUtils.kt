package com.tech.maxclub.nasaapod.presentation.util

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.tech.maxclub.nasaapod.R
import com.tech.maxclub.nasaapod.domain.model.Apod
import com.tech.maxclub.nasaapod.domain.model.ImageInfo
import com.tech.maxclub.nasaapod.presentation.image_viewer.ImageViewerActivity
import com.tech.maxclub.nasaapod.util.formatDate
import java.util.*

fun openImageMedia(imageInfo: ImageInfo, context: Context) {
    ImageViewerActivity.newIntent(context, imageInfo).also { intent ->
        context.startActivity(intent)
    }
}

fun openVideoMedia(videoUrl: String, context: Context) {
    Intent(Intent.ACTION_VIEW, videoUrl.toUri()).also { intent ->
        context.startActivity(intent)
    }
}

fun shareApod(apod: Apod, context: Context) {
    val subject = "${context.getString(R.string.app_name)} - ${apod.title}"
    val text = """
                |${apod.title}
                |
                |${
        formatDate(
            apod.date,
            context.getString(R.string.date_format_pattern),
            Locale(context.getString(R.string.language), context.getString(R.string.country))
        )
    }
                |${if (apod.explanation.isNotBlank()) "\n${apod.explanation}" else ""}
                |${
        apod.copyright?.let { copyright ->
            "\n${context.getString(R.string.copyright_placeholder, copyright)}\n"
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
        context.startActivity(chooserIntent)
    }
}