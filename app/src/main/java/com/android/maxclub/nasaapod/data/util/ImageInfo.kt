package com.android.maxclub.nasaapod.data.util

import java.io.Serializable
import java.util.*

data class ImageInfo(
    val date: Date,
    val title: String,
    val url: String,
    val hdUrl: String? = null,
    val copyright: String? = null
) : Serializable